package io.polaris.mybatis.interceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.polaris.core.collection.Lists;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Qt
 * @since Jul 04, 2024
 */
@Slf4j
public class StatementBuilder extends MappedStatement.Builder {
	public static final String RESOURCE_MARK = "|Generated";
	private final String id;
	private final Configuration configuration;
	private final MappedStatement statement;
	private final TypeHandlerRegistry typeHandlerRegistry;


	public StatementBuilder(MappedStatement statement) {
		super(statement.getConfiguration(), statement.getId(), statement.getSqlSource(), statement.getSqlCommandType());
		this.id = statement.getId();
		this.statement = statement;
		this.configuration = statement.getConfiguration();
		this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
		// copy
		this.resource(statement.getResource())
			.fetchSize(statement.getFetchSize())
			.timeout(statement.getTimeout())
			.statementType(statement.getStatementType())
			.keyGenerator(statement.getKeyGenerator())
			.keyProperty(this.joining(statement.getKeyProperties()))
			.keyColumn(this.joining(statement.getKeyColumns()))
			.databaseId(statement.getDatabaseId())
			.lang(statement.getLang())
			.resultOrdered(statement.isResultOrdered())
			.resultSets(this.joining(statement.getResultSets()))
			.resultMaps(statement.getResultMaps())
			.resultSetType(statement.getResultSetType())
			.flushCacheRequired(statement.isFlushCacheRequired())
			.useCache(statement.isUseCache())
			.parameterMap(statement.getParameterMap())
			.cache(statement.getCache())
		;
	}

	private String joining(String[] arr) {
		return arr == null ? null : String.join(SymbolConsts.COMMA, arr);
	}

	@Override
	public MappedStatement build() {
		this.resource(statement.getResource() + RESOURCE_MARK);
		return super.build();
	}

	public StatementBuilder useGeneratedKeys(List<ColumnMeta> columns, String prefix) {
		StringJoiner keyProperty = new StringJoiner(SymbolConsts.COMMA);
		StringJoiner keyColumn = new StringJoiner(SymbolConsts.COMMA);
		for (ColumnMeta column : columns) {
			String columnName = column.getColumnName();
			String fieldName = column.getFieldName();
			keyColumn.add(columnName);
			if (Strings.isBlank(prefix)) {
				keyProperty.add(fieldName);
			} else {
				keyProperty.add(prefix + "." + fieldName);
			}
		}
		this.keyProperty(keyProperty.toString());
		this.keyColumn(keyColumn.toString());
		// TODO 后续添加 SelectKeyGenerator 的支持
		this.keyGenerator(Jdbc3KeyGenerator.INSTANCE);
		return this;
	}


	public StatementBuilder useResultMappings(Class<?> resultType, List<ResultMapping> resultMappings) {
		List<ResultMap> originalResultMaps = statement.getResultMaps();
		if (originalResultMaps != null && !originalResultMaps.isEmpty()) {
			ResultMap originalResultMap = originalResultMaps.get(0);
			List<ResultMapping> filteredMappings = new ArrayList<>(originalResultMap.getResultMappings());
			if (!filteredMappings.isEmpty()) {
				// 存在自定义映射，则忽略自动映射生成
				return this;
			}
			Set<String> propertySet = new HashSet<>();
			addPropertySet(originalResultMap.getResultMappings(), propertySet);

			for (ResultMapping mapping : resultMappings) {
				String property = mapping.getProperty();
				if (property != null && propertySet.contains(property)) {
					continue;
				}
				filteredMappings.add(mapping);
			}

			List<ResultMap> resultMaps = new ArrayList<>(originalResultMaps);
			ResultMap resultMap = new ResultMap.Builder(this.configuration, originalResultMap.getId() + RESOURCE_MARK, resultType, filteredMappings, originalResultMap.getAutoMapping())
				.build();
			// 替换已有的结果映射
			resultMaps.set(0, resultMap);
			this.resultMaps(resultMaps);
		} else {
			String id = this.id + "__ResultMap" + RESOURCE_MARK;
			ResultMap resultMap = new ResultMap.Builder(this.configuration, id, resultType, resultMappings, null)
				.build();
			this.resultMaps(Lists.asList(resultMap));
		}
		return this;
	}

	private static void addPropertySet(List<ResultMapping> list, Set<String> propertySet) {
		if (list != null && !list.isEmpty()) {
			for (ResultMapping mapping : list) {
				String property = mapping.getProperty();
				if (property != null) {
					propertySet.add(property);
				}
			}
		}
	}


	@SuppressWarnings("all")
	public TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
		if (typeHandlerType == null) {
			return null;
		}
		// 使用自定义缓存，强制创建带构造参数的TypeHandler
		Map<Tuple2<Class<?>, Class<? extends TypeHandler<?>>>, Ref<TypeHandler<?>>> cache = getTypeHandlerCache(configuration);
		Ref<TypeHandler<?>> ref = cache.computeIfAbsent(Tuple2.of(javaType, typeHandlerType), k -> {
			try {
				// 借用typeHandlerRegistry.getInstance构建，未来考虑支持更灵活的方式
				TypeHandler<?> handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
				return Ref.of(handler);
			} catch (Exception e) {
				log.error("", e);
				return Ref.of(null);
			}
		});
		if (ref != null) {
			return ref.get();
		}
		/**
		 * @see org.apache.ibatis.builder.BaseBuilder#resolveTypeHandler(Class, Class)
		 */
		// javaType ignored for injected handlers see issue #746 for full detail
		TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
		if (handler == null) {
			// not in registry, create a new one
			handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
		}
		return handler;
	}

	private static final List<Tuple2<Configuration, Map<Tuple2<Class<?>, Class<? extends TypeHandler<?>>>, Ref<TypeHandler<?>>>>> typeHandlerCache = new CopyOnWriteArrayList<>();

	private static Map<Tuple2<Class<?>, Class<? extends TypeHandler<?>>>, Ref<TypeHandler<?>>> getTypeHandlerCache(Configuration configuration) {
		for (Tuple2<Configuration, Map<Tuple2<Class<?>, Class<? extends TypeHandler<?>>>, Ref<TypeHandler<?>>>> cache : typeHandlerCache) {
			if (cache.getFirst() == configuration) {
				return cache.getSecond();
			}
		}
		Map<Tuple2<Class<?>, Class<? extends TypeHandler<?>>>, Ref<TypeHandler<?>>> cache = new ConcurrentHashMap<>();
		typeHandlerCache.add(Tuple2.of(configuration, cache));
		return cache;
	}

	public Configuration getConfiguration() {
		return configuration;
	}
}

package io.polaris.mybatis.interceptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.string.Strings;
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
		String id = this.id + "__GeneratedResultMap";
		List<ResultMap> resultMaps = new ArrayList<>();
		List<ResultMap> originalResultMaps = statement.getResultMaps();
		if (originalResultMaps != null) {
			Set<String> propertySet = new HashSet<>();
			for (ResultMap map : originalResultMaps) {
				addPropertySet(map.getResultMappings(), propertySet);
				addPropertySet(map.getIdResultMappings(), propertySet);
				addPropertySet(map.getConstructorResultMappings(), propertySet);
				addPropertySet(map.getPropertyResultMappings(), propertySet);
			}
			List<ResultMapping> filteredMappings = new ArrayList<>();
			for (ResultMapping mapping : resultMappings) {
				String property = mapping.getProperty();
				if (property != null && propertySet.contains(property)) {
					continue;
				}
				filteredMappings.add(mapping);
			}
			ResultMap resultMap = new ResultMap.Builder(this.configuration, id, resultType, filteredMappings, null)
				.build();
			resultMaps.addAll(originalResultMaps);
			resultMaps.add(resultMap);
		} else {
			ResultMap resultMap = new ResultMap.Builder(this.configuration, id, resultType, resultMappings, null)
				.build();
			resultMaps.add(resultMap);
		}
		this.resultMaps(resultMaps);
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


	/**
	 * @see org.apache.ibatis.builder.BaseBuilder#resolveTypeHandler(Class, Class)
	 */
	@SuppressWarnings("all")
	public TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
		if (typeHandlerType == null) {
			return null;
		}
		// javaType ignored for injected handlers see issue #746 for full detail
		TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
		if (handler == null) {
			// not in registry, create a new one
			handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
		}
		return handler;
	}

	public Configuration getConfiguration() {
		return configuration;
	}
}

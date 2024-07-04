package io.polaris.mybatis.interceptor;

import java.util.ArrayList;
import java.util.List;
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


	public MappedStatement useGeneratedKeys(List<ColumnMeta> columns, String prefix) {
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
		return this.build();
	}


	@SuppressWarnings("all")
	/**
	 * @see org.apache.ibatis.builder.BaseBuilder#resolveTypeHandler(Class, Class)
	 */
	private TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
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

	private List<ResultMap> statementResultMaps(String statementId, Class<?> resultType, List<ResultMapping> resultMappings) {
		List<ResultMap> resultMaps = new ArrayList<>();
		ResultMap resultMap = new ResultMap.Builder(configuration, statementId, resultType, resultMappings, null)
			.build();
		resultMaps.add(resultMap);
		return resultMaps;
	}

}

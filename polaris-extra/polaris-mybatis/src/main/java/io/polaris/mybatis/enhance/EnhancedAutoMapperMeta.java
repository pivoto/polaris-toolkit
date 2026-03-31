package io.polaris.mybatis.enhance;

import java.util.ArrayList;
import java.util.List;

import io.polaris.core.collection.Iterables;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.interceptor.StatementBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * 存储与某个 SQL 映射语句相关的元数据信息。
 *
 * @author Qt
 * @since Mar 30, 2026
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
@lombok.Builder(builderClassName = "Builder", toBuilder = true)
public class EnhancedAutoMapperMeta {

	private final List<ColumnMeta> autoColumns;
	private final String autoColumnPrefix;
	private final List<ResultMappingMeta> resultMappings;
	private final Class<?> resultJavaType;

	public boolean applyTo(StatementBuilder statementBuilder, SqlCommandType sqlCommandType) {
		boolean modified = false;
		if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
			if (Iterables.isNotEmpty(autoColumns)){
				KeyGenerator keyGenerator = statementBuilder.keyGenerator();
				// 忽略已设置KeyGenerator的情况
				if (keyGenerator instanceof NoKeyGenerator) {
					statementBuilder.useGeneratedKeys(autoColumns, Strings.trimToNull(autoColumnPrefix));
					modified = true;
				}
			}
		} else if (SqlCommandType.SELECT.equals(sqlCommandType)) {
			if (this.resultMappings != null && !this.resultMappings.isEmpty() && this.resultJavaType != null) {
				List<ResultMapping> mappings = new ArrayList<>();
				for (EnhancedAutoMapperMeta.ResultMappingMeta resultMappingMeta : this.resultMappings) {
					ResultMapping.Builder builder = new ResultMapping.Builder(statementBuilder.getConfiguration(), resultMappingMeta.property());
					if (resultMappingMeta.typeHandler() != null) {
						builder.typeHandler(resultMappingMeta.typeHandler());
					} else if (resultMappingMeta.typeHandlerClass() != null) {
						TypeHandler<?> typeHandler = statementBuilder.resolveTypeHandler(resultMappingMeta.javaType(), resultMappingMeta.typeHandlerClass());
						builder.typeHandler(typeHandler);
					}
					if (resultMappingMeta.column() != null) {
						builder.column(resultMappingMeta.column());
					}
					if (resultMappingMeta.javaType() != null) {
						builder.javaType(resultMappingMeta.javaType());
					}
					if (resultMappingMeta.jdbcType() != null) {
						builder.jdbcType(resultMappingMeta.jdbcType());
					}
					if (resultMappingMeta.columnPrefix() != null) {
						builder.columnPrefix(resultMappingMeta.columnPrefix());
					}
					if (resultMappingMeta.flags() != null) {
						builder.flags(resultMappingMeta.flags());
					}
					mappings.add(builder.build());
				}
				statementBuilder.useResultMappings(this.resultJavaType, mappings);
				modified = true;
			}
		}
		return modified;
	}

	@RequiredArgsConstructor
	@Accessors(fluent = true)
	@Getter
	@lombok.Builder(builderClassName = "Builder", toBuilder = true)
	public static class ResultMappingMeta {
		private final String property;
		private final String column;
		private final Class<?> javaType;
		private final JdbcType jdbcType;
		private final Class<? extends TypeHandler<?>> typeHandlerClass;
		private final TypeHandler<?> typeHandler;
		private final String columnPrefix;
		private final List<ResultFlag> flags;
	}

}

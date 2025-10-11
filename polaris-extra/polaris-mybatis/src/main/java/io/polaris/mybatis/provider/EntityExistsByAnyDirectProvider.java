package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.SqlStatements;
import io.polaris.mybatis.consts.MappingKeys;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since Sep 11, 2023
 */
@Slf4j
public class EntityExistsByAnyDirectProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = isQueryExistsByCount();
		try {
			return provideSql(parameterObject, context, (map, ctx) -> {
				String sql = SqlStatements.buildExistsByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(context), queryByCount, false);
				if (log.isDebugEnabled()) {
					log.debug("<sql>\n{}\n<bindings>\n{}", sql, map);
				}
				return sql;
			});
		} finally {
			clearQueryExistsByCount();
		}
	}
}

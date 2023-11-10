package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.mybatis.consts.EntityMapperKeys;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
public class SqlUpdateProvider extends BaseProviderMethodResolver{

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		UpdateStatement<?> st = (UpdateStatement<?>) map.get(EntityMapperKeys.UPDATE);
		return BaseEntityProvider.getSqlWithBindings(map, st);
	}

}

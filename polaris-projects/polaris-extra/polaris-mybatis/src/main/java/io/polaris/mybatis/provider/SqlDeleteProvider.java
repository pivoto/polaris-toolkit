package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.mybatis.consts.EntityMapperKeys;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
public class SqlDeleteProvider extends BaseProviderMethodResolver{

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		DeleteStatement<?> st = (DeleteStatement<?>) map.get(EntityMapperKeys.DELETE);
		return BaseEntityProvider.getSqlWithBindings(map, st);
	}

}

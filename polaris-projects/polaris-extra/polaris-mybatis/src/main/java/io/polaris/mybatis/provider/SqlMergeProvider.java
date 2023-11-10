package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.consts.EntityMapperKeys;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
public class SqlMergeProvider extends BaseProviderMethodResolver{

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		MergeStatement<?> st = (MergeStatement<?>) map.get(EntityMapperKeys.MERGE);
		return BaseEntityProvider.getSqlWithBindings(map, st);
	}

}

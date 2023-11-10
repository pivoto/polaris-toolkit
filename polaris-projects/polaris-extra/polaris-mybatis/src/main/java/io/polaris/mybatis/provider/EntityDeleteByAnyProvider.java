package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
public class EntityDeleteByAnyProvider extends BaseProviderMethodResolver{

	@Published
	public static String provideSql(Map<String, Object> map, ProviderContext context) {
		return BaseEntityProvider.doDeleteEntity(map, context, false);
	}

}

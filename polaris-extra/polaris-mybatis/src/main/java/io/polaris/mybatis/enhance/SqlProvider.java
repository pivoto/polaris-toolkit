package io.polaris.mybatis.enhance;

import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since Apr 02, 2026
 */
public interface SqlProvider {


	String provideSql(Map<String, Object> param, EnhancedProviderContext ctx);

	default String provideSql(Map<String, Object> param, ProviderContext ctx) {
		return provideSql(param, EnhancedProviderContext.ofLazy(ctx));
	}

}

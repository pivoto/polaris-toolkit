package io.polaris.mybatis.auth.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public class TableConfig {

	/** 数据表对应数据权限的配置 */
	private final Map<String, ScopeConfig> scopes = new ConcurrentHashMap<>();

	public void addScopeConfig(String scopeName, ScopeConfig scopeConfig) {
		scopes.put(scopeName, scopeConfig);
	}

	public ScopeConfig getScopeConfig(String scopeName) {
		return scopes.get(scopeName);
	}

	public void removeScopeConfig(String scopeName) {
		scopes.remove(scopeName);
	}
}

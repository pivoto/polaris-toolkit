package io.polaris.mybatis.auth.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public class TableConfig {

	/** 数据表对应数据权限的配置 */
	private final Map<String, ScopeConfig> scopeConfigs = new ConcurrentHashMap<>();

	public void addScopeConfig(String scopeName, ScopeConfig scopeConfig) {
		scopeConfigs.put(scopeName, scopeConfig);
	}

	public ScopeConfig getScopeConfig(String scopeName) {
		return scopeConfigs.get(scopeName);
	}

	public void removeScopeConfig(String scopeName) {
		scopeConfigs.remove(scopeName);
	}
}

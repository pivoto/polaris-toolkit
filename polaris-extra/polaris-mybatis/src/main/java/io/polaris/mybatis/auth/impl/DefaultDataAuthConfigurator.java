package io.polaris.mybatis.auth.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import io.polaris.mybatis.auth.DataAuthConfigurator;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
public class DefaultDataAuthConfigurator implements DataAuthConfigurator {

	private volatile boolean mybatisInterceptorEnabled = true;
	private final Map<String, TableConfig> tableConfigs = new ConcurrentHashMap<>();

	@Override
	public boolean isScopeEnabled(String tableName, String scopeName) {
		TableConfig tableConfig = tableConfigs.get(tableName);
		if (tableConfig == null) {
			return false;
		}
		ScopeConfig scopeConfig = tableConfig.getScopeConfig(scopeName);
		if (scopeConfig == null) {
			return false;
		}
		return scopeConfig.isEnabled();
	}

	@Nonnull
	@Override
	public Set<String> getGlobalScopes() {
		return Collections.emptySet();
	}

	@Override
	public boolean isMybatisInterceptorEnabled() {
		return mybatisInterceptorEnabled;
	}

	public void setMybatisInterceptorEnabled(boolean mybatisInterceptorEnabled) {
		this.mybatisInterceptorEnabled = mybatisInterceptorEnabled;
	}

	public void addTableConfig(String tableName, TableConfig tableConfig) {
		tableConfigs.put(tableName, tableConfig);
	}

	public void removeTableConfig(String tableName) {
		tableConfigs.remove(tableName);
	}

	public TableConfig getTableConfig(String tableName) {
		return tableConfigs.get(tableName);
	}

}

package io.polaris.mybatis.auth.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import io.polaris.core.spi.SpiDefault;
import io.polaris.mybatis.auth.DataAuthConfigurator;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
@SpiDefault
public class DefaultDataAuthConfigurator implements DataAuthConfigurator {

	private static final class MemoryConfiguratorHolder {
		static final MemoryDataAuthConfigurator memoryConfigurator = new MemoryDataAuthConfigurator();
	}

	public static MemoryDataAuthConfigurator memoryConfigurator() {
		return MemoryConfiguratorHolder.memoryConfigurator;
	}

	@Override
	public boolean isScopeEnabled(String tableName, String scopeName) {
		return memoryConfigurator().isScopeEnabled(tableName, scopeName);
	}

	@Nonnull
	@Override
	public Set<String> getGlobalScopes() {
		return memoryConfigurator().getGlobalScopes();
	}

	@Override
	public boolean isMybatisInterceptorEnabled() {
		return memoryConfigurator().isMybatisInterceptorEnabled();
	}

}

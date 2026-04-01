package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;

import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;

/**
 * 在不启用 EnhancedProviderSqlSource 时，尝试强制解析生成新的 Mapper 与 MappedStatement 并覆盖原有配置
 *
 * @author Qt
 * @since Mar 31, 2026
 */
public class MapperRegistryHelper {
	private final EnhancedMapperRegistry mapperRegistry;

	public static MapperRegistryHelper of(Configuration configuration) {
		return new MapperRegistryHelper(configuration.getMapperRegistry());
	}

	public static MapperRegistryHelper of(MapperRegistry mapperRegistry) {
		return new MapperRegistryHelper(mapperRegistry);
	}

	private MapperRegistryHelper(MapperRegistry mapperRegistry) {
		this.mapperRegistry = mapperRegistry instanceof EnhancedMapperRegistry
			? (EnhancedMapperRegistry) mapperRegistry
			: new EnhancedMapperRegistry(mapperRegistry);
	}

	public void addMapperForcibly(Class<?> type) {
		mapperRegistry.addMapperForcibly(type);
	}

	public void addMapperForcibly(Method method) {
		mapperRegistry.addMapperForcibly(method);
	}

	public <T> void addMapper(Class<T> type) {
		mapperRegistry.addMapper(type);
	}

	public <T> MapperProxyFactory<T> getMapperProxyFactory(Class<T> type) {
		return mapperRegistry.getMapperProxyFactory(type);
	}
}

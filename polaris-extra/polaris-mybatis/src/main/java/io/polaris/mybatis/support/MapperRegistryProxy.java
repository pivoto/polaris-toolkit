package io.polaris.mybatis.support;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.Collection;

/**
 * @author Qt
 * @since  Sep 11, 2023
 */
public class MapperRegistryProxy extends MapperRegistry {

	private final MapperRegistry registry;

	public MapperRegistryProxy( MapperRegistry registry) {
		super(null);
		this.registry = registry;
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return registry.getMapper(type, sqlSession);
	}

	@Override
	public <T> boolean hasMapper(Class<T> type) {
		return registry.hasMapper(type);
	}

	@Override
	public <T> void addMapper(Class<T> type) {
		registry.addMapper(type);
	}

	@Override
	public Collection<Class<?>> getMappers() {
		return registry.getMappers();
	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		registry.addMappers(packageName, superType);
	}

	@Override
	public void addMappers(String packageName) {
		registry.addMappers(packageName);
	}
}

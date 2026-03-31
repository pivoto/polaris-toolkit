package io.polaris.mybatis.enhance;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.polaris.core.reflect.Methods;
import io.polaris.core.reflect.Reflects;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * 重写 MapperRegistry 中相关方法，增加扩展功能
 *
 * @author Qt
 * @since Mar 28, 2026
 */
public class EnhancedMapperRegistry extends MapperRegistry {
	private final EnhancedConfiguration config;
	private final Map<Class<?>, MapperProxyFactory<?>> knownMappers;


	public EnhancedMapperRegistry(EnhancedConfiguration config) {
		super(config);
		this.config = config;
		this.knownMappers = getSuperKnownMappers(this);
	}

	public EnhancedMapperRegistry(MapperRegistry registry) {
		this(getSuperConfiguration(registry), registry);
	}

	private EnhancedMapperRegistry(EnhancedConfiguration config, MapperRegistry registry) {
		super(config);
		this.config = config;
		this.knownMappers = getSuperKnownMappers(registry);
	}

	private static EnhancedConfiguration getSuperConfiguration(MapperRegistry mapperRegistry) {
		Configuration config = null;
		try {
			MethodHandle methodHandle = Methods.findPrivateGetter(MapperRegistry.class, "config", Configuration.class);
			config = (Configuration) methodHandle.invokeExact(mapperRegistry);
		} catch (Throwable e) {
			config = (Configuration) Reflects.getFieldValueQuietly(mapperRegistry, "config");
		}
		if (!(config instanceof EnhancedConfiguration)) {
			throw new IllegalStateException();
		}
		return (EnhancedConfiguration) config;
	}

	@SuppressWarnings("unchecked")
	private static Map<Class<?>, MapperProxyFactory<?>> getSuperKnownMappers(MapperRegistry mapperRegistry) {
		Map<Class<?>, MapperProxyFactory<?>> knownMappers = null;
		try {
			@SuppressWarnings("rawtypes")
			Class<Map<Class<?>, MapperProxyFactory<?>>> fieldType = (Class) Map.class;
			MethodHandle methodHandle = Methods.findPrivateGetter(MapperRegistry.class, "knownMappers", fieldType);
			knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) methodHandle.invokeExact(mapperRegistry);
		} catch (Throwable e) {
			knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) Reflects.getFieldValueQuietly(mapperRegistry, "knownMappers");
		}
		if (knownMappers == null) {
			throw new IllegalStateException();
		}
		return knownMappers;
	}


	public void copyKnownMappers(MapperRegistry mapperRegistry) {
		if (mapperRegistry instanceof EnhancedMapperRegistry) {
			this.knownMappers.putAll(((EnhancedMapperRegistry) mapperRegistry).getKnownMappers());
		} else {
			Map<Class<?>, MapperProxyFactory<?>> superKnownMappers = getSuperKnownMappers(mapperRegistry);
			this.knownMappers.putAll(superKnownMappers);
		}
	}

	public Configuration getConfig() {
		return config;
	}

	public Map<Class<?>, MapperProxyFactory<?>> getKnownMappers() {
		return knownMappers;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
		if (mapperProxyFactory == null) {
			throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
		}
		try {
			return mapperProxyFactory.newInstance(sqlSession);
		} catch (Exception e) {
			throw new BindingException("Error getting mapper instance. Cause: " + e, e);
		}
	}

	@Override
	public <T> boolean hasMapper(Class<T> type) {
		return knownMappers.containsKey(type);
	}


	@Override
	public <T> void addMapper(Class<T> type) {
		if (type.isInterface()) {
			if (hasMapper(type)) {
				throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
			}
			boolean loadCompleted = false;
			try {
				knownMappers.put(type, new MapperProxyFactory<>(type));
				// It's important that the type is added before the parser is run
				// otherwise the binding may automatically be attempted by the
				// mapper parser. If the type is already known, it won't try.
				EnhancedMapperAnnotationBuilder parser = new EnhancedMapperAnnotationBuilder(config, type);
				parser.parse();
				loadCompleted = true;
			} finally {
				if (!loadCompleted) {
					knownMappers.remove(type);
				}
			}
		}
	}

	public void addMapperForcibly(Class<?> type) {
		if (type.isInterface()) {
			EnhancedMapperAnnotationBuilder parser = new EnhancedMapperAnnotationBuilder(config, type);
			parser.parseForcibly();
		}
	}

	public void addMapperForcibly(Method method) {
		Class<?> type = method.getDeclaringClass();
		if (type.isInterface()) {
			EnhancedMapperAnnotationBuilder parser = new EnhancedMapperAnnotationBuilder(config, type);
			parser.parseMethodForcibly(method);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> MapperProxyFactory<T> getMapperProxyFactory(Class<T> type) {
		return (MapperProxyFactory<T>) knownMappers.computeIfAbsent(type, k -> new MapperProxyFactory<>(type));
	}


	/**
	 * Gets the mappers.
	 *
	 * @return the mappers
	 * @since 3.2.2
	 */
	@Override
	public Collection<Class<?>> getMappers() {
		return Collections.unmodifiableCollection(knownMappers.keySet());
	}

	/**
	 * Adds the mappers.
	 *
	 * @param packageName the package name
	 * @param superType   the super type
	 * @since 3.2.2
	 */
	@Override
	public void addMappers(String packageName, Class<?> superType) {
		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
		resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
		Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
		for (Class<?> mapperClass : mapperSet) {
			addMapper(mapperClass);
		}
	}

	/**
	 * Adds the mappers.
	 *
	 * @param packageName the package name
	 * @since 3.2.2
	 */
	@Override
	public void addMappers(String packageName) {
		addMappers(packageName, Object.class);
	}

}

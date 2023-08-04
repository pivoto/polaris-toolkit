package io.polaris.core.lang.bean;

import io.polaris.core.compiler.MemoryClassLoader;
import io.polaris.core.map.Maps;
import io.polaris.core.tuple.Tuple2;

import java.beans.IntrospectionException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 03, 2023
 */
public class BeanMetadatas {

	private static final Map<Class<?>, Class<BeanMetadata>> METADATA_CLASSES = Maps.newSoftMap(new ConcurrentHashMap<>());
	private static final Map<Class<?>, BeanMetadata> METADATA_CACHES = Maps.newSoftMap(new ConcurrentHashMap<>());

	public static <T> Class<BeanMetadata> getMetadataClass(Class<T> beanType) {
		return METADATA_CLASSES.computeIfAbsent(beanType, c -> {
			try {
				MemoryClassLoader loader = MemoryClassLoader.getInstance(beanType.getClassLoader());
				Tuple2<String, Map<String, byte[]>> rs = BeanMetadataBuilder.build(beanType);
				Map<String, byte[]> classes = rs.getSecond();
				classes.forEach((n, b) -> loader.add(n, b));
				Class<?> clazz = loader.loadClass(rs.getFirst());
				return (Class<BeanMetadata>) clazz;
			} catch (IntrospectionException e) {
				throw new IllegalStateException(e);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	public static <T> BeanMetadata getMetadata(Class<T> beanType) {
		BeanMetadata cache = METADATA_CACHES.computeIfAbsent(beanType, c -> {
			try {
				BeanMetadata metadata = BeanMetadatas.getMetadataClass(beanType).newInstance();
				return new BeanMetadatas.BeanMetadataCache(metadata);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		});
		return cache;
	}

	protected static class BeanMetadataCache implements BeanMetadata {
		private final Map<String, Type> types;
		private final Map<String, Function<Object, Object>> getters;
		private final Map<String, BiConsumer<Object, Object>> setters;

		protected BeanMetadataCache(BeanMetadata metadata) {
			this.types = metadata.types();
			this.getters = metadata.getters();
			this.setters = metadata.setters();
		}

		public BeanMetadataCache(Map<String, Type> types, Map<String, Function<Object, Object>> getters, Map<String, BiConsumer<Object, Object>> setters) {
			this.types = types;
			this.getters = getters;
			this.setters = setters;
		}

		@Override
		public Map<String, Type> types() {
			return types;
		}

		@Override
		public Map<String, Function<Object, Object>> getters() {
			return getters;
		}

		@Override
		public Map<String, BiConsumer<Object, Object>> setters() {
			return setters;
		}
	}
}

package io.polaris.core.lang.bean;

import io.polaris.core.map.Maps;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
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

	public static <T> Map<String, Type> getPropertyTypes(Class<T> beanType) {
		try {
			Map<String, Type> types = new HashMap<>();
			BeanInfo beanInfo = Introspector.getBeanInfo(beanType);
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String name = pd.getName();
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod != null) {
					Type type = writeMethod.getGenericParameterTypes()[0];
					types.put(name, type);
				}
			}
			return types;
		} catch (IntrospectionException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> Class<BeanMetadata> getMetadataClass(Class<T> beanType) {
		return METADATA_CLASSES.computeIfAbsent(beanType, c -> BeanMetadataBuilder.buildMetadataClass(beanType));
	}

	protected static class BeanMetadataCache implements BeanMetadata {
		private final Map<String, Type> types;
		private final Map<String, Function<Object, Object>> getters;
		private final Map<String, BiConsumer<Object, Object>> setters;

		protected BeanMetadataCache(BeanMetadata metadata) {
			this(metadata.types(), metadata.getters(), metadata.setters());
		}

		public BeanMetadataCache(Map<String, Type> types, Map<String, Function<Object, Object>> getters, Map<String, BiConsumer<Object, Object>> setters) {
			this.types = Collections.unmodifiableMap(types);
			this.getters = Collections.unmodifiableMap(getters);
			this.setters = Collections.unmodifiableMap(setters);
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

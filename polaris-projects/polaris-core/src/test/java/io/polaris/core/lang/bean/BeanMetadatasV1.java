package io.polaris.core.lang.bean;

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

import io.polaris.core.map.Maps;

/**
 * @author Qt
 * @since  Aug 03, 2023
 */
public class BeanMetadatasV1 {

	private static final Map<Class<?>, Class<BeanMetadataV1>> METADATA_CLASSES = Maps.newSoftMap(new ConcurrentHashMap<>());
	private static final Map<Class<?>, BeanMetadataV1> METADATA_CACHES = Maps.newSoftMap(new ConcurrentHashMap<>());

	public static <T> BeanMetadataV1 getMetadata(Class<T> beanType) {
		BeanMetadataV1 cache = METADATA_CACHES.computeIfAbsent(beanType, c -> {
			try {
				BeanMetadataV1 metadata = BeanMetadatasV1.getMetadataClass(beanType).newInstance();
				return new BeanMetadataV1Cache(metadata);
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

	public static <T> Class<BeanMetadataV1> getMetadataClass(Class<T> beanType) {
		return METADATA_CLASSES.computeIfAbsent(beanType, c -> BeanMetadataV1Builder.buildMetadataClass(beanType));
	}

	protected static class BeanMetadataV1Cache implements BeanMetadataV1 {
		private final Map<String, Type> types;
		private final Map<String, Function<Object, Object>> getters;
		private final Map<String, BiConsumer<Object, Object>> setters;

		protected BeanMetadataV1Cache(BeanMetadataV1 metadata) {
			this(metadata.types(), metadata.getters(), metadata.setters());
		}

		public BeanMetadataV1Cache(Map<String, Type> types, Map<String, Function<Object, Object>> getters, Map<String, BiConsumer<Object, Object>> setters) {
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

package io.polaris.core.lang.bean;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.asm.reflect.BeanLambdaAccess;
import io.polaris.core.asm.reflect.BeanPropertyInfo;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.map.Maps;
import io.polaris.core.string.StringCases;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
public class LambdaMetaObject<T> extends MetaObject<T> {
	private static final ILogger log = ILoggers.of(LambdaMetaObject.class);
	private static final Map<JavaType<?>, LambdaMetaObject<?>> CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private Map<String, LambdaAccessor> properties;
	private Map<String, LambdaAccessor> propertiesCaseInsensitive;


	private LambdaMetaObject(JavaType<T> beanType) {
		super(beanType);
	}


	@Getter
	@Setter
	static class LambdaAccessor {
		private final LambdaMetaObject<?> meta;
		private final Function<Object, Object> getter;
		private final BiConsumer<Object, Object> setter;

		LambdaAccessor(LambdaMetaObject<?> meta, Function<Object, Object> getter, BiConsumer<Object, Object> setter) {
			this.meta = meta;
			this.getter = getter;
			this.setter = setter;
		}

		public Object get(Object bean) {
			return getter.apply(bean);
		}

		public void set(Object bean, Object val) {
			setter.accept(bean, val);
		}
	}

	@SuppressWarnings("all")
	public static <T> LambdaMetaObject<T> of(JavaType<T> beanType) {
		LambdaMetaObject<T> metaObject = (LambdaMetaObject<T>) CACHE.computeIfAbsent(beanType, LambdaMetaObject::new);
		if (metaObject.state() == INIT) {
			synchronized (metaObject) {
				if (metaObject.state() == INIT) {
					metaObject.parse();
				}
			}
		}
		return metaObject;
	}

	public static <T> LambdaMetaObject<T> of(Class<T> beanType) {
		return of(JavaType.of(beanType));
	}

	public static <T> LambdaMetaObject<T> of(TypeRef<T> beanType) {
		return of(JavaType.of(beanType));
	}

	public static <T> LambdaMetaObject<T> of(Type beanType) {
		return of(JavaType.of(beanType));
	}


	@Override
	protected <E> MetaObject<E> createMetaObject(JavaType<E> beanType) {
		return LambdaMetaObject.of(beanType);
	}

	@SuppressWarnings("all")
	@Override
	protected boolean initBeanAccessor(Class<T> rawClass) {
		BeanLambdaAccess<T> access = BeanLambdaAccess.get(rawClass);

		Map<String, LambdaAccessor> properties = new HashMap<>(64);
		for (Map.Entry<String, BeanPropertyInfo> entry : access.properties().entrySet()) {
			BeanPropertyInfo beanPropertyInfo = entry.getValue();
			Type propertyGenericType = beanPropertyInfo.getPropertyGenericType();
			String propertyName = beanPropertyInfo.getPropertyName();
			if (beanPropertyInfo.getField() != null) {
				// 忽略static field
				if (Modifier.isStatic(beanPropertyInfo.getField().getModifiers())) {
					continue;
				}
				LambdaMetaObject<Object> meta = LambdaMetaObject.of(propertyGenericType);
				Function<Object, Object> getter = access.getFieldGetter(propertyName);
				BiConsumer<Object, Object> setter = access.getFieldSetter(propertyName);
				LambdaAccessor accessor = new LambdaAccessor(meta, getter, setter);
				properties.put(propertyName, accessor);
			} else {
				LambdaMetaObject<Object> meta = LambdaMetaObject.of(propertyGenericType);
				Function<Object, Object> getter = access.getGetter(propertyName);
				BiConsumer<Object, Object> setter = access.getSetter(propertyName);
				LambdaAccessor accessor = new LambdaAccessor(meta, getter, setter);
				properties.put(propertyName, accessor);
			}
		}

		int size = properties.size();
		if (size > 0) {
			this.properties = new HashMap<>(2 * size);
			this.properties.putAll(properties);
			this.properties = Collections.unmodifiableMap(properties);

			this.propertiesCaseInsensitive = new CaseInsensitiveMap<>(new HashMap<>(2 * size), true);
			this.propertiesCaseInsensitive.putAll(properties);
			this.propertiesCaseInsensitive = Collections.unmodifiableMap(this.propertiesCaseInsensitive);
			return true;
		}
		return false;
	}


	@Override
	protected Object getBeanPropertyOrSetDefault(@Nonnull T o, int caseModel, @Nonnull String property) {
		LambdaAccessor accessor = properties.get(property);
		if (accessor == null || accessor.getter == null || accessor.setter == null) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter == null || accessor.setter == null) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter == null || accessor.setter == null) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter == null || accessor.setter == null) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					log.debug("不支持的属性：{}:{}", getBeanType().getTypeName(), property);
					return null;
				} else {
					return rtMeta.getPropertyOrSetDefault(o, caseModel, property);
				}
			}
		}
		Object val = accessor.get(o);
		if (val == null) {
			val = accessor.meta.newInstance();
			accessor.set(o, val);
		}
		return val;
	}

	@Override
	protected Object setBeanProperty(@Nonnull T o, int caseModel, @Nonnull String property, Object val) {
		LambdaAccessor accessor = properties.get(property);
		if (accessor == null || accessor.setter == null) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.setter == null) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.setter == null) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.setter == null) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					log.debug("不支持的属性：{}:{}", getBeanType().getTypeName(), property);
					return null;
				} else {
					return rtMeta.setProperty(o, caseModel, property, val);
				}
			}
		}
		accessor.set(o, val = Converters.convertQuietly(accessor.meta.getBeanType(), val));
		return val;
	}

	@Override
	@SuppressWarnings("all")
	protected MetaObject<?> getBeanProperty(int caseModel, @Nonnull String property) {
		LambdaAccessor accessor = properties.get(property);
		if (accessor == null) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null) {
				log.debug("不支持的属性：{}:{}", getBeanType().getTypeName(), property);
				return null;
			}
		}
		return accessor.meta;
	}

	@Override
	protected Object getBeanProperty(@Nonnull T o, int caseModel, @Nonnull String property) {
		LambdaAccessor accessor = properties.get(property);
		if (accessor == null || accessor.getter == null) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter == null) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter == null) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter == null) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					log.debug("不支持的属性：{}:{}", getBeanType().getTypeName(), property);
					return null;
				} else {
					return rtMeta.getProperty(o, caseModel, property);
				}
			}
		}
		return accessor.get(o);
	}

	@Override
	protected boolean hasBeanProperty(@Nonnull T o, int caseModel, @Nonnull String property) {
		LambdaAccessor accessor = properties.get(property);
		if (accessor == null || accessor.getter == null) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter == null) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter == null) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter == null) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					return false;
				} else {
					return rtMeta.hasProperty(o, caseModel, property);
				}
			}
		}
		return accessor.get(o) != null;
	}


}

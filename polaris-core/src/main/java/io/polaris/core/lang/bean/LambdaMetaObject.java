package io.polaris.core.lang.bean;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.map.Maps;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since Apr 12, 2024
 */
public class LambdaMetaObject<T> extends MetaObject<T> {
	private static final Logger log = Loggers.of(LambdaMetaObject.class);
	private static final Map<JavaType<?>, LambdaMetaObject<?>> CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private Map<String, LambdaProperty> properties;
	private Map<String, LambdaProperty> propertiesCaseInsensitive;


	private LambdaMetaObject(JavaType<T> beanType) {
		super(beanType);
	}


	static class LambdaProperty {
		private final MetaObject<?> meta;
		private final PropertyAccessor accessor;

		LambdaProperty(MetaObject<?> meta, PropertyAccessor accessor) {
			this.meta = meta;
			this.accessor = accessor;
		}

	}

	@SuppressWarnings("all")
	public static <T> LambdaMetaObject<T> of(JavaType<T> beanType) {
		LambdaMetaObject<T> metaObject = null;
		// 防止因对象回收后导致WeakMap结果丢失，尝试多次获取
		while ((metaObject = (LambdaMetaObject<T>) CACHE.computeIfAbsent(beanType, LambdaMetaObject::new)) == null) {
		}
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
		Map<String, PropertyAccessor> accessors = Beans.getLambdaFieldAndPropertyAccessors(rawClass);
		int size = accessors.size();
		if (size > 0) {
			Map<String, LambdaProperty> properties = new HashMap<>(size);
			Map<String, LambdaProperty> propertiesCaseInsensitive = new CaseInsensitiveMap<>(new HashMap<>(size), true);

			for (Map.Entry<String, PropertyAccessor> entry : accessors.entrySet()) {
				String propertyName = entry.getKey();
				PropertyAccessor accessor = entry.getValue();
				Type type = accessor.type();
				LambdaMetaObject<Object> meta = LambdaMetaObject.of(type);
				LambdaProperty lambdaProperty = new LambdaProperty(meta, accessor);
				properties.put(propertyName, lambdaProperty);
				propertiesCaseInsensitive.put(propertyName, lambdaProperty);
			}
			this.properties = Collections.unmodifiableMap(properties);
			this.propertiesCaseInsensitive = Collections.unmodifiableMap(propertiesCaseInsensitive);
			return true;
		}
		return false;
	}


	@Override
	protected Object getBeanPropertyOrSetDefault(@Nonnull T o, CaseModeOption caseMode, @Nonnull String name) {
		LambdaProperty property = properties.get(name);
		if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
			if (CaseMode.INSENSITIVE.is(caseMode)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
				if (CaseMode.CAMEL.is(caseMode)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
						if (CaseMode.INSENSITIVE.is(caseMode)) {
							property = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					log.debug("Unsupported property：{}:{}", getBeanType().getTypeName(), name);
					return null;
				} else {
					return rtMeta.getPropertyOrSetDefault(o, caseMode, name);
				}
			}
		}
		Object val = property.accessor.get(o);
		if (val == null) {
			val = property.meta.newInstance();
			property.accessor.set(o, val);
		}
		return val;
	}

	@Override
	protected Object setBeanProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String name, Object val) {
		LambdaProperty property = properties.get(name);
		if (property == null || !property.accessor.hasSetter()) {
			if (CaseMode.INSENSITIVE.is(caseMode)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasSetter()) {
				if (CaseMode.CAMEL.is(caseMode)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasSetter()) {
						if (CaseMode.INSENSITIVE.is(caseMode)) {
							property = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (property == null || !property.accessor.hasSetter()) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					log.debug("Unsupported property：{}:{}", getBeanType().getTypeName(), name);
					return null;
				} else {
					return rtMeta.setProperty(o, caseMode, name, val);
				}
			}
		}
		Object val1 = val = Converters.convertQuietly(property.meta.getBeanType(), val);
		property.accessor.set(o, val1);
		return val;
	}

	@Override
	@SuppressWarnings("all")
	protected MetaObject<?> getBeanProperty(CaseModeOption caseMode, @Nonnull String name) {
		LambdaProperty property = properties.get(name);
		if (property == null) {
			if (CaseMode.INSENSITIVE.is(caseMode)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null) {
				if (CaseMode.CAMEL.is(caseMode)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null) {
						if (CaseMode.INSENSITIVE.is(caseMode)) {
							property = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (property == null) {
				log.debug("Unsupported property：{}:{}", getBeanType().getTypeName(), name);
				return null;
			}
		}
		return property.meta;
	}

	@Override
	protected Object getBeanProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String name) {
		LambdaProperty property = properties.get(name);
		if (property == null || !property.accessor.hasGetter()) {
			if (CaseMode.INSENSITIVE.is(caseMode)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasGetter()) {
				if (CaseMode.CAMEL.is(caseMode)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasGetter()) {
						if (CaseMode.INSENSITIVE.is(caseMode)) {
							property = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (property == null || !property.accessor.hasGetter()) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					log.debug("Unsupported property：{}:{}", getBeanType().getTypeName(), name);
					return null;
				} else {
					return rtMeta.getProperty(o, caseMode, name);
				}
			}
		}
		return property.accessor.get(o);
	}

	@Override
	protected boolean hasBeanProperty(@Nonnull T o, CaseModeOption caseMode, @Nonnull String name) {
		LambdaProperty property = properties.get(name);
		if (property == null || !property.accessor.hasGetter()) {
			if (CaseMode.INSENSITIVE.is(caseMode)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasGetter()) {
				if (CaseMode.CAMEL.is(caseMode)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasGetter()) {
						if (CaseMode.INSENSITIVE.is(caseMode)) {
							property = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (property == null || !property.accessor.hasGetter()) {
				@SuppressWarnings("unchecked")
				MetaObject<T> rtMeta = createMetaObject(JavaType.of((Class<T>) o.getClass()));
				if (this.equals(rtMeta) || rtMeta.isObject() || rtMeta.isBasic() || rtMeta.isEnum()) {
					return false;
				} else {
					return rtMeta.hasProperty(o, caseMode, name);
				}
			}
		}
		return property.accessor.get(o) != null;
	}


}

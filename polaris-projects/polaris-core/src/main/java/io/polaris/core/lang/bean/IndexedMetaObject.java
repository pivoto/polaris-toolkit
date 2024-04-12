package io.polaris.core.lang.bean;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.asm.reflect.BeanPropertyInfo;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.map.Maps;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
public class IndexedMetaObject<T> extends MetaObject<T> {
	private static final ILogger log = ILoggers.of(IndexedMetaObject.class);
	private static final Map<JavaType<?>, IndexedMetaObject<?>> CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private Map<String, IndexedAccessor> properties;
	private Map<String, IndexedAccessor> propertiesCaseInsensitive;


	private IndexedMetaObject(JavaType<T> beanType) {
		super(beanType);
	}

	static class IndexedAccessor {
		private final BeanAccess<?> access;
		private final MetaObject<?> meta;
		private final boolean field;
		private final int getter;
		private final int setter;

		IndexedAccessor(BeanAccess<?> access, MetaObject<?> meta, boolean field, int getter, int setter) {
			this.access = access;
			this.meta = meta;
			this.field = field;
			this.getter = getter;
			this.setter = setter;
		}

		public Object get(Object bean) {
			if (field) {
				return access.getIndexField(bean, getter);
			} else {
				return access.getIndexProperty(bean, getter);
			}
		}

		public void set(Object bean, Object val) {
			if (field) {
				access.setIndexField(bean, setter, val);
			} else {
				access.setIndexProperty(bean, setter, val);
			}
		}
	}


	@SuppressWarnings("all")
	public static <T> IndexedMetaObject<T> of(JavaType<T> beanType) {
		IndexedMetaObject<T> metaObject = (IndexedMetaObject<T>) CACHE.computeIfAbsent(beanType, IndexedMetaObject::new);
		if (metaObject.state() == INIT) {
			synchronized (metaObject) {
				if (metaObject.state() == INIT) {
					metaObject.parse();
				}
			}
		}
		return metaObject;
	}

	public static <T> IndexedMetaObject<T> of(Class<T> beanType) {
		return of(JavaType.of(beanType));
	}

	public static <T> IndexedMetaObject<T> of(TypeRef<T> beanType) {
		return of(JavaType.of(beanType));
	}

	public static <T> IndexedMetaObject<T> of(Type beanType) {
		return of(JavaType.of(beanType));
	}


	@Override
	protected <E> IndexedMetaObject<E> createMetaObject(JavaType<E> beanType) {
		return IndexedMetaObject.of(beanType);
	}


	@SuppressWarnings("all")
	@Override
	protected boolean initBeanAccessor(Class<T> rawClass) {
		BeanAccess<T> access = BeanAccess.get(rawClass);

		Map<String, IndexedAccessor> properties = new HashMap<>(64);
		for (Map.Entry<String, BeanPropertyInfo> entry : access.properties().entrySet()) {
			BeanPropertyInfo beanPropertyInfo = entry.getValue();
			Type propertyGenericType = beanPropertyInfo.getPropertyGenericType();
			String propertyName = beanPropertyInfo.getPropertyName();

			if (beanPropertyInfo.getField() != null) {
				// 忽略static field
				if (Modifier.isStatic(beanPropertyInfo.getField().getModifiers())) {
					continue;
				}
				IndexedMetaObject<Object> meta = IndexedMetaObject.of(propertyGenericType);
				int fieldIndex = access.getFieldIndex(propertyName);
				IndexedAccessor accessor = new IndexedAccessor(access, meta, true, fieldIndex, fieldIndex);
				properties.put(propertyName, accessor);
			} else {
				IndexedMetaObject<Object> meta = IndexedMetaObject.of(propertyGenericType);
				int getterIndex = access.getGetterIndex(propertyName);
				int setterIndex = access.getSetterIndex(propertyName);
				IndexedAccessor accessor = new IndexedAccessor(access, meta, false, getterIndex, setterIndex);
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
		IndexedAccessor accessor = properties.get(property);
		if (accessor == null || accessor.getter < 0 || accessor.setter < 0) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter < 0 || accessor.setter < 0) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter < 0 || accessor.setter < 0) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter < 0 || accessor.setter < 0) {
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
		IndexedAccessor accessor = properties.get(property);
		if (accessor == null || accessor.setter < 0) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.setter < 0) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.setter < 0) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.setter < 0) {
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
		IndexedAccessor accessor = properties.get(property);
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
		IndexedAccessor accessor = properties.get(property);
		if (accessor == null || accessor.getter < 0) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter < 0) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter < 0) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter < 0) {
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
		IndexedAccessor accessor = properties.get(property);
		if (accessor == null || accessor.getter < 0) {
			if (isCaseInsensitive(caseModel)) {
				accessor = propertiesCaseInsensitive.get(property);
			}
			if (accessor == null || accessor.getter < 0) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(property);
					accessor = properties.get(propertyCamelCase);
					if (accessor == null || accessor.getter < 0) {
						if (isCaseInsensitive(caseModel)) {
							accessor = propertiesCaseInsensitive.get(propertyCamelCase);
						}
					}
				}
			}
			if (accessor == null || accessor.getter < 0) {
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

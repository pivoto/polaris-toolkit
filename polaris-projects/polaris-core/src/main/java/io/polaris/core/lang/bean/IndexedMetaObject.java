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
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.map.Maps;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
public class IndexedMetaObject<T> extends MetaObject<T> {
	private static final ILogger log = ILoggers.of(IndexedMetaObject.class);
	private static final Map<JavaType<?>, IndexedMetaObject<?>> CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private Map<String, IndexedProperty> properties;
	private Map<String, IndexedProperty> propertiesCaseInsensitive;


	private IndexedMetaObject(JavaType<T> beanType) {
		super(beanType);
	}

	static class IndexedProperty {
		private final MetaObject<?> meta;
		private final PropertyAccessor accessor;

		IndexedProperty(MetaObject<?> meta, PropertyAccessor accessor) {
			this.meta = meta;
			this.accessor = accessor;
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
		Map<String, PropertyAccessor> accessors = Beans.getIndexedFieldAndPropertyAccessors(rawClass);
		int size = accessors.size();
		if (size > 0) {
			Map<String, IndexedProperty> properties = new HashMap<>(size);
			Map<String, IndexedProperty> propertiesCaseInsensitive = new CaseInsensitiveMap<>(new HashMap<>(size), true);
			for (Map.Entry<String, PropertyAccessor> entry : accessors.entrySet()) {
				String propertyName = entry.getKey();
				PropertyAccessor accessor = entry.getValue();
				Type type = accessor.type();
				IndexedMetaObject<?> meta = IndexedMetaObject.of(type);
				IndexedProperty indexedProperty =  	new IndexedProperty(meta, accessor);
				properties.put(propertyName, indexedProperty);
				propertiesCaseInsensitive.put(propertyName, indexedProperty);
			}
			this.properties = Collections.unmodifiableMap(properties);
			this.propertiesCaseInsensitive = Collections.unmodifiableMap(propertiesCaseInsensitive);
			return true;
		}

		return false;
	}

	@Override
	protected Object getBeanPropertyOrSetDefault(@Nonnull T o, int caseModel, @Nonnull String name) {
		IndexedProperty property = properties.get(name);
		if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
			if (isCaseInsensitive(caseModel)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasGetter() || !property.accessor.hasSetter()) {
						if (isCaseInsensitive(caseModel)) {
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
					return rtMeta.getPropertyOrSetDefault(o, caseModel, name);
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
	protected Object setBeanProperty(@Nonnull T o, int caseModel, @Nonnull String name, Object val) {
		IndexedProperty property = properties.get(name);
		if (property == null || !property.accessor.hasSetter()) {
			if (isCaseInsensitive(caseModel)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasSetter()) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasSetter()) {
						if (isCaseInsensitive(caseModel)) {
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
					return rtMeta.setProperty(o, caseModel, name, val);
				}
			}
		}
		Object val1 = val = Converters.convertQuietly(property.meta.getBeanType(), val);
		property.accessor.set(o, val1);
		return val;
	}

	@Override
	@SuppressWarnings("all")
	protected MetaObject<?> getBeanProperty(int caseModel, @Nonnull String name) {
		IndexedProperty property = properties.get(name);
		if (property == null) {
			if (isCaseInsensitive(caseModel)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null) {
						if (isCaseInsensitive(caseModel)) {
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
	protected Object getBeanProperty(@Nonnull T o, int caseModel, @Nonnull String name) {
		IndexedProperty property = properties.get(name);
		if (property == null || !property.accessor.hasGetter()) {
			if (isCaseInsensitive(caseModel)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasGetter()) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasGetter()) {
						if (isCaseInsensitive(caseModel)) {
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
					return rtMeta.getProperty(o, caseModel, name);
				}
			}
		}
		return property.accessor.get(o);
	}

	@Override
	protected boolean hasBeanProperty(@Nonnull T o, int caseModel, @Nonnull String name) {
		IndexedProperty property = properties.get(name);
		if (property == null || !property.accessor.hasGetter()) {
			if (isCaseInsensitive(caseModel)) {
				property = propertiesCaseInsensitive.get(name);
			}
			if (property == null || !property.accessor.hasGetter()) {
				if (isCaseCamel(caseModel)) {
					String propertyCamelCase = StringCases.underlineToCamelCase(name);
					property = properties.get(propertyCamelCase);
					if (property == null || !property.accessor.hasGetter()) {
						if (isCaseInsensitive(caseModel)) {
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
					return rtMeta.hasProperty(o, caseModel, name);
				}
			}
		}
		return property.accessor.get(o) != null;
	}


}

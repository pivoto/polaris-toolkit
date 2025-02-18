package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class BeanToMapCopier<T> implements Copier<Map> {
	private static final ILogger log = ILoggers.of(BeanToMapCopier.class);
	private final T source;
	private final Map target;
	private final Type sourceType;
	private final Type targetType;
	private final CopyOptions options;

	/**
	 * @param sourceType 来源类型
	 * @param source     来源Map
	 * @param targetType 目标类型
	 * @param target     目标Map对象
	 * @param options    拷贝选项
	 */
	public BeanToMapCopier(Type sourceType, T source, Type targetType, Map target, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.sourceType = sourceType != null ? sourceType : source.getClass();
		this.targetType = targetType != null ? targetType : target.getClass();
		this.options = options != null ? options : CopyOptions.DEFAULT;
	}

	@Override
	public Map copy() {
		return copy(false);
	}

	@Override
	public Map deepCopy() {
		return copy(true);
	}

	@SuppressWarnings("unchecked")
	public Map copy(boolean deep) {
		try {
			final Map<String, PropertyAccessor> sourceAccessors = Beans.getIndexedFieldAndPropertyAccessors(JavaType.of(sourceType).getRawClass());

			JavaType<Object> javaType = JavaType.of(this.targetType);
			Type keyType = javaType.getActualType(Map.class, 0);
			Type valueType = javaType.getActualType(Map.class, 1);

			for (Map.Entry<String, PropertyAccessor> entry : sourceAccessors.entrySet()) {
				String sourceKey = entry.getKey();
				PropertyAccessor sourceAccessor = entry.getValue();
				if (sourceKey == null) {
					continue;
				}
				try {
					String key = options.editKey(sourceKey);
					if (options.isIgnoredKey(key)) {
						continue;
					}
					if (!sourceAccessor.hasSetter()) {
						continue;
					}
					Object value = sourceAccessor.get(source);
					if (value == null && options.ignoreNull()) {
						continue;
					}
					Object targetKey = options.convert(keyType, key);
					Object old = null;
					if ((deep || !options.override())) {
						// 只在深度复制或判断覆盖时才获取原值
						old = target.get(key);
						if (!options.override() && old != null) {
							continue;
						}
					}
					value = options.convert(valueType, value);
					value = options.editValue(key, value);
					if (value == null && options.ignoreNull()) {
						continue;
					}
					if (deep && value != null) {
						if (old == null) {
							value = Copiers.deepClone(value, valueType, options);
							if (value == null && options.ignoreNull()) {
								continue;
							}
						} else {
							// 复制子属性对象并完成本次循环
							Copiers.deepCopy(value.getClass(), value, valueType, old, options);
							continue;
						}
					}
					target.put(targetKey, value);
				} catch (Exception e) {
					if (!options.ignoreError()) {
						throw new IllegalArgumentException(e);
					} else {
						log.warn("复制属性[{}]失败：{}", sourceKey, e.getMessage());
						if (log.isDebugEnabled()) {
							log.debug(e.getMessage(), e);
						}
					}
				}
			}

		} catch (Exception e) {
			if (!options.ignoreError()) {
				throw new IllegalArgumentException(e);
			} else {
				log.warn("复制属性失败：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}

		return this.target;
	}
}

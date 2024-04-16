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
public class BeanToMapCopier<T> extends BaseCopier<T, Map> {
	private static final ILogger log = ILoggers.of(BeanToMapCopier.class);

	/**
	 * @param source      来源Map
	 * @param sourceType  来源类型
	 * @param target      目标Map对象
	 * @param targetType  目标类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToMapCopier(T source, Type sourceType, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, sourceType, target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map copy() {
		try {
			final Map<String, PropertyAccessor> sourceAccessors = Beans.getIndexedFieldAndPropertyAccessors(JavaType.of(sourceType).getRawClass());

			JavaType<Object> javaType = JavaType.of(this.targetType);
			Type keyType = javaType.getActualType(Map.class, 0);
			Type valueType = javaType.getActualType(Map.class, 1);

			sourceAccessors.forEach(wrapConsumer((sourceKey, sourceAccessor) -> {
				sourceKey = options.editKey(sourceKey);
				if (sourceKey == null) {
					return;
				}
				if (options.isIgnoredKey(sourceKey)) {
					return;
				}
				if (!sourceAccessor.hasSetter()) {
					return;
				}
				Object value = sourceAccessor.get(source);
				if (value == null && options.ignoreNull()) {
					return;
				}
				Object targetKey = options.convert(keyType, sourceKey);
				if (!options.override()) {
					Object orig = target.get(sourceKey);
					if (orig != null) {
						return;
					}
				}
				value = options.convert(valueType, value);
				value = options.editValue(sourceKey, value);
				if (value == null && options.ignoreNull()) {
					return;
				}

				target.put(targetKey, value);
			}));

		} catch (Exception e) {
			if (!options.ignoreError()) {
				throw new UnsupportedOperationException(e);
			} else {
				log.warn("Copy failed：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}

		return this.target;
	}
}

package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
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
	 * @param target      目标Map对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToMapCopier(T source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map copy() {
		Class<?> actualEditable = source.getClass();
		if (options.getEditable() != null && options.getEditable().isAssignableFrom(actualEditable)) {
			actualEditable = options.getEditable();
		}
		try {
			BeanMap<T> sourceMap = Beans.newBeanMap(source, actualEditable);
			sourceMap.forEach(wrapConsumer((sourceKey, value) -> {
				sourceKey = super.editKey(sourceKey);
				if (sourceKey == null) {
					return;
				}
				if (super.isIgnore(sourceKey)) {
					return;
				}
				if (value == null && options.isIgnoreNull()) {
					return;
				}
				Type type = sourceMap.getType(sourceKey);
				if (!super.filter(sourceKey, type, value)) {
					return;
				}
				JavaType<Object> javaType = JavaType.of(this.targetType);
				Object targetKey = super.convert(javaType.getActualType(Map.class, 0), sourceKey);
				if (!options.isOverride()) {
					Object orig = target.get(sourceKey);
					if (orig != null) {
						return;
					}
				}
				value = super.convert(javaType.getActualType(Map.class, 1), value);
				value = super.editValue(sourceKey, value);
				if (value == null && options.isIgnoreNull()) {
					return;
				}

				target.put(targetKey, value);
			}));

		} catch (Exception e) {
			if (!options.isIgnoreError()) {
				throw new UnsupportedOperationException(e);
			} else {
				log.warn("对象复制失败：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}

		return this.target;
	}
}

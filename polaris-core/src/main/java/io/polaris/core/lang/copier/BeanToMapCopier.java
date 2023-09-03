package io.polaris.core.lang.copier;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class BeanToMapCopier<T> extends BaseCopier<T, Map> {
	private static final ILogger log = ILogger.of(BeanToMapCopier.class);

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
			sourceMap.forEach((name, value) -> {
				try {
					name = super.editKey(name);
					if (name == null) {
						return;
					}
					if (super.isIgnore(name)) {
						return;
					}
					if (value == null && options.isIgnoreNull()) {
						return;
					}
					Type type = sourceMap.getType(name);
					if (!super.filter(name, type, value)) {
						return;
					}
					JavaType<Object> javaType = JavaType.of(this.targetType);
					Object key = super.convert(javaType.getActualType(Map.class,0), name);
					if (!options.isOverride()) {
						Object orig = target.get(name);
						if (orig != null) {
							return;
						}
					}
					value = super.convert(javaType.getActualType(Map.class,1), value);
					value = super.editValue(name, value);
					if (value == null && options.isIgnoreNull()) {
						return;
					}

					target.put(key, value);
				} catch (Exception e) {
					if (!options.isIgnoreError()) {
						throw new UnsupportedOperationException(e);
					} else {
						log.warn("对象复制失败：{}",  e.getMessage());
						if (log.isDebugEnabled()) {
							log.debug(e.getMessage(), e);
						}
					}
				}
			});

		} catch (Exception e) {
			if (!options.isIgnoreError()) {
				throw new UnsupportedOperationException(e);
			} else {
				log.warn("对象复制失败：{}",  e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}

		return this.target;
	}
}

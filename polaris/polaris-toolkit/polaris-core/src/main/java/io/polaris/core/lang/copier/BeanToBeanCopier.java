package io.polaris.core.lang.copier;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;
import io.polaris.core.map.ListMultiMap;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class BeanToBeanCopier<S, T> extends BaseCopier<S, T> {
	private static final ILogger log = ILogger.of(BeanToBeanCopier.class);

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@Override
	public T copy() {
		Class<?> actualEditable = target.getClass();
		if (options.getEditable() != null && options.getEditable().isAssignableFrom(actualEditable)) {
			actualEditable = options.getEditable();
		}

		try {
			BeanMap<S> sourceMap = Beans.asBeanMap(source);
			BeanMap<T> targetMap = Beans.asBeanMap(target, actualEditable);
			final ListMultiMap<String, String> rel;
			if (options.isIgnoreCase()) {
				rel = new ListMultiMap<>();
				for (String key : targetMap.keySet()) {
					rel.putOne(key.toUpperCase(), key);
				}
			} else {
				rel = null;
			}

			sourceMap.forEach((name, value) -> {
				try {
					name = super.editName(name);
					if (name == null) {
						return;
					}
					if (super.isIgnore(name)) {
						return;
					}
					if (value == null && options.isIgnoreNull()) {
						return;
					}
					List<String> list;
					if (rel != null) {
						list = rel.get(name.toUpperCase());
						if (list == null) {
							return;
						}
					} else {
						list = Collections.singletonList(name);
					}
					for (String key : list) {
						Type type = targetMap.getType(key);
						if (type == null) {
							// 无此属性
							continue;
						}
						if (!super.filter(name, type, value)) {
							continue;
						}
						if (!options.isOverride()) {
							Object orig = targetMap.get(key);
							if (orig != null) {
								continue;
							}
						}
						Object newValue = super.convert(type, value);
						newValue = super.editValue(name, newValue);
						if (newValue == null && options.isIgnoreNull()) {
							continue;
						}
						targetMap.put(key, newValue);
					}
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

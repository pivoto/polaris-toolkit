package io.polaris.core.object.copier;

import io.polaris.core.map.ListMultiMap;
import io.polaris.core.object.BeanMap;
import io.polaris.core.object.Beans;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class MapToBeanCopier<T> extends BaseCopier<Map, T> {

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToBeanCopier(Map source, T target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@Override
	public T copy() {
		Class<?> actualEditable = target.getClass();
		if (options.getEditable() != null && options.getEditable().isAssignableFrom(actualEditable)) {
			actualEditable = options.getEditable();
		}
		try {
			BeanMap targetMap = Beans.asBeanMap(target, actualEditable);
			final ListMultiMap<String, String> rel;
			if (options.isIgnoreCase()) {
				rel = new ListMultiMap<>();
				for (String key : targetMap.keySet()) {
					rel.putOne(key.toUpperCase(), key);
				}
			} else {
				rel = null;
			}
			this.source.forEach((k, value) -> {
				try {
					if (k == null) {
						return;
					}
					String name = super.editName(k.toString());
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

package io.polaris.core.lang.copier;

import io.polaris.core.lang.JavaType;
import io.polaris.core.log.ILogger;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToMapCopier extends BaseCopier<Map, Map> {
	private static final ILogger log = ILogger.of(MapToMapCopier.class);

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToMapCopier(Map source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@Override
	public Map copy() {
		try {
			this.source.forEach((key, value) -> {
				try {
					if (key == null) {
						return;
					}
					if (options.isIgnoreNull() && value == null) {
						return;
					}

					JavaType<Object> javaType = JavaType.of(this.targetType);
					if (key instanceof String) {
						final String keyStr = super.editName(key.toString());
						if (keyStr == null) {
							return;
						}
						if (super.isIgnore(keyStr)) {
							return;
						}
						value = super.convert(javaType.getActualType(Map.class, 1), value);
						value = super.editValue(keyStr, value);
						key = super.convert(javaType.getActualType(Map.class, 0), keyStr);
					} else {
						key = super.convert(javaType.getActualType(Map.class, 0), key);
						value = super.convert(javaType.getActualType(Map.class, 1), value);
					}
					if (value == null && options.isIgnoreNull()) {
						return;
					}
					if (!options.isOverride() && null != target.get(key)) {
						return;
					}
					// 目标赋值
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

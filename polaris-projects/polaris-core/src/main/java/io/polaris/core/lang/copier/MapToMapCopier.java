package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;

import io.polaris.core.lang.JavaType;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToMapCopier extends BaseCopier<Map, Map> {
	private static final ILogger log = ILoggers.of(MapToMapCopier.class);

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToMapCopier(Map source, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map copy() {
		try {
			JavaType<Object> javaType = JavaType.of(this.targetType);
			Type valueType = javaType.getActualType(Map.class, 1);
			Type keyType = javaType.getActualType(Map.class, 0);

			this.source.forEach(wrapConsumer((key, value) -> {
				if (key == null) {
					return;
				}
				if (options.isIgnoreNull() && value == null) {
					return;
				}

				if (key instanceof String) {
					final String keyStr = super.editKey(key.toString());
					if (keyStr == null) {
						return;
					}
					if (super.isIgnore(keyStr)) {
						return;
					}
					value = super.convert(valueType, value);
					value = super.editValue(keyStr, value);
					key = super.convert(keyType, keyStr);
				} else {
					key = super.convert(keyType, key);
					value = super.convert(valueType, value);
				}
				if (value == null && options.isIgnoreNull()) {
					return;
				}
				if (!options.isOverride() && null != target.get(key)) {
					return;
				}
				// 目标赋值
				target.put(key, value);
			}));
		} catch (Exception e) {
			if (!options.isIgnoreError()) {
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

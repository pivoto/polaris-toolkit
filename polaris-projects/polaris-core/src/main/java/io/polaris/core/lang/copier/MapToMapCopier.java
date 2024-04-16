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
	 * @param sourceType  来源类型
	 * @param target      目标Bean对象
	 * @param targetType  目标类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToMapCopier(Map source, Type sourceType, Map target, Type targetType, CopyOptions copyOptions) {
		super(source, sourceType, target, targetType, copyOptions);
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
				if (options.ignoreNull() && value == null) {
					return;
				}

				if (key instanceof String) {
					String key1 = key.toString();
					final String keyStr = options.editKey(key1);
					if (keyStr == null) {
						return;
					}
					if (options.isIgnoredKey(keyStr)) {
						return;
					}
					value = options.editValue(keyStr, value);
					value = options.convert(valueType, value);
					key = options.convert(keyType, keyStr);
				} else {
					value = options.convert(valueType, value);
					key = options.convert(keyType, key);
				}
				if (value == null && options.ignoreNull()) {
					return;
				}
				if (!options.override() && null != target.get(key)) {
					return;
				}
				// 目标赋值
				target.put(key, value);
			}));
		} catch (Exception e) {
			if (!options.ignoreError()) {
				throw new UnsupportedOperationException(e);
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

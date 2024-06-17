package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import io.polaris.core.lang.JavaType;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToMapCopier implements Copier<Map> {
	private static final ILogger log = ILoggers.of(MapToMapCopier.class);
	private final Map source;
	private final Map target;
	private final Type targetType;
	private final CopyOptions options;

	/**
	 * @param source     来源Map
	 * @param targetType 目标类型
	 * @param target     目标Bean对象
	 * @param options    拷贝选项
	 */
	public MapToMapCopier(Map source, Type targetType, Map target, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.targetType = targetType != null ? targetType : target.getClass();
		this.options = options != null ? options : CopyOptions.create();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map copy() {
		try {
			JavaType<Object> javaType = JavaType.of(this.targetType);
			Type valueType = javaType.getActualType(Map.class, 1);
			Type keyType = javaType.getActualType(Map.class, 0);
			Set<Map.Entry> set = this.source.entrySet();
			for (Map.Entry entry : set) {
				Object sourceKey = entry.getKey();
				Object value = entry.getValue();
				if (sourceKey == null) {
					continue;
				}
				try {
					Object key = sourceKey;
					if (key instanceof String) {
						String key1 = key.toString();
						final String keyStr = options.editKey(key1);
						if (keyStr == null) {
							continue;
						}
						if (options.isIgnoredKey(keyStr)) {
							continue;
						}
						value = options.editValue(keyStr, value);
						value = options.convert(valueType, value);
						key = options.convert(keyType, keyStr);
					} else {
						value = options.convert(valueType, value);
						key = options.convert(keyType, key);
					}
					if (value == null && options.ignoreNull()) {
						continue;
					}
					if (!options.override() && null != target.get(key)) {
						continue;
					}
					// 目标赋值
					target.put(key, value);
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

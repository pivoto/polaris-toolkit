package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.CaseInsensitiveMap;
import io.polaris.core.string.StringCases;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToBeanCopier<T> extends BaseCopier<Map, T> {
	private static final ILogger log = ILoggers.of(MapToBeanCopier.class);

	/**
	 * @param source      来源Map
	 * @param sourceType  来源类型
	 * @param target      目标Bean对象
	 * @param targetType  目标类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToBeanCopier(Map source, Type sourceType, T target, Type targetType, CopyOptions copyOptions) {
		super(source, sourceType, target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T copy() {
		try {
			final Map<String, PropertyAccessor> accessors = Beans.getIndexedFieldAndPropertyAccessors(JavaType.of(targetType).getRawClass());

			Map<String, PropertyAccessor> targetAccessors = new HashMap<>();
			accessors.forEach((key, accessor) -> {
				if (accessor.hasSetter()) {
					targetAccessors.put(key, accessor);
				}
			});
			action:
			{
				// full match
				Function<String, String> mapping = options.keyMapping();
				if (mapping == null) {
					mapping = Function.identity();
				}
				copyProperties(mapping, targetAccessors);
				if (targetAccessors.isEmpty()) {
					break action;
				}
				// ignoreCapitalize
				if (options.ignoreCapitalize()) {
					mapping = key -> {
						if (key.length() > 1) {
							String mapKey;
							if (Character.isUpperCase(key.charAt(0))) {
								mapKey = Character.toLowerCase(key.charAt(0)) + key.substring(1);
							} else {
								mapKey = Character.toUpperCase(key.charAt(0)) + key.substring(1);
							}
							return mapKey;
						}
						return null;
					};
					copyProperties(mapping, targetAccessors);
					if (targetAccessors.isEmpty()) {
						break action;
					}
				}
				// underlineToCamelCase
				if (options.enableUnderlineToCamelCase()) {
					mapping = key -> {
						String mapKey = StringCases.underlineToCamelCase(key);
						if (!key.equals(mapKey)) {
							return mapKey;
						}
						return null;
					};
					copyProperties(mapping, targetAccessors);
					if (targetAccessors.isEmpty()) {
						break action;
					}
				}
				// camelToUnderlineCase
				if (options.enableCamelToUnderlineCase()) {
					mapping = key -> {
						String mapKey = StringCases.camelToUnderlineCase(key);
						if (!key.equals(mapKey)) {
							return mapKey;
						}
						return null;
					};
					copyProperties(mapping, targetAccessors);
					if (targetAccessors.isEmpty()) {
						break action;
					}
				}
				// ignoreCase
				if (options.ignoreCase()) {
					Map<String, PropertyAccessor> upperTargetAccessors = new CaseInsensitiveMap<>(HashMap::new, targetAccessors);
					mapping = key -> options.editKey(key).toUpperCase();
					copyProperties(mapping, upperTargetAccessors);
					if (upperTargetAccessors.isEmpty()) {
						break action;
					}
					// underlineToCamelCase
					if (options.enableUnderlineToCamelCase()) {
						mapping = key -> {
							String mapKey = StringCases.underlineToCamelCase(key);
							if (!key.equals(mapKey)) {
								return mapKey;
							}
							return null;
						};
						copyProperties(mapping, upperTargetAccessors);
						if (upperTargetAccessors.isEmpty()) {
							break action;
						}
					}
					// camelToUnderlineCase
					if (options.enableCamelToUnderlineCase()) {
						mapping = key -> {
							String mapKey = StringCases.camelToUnderlineCase(key);
							if (!key.equals(mapKey)) {
								return mapKey;
							}
							return null;
						};
						copyProperties(mapping, upperTargetAccessors);
					}
				}
			}
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


	void copyProperties(Function<String, String> mapping, Map<String, PropertyAccessor> targetAccessors) {
		Set<Map.Entry> set = this.source.entrySet();
		for (Map.Entry entry : set) {
			if (targetAccessors.isEmpty()) {
				return;
			}
			String sourceKey = Objects.toString(entry.getKey(), null);
			if (sourceKey == null) {
				continue;
			}
			try {
				Object value = entry.getValue();
				if (options.isIgnoredKey(sourceKey)) {
					continue;
				}
				String mapKey = options.editKey(sourceKey);
				if (mapKey == null) {
					continue;
				}
				String targetKey = mapping.apply(mapKey);
				if (targetKey == null) {
					continue;
				}
				PropertyAccessor accessor = targetAccessors.get(targetKey);
				if (accessor == null) {
					continue;
				}
				if (value == null && options.ignoreNull()) {
					continue;
				}
				Type type = accessor.type();
				value = options.editValue(sourceKey, value);
				value = options.convert(type, value);
				if (value == null) {
					if (options.ignoreNull()) {
						continue;
					}
					if (Types.isPrimitive(JavaType.of(type).getRawClass())) {
						continue;
					}
				}
				accessor.set(target, value);
				targetAccessors.remove(targetKey);
			} catch (Exception e) {
				if (!options.ignoreError()) {
					throw new UnsupportedOperationException(e);
				} else {
					log.warn("复制属性失[{}]败：{}", sourceKey, e.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(e.getMessage(), e);
					}
				}
			}
		}
	}


}

package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
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
public class BeanToBeanCopier<S, T> implements Copier<T> {
	private static final ILogger log = ILoggers.of(BeanToBeanCopier.class);
	private final S source;
	private final T target;
	private final Type sourceType;
	private final Type targetType;
	private final CopyOptions options;

	/**
	 * @param sourceType 来源类型
	 * @param source     来源Map
	 * @param targetType 目标类型
	 * @param target     目标Bean对象
	 * @param options    拷贝选项
	 */
	public BeanToBeanCopier(Type sourceType, S source, Type targetType, T target, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.sourceType = sourceType != null ? sourceType : source.getClass();
		this.targetType = targetType != null ? targetType : target.getClass();
		this.options = options != null ? options : CopyOptions.create();
	}

	@Override
	public T copy() {
		try {
			final Map<String, PropertyAccessor> accessors = Beans.getIndexedFieldAndPropertyAccessors(JavaType.of(targetType).getRawClass());
			final Map<String, PropertyAccessor> sourceAccessors = Beans.getIndexedFieldAndPropertyAccessors(JavaType.of(sourceType).getRawClass());

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
				copyProperties(sourceAccessors, mapping, targetAccessors);
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
					copyProperties(sourceAccessors, mapping, targetAccessors);
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
					copyProperties(sourceAccessors, mapping, targetAccessors);
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
					copyProperties(sourceAccessors, mapping, targetAccessors);
					if (targetAccessors.isEmpty()) {
						break action;
					}
				}
				// ignoreCase
				if (options.ignoreCase()) {
					Map<String, PropertyAccessor> upperTargetAccessors = new CaseInsensitiveMap<>(HashMap::new, targetAccessors);
					mapping = key -> options.editKey(key).toUpperCase();
					copyProperties(sourceAccessors, mapping, upperTargetAccessors);
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
						copyProperties(sourceAccessors, mapping, upperTargetAccessors);
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
						copyProperties(sourceAccessors, mapping, upperTargetAccessors);
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

	void copyProperties(Map<String, PropertyAccessor> sourceAccessors, Function<String, String> mapping, Map<String, PropertyAccessor> targetAccessors) {
		for (Map.Entry<String, PropertyAccessor> entry : sourceAccessors.entrySet()) {
			if (targetAccessors.isEmpty()) {
				return;
			}
			PropertyAccessor sourceAccessor = entry.getValue();
			if (!sourceAccessor.hasGetter()) {
				continue;
			}
			String sourceKey = entry.getKey();
			try {
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
				Object value = sourceAccessor.get(source);
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
					throw new IllegalArgumentException(e);
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

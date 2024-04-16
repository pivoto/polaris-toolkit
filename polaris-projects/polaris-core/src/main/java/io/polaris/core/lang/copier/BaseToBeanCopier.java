package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.SetMultiMap;
import io.polaris.core.string.StringCases;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since 1.8,  Apr 12, 2024
 */
public abstract class BaseToBeanCopier<S, T> extends BaseCopier<S, T> {
	private static final ILogger log = ILoggers.of(BeanToBeanCopier.class);

	/**
	 * @param source      来源Map
	 * @param sourceType  来源类型
	 * @param target      目标Bean对象
	 * @param targetType  目标类型
	 * @param copyOptions 拷贝选项
	 */
	public BaseToBeanCopier(S source, Type sourceType, T target, Type targetType, CopyOptions copyOptions) {
		super(source, sourceType, target, targetType, copyOptions);
	}

	@Nullable
	protected SetMultiMap<String, String> createTargetBeanMapCandidateKeys(Map<String, PropertyAccessor> accessors) {
		// 驼峰转下划线的操作会因下划线数量不一致导致不或逆，因此在源端转换处理`options.isUnderlineToCamelCase() `情况
		if (!options.ignoreCase() && !options.ignoreCapitalize() && !options.enableUnderlineToCamelCase()) {
			return null;
		} else {
			SetMultiMap<String, String> candidates = new SetMultiMap<>(LinkedHashSet::new);
			if (options.ignoreCase()) {
				for (String key : accessors.keySet()) {
					candidates.putOne(key.toUpperCase(), key);
					// 忽略大小写模式已包含`options.isIgnoreCapitalize()`场景，此处不作重复处理
					// 下划线格式字段支持驼峰格式的源字段
					if (key.indexOf('_') >= 0 && options.enableUnderlineToCamelCase()) {
						String sourceKey = StringCases.underlineToCamelCase(key);
						candidates.putOne(sourceKey, key);
						candidates.putOne(sourceKey.toUpperCase(), key);
					}
				}
			} else {
				for (String key : accessors.keySet()) {
					if (key.length() > 1 && options.ignoreCapitalize()) {
						if (Character.isUpperCase(key.charAt(0))) {
							candidates.putOne(Character.toLowerCase(key.charAt(0)) + key.substring(1), key);
						} else {
							candidates.putOne(Character.toUpperCase(key.charAt(0)) + key.substring(1), key);
						}
					}
					// 下划线格式字段支持驼峰格式的源字段
					if (key.indexOf('_') >= 0 && options.enableUnderlineToCamelCase()) {
						candidates.putOne(StringCases.underlineToCamelCase(key), key);
					}
				}
			}
			if (candidates.isEmpty()) {
				return null;
			}
			return candidates;
		}
	}

	private boolean setTargetValue(String sourceKey, Object value, Map<String, PropertyAccessor> accessors, String targetKey) {
		PropertyAccessor accessor = accessors.get(targetKey);
		if (accessor == null || !accessor.hasSetter()) {
			// 无此属性
			return false;
		}
		if (!options.override() && accessor.hasGetter()) {
			Object orig = accessor.get(target);
			if (orig != null) {
				return false;
			}
		}
		Type type = accessor.type();
		value = options.editValue(sourceKey, value);
		value = options.convert(type, value);
		if (value == null && options.ignoreNull()) {
			return false;
		}
		accessor.set(target, value);
		return true;
	}

	protected void setTargetValues(List<Tuple2<String, Object>> sourceEntries,
		Map<String, PropertyAccessor> accessors, SetMultiMap<String, String> candidates, Set<String> recorder) {
		// 先复制匹配度高的属性
		sourceEntries.forEach(wrapConsumer(sourceEntry -> {
			String sourceKey = sourceEntry.getFirst();
			Object value = sourceEntry.getSecond();
			if (setTargetValue(sourceKey, value, accessors, sourceKey)) {
				recorder.add(sourceKey);
			}
		}));
		// 下划线映射驼峰
		if (options.enableCamelToUnderlineCase()) {
			sourceEntries.forEach(wrapConsumer(sourceEntry -> {
				String sourceKey = sourceEntry.getFirst();
				Object value = sourceEntry.getSecond();
				String targetKey = StringCases.underlineToCamelCase(sourceKey);
				// 忽略已匹配
				if (recorder.contains(targetKey)) {
					return;
				}
				if (setTargetValue(sourceKey, value, accessors, targetKey)) {
					recorder.add(sourceKey);
				}
			}));
		}
		// 再复制侯选匹配的属性
		if (candidates != null) {
			sourceEntries.forEach(wrapConsumer(sourceEntry -> {
				String sourceKey = sourceEntry.getFirst();
				Object value = sourceEntry.getSecond();
				setCandidatesTargetValue(sourceKey, value, accessors, candidates, recorder);
			}));
		}
	}

	private void setCandidatesTargetValue(@Nonnull String sourceKey, Object value, @Nonnull Map<String, PropertyAccessor> accessors, @Nonnull SetMultiMap<String, String> candidates, Set<String> recorder) {
		// 只匹配并处理一次，已完成的属性则即时清理
		Set<String> targetKeys = candidates.get(sourceKey);
		if (targetKeys != null) {
			setCandidatesTargetValue(sourceKey, value, accessors, targetKeys, recorder);
			if (targetKeys.isEmpty()) {
				candidates.remove(sourceKey);
			}
		}
		if (options.ignoreCase()) {
			String upperSourceKey = sourceKey.toUpperCase();
			Set<String> upperTargetKeys = candidates.get(upperSourceKey);
			if (upperTargetKeys != null && upperTargetKeys != targetKeys) {
				setCandidatesTargetValue(sourceKey, value, accessors, upperTargetKeys, recorder);
				if (upperTargetKeys.isEmpty()) {
					candidates.remove(upperSourceKey);
				}
			}
		}
		if (options.enableCamelToUnderlineCase()) {
			String camelSourceKeys = StringCases.underlineToCamelCase(sourceKey);
			Set<String> camelTargetKeys = candidates.get(camelSourceKeys);
			if (camelTargetKeys != null && camelTargetKeys != targetKeys) {
				setCandidatesTargetValue(sourceKey, value, accessors, camelTargetKeys, recorder);
				if (camelTargetKeys.isEmpty()) {
					candidates.remove(camelSourceKeys);
				}
			}
		}
	}

	private void setCandidatesTargetValue(String sourceKey, Object value, Map<String, PropertyAccessor> accessors, Set<String> targetKeys, Set<String> recorder) {
		for (Iterator<String> it = targetKeys.iterator(); it.hasNext(); ) {
			String targetKey = it.next();
			// 忽略已匹配
			if (recorder.contains(targetKey)) {
				it.remove();
				continue;
			}
			if (setTargetValue(sourceKey, value, accessors, targetKey)) {
				recorder.add(sourceKey);
				it.remove();
			}
		}
	}

}

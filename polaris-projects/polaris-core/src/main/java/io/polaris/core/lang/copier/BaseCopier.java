package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.SetMultiMap;
import io.polaris.core.string.StringCases;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class BaseCopier<S, T> implements Copier<T> {
	private static final ILogger log = ILoggers.of(BaseCopier.class);
	protected final S source;
	protected final T target;
	protected final CopyOptions options;
	protected final Type targetType;

	public BaseCopier(S source, T target, Type targetType, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.options = options != null ? options : CopyOptions.create();
		this.targetType = targetType;
	}

	protected Object convert(Type targetType, Object value) {
		return (options.getConverter() != null) ?
			options.getConverter().apply(targetType, value) : value;
	}

	protected Object editValue(String key, Object value) {
		return (options.getValueEditor() != null) ?
			options.getValueEditor().apply(key, value) : value;
	}

	protected String editKey(String key) {
		return (options.getKeyMapping() != null) ?
			options.getKeyMapping().apply(key) : key;
	}

	protected boolean filter(String key, Type type, Object value) {
		return options.getFilter() == null || Boolean.TRUE.equals(options.getFilter().apply(key, type, value));
	}

	protected boolean isIgnore(String key) {
		return options.getIgnoreKeys() != null && options.getIgnoreKeys().contains(key);
	}

	@Nullable
	protected SetMultiMap<String, String> createTargetBeanMapCandidateKeys(BeanMap<T> targetMap) {
		// 驼峰转下划线的操作会因下划线数量不一致导致不或逆，因此在源端转换处理`options.isUnderlineToCamelCase() `情况
		if (!options.isIgnoreCase() && !options.isIgnoreCapitalize() && !options.isCamelToUnderlineCase()) {
			return null;
		} else {
			SetMultiMap<String, String> candidates = new SetMultiMap<>(LinkedHashSet::new);
			if (options.isIgnoreCase()) {
				for (String key : targetMap.keySet()) {
					candidates.putOne(key.toUpperCase(), key);
					// 忽略大小写模式已包含`options.isIgnoreCapitalize()`场景，此处不作重复处理
					// 下划线格式字段支持驼峰格式的源字段
					if (key.indexOf('_') >= 0 && options.isCamelToUnderlineCase()) {
						String sourceKey = StringCases.underlineToCamelCase(key);
						candidates.putOne(sourceKey, key);
						candidates.putOne(sourceKey.toUpperCase(), key);
					}
				}
			} else {
				for (String key : targetMap.keySet()) {
					if (key.length() > 1 && options.isIgnoreCapitalize()) {
						if (Character.isUpperCase(key.charAt(0))) {
							candidates.putOne(Character.toLowerCase(key.charAt(0)) + key.substring(1), key);
						} else {
							candidates.putOne(Character.toUpperCase(key.charAt(0)) + key.substring(1), key);
						}
					}
					// 下划线格式字段支持驼峰格式的源字段
					if (key.indexOf('_') >= 0 && options.isCamelToUnderlineCase()) {
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


	protected <K, V> BiConsumer<K, V> wrapConsumer(BiConsumer<K, V> consumer) {
		return (sourceKey, value) -> {
			try {
				consumer.accept(sourceKey, value);
			} catch (Exception e) {
				if (!options.isIgnoreError()) {
					throw new UnsupportedOperationException(e);
				} else {
					log.warn("对象复制失败：{}", e.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(e.getMessage(), e);
					}
				}
			}
		};
	}

	protected <V> Consumer<V> wrapConsumer(Consumer<V> consumer) {
		return (value) -> {
			try {
				consumer.accept(value);
			} catch (Exception e) {
				if (!options.isIgnoreError()) {
					throw new UnsupportedOperationException(e);
				} else {
					log.warn("对象复制失败：{}", e.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(e.getMessage(), e);
					}
				}
			}
		};
	}

	protected void setTargetValues(List<Tuple2<String, Object>> sourceEntries, BeanMap<T> targetMap, SetMultiMap<String, String> candidates) {
		// 先复制匹配度高的属性
		sourceEntries.forEach(wrapConsumer(sourceEntry -> {
			String sourceKey = sourceEntry.getFirst();
			Object value = sourceEntry.getSecond();
			setTargetValue(sourceKey, value, targetMap, sourceKey);
		}));
		if (options.isUnderlineToCamelCase()) {
			sourceEntries.forEach(wrapConsumer(sourceEntry -> {
				String sourceKey = sourceEntry.getFirst();
				Object value = sourceEntry.getSecond();
				setTargetValue(sourceKey, value, targetMap, StringCases.underlineToCamelCase(sourceKey));
			}));
		}
		// 再复制侯选匹配的属性
		if (candidates != null) {
			sourceEntries.forEach(wrapConsumer(sourceEntry -> {
				String sourceKey = sourceEntry.getFirst();
				Object value = sourceEntry.getSecond();
				setTargetValue(sourceKey, value, targetMap, candidates);
			}));
		}
	}


	private void setTargetValue(@Nonnull String sourceKey, Object value, @Nonnull BeanMap<T> targetMap, @Nonnull SetMultiMap<String, String> candidates) {
		Set<String> targetKeys = candidates.get(sourceKey);
		if (targetKeys != null) {
			setTargetValue(sourceKey, value, targetMap, targetKeys);
		}
		if (options.isIgnoreCase()) {
			Set<String> upperTargetKeys = candidates.get(sourceKey.toUpperCase());
			if (upperTargetKeys != null && upperTargetKeys != targetKeys) {
				setTargetValue(sourceKey, value, targetMap, upperTargetKeys);
			}
		}
		if (options.isUnderlineToCamelCase()) {
			Set<String> camelTargetKeys = candidates.get(StringCases.underlineToCamelCase(sourceKey));
			if (camelTargetKeys != null && camelTargetKeys != targetKeys) {
				setTargetValue(sourceKey, value, targetMap, camelTargetKeys);
			}
		}
	}

	private void setTargetValue(String sourceKey, Object value, BeanMap<T> targetMap, Set<String> targetKeys) {
		for (String key : targetKeys) {
			setTargetValue(sourceKey, value, targetMap, key);
		}
	}

	private void setTargetValue(String sourceKey, Object value, BeanMap<T> targetMap, String targetKey) {
		Type type = targetMap.getType(targetKey);
		if (type == null) {
			// 无此属性
			return;
		}
		if (!this.filter(sourceKey, type, value)) {
			return;
		}
		if (!options.isOverride()) {
			Object orig = targetMap.get(targetKey);
			if (orig != null) {
				return;
			}
		}
		Object newValue = this.convert(type, value);
		newValue = this.editValue(sourceKey, newValue);
		if (newValue == null && options.isIgnoreNull()) {
			return;
		}
		targetMap.put(targetKey, newValue);
	}
}

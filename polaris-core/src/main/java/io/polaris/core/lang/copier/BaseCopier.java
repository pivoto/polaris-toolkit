package io.polaris.core.lang.copier;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class BaseCopier<S, T> implements Copier<T> {

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
}

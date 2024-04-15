package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

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
		if (!options.isEnableConverter()) {
			return value;
		}
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


	protected <K, V> BiConsumer<K, V> wrapConsumer(BiConsumer<K, V> consumer) {
		return (sourceKey, value) -> {
			try {
				consumer.accept(sourceKey, value);
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
					log.warn("Copy failed：{}", e.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(e.getMessage(), e);
					}
				}
			}
		};
	}

}

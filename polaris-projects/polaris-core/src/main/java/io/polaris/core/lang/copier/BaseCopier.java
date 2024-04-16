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
	protected final Type sourceType;
	protected final Type targetType;
	protected final CopyOptions options;

	public BaseCopier(S source, Type sourceType, T target, Type targetType, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.sourceType = sourceType != null ? sourceType : source.getClass();
		this.targetType = targetType != null ? targetType : target.getClass();
		this.options = options != null ? options : CopyOptions.create();
	}


	protected <K, V> BiConsumer<K, V> wrapConsumer(BiConsumer<K, V> consumer) {
		return (sourceKey, value) -> {
			try {
				consumer.accept(sourceKey, value);
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
		};
	}

	protected <V> Consumer<V> wrapConsumer(Consumer<V> consumer) {
		return (value) -> {
			try {
				consumer.accept(value);
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
		};
	}

}

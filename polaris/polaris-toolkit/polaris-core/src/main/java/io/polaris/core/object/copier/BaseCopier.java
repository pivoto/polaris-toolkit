package io.polaris.core.object.copier;

import io.polaris.core.object.Copier;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class BaseCopier<S, T> implements Copier<T> {

	protected final S source;
	protected final T target;
	protected final CopyOptions options;

	public BaseCopier(S source, T target, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.options = options != null ? options : CopyOptions.create();
	}
}

package io.polaris.core.lang.copier;

import java.lang.reflect.Type;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class IndexedObjectCopier<T> implements Copier<T> {
	private static final ILogger log = ILoggers.of(IndexedObjectCopier.class);
	private final Object source;
	private final T target;
	private final CopyOptions options;
	private final Type targetType;

	/**
	 * @param source  来源Map
	 * @param target  目标Bean对象
	 * @param options 拷贝选项
	 */
	public IndexedObjectCopier(Object source, T target, Type targetType, CopyOptions options) {
		this.source = source;
		this.target = target;
		this.targetType = targetType != null ? targetType : target.getClass();
		this.options = options != null ? options : CopyOptions.DEFAULT;
	}

	@Override
	public T copy() {
		return Copiers.deepCopyIndexed(this.source, targetType, this.target, options, false);
	}

	@Override
	public T deepCopy() {
		return Copiers.deepCopyIndexed(this.source, targetType, this.target, options, true);
	}


}

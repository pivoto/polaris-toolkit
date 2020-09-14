package io.polaris.core.lang.copier;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
public class Copiers {

	public static <E> Copier<E> create(Object source, E target) {
		return new GeneralCopier<>(source, target, target.getClass(), null);
	}

	public static <E> Copier<E> create(Object source, E target, CopyOptions copyOptions) {
		return new GeneralCopier<>(source, target, target.getClass(), copyOptions);
	}

	public static <E> Copier<E> create(Object source, E target, Type targetType) {
		return new GeneralCopier<>(source, target, targetType, null);
	}

	public static <E> Copier<E> create(Object source, E target, Type targetType, CopyOptions copyOptions) {
		return new GeneralCopier<>(source, target, targetType, copyOptions);
	}

	public static <E> E copy(Object source, E target) {
		return create(source, target, target.getClass(), null).copy();
	}

	public static <E> E copy(Object source, E target, CopyOptions copyOptions) {
		return create(source, target, target.getClass(), copyOptions).copy();
	}

	public static <E> E copy(Object source, E target, Type targetType) {
		return create(source, target, targetType, null).copy();
	}

	public static <E> E copy(Object source, E target, Type targetType, CopyOptions copyOptions) {
		return create(source, target, targetType, copyOptions).copy();
	}

}

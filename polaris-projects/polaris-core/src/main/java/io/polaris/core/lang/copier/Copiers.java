package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class Copiers {
	/*
	TODO fastCopy, deepCopy
	 */


	public static <E> Copier<E> create(Object source, E target) {
		return create(source, source.getClass(), target, target.getClass(), null);
	}

	public static <E> Copier<E> create(Object source, E target, CopyOptions copyOptions) {
		return create(source, source.getClass(), target, target.getClass(), copyOptions);
	}

	public static <E> Copier<E> create(Object source, E target, Type targetType) {
		return create(source, source.getClass(), target, targetType, null);
	}

	public static <E> Copier<E> create(Object source, E target, Type targetType, CopyOptions copyOptions) {
		return create(source, source.getClass(), target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	public static <E> Copier<E> create(Object source, Type sourceType, E target, Type targetType, CopyOptions copyOptions) {
		Copier<E> copier;
		if (source instanceof Map) {
			if (target instanceof Map) {
				copier = (Copier<E>) new MapToMapCopier((Map<?, ?>) source, sourceType, (Map<?, ?>) target, targetType, copyOptions);
			} else {
				copier = new MapToBeanCopier<>((Map<?, ?>) source, sourceType, target, targetType, copyOptions);
			}
		} else {
			if (target instanceof Map) {
				copier = (Copier<E>) new BeanToMapCopier<>(source, sourceType, (Map<?, ?>) target, targetType, copyOptions);
			} else {
				copier = new BeanToBeanCopier<>(source, sourceType, target, targetType, copyOptions);
			}
		}
		return copier;
	}

	public static <E> E copy(Object source, E target) {
		return create(source, source.getClass(), target, target.getClass(), null).copy();
	}

	public static <E> E copy(Object source, E target, CopyOptions copyOptions) {
		return create(source, source.getClass(), target, target.getClass(), copyOptions).copy();
	}

	public static <E> E copy(Object source, E target, Type targetType) {
		return create(source, source.getClass(), target, targetType, null).copy();
	}

	public static <E> E copy(Object source, E target, Type targetType, CopyOptions copyOptions) {
		return create(source, source.getClass(), target, targetType, copyOptions).copy();
	}

	public static <E> E copy(Object source, Type sourceType, E target, Type targetType, CopyOptions copyOptions) {
		return create(source, sourceType, target, targetType, copyOptions).copy();
	}

}

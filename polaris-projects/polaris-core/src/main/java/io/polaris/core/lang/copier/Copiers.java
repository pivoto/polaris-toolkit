package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.asm.reflect.BeanCopier;
import io.polaris.core.lang.JavaType;

/**
 * @author Qt
 * @since 1.8
 */
public class Copiers {
	// TODO fastCopy, deepCopy


	public static <E> E fastCopy(Object source, E target) {
		return fastCopy(source, source.getClass(), target, target.getClass());
	}

	public static <E> E fastCopy(Object source, E target, Type targetType) {
		return fastCopy(source, source.getClass(), target, targetType);
	}

	public static <E> E fastCopy(Object source, Type sourceType, E target, Type targetType) {
		if (source instanceof Map) {
			if (target instanceof Map) {
				((Map) target).putAll((Map) source);
			} else {
				BeanCopier.get(JavaType.of(targetType).getRawClass()).copyMapToBean((Map) source, target);
			}
		} else {
			if (target instanceof Map) {
				BeanCopier.get(JavaType.of(sourceType).getRawClass()).copyBeanToMap(source, ((Map) target));
			} else {
				BeanCopier.get(JavaType.of(sourceType).getRawClass()).copyBeanToBean(source, JavaType.of(targetType).getRawClass(), target);
			}
		}
		return target;
	}

	public static <E> E fastCopy(Object source, E target, CopyOptions copyOptions) {
		return fastCopy(source, source.getClass(), target, target.getClass(), copyOptions);
	}

	public static <E> E fastCopy(Object source, E target, Type targetType, CopyOptions copyOptions) {
		return fastCopy(source, source.getClass(), target, targetType, copyOptions);
	}

	public static <E> E fastCopy(Object source, Type sourceType, E target, Type targetType, CopyOptions copyOptions) {
		if (source instanceof Map) {
			if (target instanceof Map) {
				new MapToMapCopier((Map<?, ?>) source, sourceType, (Map<?, ?>) target, targetType, copyOptions).copy();
			} else {
				BeanCopier.get(JavaType.of(targetType).getRawClass()).copyMapToBean((Map) source, target, copyOptions);
			}
		} else {
			if (target instanceof Map) {
				BeanCopier.get(JavaType.of(sourceType).getRawClass()).copyBeanToMap(source, ((Map) target), copyOptions);
			} else {
				BeanCopier.get(JavaType.of(sourceType).getRawClass()).copyBeanToBean(source, JavaType.of(targetType).getRawClass(), target, copyOptions);
			}
		}
		return target;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <S> void fastCopyBeanToMap(S source, Map<String, Object> target) {
		fastCopyBeanToMap((Class) source.getClass(), source, target);
	}

	public static <S> void fastCopyBeanToMap(Class<S> sourceType, S source, Map<String, Object> target) {
		BeanCopier.get(sourceType).copyBeanToMap(source, target);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> void fastCopyMapToBean(Map<String, Object> source, T target) {
		fastCopyMapToBean(source, (Class) target.getClass(), target);
	}

	public static <T> void fastCopyMapToBean(Map<String, Object> source, Class<T> targetType, T target) {
		BeanCopier.get(targetType).copyMapToBean(source, target);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> void fastCopyMapToBean(Map<String, Object> source, T target, BiFunction<Type, Object, Object> converter) {
		fastCopyMapToBean(source, (Class) target.getClass(), target, converter);
	}

	public static <T> void fastCopyMapToBean(Map<String, Object> source, Class<T> targetType, T target, BiFunction<Type, Object, Object> converter) {
		BeanCopier.get(targetType).copyMapToBean(source, target, converter);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> void fastCopyMapToBean(Map<String, Object> source, T target, CopyOptions options) {
		fastCopyMapToBean(source, (Class) target.getClass(), target, options);
	}

	public static <T> void fastCopyMapToBean(Map<String, Object> source, Class<T> targetType, T target, CopyOptions options) {
		BeanCopier.get(targetType).copyMapToBean(source, target, options);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <S, T> void fastCopyBeanToBean(S source, T target) {
		fastCopyBeanToBean((Class) source.getClass(), source, (Class) target.getClass(), target);
	}

	public static <S, T> void fastCopyBeanToBean(Class<S> sourceType, S source, Class<T> targetType, T target) {
		BeanCopier.get(sourceType).copyBeanToBean(source, targetType, target);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <S, T> void fastCopyBeanToBean(S source, T target, CopyOptions options) {
		fastCopyBeanToBean((Class) source.getClass(), source, (Class) target.getClass(), target, options);
	}

	public static <S, T> void fastCopyBeanToBean(Class<S> sourceType, S source, Class<T> targetType, T target, CopyOptions options) {
		BeanCopier.get(sourceType).copyBeanToBean(source, targetType, target, options);
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
}

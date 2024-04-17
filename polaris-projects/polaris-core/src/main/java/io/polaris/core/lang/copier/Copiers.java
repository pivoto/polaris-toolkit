package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.asm.reflect.BeanCopier;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;

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

	@SuppressWarnings({"unchecked", "rawtypes"})
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

	public static <E> E fastCopy(Object source, E target, CopyOptions options) {
		return fastCopy(source, source.getClass(), target, target.getClass(), options);
	}

	public static <E> E fastCopy(Object source, E target, Type targetType, CopyOptions options) {
		return fastCopy(source, source.getClass(), target, targetType, options);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <E> E fastCopy(Object source, Type sourceType, E target, Type targetType, CopyOptions options) {
		if (source instanceof Map) {
			if (target instanceof Map) {
				new MapToMapCopier((Map<?, ?>) source, targetType, (Map<?, ?>) target, options).copy();
			} else {
				BeanCopier.get(JavaType.of(targetType).getRawClass()).copyMapToBean((Map) source, target, options);
			}
		} else {
			if (target instanceof Map) {
				BeanCopier.get(JavaType.of(sourceType).getRawClass()).copyBeanToMap(source, ((Map) target), options);
			} else {
				BeanCopier.get(JavaType.of(sourceType).getRawClass()).copyBeanToBean(source, JavaType.of(targetType).getRawClass(), target, options);
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

	public static <S, K, V> void fastCopyBeanToMap(Class<S> sourceType, S source, Type targetType, Map<K, V> target, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		BeanCopier.get(sourceType).copyBeanToMap(source, targetType, target, converter);
	}

	public static <S> void fastCopyBeanToMap(Class<S> sourceType, S source, Map<String, Object> target, CopyOptions options) {
		BeanCopier.get(sourceType).copyBeanToMap(source, target, options);
	}

	public static <S, K, V> void fastCopyBeanToMap(Class<S> sourceType, S source, Type targetType, Map<K, V> target, CopyOptions options) {
		BeanCopier.get(sourceType).copyBeanToMap(source, targetType, target, options);
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


	@SuppressWarnings("rawtypes")
	public static void copyMapToMap(Map source, Map target, CopyOptions options) {
		Copier<?> copier = new MapToMapCopier(source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static void copyMapToMap(Map source, Type targetType, Map target, CopyOptions options) {
		Copier<?> copier = new MapToMapCopier(source, targetType, target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> void copyBeanToMap(Type sourceType, S source, Type targetType, Map target, CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(sourceType, source, targetType, target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> void copyBeanToMap(Type sourceType, S source, Map target, CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(sourceType, source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> void copyBeanToMap(S source, Type targetType, Map target, CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(source.getClass(), source, targetType, target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> void copyBeanToMap(S source, Map target, CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(source.getClass(), source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static <T> void copyMapToBean(Map source, Type targetType, T target, CopyOptions options) {
		Copier<T> copier = new MapToBeanCopier<>(source, targetType, target, options);
		copier.copy();
	}

	@SuppressWarnings("rawtypes")
	public static <T> void copyMapToBean(Map source, T target, CopyOptions options) {
		Copier<T> copier = new MapToBeanCopier<>(source, target.getClass(), target, options);
		copier.copy();
	}

	public static <S, T> void copyBeanToBean(Type sourceType, S source, Type targetType, T target, CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(sourceType, source, targetType, target, options);
		copier.copy();
	}

	public static <S, T> void copyBeanToBean(S source, Type targetType, T target, CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(source.getClass(), source, targetType, target, options);
		copier.copy();
	}

	public static <S, T> void copyBeanToBean(Type sourceType, S source, T target, CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(sourceType, source, target.getClass(), target, options);
		copier.copy();
	}

	public static <S, T> void copyBeanToBean(S source, T target, CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(source.getClass(), source, target.getClass(), target, options);
		copier.copy();
	}

	public static <E> E copy(Object source, E target) {
		return create(source.getClass(), source, target.getClass(), target, null).copy();
	}

	public static <E> E copy(Object source, E target, CopyOptions options) {
		return create(source.getClass(), source, target.getClass(), target, options).copy();
	}

	public static <E> E copy(Object source, Type targetType, E target) {
		return create(source.getClass(), source, targetType, target, null).copy();
	}

	public static <E> E copy(Object source, Type targetType, E target, CopyOptions options) {
		return create(source.getClass(), source, targetType, target, options).copy();
	}

	public static <E> E copy(Type sourceType, Object source, E target, Type targetType, CopyOptions options) {
		return create(sourceType, source, targetType, target, options).copy();
	}

	@SuppressWarnings("unchecked")
	public static <E> Copier<E> create(Type sourceType, Object source, Type targetType, E target, CopyOptions options) {
		Copier<E> copier;
		if (source instanceof Map) {
			if (target instanceof Map) {
				copier = (Copier<E>) new MapToMapCopier((Map<?, ?>) source, targetType, (Map<?, ?>) target, options);
			} else {
				copier = new MapToBeanCopier<>((Map<?, ?>) source, targetType, target, options);
			}
		} else {
			if (target instanceof Map) {
				copier = (Copier<E>) new BeanToMapCopier<>(sourceType, source, targetType, (Map<?, ?>) target, options);
			} else {
				copier = new BeanToBeanCopier<>(sourceType, source, targetType, target, options);
			}
		}
		return copier;
	}

	public static <E> Copier<E> create(Object source, E target) {
		return create(source.getClass(), source, target.getClass(), target, null);
	}

	public static <E> Copier<E> create(Object source, E target, CopyOptions options) {
		return create(source.getClass(), source, target.getClass(), target, options);
	}

	public static <E> Copier<E> create(Object source, Type targetType, E target) {
		return create(source.getClass(), source, targetType, target, null);
	}

	public static <E> Copier<E> create(Object source, Type targetType, E target, CopyOptions options) {
		return create(source.getClass(), source, targetType, target, options);
	}

}

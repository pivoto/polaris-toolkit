package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import io.polaris.core.asm.reflect.BeanCopier;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;

/**
 * @author Qt
 * @since 1.8
 */
public class Copiers {
	// TODO fastCopy, deepCopy


	public static <E> E fastCopy(@Nonnull Object source, @Nonnull E target) {
		return fastCopy(source, source.getClass(), target, target.getClass());
	}

	public static <E> E fastCopy(@Nonnull Object source, @Nonnull E target, @Nonnull Type targetType) {
		return fastCopy(source, source.getClass(), target, targetType);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <E> E fastCopy(@Nonnull Object source, @Nonnull Type sourceType, @Nonnull E target, @Nonnull Type targetType) {
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

	public static <E> E fastCopy(@Nonnull Object source, @Nonnull E target, @Nonnull CopyOptions options) {
		return fastCopy(source, source.getClass(), target, target.getClass(), options);
	}

	public static <E> E fastCopy(@Nonnull Object source, @Nonnull E target, @Nonnull Type targetType, @Nonnull CopyOptions options) {
		return fastCopy(source, source.getClass(), target, targetType, options);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <E> E fastCopy(@Nonnull Object source, @Nonnull Type sourceType, @Nonnull E target, @Nonnull Type targetType, @Nonnull CopyOptions options) {
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
	public static <S> Map<String, Object> fastCopyBeanToMap(@Nonnull S source, @Nonnull Map<String, Object> target) {
		return fastCopyBeanToMap((Class) source.getClass(), source, target);
	}

	public static <S> Map<String, Object> fastCopyBeanToMap(@Nonnull Class<S> sourceType, @Nonnull S source, @Nonnull Map<String, Object> target) {
		BeanCopier.get(sourceType).copyBeanToMap(source, target);
		return target;
	}

	public static <S, K, V> Map<K, V> fastCopyBeanToMap(@Nonnull Class<S> sourceType, @Nonnull S source, @Nonnull Type targetType, @Nonnull Map<K, V> target, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		BeanCopier.get(sourceType).copyBeanToMap(source, targetType, target, converter);
		return target;
	}

	public static <S> Map<String, Object> fastCopyBeanToMap(@Nonnull Class<S> sourceType, @Nonnull S source, @Nonnull Map<String, Object> target, @Nonnull CopyOptions options) {
		BeanCopier.get(sourceType).copyBeanToMap(source, target, options);
		return target;
	}

	public static <S, K, V> Map<K, V> fastCopyBeanToMap(@Nonnull Class<S> sourceType, @Nonnull S source, @Nonnull Type targetType, @Nonnull Map<K, V> target, @Nonnull CopyOptions options) {
		BeanCopier.get(sourceType).copyBeanToMap(source, targetType, target, options);
		return target;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull T target) {
		fastCopyMapToBean(source, (Class) target.getClass(), target);
		return target;
	}

	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull Class<T> targetType, @Nonnull T target) {
		BeanCopier.get(targetType).copyMapToBean(source, target);
		return target;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull T target, BiFunction<Type, Object, Object> converter) {
		return fastCopyMapToBean(source, (Class<T>) target.getClass(), target, converter);
	}

	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull Class<T> targetType, @Nonnull T target, BiFunction<Type, Object, Object> converter) {
		BeanCopier.get(targetType).copyMapToBean(source, target, converter);
		return target;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull T target, @Nonnull CopyOptions options) {
		return fastCopyMapToBean(source, (Class<T>) target.getClass(), target, options);
	}

	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull Class<T> targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		BeanCopier.get(targetType).copyMapToBean(source, target, options);
		return target;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <S, T> T fastCopyBeanToBean(@Nonnull S source, @Nonnull T target) {
		return fastCopyBeanToBean((Class<S>) source.getClass(), source, (Class<T>) target.getClass(), target);
	}

	public static <S, T> T fastCopyBeanToBean(@Nonnull Class<S> sourceType, @Nonnull S source, @Nonnull Class<T> targetType, @Nonnull T target) {
		BeanCopier.get(sourceType).copyBeanToBean(source, targetType, target);
		return target;
	}

	@SuppressWarnings({"unchecked"})
	public static <S, T> T fastCopyBeanToBean(@Nonnull S source, @Nonnull T target, @Nonnull CopyOptions options) {
		return fastCopyBeanToBean((Class<S>) source.getClass(), source, (Class<T>) target.getClass(), target, options);
	}

	public static <S, T> T fastCopyBeanToBean(@Nonnull Class<S> sourceType, @Nonnull S source, @Nonnull Class<T> targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		BeanCopier.get(sourceType).copyBeanToBean(source, targetType, target, options);
		return target;
	}


	@SuppressWarnings("rawtypes")
	public static Map copyMapToMap(@Nonnull Map source, @Nonnull Map target, @Nonnull CopyOptions options) {
		Copier<?> copier = new MapToMapCopier(source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static Map copyMapToMap(@Nonnull Map source, @Nonnull Type targetType, @Nonnull Map target, @Nonnull CopyOptions options) {
		Copier<?> copier = new MapToMapCopier(source, targetType, target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull Type sourceType, @Nonnull S source, @Nonnull Type targetType, @Nonnull Map target, @Nonnull CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(sourceType, source, targetType, target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull Type sourceType, @Nonnull S source, @Nonnull Map target, @Nonnull CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(sourceType, source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull S source, @Nonnull Type targetType, @Nonnull Map target, @Nonnull CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(source.getClass(), source, targetType, target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull S source, @Nonnull Map target, @Nonnull CopyOptions options) {
		@SuppressWarnings("rawtypes")
		Copier<Map> copier = new BeanToMapCopier<>(source.getClass(), source, new TypeRef<Map<String, Object>>() {}.getType(), target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T copyMapToBean(@Nonnull Map source, @Nonnull Type targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		Copier<T> copier = new MapToBeanCopier<>(source, targetType, target, options);
		copier.copy();
		return target;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T copyMapToBean(@Nonnull Map source, @Nonnull T target, @Nonnull CopyOptions options) {
		Copier<T> copier = new MapToBeanCopier<>(source, target.getClass(), target, options);
		copier.copy();
		return target;
	}

	public static <S, T> T copyBeanToBean(@Nonnull Type sourceType, @Nonnull S source, @Nonnull Type targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(sourceType, source, targetType, target, options);
		copier.copy();
		return target;
	}

	public static <S, T> T copyBeanToBean(@Nonnull S source, @Nonnull Type targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(source.getClass(), source, targetType, target, options);
		copier.copy();
		return target;
	}

	public static <S, T> T copyBeanToBean(@Nonnull Type sourceType, @Nonnull S source, @Nonnull T target, @Nonnull CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(sourceType, source, target.getClass(), target, options);
		copier.copy();
		return target;
	}

	public static <S, T> T copyBeanToBean(@Nonnull S source, @Nonnull T target, @Nonnull CopyOptions options) {
		Copier<T> copier = new BeanToBeanCopier<>(source.getClass(), source, target.getClass(), target, options);
		copier.copy();
		return target;
	}

	public static <E> E copy(@Nonnull Object source, @Nonnull E target) {
		return create(source.getClass(), source, target.getClass(), target, null).copy();
	}

	public static <E> E copy(@Nonnull Object source, @Nonnull E target, CopyOptions options) {
		return create(source.getClass(), source, target.getClass(), target, options).copy();
	}

	public static <E> E copy(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target) {
		return create(source.getClass(), source, targetType, target, null).copy();
	}

	public static <E> E copy(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
		return create(source.getClass(), source, targetType, target, options).copy();
	}

	public static <E> E copy(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull E target, @Nonnull Type targetType, CopyOptions options) {
		return create(sourceType, source, targetType, target, options).copy();
	}

	@SuppressWarnings("unchecked")
	public static <E> Copier<E> create(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
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

	public static <E> Copier<E> create(@Nonnull Object source, @Nonnull E target) {
		return create(source.getClass(), source, target.getClass(), target, null);
	}

	public static <E> Copier<E> create(@Nonnull Object source, @Nonnull E target, CopyOptions options) {
		return create(source.getClass(), source, target.getClass(), target, options);
	}

	public static <E> Copier<E> create(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target) {
		return create(source.getClass(), source, targetType, target, null);
	}

	public static <E> Copier<E> create(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
		return create(source.getClass(), source, targetType, target, options);
	}

}

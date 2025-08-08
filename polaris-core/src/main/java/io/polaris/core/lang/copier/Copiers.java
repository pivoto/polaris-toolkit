package io.polaris.core.lang.copier;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import io.polaris.core.asm.reflect.BeanCopier;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.MetaObject;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since 1.8
 */
public class Copiers {
	private static final Logger log = Loggers.of(Copiers.class);

	public static <E> E fastCopy(@Nonnull Object source, @Nonnull E target) {
		return fastCopy(source.getClass(), source, target.getClass(), target);
	}

	public static <E> E fastCopy(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target) {
		return fastCopy(source.getClass(), source, targetType, target);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <E> E fastCopy(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull Type targetType, @Nonnull E target) {
		if (source instanceof Collection || target instanceof Collection
			|| source.getClass().isArray() || target.getClass().isArray()) {
			return deepCopyIndexed(source, targetType, target, CopyOptions.DEFAULT, false);
		}
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
		return fastCopy(source.getClass(), source, target.getClass(), target, options);
	}

	public static <E> E fastCopy(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, @Nonnull CopyOptions options) {
		return fastCopy(source.getClass(), source, targetType, target, options);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <E> E fastCopy(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, @Nonnull CopyOptions options) {
		if (source instanceof Collection || target instanceof Collection
			|| source.getClass().isArray() || target.getClass().isArray()) {
			return deepCopyIndexed(source, targetType, target, options, false);
		}
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

	@SuppressWarnings({"unchecked"})
	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull T target, BiFunction<Type, Object, Object> converter) {
		return fastCopyMapToBean(source, (Class<T>) target.getClass(), target, converter);
	}

	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull Class<T> targetType, @Nonnull T target, BiFunction<Type, Object, Object> converter) {
		BeanCopier.get(targetType).copyMapToBean(source, target, converter);
		return target;
	}

	@SuppressWarnings({"unchecked"})
	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull T target, @Nonnull CopyOptions options) {
		return fastCopyMapToBean(source, (Class<T>) target.getClass(), target, options);
	}

	public static <T> T fastCopyMapToBean(@Nonnull Map<String, Object> source, @Nonnull Class<T> targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		BeanCopier.get(targetType).copyMapToBean(source, target, options);
		return target;
	}

	@SuppressWarnings({"unchecked"})
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
		return ((Copier<Map>) new MapToMapCopier(source, new TypeRef<Map<String, Object>>() {}.getType(), target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static Map copyMapToMap(@Nonnull Map source, @Nonnull Type targetType, @Nonnull Map target, @Nonnull CopyOptions options) {
		return ((Copier<Map>) new MapToMapCopier(source, targetType, target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull Type sourceType, @Nonnull S source, @Nonnull Type targetType, @Nonnull Map target, @Nonnull CopyOptions options) {
		return ((Copier<Map>) new BeanToMapCopier<>(sourceType, source, targetType, target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull Type sourceType, @Nonnull S source, @Nonnull Map target, @Nonnull CopyOptions options) {
		return ((Copier<Map>) new BeanToMapCopier<>(sourceType, source, new TypeRef<Map<String, Object>>() {}.getType(), target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull S source, @Nonnull Type targetType, @Nonnull Map target, @Nonnull CopyOptions options) {
		return ((Copier<Map>) new BeanToMapCopier<>(source.getClass(), source, targetType, target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static <S> Map copyBeanToMap(@Nonnull S source, @Nonnull Map target, @Nonnull CopyOptions options) {
		return ((Copier<Map>) new BeanToMapCopier<>(source.getClass(), source, new TypeRef<Map<String, Object>>() {}.getType(), target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static <T> T copyMapToBean(@Nonnull Map source, @Nonnull Type targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		return ((Copier<T>) new MapToBeanCopier<>(source, targetType, target, options)).copy();
	}

	@SuppressWarnings("rawtypes")
	public static <T> T copyMapToBean(@Nonnull Map source, @Nonnull T target, @Nonnull CopyOptions options) {
		return ((Copier<T>) new MapToBeanCopier<>(source, target.getClass(), target, options)).copy();
	}

	public static <S, T> T copyBeanToBean(@Nonnull Type sourceType, @Nonnull S source, @Nonnull Type targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		return ((Copier<T>) new BeanToBeanCopier<>(sourceType, source, targetType, target, options)).copy();
	}

	public static <S, T> T copyBeanToBean(@Nonnull S source, @Nonnull Type targetType, @Nonnull T target, @Nonnull CopyOptions options) {
		return ((Copier<T>) new BeanToBeanCopier<>(source.getClass(), source, targetType, target, options)).copy();
	}

	public static <S, T> T copyBeanToBean(@Nonnull Type sourceType, @Nonnull S source, @Nonnull T target, @Nonnull CopyOptions options) {
		return ((Copier<T>) new BeanToBeanCopier<>(sourceType, source, target.getClass(), target, options)).copy();
	}

	public static <S, T> T copyBeanToBean(@Nonnull S source, @Nonnull T target, @Nonnull CopyOptions options) {
		return ((Copier<T>) new BeanToBeanCopier<>(source.getClass(), source, target.getClass(), target, options)).copy();
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

	public static <E> E copy(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
		return create(sourceType, source, targetType, target, options).copy();
	}

	@SuppressWarnings("unchecked")
	public static <E> Copier<E> create(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
		Copier<E> copier;
		// 数组或集合类型单独处理
		if (source instanceof Collection || target instanceof Collection
			|| source.getClass().isArray() || target.getClass().isArray()) {
			copier = new IndexedObjectCopier<>(source, target, targetType, options);
		} else {
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


	public static <E> E deepCopy(@Nonnull Object source, @Nonnull E target) {
		return create(source.getClass(), source, target.getClass(), target, null).deepCopy();
	}

	public static <E> E deepCopy(@Nonnull Object source, @Nonnull E target, CopyOptions options) {
		return create(source.getClass(), source, target.getClass(), target, options).deepCopy();
	}

	public static <E> E deepCopy(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target) {
		return create(source.getClass(), source, targetType, target, null).deepCopy();
	}

	public static <E> E deepCopy(@Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
		return create(source.getClass(), source, targetType, target, options).deepCopy();
	}

	public static <E> E deepCopy(@Nonnull Type sourceType, @Nonnull Object source, @Nonnull Type targetType, @Nonnull E target, CopyOptions options) {
		return create(sourceType, source, targetType, target, options).deepCopy();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <E> E deepClone(@Nonnull Object source, @Nonnull Type targetType, CopyOptions options) {
		MetaObject<?> metaObject = MetaObject.of(targetType);
		if (metaObject.isBasic() || metaObject.isEnum() || metaObject.isObject()) {
			return (E) options.convert(targetType, source);
		}
		if (metaObject.isArray()) {
			if (source.getClass().isArray()) {
				Object target = Array.newInstance(
					metaObject.getElementType().getBeanType().getRawClass(),
					Array.getLength(source));
				deepCopy(source.getClass(), source, targetType, target, options);
				return (E) target;
			}
			if (source instanceof List) {
				Object target = Array.newInstance(
					metaObject.getElementType().getBeanType().getRawClass(),
					((List) source).size());
				deepCopy(source.getClass(), source, targetType, target, options);
				return (E) target;
			}
			return (E) Reflects.newInstanceIfPossible(Types.getClass(targetType));
		}
		// else: isMap, isCollection, isBean
		Object target = metaObject.newInstance();
		if (target != null) {
			deepCopy(source.getClass(), source, targetType, target, options);
		} else {
			target = options.convert(targetType, deepClone(source, options));
		}
		return (E) target;
	}

	public static Object deepClone(@Nonnull Object source) {
		return deepClone(source, CopyOptions.DEFAULT);
	}

	public static Object deepClone(@Nonnull Object source, CopyOptions options) {
		MetaObject<?> metaObject = MetaObject.of(source.getClass());
		if (metaObject.isBasic() || metaObject.isEnum() || metaObject.isObject()) {
			return source;
		}
		if (metaObject.isArray()) {
			Object target = Array.newInstance(
				metaObject.getElementType().getBeanType().getRawClass(),
				Array.getLength(source));
			deepCopy(source.getClass(), source, target.getClass(), target, options);
			return target;
		}
		Object target = metaObject.newInstance();
		if (target != null) {
			deepCopy(source.getClass(), source, target.getClass(), target, options);
		}
		return target;
	}


	static void copyArrayToArray(Object source, Type targetType, Object target, CopyOptions options, boolean deep) {
		int sourceLength = Array.getLength(source);
		int targetLength = Array.getLength(target);
		int minLength = Math.min(sourceLength, targetLength);
		JavaType<?> javaType = JavaType.of(targetType);
		JavaType<?> componentType;
		if (javaType.getRawType() instanceof GenericArrayType) {
			componentType = JavaType.of(((GenericArrayType) javaType.getRawType()).getGenericComponentType());
		} else {
			componentType = JavaType.of((Type) javaType.getRawClass().getComponentType());
		}
		for (int i = 0; i < minLength; i++) {
			Object value = Array.get(source, i);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			Object old = null;
			if ((deep || !options.override())) {
				// 只在深度复制或判断覆盖时才获取原值
				old = Array.get(target, i);
				if (old != null && !options.override()) {
					continue;
				}
			}
			value = options.convert(componentType, value);
			value = options.editValue(String.valueOf(i), value);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			if (deep && value != null) {
				if (old == null) {
					value = Copiers.deepClone(value, componentType, options);
					if (value == null && options.ignoreNull()) {
						continue;
					}
				} else {
					// 复制子属性对象并完成本次循环
					Copiers.deepCopy(value.getClass(), value, componentType, old, options);
					continue;
				}
			}
			Array.set(target, i, value);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static void copyArrayToList(Object source, Type targetType, List target, CopyOptions options, boolean deep) {
		int sourceLength = Array.getLength(source);
		int targetLength = target.size();
		JavaType<?> javaType = JavaType.of(targetType);
		JavaType<?> componentType = JavaType.of(javaType.getActualType(List.class, 0));
		for (int i = 0; i < sourceLength; i++) {
			Object value = Array.get(source, i);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			Object old = null;
			if ((deep || !options.override())) {
				// 只在深度复制或判断覆盖时才获取原值
				old = i >= targetLength ? null : target.get(i);
				if (old != null && !options.override()) {
					continue;
				}
			}
			value = options.convert(componentType, value);
			value = options.editValue(String.valueOf(i), value);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			if (deep && value != null) {
				if (old == null) {
					value = Copiers.deepClone(value, componentType, options);
					if (value == null && options.ignoreNull()) {
						continue;
					}
				} else {
					// 复制子属性对象并完成本次循环
					Copiers.deepCopy(value.getClass(), value, componentType, old, options);
					continue;
				}
			}
			if (i < targetLength) {
				target.set(i, value);
			} else {
				for (int j = 0; j < i - target.size(); j++) {
					target.add(null);
				}
				target.add(value);
			}
		}
	}

	static void copyListToArray(List source, Type targetType, Object target, CopyOptions options, boolean deep) {
		int sourceLength = source.size();
		int targetLength = Array.getLength(target);
		int minLength = Math.min(sourceLength, targetLength);
		JavaType<?> javaType = JavaType.of(targetType);
		JavaType<?> componentType;
		if (javaType.getRawType() instanceof GenericArrayType) {
			componentType = JavaType.of(((GenericArrayType) javaType.getRawType()).getGenericComponentType());
		} else {
			componentType = JavaType.of((Type) javaType.getRawClass().getComponentType());
		}
		for (int i = 0; i < minLength; i++) {
			Object value = source.get(i);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			Object old = null;
			if ((deep || !options.override())) {
				// 只在深度复制或判断覆盖时才获取原值
				old = Array.get(target, i);
				if (old != null && !options.override()) {
					continue;
				}
			}
			value = options.convert(componentType, value);
			value = options.editValue(String.valueOf(i), value);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			if (deep && value != null) {
				if (old == null) {
					value = Copiers.deepClone(value, componentType, options);
					if (value == null && options.ignoreNull()) {
						continue;
					}
				} else {
					// 复制子属性对象并完成本次循环
					Copiers.deepCopy(value.getClass(), value, componentType, old, options);
					continue;
				}
			}
			Array.set(target, i, value);
		}
	}

	@SuppressWarnings("unchecked")
	static void copyListToList(List source, Type targetType, List target, CopyOptions options, boolean deep) {
		int sourceLength = source.size();
		int targetLength = target.size();
		JavaType<?> javaType = JavaType.of(targetType);
		JavaType<?> componentType = JavaType.of(javaType.getActualType(List.class, 0));
		for (int i = 0; i < sourceLength; i++) {
			Object value = source.get(i);
			if (value == null && options.ignoreNull()) {
				continue;
			}

			Object old = null;
			if ((deep || !options.override())) {
				old = i >= targetLength ? null : target.get(i);
				if (old != null && !options.override()) {
					continue;
				}
			}
			value = options.convert(componentType, value);
			value = options.editValue(String.valueOf(i), value);
			if (value == null && options.ignoreNull()) {
				continue;
			}
			if (deep && value != null) {
				if (old == null) {
					value = Copiers.deepClone(value, componentType, options);
					if (value == null && options.ignoreNull()) {
						continue;
					}
				} else {
					// 复制子属性对象并完成本次循环
					Copiers.deepCopy(value.getClass(), value, componentType, old, options);
					continue;
				}
			}
			if (i < targetLength) {
				target.set(i, value);
			} else {
				for (int j = 0; j < i - target.size(); j++) {
					target.add(null);
				}
				target.add(value);
			}
		}
	}


	static <T> T deepCopyIndexed(Object source, Type targetType, T target, CopyOptions options, boolean deep) {
		options = options != null ? options : CopyOptions.DEFAULT;
		try {
			if (source.getClass().isArray()) {
				if (target.getClass().isArray()) {
					Copiers.copyArrayToArray(source, targetType, target, options, deep);
				} else if (target instanceof List) {
					Copiers.copyArrayToList(source, targetType, (List) target, options, deep);
				} else {
					// ignore
					if (log.isDebugEnabled()) {
						log.debug("不支持数组到非数组或列表间的属性复制");
					}
				}
			} else if (source instanceof List) {
				if (target.getClass().isArray()) {
					Copiers.copyListToArray((List) source, targetType, target, options, deep);
				} else if (target instanceof List) {
					Copiers.copyListToList((List) source, targetType, (List) target, options, deep);
				} else {
					// ignore
					if (log.isDebugEnabled()) {
						log.debug("不支持列表到非数组或列表间的属性复制");
					}
				}
			} else {
				// ignore
				if (log.isDebugEnabled()) {
					log.debug("不支持非数组或列表间的属性复制");
				}
			}
		} catch (Exception e) {
			if (!options.ignoreError()) {
				throw new IllegalArgumentException(e);
			} else {
				log.warn("复制属性失败：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}
		return target;
	}
}

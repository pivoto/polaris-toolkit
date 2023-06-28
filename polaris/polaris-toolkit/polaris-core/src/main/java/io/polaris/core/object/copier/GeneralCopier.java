package io.polaris.core.object.copier;

import io.polaris.core.object.Copier;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class GeneralCopier<T> implements Copier<T> {

	private final Copier<T> copier;

	/**
	 * @param source      来源对象
	 * @param target      目标对象
	 * @param targetType  目标的泛型类型，用于标注有泛型参数的Bean对象
	 * @param copyOptions 拷贝属性选项
	 */
	public GeneralCopier(Object source, T target, Type targetType, CopyOptions copyOptions) {
		if (source instanceof Map) {
			if (target instanceof Map) {
				copier = (Copier<T>) new MapToMapCopier((Map<?, ?>) source, (Map<?, ?>) target, targetType, copyOptions);
			} else {
				copier = new MapToBeanCopier<>((Map<?, ?>) source, target, targetType, copyOptions);
			}
		} else {
			if (target instanceof Map) {
				copier = (Copier<T>) new BeanToMapCopier(source, (Map<?, ?>) target, targetType, copyOptions);
			} else {
				copier = new BeanToBeanCopier<>(source, target, targetType, copyOptions);
			}
		}
	}

	public static <T> GeneralCopier<T> create(Object source, T target, CopyOptions copyOptions) {
		return create(source, target, target.getClass(), copyOptions);
	}

	public static <T> GeneralCopier<T> create(Object source, T target, Type destType, CopyOptions copyOptions) {
		return new GeneralCopier<>(source, target, destType, copyOptions);
	}

	@Override
	public T copy() {
		return copier.copy();
	}
}

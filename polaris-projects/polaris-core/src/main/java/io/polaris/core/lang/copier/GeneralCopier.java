//package io.polaris.core.lang.copier;
//
//import java.lang.reflect.Type;
//import java.util.Map;
//
///**
// * @author Qt
// * @since 1.8
// */
//public class GeneralCopier<T> implements Copier<T> {
//
//	private final Copier<T> copier;
//
//	/**
//	 * @param source      来源对象
//	 * @param sourceType  来源对象类型，必须为目标对象的实现接口或父类，用于限制拷贝的属性，可以是标注有泛型参数的Bean对象
//	 * @param target      目标对象
//	 * @param targetType  目标的类型，必须为目标对象的实现接口或父类，用于限制拷贝的属性，可以是标注有泛型参数的Bean对象
//	 * @param copyOptions 拷贝属性选项
//	 */
//	@SuppressWarnings("unchecked")
//	public GeneralCopier(Object source, Type sourceType, T target, Type targetType, CopyOptions copyOptions) {
//		if (source instanceof Map) {
//			if (target instanceof Map) {
//				copier = (Copier<T>) new MapToMapCopier((Map<?, ?>) source, sourceType, (Map<?, ?>) target, targetType, copyOptions);
//			} else {
//				copier = new MapToBeanCopier<>((Map<?, ?>) source, sourceType, target, targetType, copyOptions);
//			}
//		} else {
//			if (target instanceof Map) {
//				copier = (Copier<T>) new BeanToMapCopier<>(source, sourceType, (Map<?, ?>) target, targetType, copyOptions);
//			} else {
//				copier = new BeanToBeanCopier<>(source, sourceType, target, targetType, copyOptions);
//			}
//		}
//	}
//
//	@Override
//	public T copy() {
//		return copier.copy();
//	}
//}

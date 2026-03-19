package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since Nov 12, 2023
 */
public class Annotations {


	/**
	 * 获取指定元素上的原始注解
	 * <p>
	 * 该方法用于获取元素上的注解，支持在继承结构中查找。
	 * 委托给 {@link RawAnnotations#getAnnotation(AnnotatedElement, Class)} 实现完整的注解查找逻辑。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 找到的注解实例，如果未找到则返回 null
	 */
	@Nullable
	public static <A extends Annotation> A getRawAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return RawAnnotations.getAnnotation(element, annotationType);
	}

	/**
	 * 获取指定元素上的原始可重复注解数组
	 * <p>
	 * 该方法用于获取元素上的可重复注解，支持在继承结构中查找。
	 * 委托给 {@link RawAnnotations#getRepeatableAnnotation(AnnotatedElement, Class)} 实现完整的可重复注解查找逻辑。
	 * 返回的数组可能为空数组，但不会是 null。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 注解数组，如果未找到则返回空数组而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> A[] getRawRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return RawAnnotations.getRepeatableAnnotation(element, annotationType);
	}


	/**
	 * 查找指定元素上满足条件的原始注解
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的原始注解实例。
	 * 委托给 {@link RawAnnotations#findAnnotation(AnnotatedElement, Predicate)} 实现完整的注解查找逻辑。
	 * 返回的集合包含所有满足条件的注解实例，支持在继承结构中查找。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被选中；不能为 null
	 * @return 满足条件的注解实例集合，如果没有任何注解满足条件则返回空集合而不是 null
	 */
	@Nonnull
	public static Set<Annotation> findRawAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return RawAnnotations.findAnnotation(element, predicate);
	}

	/**
	 * 获取指定元素上的合并注解
	 * <p>
	 * 该方法用于获取经过合并处理后的注解实例，支持注解的层次结构查找和属性合并。
	 * 委托给 {@link MergedAnnotations#getMergedAnnotation(AnnotatedElement, Class)} 实现完整的注解合并逻辑。
	 * 合并过程包括元注解查找和别名属性处理。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解实例，如果未找到则返回 null
	 */
	@Nullable
	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getMergedAnnotation(element, annotationType);
	}

	/**
	 * 获取指定元素上的合并注解属性
	 * <p>
	 * 该方法用于获取经过合并处理后的注解属性，支持注解的层次结构查找和属性合并。
	 * 委托给 {@link MergedAnnotations#getMergedAnnotationAttributes(AnnotatedElement, Class)} 实现完整的注解合并逻辑。
	 * 合并过程包括元注解查找和别名属性处理。
	 * 返回的是 AnnotationAttributes 对象，可以方便地访问注解的各种属性值。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解属性，如果未找到则返回 null
	 */
	@Nullable
	public static <A extends Annotation> AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getMergedAnnotationAttributes(element, annotationType);
	}

	/**
	 * 获取指定元素上的合并可重复注解集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解集合，支持在注解层次结构中查找所有匹配的可重复注解。
	 * 委托给 {@link MergedAnnotations#getMergedRepeatableAnnotation(AnnotatedElement, Class)} 实现完整的注解合并逻辑。
	 * 返回的集合包含所有层级中找到的可重复注解实例。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 合并后的可重复注解集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getMergedRepeatableAnnotation(element, annotationType);
	}

	/**
	 * 获取指定元素上的合并可重复注解属性集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解属性集合，支持在注解层次结构中查找所有匹配的可重复注解。
	 * 委托给 {@link MergedAnnotations#getMergedRepeatableAnnotationAttributes(AnnotatedElement, Class)} 实现完整的注解合并逻辑。
	 * 返回的集合包含所有层级中找到的可重复注解的 AnnotationAttributes 对象。
	 * 与 {@link #getMergedRepeatableAnnotation(AnnotatedElement, Class)} 不同，该方法返回的是注解属性而非注解实例。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解属性集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<AnnotationAttributes> getMergedRepeatableAnnotationAttributes(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getMergedRepeatableAnnotationAttributes(element, annotationType);
	}

	/**
	 * 获取指定元素上的顶层合并可重复注解集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解集合，但只返回最顶层（最近距离）的注解实例。
	 * 委托给 {@link MergedAnnotations#getTopMergedRepeatableAnnotation(AnnotatedElement, Class)} 实现完整的注解合并逻辑。
	 * 与 {@link #getMergedRepeatableAnnotation(AnnotatedElement, Class)} 不同，该方法不会返回继承层级中的所有注解。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 顶层合并后的可重复注解集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<A> getTopMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getTopMergedRepeatableAnnotation(element, annotationType);
	}

	/**
	 * 获取指定元素上的顶层合并可重复注解属性集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解属性集合，但只返回最顶层（最近距离）的注解实例的属性。
	 * 委托给 {@link MergedAnnotations#getTopMergedRepeatableAnnotationAttributes(AnnotatedElement, Class)} 实现完整的注解合并逻辑。
	 * 与 {@link #getMergedRepeatableAnnotationAttributes(AnnotatedElement, Class)} 不同，该方法不会返回继承层级中的所有注解属性，
	 * 而是只返回距离元素最近的注解层中的可重复注解属性。
	 * 返回的是 AnnotationAttributes 集合，可以方便地访问每个注解的各种属性值。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 顶层合并后的注解属性集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<AnnotationAttributes> getTopMergedRepeatableAnnotationAttributes(AnnotatedElement element, Class<A> annotationType) {
		return MergedAnnotations.getTopMergedRepeatableAnnotationAttributes(element, annotationType);
	}

	/**
	 * 查找指定元素上满足条件的合并注解属性
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并注解属性。
	 * 委托给 {@link MergedAnnotations#findMergedAnnotationAttributes(AnnotatedElement, Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的合并注解属性。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并注解属性；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, AnnotationAttributes> findMergedAnnotationAttributes(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return MergedAnnotations.findMergedAnnotationAttributes(element, predicate);
	}

	/**
	 * 查找指定元素上满足条件的合并注解
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并注解实例。
	 * 委托给 {@link MergedAnnotations#findMergedAnnotation(AnnotatedElement, Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的合并注解实例。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并注解实例；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Annotation> findMergedAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return MergedAnnotations.findMergedAnnotation(element, predicate);
	}

	/**
	 * 查找指定元素上满足条件的合并可重复注解属性集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并可重复注解属性集合。
	 * 委托给 {@link MergedAnnotations#findMergedRepeatableAnnotationAttributes(AnnotatedElement, Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的所有合并可重复注解属性。
	 * 即使某个注解类型没有可重复注解，也会在结果中包含该类型（值为空集合）。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并可重复注解属性集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<AnnotationAttributes>> findMergedRepeatableAnnotationAttributes(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return MergedAnnotations.findMergedRepeatableAnnotationAttributes(element, predicate);
	}

	/**
	 * 查找指定元素上满足条件的合并可重复注解集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并可重复注解实例集合。
	 * 委托给 {@link MergedAnnotations#findMergedRepeatableAnnotation(AnnotatedElement, Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的所有合并可重复注解实例。
	 * 即使某个注解类型没有可重复注解，也会在结果中包含该类型（值为空集合）。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并可重复注解实例集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<? extends Annotation>> findMergedRepeatableAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return MergedAnnotations.findMergedRepeatableAnnotation(element, predicate);
	}

	/**
	 * 查找指定元素上满足条件的顶层合并可重复注解属性集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的顶层（最近距离）合并可重复注解属性集合。
	 * 委托给 {@link MergedAnnotations#findTopMergedRepeatableAnnotationAttributes(AnnotatedElement, Predicate)} 实现完整的查找逻辑。
	 * 与 {@link #findMergedRepeatableAnnotationAttributes(AnnotatedElement, Predicate)} 不同，该方法只返回最近层级的注解属性，
	 * 而不包含继承层级中的注解属性。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的顶层合并可重复注解属性。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的顶层合并可重复注解属性集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<AnnotationAttributes>> findTopMergedRepeatableAnnotationAttributes(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return MergedAnnotations.findTopMergedRepeatableAnnotationAttributes(element, predicate);
	}

	/**
	 * 查找指定元素上满足条件的顶层合并可重复注解集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的顶层（最近距离）合并可重复注解实例集合。
	 * 委托给 {@link MergedAnnotations#findTopMergedRepeatableAnnotation(AnnotatedElement, Predicate)} 实现完整的查找逻辑。
	 * 与 {@link #findMergedRepeatableAnnotation(AnnotatedElement, Predicate)} 不同，该方法只返回最近层级的注解实例，
	 * 而不包含继承层级中的注解实例。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的顶层合并可重复注解实例。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的顶层合并可重复注解实例集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<? extends Annotation>> findTopMergedRepeatableAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return MergedAnnotations.findTopMergedRepeatableAnnotation(element, predicate);
	}


	/**
	 * 创建注解实例
	 * <p>
	 * 该方法用于根据指定的属性值创建注解实例。
	 * 通过 {@link AnnotationInvocationHandler#createProxy(Class, Map)} 创建注解的动态代理实例。
	 * 只使用提供的属性值，未提供的属性将使用默认值。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要创建的注解类型，必须是 Annotation 的子类
	 * @param values         注解属性值映射，键为属性名，值为属性值
	 * @return 创建的注解实例
	 */
	public static <A extends Annotation> A newInstance(Class<A> annotationType, Map<String, Object> values) {
		return AnnotationInvocationHandler.createProxy(annotationType, values);
	}

	/**
	 * 创建注解实例（支持不完整配置）
	 * <p>
	 * 该方法用于根据指定的属性值创建注解实例，支持允许不完整配置的模式。
	 * 通过 {@link AnnotationInvocationHandler#createProxy(Class, Map, boolean)} 创建注解的动态代理实例。
	 * 当 allowedIncomplete 为 true 时，允许某些必需属性未提供值。
	 *
	 * @param <A>               注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType    要创建的注解类型，必须是 Annotation 的子类
	 * @param values            注解属性值映射，键为属性名，值为属性值
	 * @param allowedIncomplete 是否允许不完整的配置，true 表示允许某些必需属性未提供值
	 * @return 创建的注解实例
	 */
	public static <A extends Annotation> A newInstance(Class<A> annotationType, Map<String, Object> values, boolean allowedIncomplete) {
		return AnnotationInvocationHandler.createProxy(annotationType, values, allowedIncomplete);
	}

	/**
	 * 使用默认值创建注解实例
	 * <p>
	 * 该方法用于创建基于注解类型默认值的注解实例，并使用提供的值覆盖部分默认值。
	 * 通过 {@link AnnotationAttributes#of(Class)} 创建注解属性，然后转换为注解实例。
	 * 与 {@link #newInstance(Class, Map)} 不同，该方法优先使用注解的默认值。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要创建的注解类型，必须是 Annotation 的子类
	 * @param values         注解属性值映射，键为属性名，值为属性值，用于覆盖默认值
	 * @return 创建的注解实例，使用默认值并用提供的值覆盖
	 */
	public static <A extends Annotation> A newInstanceWithDefaults(Class<A> annotationType, Map<String, Object> values) {
		return AnnotationAttributes.of(annotationType).asAnnotation();
	}

	/**
	 * 获取可重复注解的所有重复实例
	 * <p>
	 * 该方法用于获取可重复注解容器中包含的所有重复注解实例。
	 * 首先检查注解类型是否为可重复注解的容器类型，如果是则通过反射调用 value() 方法获取所有重复注解。
	 * 如果不是可重复注解，则返回 null。
	 *
	 * @param annotation 注解实例，可能是可重复注解的容器注解
	 * @return 重复注解数组，如果注解不是可重复注解的容器则返回 null
	 */
	public static Annotation[] getRepeatedAnnotations(Annotation annotation) {
		// 获取注解类型
		Class<? extends Annotation> annotationType = annotation.annotationType();
		// 检查是否为可重复注解的容器类型
		if (!isRepeatable(annotationType)) {
			return null;
		}
		// 通过反射调用 value() 方法获取所有重复注解
		return (Annotation[]) Reflects.invokeQuietly(annotation, annotationType.getDeclaredMethods()[0]);
	}

	/**
	 * 获取可重复注解的目标元素类型
	 * <p>
	 * 通过检查注解类型是否是某个可重复注解的容器来确定其对应的可重复注解类型
	 *
	 * @param annotationType 注解类型
	 * @return 如果是可重复注解的容器类型，则返回对应的可重复注解类型；否则返回null
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends Annotation> getRepeatedAnnotationType(Class<? extends Annotation> annotationType) {
		Method[] methods = annotationType.getDeclaredMethods();
		if (methods.length != 1) {
			return null;
		}
		Method method = methods[0];
		if (!method.getName().equals("value")) {
			return null;
		}
		Class<?> returnType = method.getReturnType();
		if (!returnType.isArray()) {
			return null;
		}
		Class<?> componentType = returnType.getComponentType();
		if (!componentType.isAnnotation()) {
			return null;
		}
		Repeatable annotation = componentType.getAnnotation(Repeatable.class);
		if (annotation == null) {
			return null;
		}
		if (annotation.value() == annotationType) {
			return (Class<? extends Annotation>) componentType;
		}
		return null;
	}

	/**
	 * 判断注解类型是否为可重复注解
	 * <p>
	 * 该方法用于检查指定的注解类型是否为可重复注解的容器类型。
	 * 通过调用 {@link #getRepeatedAnnotationType(Class)} 来判断，如果返回非 null 则表示是可重复注解。
	 * 可重复注解是指使用了 {@code @Repeatable} 元注解的注解类型。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要检查的注解类型
	 * @return 如果是可重复注解则返回 true，否则返回 false
	 */
	public static <A extends Annotation> boolean isRepeatable(Class<A> annotationType) {
		// 通过检查是否存在对应的可重复注解类型来判断
		return getRepeatedAnnotationType(annotationType) != null;
	}

	/**
	 * 判断注解类型是否有别名定义
	 * <p>
	 * 该方法用于检查注解类型中是否定义了别名注解。
	 * 通过扫描注解的所有成员方法，查找使用了别名注解（如 {@code @AliasFor}）的方法。
	 * 别名定义要求别名注解类型与当前注解类型相同或是 Annotation.class。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要检查的注解类型
	 * @return 如果存在别名定义则返回 true，否则返回 false
	 */
	public static <A extends Annotation> boolean hasAliasDefinition(Class<A> annotationType) {
		// 获取注解的所有成员方法
		Method[] methods = AnnotationAttributes.getAnnotationMembers(annotationType);
		// 遍历所有成员方法，查找别名注解
		for (Method method : methods) {
			Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(method);
			if (aliasAttributes != null) {
				for (AliasAttribute alias : aliasAttributes) {
					// 检查别名注解类型是否与当前注解类型相同或是 Annotation.class
					if (alias != null && (alias.annotationType() == annotationType || alias.annotationType() == Annotation.class)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 使用指定的别名查找器判断注解类型是否有别名定义
	 * <p>
	 * 该方法是 {@link #hasAliasDefinition(Class)} 的重载版本，允许使用自定义的别名查找器。
	 * 通过扫描注解的所有成员方法，使用指定的 aliasFinder 查找使用了别名注解的方法。
	 * 别名定义要求别名注解类型与当前注解类型相同或是 Annotation.class。
	 *
	 * @param aliasFinder    别名查找器，用于查找方法上的别名注解
	 * @param annotationType 要检查的注解类型
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @return 如果存在别名定义则返回 true，否则返回 false
	 */
	public static <A extends Annotation> boolean hasAliasDefinition(AliasFinder aliasFinder, Class<A> annotationType) {
		// 获取注解的所有成员方法
		Method[] methods = AnnotationAttributes.getAnnotationMembers(annotationType);
		// 遍历所有成员方法，使用指定的别名查找器查找别名注解
		for (Method method : methods) {
			Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(aliasFinder, method);
			if (aliasAttributes != null) {
				for (AliasAttribute alias : aliasAttributes) {
					// 检查别名注解类型是否与当前注解类型相同或是 Annotation.class
					if (alias != null && (alias.annotationType() == annotationType || alias.annotationType() == Annotation.class)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 将注解转换为注解属性
	 * <p>
	 * 该方法用于将注解实例转换为 AnnotationAttributes 对象，便于访问和操作注解的属性值。
	 * 委托给 {@link AnnotationAttributes#of(Annotation)} 实现转换逻辑。
	 *
	 * @param annotation 注解实例，不能为 null
	 * @return 注解属性对象，包含注解的所有属性值
	 */
	public static AnnotationAttributes asAnnotationAttributes(Annotation annotation) {
		return AnnotationAttributes.of(annotation);
	}

	/**
	 * 将注解类型转换为注解属性
	 * <p>
	 * 该方法用于根据注解类型创建 AnnotationAttributes 对象，通常用于获取注解的默认属性值。
	 * 委托给 {@link AnnotationAttributes#of(Class)} 实现转换逻辑。
	 * 与 {@link #asAnnotationAttributes(Annotation)} 不同，该方法不需要注解实例。
	 *
	 * @param annotationType 注解类型，不能为 null
	 * @return 注解属性对象，包含注解类型的默认属性值
	 */
	public static AnnotationAttributes asAnnotationAttributes(Class<? extends Annotation> annotationType) {
		return AnnotationAttributes.of(annotationType);
	}

	/**
	 * 将注解类型和属性值转换为注解属性
	 * <p>
	 * 该方法用于根据注解类型和指定的属性值创建 AnnotationAttributes 对象。
	 * 首先基于注解类型创建包含所有默认值的 AnnotationAttributes 实例，
	 * 然后使用提供的 values 映射覆盖对应的属性值。
	 * 委托给 {@link AnnotationAttributes#of(Class, Map)} 实现转换逻辑。
	 * 适用于需要自定义部分属性值而保留其他默认值的场景。
	 *
	 * @param annotationType 注解类型，不能为 null
	 * @param values         属性值映射，键为属性名，值为属性值，用于覆盖默认值；可以为 null 或空映射
	 * @return 注解属性对象，包含注解类型的默认属性值并用提供的值进行覆盖
	 */
	public static AnnotationAttributes asAnnotationAttributes(Class<? extends Annotation> annotationType, Map<String, Object> values) {
		return AnnotationAttributes.of(annotationType, values);
	}

	/**
	 * 将注解转换为合并注解
	 * <p>
	 * 该方法用于将普通注解实例转换为 MergedAnnotation 对象，以便进行注解合并操作。
	 * 委托给 {@link MergedAnnotation#of(Annotation)} 实现转换逻辑。
	 * MergedAnnotation 包含了注解的层级信息和别名映射关系。
	 *
	 * @param annotation 注解实例，不能为 null
	 * @return 合并注解对象，封装了注解的完整信息
	 */
	public static MergedAnnotation asMergedAnnotation(Annotation annotation) {
		return MergedAnnotation.of(annotation);
	}

	/**
	 * 将注解属性转换为合并注解
	 * <p>
	 * 该方法用于将 AnnotationAttributes 对象转换为 MergedAnnotation 对象，以便进行注解合并操作。
	 * 委托给 {@link MergedAnnotation#of(AnnotationAttributes)} 实现转换逻辑。
	 * MergedAnnotation 包含了注解的层级信息和别名映射关系。
	 * 与 {@link #asMergedAnnotation(Annotation)} 不同，该方法接受的是注解属性而非注解实例。
	 *
	 * @param attributes 注解属性对象，不能为 null
	 * @return 合并注解对象，封装了注解的完整信息
	 */
	public static MergedAnnotation asMergedAnnotation(AnnotationAttributes attributes) {
		return MergedAnnotation.of(attributes);
	}

	/**
	 * 将注解层次结构列表转换为层次化合并注解
	 * <p>
	 * 该方法用于将按层级组织的注解集合列表转换为 HierarchyMergedAnnotation 对象。
	 * 委托给 {@link HierarchyMergedAnnotation#of(List)} 实现转换逻辑。
	 * 列表的索引表示层级深度，索引越小表示距离原始注解越近。
	 *
	 * @param hierarchyAnnotations 按层级组织的注解集合列表，不能为 null
	 * @return 层次化合并注解对象，封装了完整的注解层次结构信息
	 */
	public static HierarchyMergedAnnotation asHierarchyMergedAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations) {
		return HierarchyMergedAnnotation.of(hierarchyAnnotations);
	}

	/**
	 * 从注解元素创建层次化合并注解
	 * <p>
	 * 该方法用于直接从注解元素（类、方法、参数等）创建 HierarchyMergedAnnotation 对象。
	 * 委托给 {@link HierarchyMergedAnnotation#of(AnnotatedElement)} 实现完整的注解层次结构扫描和转换逻辑。
	 * 会自动扫描元素的直接注解以及继承结构中的所有注解。
	 *
	 * @param element 注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @return 层次化合并注解对象，封装了元素及其继承结构中的完整注解信息
	 */
	public static HierarchyMergedAnnotation asHierarchyMergedAnnotation(AnnotatedElement element) {
		return HierarchyMergedAnnotation.of(element);
	}

}

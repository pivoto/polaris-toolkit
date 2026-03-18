package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.tuple.Ref;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 匹配的合并注解类，用于处理注解及其别名的合并逻辑
 *
 * @param <A> 注解类型
 * @author Qt
 * @since Jan 07, 2024
 */
@EqualsAndHashCode
@ToString
public class MatchedMergedAnnotation<A extends Annotation> {

	/** 注解类型 */
	@Nonnull
	private final Class<A> annotationType;
	/** 匹配到的注解实例 */
	@Nullable
	private final MergedAnnotation matchedAnnotation;

	/** 别名注解列表 */
	@Nonnull
	private final List<MergedAnnotation> aliasAnnotations;

	/** 注解属性缓存 */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	private transient Ref<AnnotationAttributes> annotationAttributes;

	/**
	 * 构造函数
	 * <p>
	 * 创建一个匹配的合并注解实例，用于处理注解及其别名的合并逻辑。
	 * 该构造器允许传入 null 的别名注解列表，此时会自动转换为空列表。
	 *
	 * @param annotationType    注解类型，指定要创建的注解的类型，不能为 null
	 * @param matchedAnnotation 匹配的注解实例，可以是 null，表示没有直接匹配的注解
	 * @param aliasAnnotations  别名注解列表，可以是 null，为 null 时会自动转换为空列表
	 */
	public MatchedMergedAnnotation(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation, @Nullable List<MergedAnnotation> aliasAnnotations) {
		this.annotationType = annotationType;
		this.matchedAnnotation = matchedAnnotation;
		this.aliasAnnotations = aliasAnnotations == null ? Collections.emptyList() : aliasAnnotations;
	}

	/**
	 * 创建匹配的合并注解实例
	 * <p>
	 * 这是一个静态工厂方法，用于创建包含匹配注解和别名注解的合并注解实例。
	 * 该方法允许别名注解列表为 null，此时会创建不包含别名的合并注解。
	 *
	 * @param annotationType    注解类型，指定要创建的注解的类型，不能为 null
	 * @param matchedAnnotation 匹配的注解实例，可以是 null，表示没有直接匹配的注解
	 * @param aliasAnnotations  别名注解列表，可以是 null，为 null 时会在构造函数中转换为空列表
	 * @param <A>               注解类型泛型参数，必须是 Annotation 的子类
	 * @return MatchedMergedAnnotation 实例，封装了注解类型、匹配注解和别名注解
	 */
	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation, @Nullable List<MergedAnnotation> aliasAnnotations) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, aliasAnnotations);
	}

	/**
	 * 创建匹配的合并注解实例（无别名注解）
	 * <p>
	 * 这是一个静态工厂方法的便捷重载，用于创建只包含匹配注解而不包含别名注解的合并注解实例。
	 * 适用于只有直接匹配的注解而没有别名注解的场景。
	 *
	 * @param annotationType    注解类型，指定要创建的注解的类型，不能为 null
	 * @param matchedAnnotation 匹配的注解实例，可以是 null，表示没有直接匹配的注解
	 * @param <A>               注解类型泛型参数，必须是 Annotation 的子类
	 * @return MatchedMergedAnnotation 实例，封装了注解类型和匹配注解，不包含别名注解
	 */
	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, Collections.emptyList());
	}

	/**
	 * 获取注解属性
	 * <p>
	 * 该方法用于获取合并后的注解属性，支持懒加载和缓存机制。
	 * 第一次调用时会通过 {@link #newAnnotationAttributes()} 创建注解属性并缓存，
	 * 后续调用直接返回缓存的属性实例，避免重复计算。
	 *
	 * @return 注解属性，如果没有任何匹配的注解或别名注解则返回 null
	 */
	@Nullable
	public AnnotationAttributes annotationAttributes() {
		// 使用懒加载模式，首次访问时才创建注解属性
		if (this.annotationAttributes == null) {
			this.annotationAttributes = Ref.of(newAnnotationAttributes());
		}
		return this.annotationAttributes.get();
	}

	/**
	 * 创建新的注解属性实例
	 * <p>
	 * 该方法是注解属性合并的核心实现，负责将匹配注解和所有别名注解的属性合并成一个完整的注解属性。
	 * 合并策略如下：
	 * <br>1. 如果没有别名注解，直接返回匹配注解的属性副本或 null
	 * <br>2. 如果存在匹配注解，以其属性为基础，只从别名注解中获取默认值的成员
	 * <br>3. 如果没有匹配注解，创建基于注解类型的空属性，然后从别名注解中填充
	 * <br>4. 遍历所有别名注解，用非默认值的属性填充待处理的成员
	 * <p>
	 * 合并时遵循"第一个非默认值优先"的原则，即对于每个属性，只接受第一个遇到的非默认值。
	 *
	 * @return 合并后的注解属性实例，如果没有任何匹配的注解且无法从别名注解构建则返回 null
	 */
	@Nullable
	public AnnotationAttributes newAnnotationAttributes() {
		MergedAnnotation matchedAnnotation = this.matchedAnnotation;
		List<MergedAnnotation> aliasAnnotations = this.aliasAnnotations;

		// 如果没有别名注解，则直接返回匹配注解的属性或 null
		if (aliasAnnotations.isEmpty()) {
			if (matchedAnnotation != null) {
				// 有匹配注解，返回其属性的克隆
				return matchedAnnotation.annotationAttributes().clone();
			} else {
				// 没有匹配到注解，返回 null
				return null;
			}
		}

		// 合并直接注解与别名属性，合并时只接受第一个非默认值的别名属性
		AnnotationAttributes annotationAttributes;
		Set<String> pending;
		if (matchedAnnotation != null) {
			// 如果存在匹配的注解，使用其属性作为基础
			annotationAttributes = matchedAnnotation.annotationAttributes().clone();
			Set<String> memberNames = annotationAttributes.getMemberNames();
			if (!memberNames.isEmpty()) {
				pending = new HashSet<>(memberNames);
				// 过滤出默认值的成员，保留需要从别名注解中获取非默认值的成员
				for (Iterator<String> it = pending.iterator(); it.hasNext(); ) {
					String next = it.next();
					AnnotationAttributes.Member member = annotationAttributes.getMember(next);
					if (!member.isDefault()) {
						// 如果不是默认值，则不需要从别名注解中获取
						it.remove();
					}
				}
			} else {
				pending = Collections.emptySet();
			}
		} else {
			// 如果没有匹配的注解，创建基于注解类型的空属性
			annotationAttributes = AnnotationAttributes.of(annotationType);
			Set<String> memberNames = annotationAttributes.getMemberNames();
			if (!memberNames.isEmpty()) {
				pending = new HashSet<>(memberNames);
			} else {
				pending = Collections.emptySet();
			}
		}

		// 用别名注解的值填充待处理的属性
		if (!pending.isEmpty()) {
			// 遍历所有别名注解，尝试填充待处理的属性
			for (Iterator<MergedAnnotation> iter = aliasAnnotations.iterator(); !pending.isEmpty() && iter.hasNext(); ) {
				MergedAnnotation mergedAnnotation = iter.next();
				Map<String, Object> aliasValues = mergedAnnotation.annotationAttributes().asMap();

				// 遍历别名注解的所有属性
				for (Map.Entry<String, Object> entry : aliasValues.entrySet()) {
					String aliasName = entry.getKey();
					Object aliasValue = entry.getValue();

					// 如果这个属性是我们需要的且尚未设置非默认值
					if (pending.contains(aliasName)) {
						if (annotationAttributes.setIfNotDefault(aliasName, aliasValue)) {
							// 成功设置了非默认值，从待处理列表中移除
							pending.remove(aliasName);
						}
					}
				}
			}
		}
		return annotationAttributes;
	}

	/**
	 * 将注解属性转换为实际注解实例
	 * <p>
	 * 该方法用于将合并后的注解属性转换为实际的注解对象。
	 * 首先调用 {@link #annotationAttributes()} 获取合并后的属性，
	 * 然后通过 AnnotationAttributes.asAnnotation() 方法将其转换为注解实例。
	 * 如果属性为 null，则返回 null。
	 *
	 * @return 实际的注解实例，如果注解属性为 null 则返回 null
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public A asAnnotation() {
		// 获取合并后的注解属性
		AnnotationAttributes annotationAttributes = annotationAttributes();
		if (annotationAttributes == null) {
			// 属性为 null，无法转换为注解实例
			return null;
		}
		// 将属性转换为实际的注解类型
		return (A) annotationAttributes.asAnnotation();
	}

	/**
	 * 获取注解类型
	 * <p>
	 * 返回此 MatchedMergedAnnotation 所表示的注解的类型。
	 * 该类型在构造时指定，用于标识要合并的注解类型。
	 *
	 * @return 注解类型，不会是 null
	 */
	@Nonnull
	public Class<A> annotationType() {
		return annotationType;
	}

	/**
	 * 获取匹配的注解
	 * <p>
	 * 返回直接匹配的注解实例。如果不存在直接匹配的注解，则返回 null。
	 * 即使返回 null，也可能存在别名注解，此时会通过别名注解构建最终的注解实例。
	 *
	 * @return 匹配的注解实例，如果不存在直接匹配的注解则返回 null
	 */
	@Nullable
	public MergedAnnotation matchedAnnotation() {
		return matchedAnnotation;
	}

	/**
	 * 获取别名注解列表
	 * <p>
	 * 返回所有别名注解的列表。这些别名注解可能来自元注解上的别名声明，
	 * 或者来自纯别名配置（没有直接匹配的注解，只有别名注解）。
	 * 如果没有任何别名注解，返回空列表而不是 null。
	 *
	 * @return 别名注解列表，不会是 null，可能是空列表
	 */
	@Nonnull
	public List<MergedAnnotation> aliasAnnotations() {
		return aliasAnnotations;
	}
}

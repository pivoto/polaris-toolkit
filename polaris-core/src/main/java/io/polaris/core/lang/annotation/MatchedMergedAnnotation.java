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
 * @author Qt
 * @since Jan 07, 2024
 */

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
	 *
	 * @param annotationType    注解类型
	 * @param matchedAnnotation 匹配的注解
	 * @param aliasAnnotations  别名注解列表
	 */
	public MatchedMergedAnnotation(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation, @Nullable List<MergedAnnotation> aliasAnnotations) {
		this.annotationType = annotationType;
		this.matchedAnnotation = matchedAnnotation;
		this.aliasAnnotations = aliasAnnotations == null ? Collections.emptyList() : aliasAnnotations;
	}

	/**
	 * 创建匹配的合并注解实例
	 *
	 * @param annotationType    注解类型
	 * @param matchedAnnotation 匹配的注解
	 * @param aliasAnnotations  别名注解列表
	 * @param <A>               注解类型泛型
	 * @return MatchedMergedAnnotation实例
	 */
	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation, @Nullable List<MergedAnnotation> aliasAnnotations) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, aliasAnnotations);
	}

	/**
	 * 创建匹配的合并注解实例（无别名注解）
	 *
	 * @param annotationType      注解类型
	 * @param matchedAnnotation   匹配的注解
	 * @param <A>                 注解类型泛型
	 * @return MatchedMergedAnnotation实例
	 */
	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, Collections.emptyList());
	}

	/**
	 * 获取注解属性
	 *
	 * @return 注解属性
	 */
	@Nullable
	public AnnotationAttributes getAnnotationAttributes() {
		if (this.annotationAttributes == null) {
			this.annotationAttributes = Ref.of(newAnnotationAttributes());
		}
		return this.annotationAttributes.get();
	}

	/**
	 * 创建新的注解属性实例
	 *
	 * @return 新的注解属性实例
	 */
	@Nullable
	public AnnotationAttributes newAnnotationAttributes() {
		MergedAnnotation matchedAnnotation = this.matchedAnnotation;
		List<MergedAnnotation> aliasAnnotations = this.aliasAnnotations;

		// 如果没有别名注解，则直接返回匹配注解的属性或null
		if (aliasAnnotations.isEmpty()) {
			if (matchedAnnotation != null) {
				return matchedAnnotation.getAnnotationAttributes().clone();
			} else {
				// 没有匹配到注解
				return null;
			}
		}

		// 合并直接注解与别名属性，合并时只接受第一个非默认值的别名属性
		AnnotationAttributes annotationAttributes;
		Set<String> pending;
		if (matchedAnnotation != null) {
			// 如果存在匹配的注解，使用其属性作为基础
			annotationAttributes = matchedAnnotation.getAnnotationAttributes().clone();
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
			for (Iterator<MergedAnnotation> iter = aliasAnnotations.iterator(); !pending.isEmpty() && iter.hasNext(); ) {
				MergedAnnotation mergedAnnotation = iter.next();
				Map<String, Object> aliasValues = mergedAnnotation.getAnnotationAttributes().asMap();

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
	 *
	 * @return 注解实例
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public A asAnnotation() {
		AnnotationAttributes annotationAttributes = getAnnotationAttributes();
		if (annotationAttributes == null) {
			return null;
		}
		return (A) annotationAttributes.asAnnotation();
	}

	/**
	 * 获取注解类型
	 *
	 * @return 注解类型
	 */
	@Nonnull
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	/**
	 * 获取匹配的注解
	 *
	 * @return 匹配的注解
	 */
	@Nullable
	public MergedAnnotation getMatchedAnnotation() {
		return matchedAnnotation;
	}

	/**
	 * 获取别名注解列表
	 *
	 * @return 别名注解列表
	 */
	@Nonnull
	public List<MergedAnnotation> getAliasAnnotations() {
		return aliasAnnotations;
	}
}

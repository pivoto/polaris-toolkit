package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.LazyRef;
import io.polaris.core.tuple.Ref;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
@ToString
@EqualsAndHashCode
public class MergedAnnotation {
	/** 注解层级 */
	private final int level;
	/** 注解类型 */
	@Nonnull
	private final Class<? extends Annotation> annotationType;
	/** 注解实例 */
	@Nullable
	private final Annotation annotation;
	/** 别名的源注解实例 */
	@Nullable
	private final MergedAnnotation aliasSourceAnnotation;
	/** 别名的源注解属性映射 */
	@Nullable
	private final Map<String, String> aliasSourceMembers;
	/** 关联的可重复配置的目标注解 */
	@Nullable
	private final Class<? extends Annotation> repeatedAnnotationType;
	/** 关联的可重复配置的目标注解实例 */
	@Nullable
	private final MergedAnnotation[] repeatedAnnotations;
	/** 注解属性值 */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final Ref<AnnotationAttributes> annotationAttributes;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final Ref<List<Set<MergedAnnotation>>> hierarchyAnnotations;

	public static MergedAnnotation of(@Nonnull Annotation annotation) {
		return of(0, annotation);
	}

	public static MergedAnnotation of(int level, @Nonnull Annotation annotation) {
		return new MergedAnnotation(level, annotation.annotationType(), annotation, null, null);
	}

	/**
	 * 创建一个合并注解实例
	 *
	 * @param annotationType        注解类型
	 * @param annotation            注解实例
	 * @param aliasSourceAnnotation 别名源注解
	 * @param aliasSourceMembers    别名成员映射, alias-attribute -> source-member
	 * @return 合并注解实例
	 */
	public static MergedAnnotation of(@Nonnull Class<? extends Annotation> annotationType, @Nullable Annotation annotation, @Nonnull MergedAnnotation aliasSourceAnnotation, @Nonnull Map<String, String> aliasSourceMembers) {
		return of(0, annotationType, annotation, aliasSourceAnnotation, aliasSourceMembers);
	}

	/**
	 * 创建一个合并注解实例
	 *
	 * @param level                 注解层级
	 * @param annotationType        注解类型
	 * @param annotation            注解实例
	 * @param aliasSourceAnnotation 别名源注解
	 * @param aliasSourceMembers    别名成员映射, alias-attribute -> source-member
	 * @return 合并注解实例
	 */
	public static MergedAnnotation of(int level, @Nonnull Class<? extends Annotation> annotationType, @Nullable Annotation annotation, @Nonnull MergedAnnotation aliasSourceAnnotation, @Nonnull Map<String, String> aliasSourceMembers) {
		return new MergedAnnotation(level, annotationType, annotation, aliasSourceAnnotation, aliasSourceMembers);
	}


	/**
	 * 构造函数
	 *
	 * @param level                 注解层级
	 * @param annotationType        注解类型
	 * @param annotation            注解实例
	 * @param aliasSourceAnnotation 别名源注解
	 * @param aliasSourceMembers    别名成员映射
	 */
	private MergedAnnotation(int level, @Nonnull Class<? extends Annotation> annotationType, @Nullable Annotation annotation, @Nullable MergedAnnotation aliasSourceAnnotation, @Nullable Map<String, String> aliasSourceMembers) {
		this.level = level;
		this.annotationType = annotationType;
		this.annotation = annotation;
		this.aliasSourceAnnotation = aliasSourceAnnotation;
		this.aliasSourceMembers = aliasSourceMembers;

		// 初始化可重复注解相关字段
		this.repeatedAnnotationType = Annotations.getRepeatedAnnotationType(annotationType);
		if (this.repeatedAnnotationType != null && annotation != null) {
			// 获取可重复注解数组
			Annotation[] annotations = Reflects.invokeQuietly(annotation, annotationType.getDeclaredMethods()[0]);
			if (annotations != null) {
				MergedAnnotation[] repeatedAnnotations = new MergedAnnotation[annotations.length];
				for (int i = 0; i < annotations.length; i++) {
					Annotation repeatedAnnotation = annotations[i];
					repeatedAnnotations[i] = of(this.level + 1, repeatedAnnotation);
				}
				this.repeatedAnnotations = repeatedAnnotations;
			} else {
				this.repeatedAnnotations = null;
			}
		} else {
			this.repeatedAnnotations = null;
		}

		// 延迟初始化层级注解列表
		this.hierarchyAnnotations = LazyRef.of(() -> Collections.unmodifiableList(MergedAnnotations.scanHierarchyAnnotations(this)));

		// 延迟初始化注解属性
		this.annotationAttributes = LazyRef.of(() -> {
			// 根据是否存在注解实例获取基本属性
			AnnotationAttributes annotationAttributes = annotation != null ? AnnotationAttributes.of(annotation) : AnnotationAttributes.of(annotationType);

			// 如果存在别名源注解，则合并别名属性值
			if (aliasSourceAnnotation != null && aliasSourceMembers != null) {
				AnnotationAttributes aliasAnnotationAttributes = aliasSourceAnnotation.annotationAttributes();
				for (Map.Entry<String, String> entry : aliasSourceMembers.entrySet()) {
					// 检查目标注解是否包含别名映射的成员
					if (annotationAttributes.hasMember(entry.getKey())) {
						Object val = aliasAnnotationAttributes.get(entry.getValue());
						if (val != null) {
							// 设置非默认值
							annotationAttributes.setIfNotDefault(entry.getKey(), val);
						}
					}
				}
			}
			return annotationAttributes;
		});
	}

	public int level() {
		return level;
	}

	@Nonnull
	public Class<? extends Annotation> annotationType() {
		return this.annotationType;
	}

	/**
	 * 获取匹配的注解实例
	 * 遍历当前注解及所有层级的注解，寻找与指定类型匹配的注解
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要匹配的注解类型
	 * @return 匹配的注解包装对象，如果未找到匹配的注解则返回null
	 */
	@Nullable
	public <A extends Annotation> MatchedMergedAnnotation<A> getMatchedAnnotation(Class<A> annotationType) {
		MergedAnnotation matchedAnnotation = null;
		List<MergedAnnotation> aliasAnnotations = new ArrayList<>();

		// 检查当前注解是否匹配
		if (this.annotationType == annotationType) {
			if (this.isAliasOnly()) {
				aliasAnnotations.add(this);
			} else {
				matchedAnnotation = this;
			}
		}

		// 遍历层级注解查找匹配项
		List<Set<MergedAnnotation>> hierarchyAnnotations = this.hierarchyAnnotations();
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			for (MergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.annotationType == annotationType) {
					if (mergedAnnotation.isAliasOnly()) {
						// 如果是仅别名注解，添加到别名列表
						aliasAnnotations.add(mergedAnnotation);
					} else {
						// 如果已有主匹配注解，则当前注解作为别名注解；否则设为主匹配注解
						if (matchedAnnotation != null) {
							aliasAnnotations.add(mergedAnnotation);
						} else {
							matchedAnnotation = mergedAnnotation;
						}
					}
				}
			}
		}

		// 如果在当前注解和层级中未找到匹配，在可重复注解中查找
		if (matchedAnnotation == null && aliasAnnotations.isEmpty()) {
			if (this.repeatedAnnotationType != null && this.repeatedAnnotations != null) {
				if (this.repeatedAnnotations.length > 0) {
					return this.repeatedAnnotations[0].getMatchedAnnotation(annotationType);
				}
			}
		}

		// 如果没有找到任何匹配的注解，返回null
		if (matchedAnnotation == null && aliasAnnotations.isEmpty()) {
			return null;
		}

		return MatchedMergedAnnotation.of(annotationType, matchedAnnotation, aliasAnnotations);
	}

	/**
	 * 获取匹配的可重复注解集合
	 * 遍历当前注解及所有层级的注解，收集所有与指定类型匹配的注解
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要匹配的注解类型
	 * @return 匹配的注解包装对象集合，如果没有找到任何匹配的注解则返回null
	 */
	@Nullable
	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> getMatchedRepeatableAnnotation(Class<A> annotationType) {

		Set<MatchedMergedAnnotation<A>> matchedSet = newMatchedSet(annotationType);

		List<Set<MergedAnnotation>> hierarchyAnnotations = this.hierarchyAnnotations();
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			for (MergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.isRepeatable()) {
					Set<MatchedMergedAnnotation<A>> matched1 = mergedAnnotation.getMatchedRepeatableAnnotation(annotationType);
					if (matched1 != null) {
						matchedSet.addAll(matched1);
					}
				} else {
					MatchedMergedAnnotation<A> matched2 = mergedAnnotation.getMatchedAnnotation(annotationType);
					if (matched2 != null) {
						matchedSet.add(matched2);
					}
				}
			}
		}
		if (matchedSet.isEmpty()) {
			return null;
		}
		return matchedSet;
	}

	/**
	 * 获取匹配的最近层级的可重复注解集合
	 * 遍历当前注解及层级注解，只查询最近的一层的可重复注解信息
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要匹配的注解类型
	 * @return 匹配的注解包装对象集合，如果没有找到任何匹配的注解则返回null
	 */
	@Nullable
	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> getTopMatchedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> matchedSet = newMatchedSet(annotationType);

		List<Set<MergedAnnotation>> hierarchyAnnotations = this.hierarchyAnnotations();
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			boolean found = false;
			for (MergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.isRepeatable()) {
					Set<MatchedMergedAnnotation<A>> matched1 = mergedAnnotation.getMatchedRepeatableAnnotation(annotationType);
					if (matched1 != null && !matched1.isEmpty()) {
						matchedSet.addAll(matched1);
						found = true;
					}
				} else {
					MatchedMergedAnnotation<A> matched2 = mergedAnnotation.getMatchedAnnotation(annotationType);
					if (matched2 != null) {
						matchedSet.add(matched2);
						found = true;
					}
				}
			}
			if (found) {
				break;
			}
		}
		if (matchedSet.isEmpty()) {
			return null;
		}
		return matchedSet;
	}

	/**
	 * 创建一个新的匹配注解集合
	 * 根据当前注解是否为可重复注解，初始化包含相关注解的集合
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要匹配的注解类型
	 * @return 新的匹配注解集合
	 */
	@Nonnull
	private <A extends Annotation> Set<MatchedMergedAnnotation<A>> newMatchedSet(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> matchedSet = new LinkedHashSet<>();
		if (this.repeatedAnnotationType != null && this.repeatedAnnotations != null) {
			for (MergedAnnotation repeatedAnnotation : this.repeatedAnnotations) {
				MatchedMergedAnnotation<A> matchedAnnotation = repeatedAnnotation.getMatchedAnnotation(annotationType);
				if (matchedAnnotation != null) {
					matchedSet.add(matchedAnnotation);
				}
			}
		} else {
			// 可重复注解与元注解必然不相同
			if (this.annotationType == annotationType) {
				matchedSet.add(MatchedMergedAnnotation.of(annotationType, this));
			}
		}
		return matchedSet;
	}


	/**
	 * 获取注解的属性
	 *
	 * @return 注解属性
	 */
	public AnnotationAttributes annotationAttributes() {
		return annotationAttributes.get();
	}

	/**
	 * 获取层级注解列表
	 *
	 * @return 层级注解列表
	 */
	public List<Set<MergedAnnotation>> hierarchyAnnotations() {
		return this.hierarchyAnnotations.get();
	}

	/**
	 * 将当前合并注解转换为普通注解实例
	 *
	 * @return 注解实例
	 */
	public Annotation asAnnotation() {
		return annotationAttributes().asAnnotation();
	}

	/**
	 * 判断是否为可重复注解
	 *
	 * @return 如果是可重复注解返回true，否则返回false
	 */
	public boolean isRepeatable() {
		return this.repeatedAnnotationType != null && this.repeatedAnnotations != null;
	}


	/**
	 * 是否仅为别名注解，不含元注解实例
	 */
	public boolean isAliasOnly() {
		return annotation == null && aliasSourceAnnotation != null && aliasSourceMembers != null;
	}

}

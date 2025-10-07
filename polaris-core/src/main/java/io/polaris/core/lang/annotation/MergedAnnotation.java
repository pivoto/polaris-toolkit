package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
@ToString
@EqualsAndHashCode
public class MergedAnnotation {
	/** 注解层级 */
	@Getter
	private final int level;
	/** 注解源元素 */
	@Getter
	private final AnnotatedElement annotatedElement;
	/** 注解类型 */
	@Getter
	private final Class<? extends Annotation> annotationType;
	/** 注解实例 */
	private final Annotation annotation;
	/** 别名的源注解实例 */
	private final MergedAnnotation aliasSourceAnnotation;
	/** 别名的源注解属性映射 */
	private final Map<String, String> aliasSourceMembers;
	/** 关联的可重复配置的目标注解 */
	private final Class<? extends Annotation> repeatedAnnotationType;
	/** 关联的可重复配置的目标注解实例 */
	private final MergedAnnotation[] repeatedAnnotations;
	/** 注解属性值 */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final Ref<AnnotationAttributes> annotationAttributes;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final Ref<List<Set<MergedAnnotation>>> hierarchyAnnotations;

	static MergedAnnotation of(int level, @Nonnull AnnotatedElement annotatedElement, @Nonnull Annotation annotation) {
		return new MergedAnnotation(level, annotatedElement, annotation.annotationType(), annotation, null, null);
	}

	static MergedAnnotation of(int level, AnnotatedElement annotatedElement, @Nonnull Class<? extends Annotation> annotationType, @Nullable Annotation annotation, @Nonnull MergedAnnotation aliasSource, @Nonnull Map<String, String> aliasSourceMembers) {
		return new MergedAnnotation(level, annotatedElement, annotationType, annotation, aliasSource, aliasSourceMembers);
	}


	private MergedAnnotation(int level, @Nonnull AnnotatedElement annotatedElement, @Nonnull Class<? extends Annotation> annotationType, @Nullable Annotation annotation, @Nullable MergedAnnotation aliasSourceAnnotation, @Nullable Map<String, String> aliasSourceMembers) {
		this.level = level;
		this.annotatedElement = annotatedElement;
		this.annotationType = annotationType;
		this.annotation = annotation;
		this.aliasSourceAnnotation = aliasSourceAnnotation;
		this.aliasSourceMembers = aliasSourceMembers;

		this.repeatedAnnotationType = Annotations.getRepeatedAnnotationType(annotationType);
		if (this.repeatedAnnotationType != null && annotation != null) {
			Annotation[] annotations = Reflects.invokeQuietly(annotation, annotationType.getDeclaredMethods()[0]);
			if (annotations != null) {
				MergedAnnotation[] repeatedAnnotations = new MergedAnnotation[annotations.length];
				for (int i = 0; i < annotations.length; i++) {
					Annotation repeatedAnnotation = annotations[i];
					repeatedAnnotations[i] = MergedAnnotation.of(this.level + 1, annotatedElement, repeatedAnnotation);
				}
				this.repeatedAnnotations = repeatedAnnotations;
			} else {
				this.repeatedAnnotations = null;
			}
		} else {
			this.repeatedAnnotations = null;
		}

		this.hierarchyAnnotations = LazyRef.of(() -> Collections.unmodifiableList(MergedAnnotations.scanHierarchyAnnotations(this)));
		this.annotationAttributes = LazyRef.of(() -> {
			AnnotationAttributes annotationAttributes = annotation != null ? AnnotationAttributes.of(annotation) : AnnotationAttributes.of(annotationType);
			if (aliasSourceAnnotation != null && aliasSourceMembers != null) {
				AnnotationAttributes aliasAnnotationAttributes = aliasSourceAnnotation.getAnnotationAttributes();
				for (Map.Entry<String, String> entry : aliasSourceMembers.entrySet()) {
					if (annotationAttributes.hasMember(entry.getKey())) {
						Object val = aliasAnnotationAttributes.get(entry.getValue());
						if (val != null) {
							annotationAttributes.setIfNotDefault(entry.getKey(), val);
						}
					}
				}
			}
			return annotationAttributes;
		});
	}


	public <A extends Annotation> MatchedMergedAnnotation<A> getMatchedAnnotation(Class<A> annotationType) {
		MergedAnnotation matchedAnnotation = null;
		List<MergedAnnotation> aliasAnnotations = new ArrayList<>();

		// 遍历所有层级的注解，第一个匹配的注解作为基础匹配注解，其他的匹配的注解作为别名注解
		if (this.annotationType == annotationType) {
			if (this.isAliasOnly()) {
				aliasAnnotations.add(this);
			} else {
				matchedAnnotation = this;
			}
		}

		List<Set<MergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			for (MergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.annotationType == annotationType) {
					// match
					if (mergedAnnotation.isAliasOnly()) {
						aliasAnnotations.add(mergedAnnotation);
					} else {
						if (matchedAnnotation != null) {
							aliasAnnotations.add(mergedAnnotation);
						} else {
							matchedAnnotation = mergedAnnotation;
						}
					}
				}
			}
		}

		if (matchedAnnotation == null && aliasAnnotations.isEmpty()) {
			if (isRepeatable()) {
				if (this.repeatedAnnotations.length > 0) {
					return this.repeatedAnnotations[0].getMatchedAnnotation(annotationType);
				}
			}
		}
		if (matchedAnnotation == null && aliasAnnotations.isEmpty()) {
			// not match
			return null;
		}
		return MatchedMergedAnnotation.of(annotationType, matchedAnnotation, aliasAnnotations);
	}

	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> getMatchedRepeatableAnnotation(Class<A> annotationType) {

		Set<MatchedMergedAnnotation<A>> matchedSet = newMatchedSet(annotationType);

		List<Set<MergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
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

	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> getTopMatchedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> matchedSet = newMatchedSet(annotationType);

		List<Set<MergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
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

	@Nonnull
	private <A extends Annotation> Set<MatchedMergedAnnotation<A>> newMatchedSet(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> matchedSet = new LinkedHashSet<>();
		if (this.isRepeatable()) {
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


	public AnnotationAttributes getAnnotationAttributes() {
		return annotationAttributes.get();
	}

	public List<Set<MergedAnnotation>> getHierarchyAnnotations() {
		return this.hierarchyAnnotations.get();
	}

	public Annotation asAnnotation() {
		return getAnnotationAttributes().asAnnotation();
	}

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

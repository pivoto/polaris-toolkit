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
@EqualsAndHashCode
@ToString
public class MatchedMergedAnnotation<A extends Annotation> {
	@Nonnull
	private final Class<A> annotationType;
	@Nullable
	private final MergedAnnotation matchedAnnotation;
	@Nonnull
	private final List<MergedAnnotation> aliasAnnotations;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	private transient Ref<AnnotationAttributes> annotationAttributes;

	public MatchedMergedAnnotation(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation, @Nullable List<MergedAnnotation> aliasAnnotations) {
		this.annotationType = annotationType;
		this.matchedAnnotation = matchedAnnotation;
		this.aliasAnnotations = aliasAnnotations == null ? Collections.emptyList() : aliasAnnotations;
	}

	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation, @Nullable List<MergedAnnotation> aliasAnnotations) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, aliasAnnotations);
	}

	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, @Nullable MergedAnnotation matchedAnnotation) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, Collections.emptyList());
	}

	@Nullable
	public AnnotationAttributes getAnnotationAttributes() {
		if (this.annotationAttributes == null) {
			this.annotationAttributes = Ref.of(newAnnotationAttributes());
		}
		return this.annotationAttributes.get();
	}

	@Nullable
	public AnnotationAttributes newAnnotationAttributes() {
		MergedAnnotation matchedAnnotation = this.matchedAnnotation;
		List<MergedAnnotation> aliasAnnotations = this.aliasAnnotations;

		if (aliasAnnotations.isEmpty()) {
			if (matchedAnnotation != null) {
				return matchedAnnotation.getAnnotationAttributes().clone();
			} else {
				// no match
				return null;
			}
		}
		// 合并直接注解与别名属性，合并时只接受第一个非默认值的别名属性
		AnnotationAttributes annotationAttributes;
		Set<String> pending;
		if (matchedAnnotation != null) {
			annotationAttributes = matchedAnnotation.getAnnotationAttributes().clone();
			Set<String> memberNames = annotationAttributes.getMemberNames();
			if (!memberNames.isEmpty()) {
				pending = new HashSet<>(memberNames);
				for (Iterator<String> it = pending.iterator(); it.hasNext(); ) {
					String next = it.next();
					AnnotationAttributes.Member member = annotationAttributes.getMember(next);
					if (!member.isDefault()) {
						it.remove();
					}
				}
			} else {
				pending = Collections.emptySet();
			}
		} else {
			annotationAttributes = AnnotationAttributes.of(annotationType);
			Set<String> memberNames = annotationAttributes.getMemberNames();
			if (!memberNames.isEmpty()) {
				pending = new HashSet<>(memberNames);
			} else {
				pending = Collections.emptySet();
			}
		}
		if (!pending.isEmpty()) {
			for (Iterator<MergedAnnotation> iter = aliasAnnotations.iterator(); !pending.isEmpty() && iter.hasNext(); ) {
				MergedAnnotation mergedAnnotation = iter.next();
				Map<String, Object> aliasValues = mergedAnnotation.getAnnotationAttributes().asMap();
				for (Map.Entry<String, Object> entry : aliasValues.entrySet()) {
					String aliasName = entry.getKey();
					Object aliasValue = entry.getValue();
					if (pending.contains(aliasName)) {
						if (annotationAttributes.setIfNotDefault(aliasName, aliasValue)) {
							pending.remove(aliasName);
						}
					}
				}
			}
		}
		return annotationAttributes;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public A asAnnotation() {
		AnnotationAttributes annotationAttributes = getAnnotationAttributes();
		if (annotationAttributes == null) {
			return null;
		}
		return (A) annotationAttributes.asAnnotation();
	}

	@Nonnull
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Nullable
	public MergedAnnotation getMatchedAnnotation() {
		return matchedAnnotation;
	}

	@Nonnull
	public List<MergedAnnotation> getAliasAnnotations() {
		return aliasAnnotations;
	}
}

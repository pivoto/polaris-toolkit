package io.polaris.core.lang.annotation;

import io.polaris.core.assertion.Assertions;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Jan 07, 2024
 */
@EqualsAndHashCode
@ToString
public class MatchedMergedAnnotation<A extends Annotation> {
	private final Class<A> annotationType;
	private final MergedAnnotation matched;
	private final List<MergedAnnotation> aliases;

	public MatchedMergedAnnotation(@Nonnull Class<A> annotationType, MergedAnnotation matched, List<MergedAnnotation> aliases) {
		Assertions.notNull(annotationType, "annotationType is null");
		this.annotationType = annotationType;
		this.matched = matched;
		this.aliases = aliases == null ? Collections.emptyList() : aliases;
	}

	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, MergedAnnotation matchedAnnotation, List<MergedAnnotation> aliasAnnotations) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, aliasAnnotations);
	}

	public static <A extends Annotation> MatchedMergedAnnotation<A> of(@Nonnull Class<A> annotationType, MergedAnnotation matchedAnnotation) {
		return new MatchedMergedAnnotation<>(annotationType, matchedAnnotation, Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	public A getAnnotation() {
		MergedAnnotation matchedOne = matched;
		List<MergedAnnotation> aliasList = aliases;

		if (aliasList.isEmpty()) {
			if (matchedOne != null) {
				if (!Annotations.hasAliasDefinition(annotationType)) {
					return (A) matchedOne.getAnnotation();
				}
			} else {
				// no match
				return null;
			}
		}

		AnnotationAttributes annotationAttributes;
		if (matchedOne != null) {
			annotationAttributes = AnnotationAttributes.of(matchedOne.getAnnotation());
		} else {
			annotationAttributes = AnnotationAttributes.of(annotationType);
		}
		if (!annotationAttributes.getMemberValues().isEmpty()) {
			Set<String> pending = new HashSet<>(annotationAttributes.getMemberValues().keySet());
			for (Iterator<MergedAnnotation> iter = aliasList.iterator(); !pending.isEmpty() && iter.hasNext(); ) {
				MergedAnnotation mergedAnnotation = iter.next();
				Map<String, Object> aliasValues = mergedAnnotation.getAliasValues();
				for (Map.Entry<String, Object> entry : aliasValues.entrySet()) {
					String aliasName = entry.getKey();
					Object aliasValue = entry.getValue();
					if (pending.contains(aliasName)) {
						if (annotationAttributes.set(aliasName, aliasValue)) {
							pending.remove(aliasName);
						}
					}
				}
			}
		}
		return Annotations.newInstance(annotationType, annotationAttributes.asMap());
	}

	@Nonnull
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Nullable
	public MergedAnnotation getMatched() {
		return matched;
	}

	@Nonnull
	public List<MergedAnnotation> getAliases() {
		return aliases;
	}
}

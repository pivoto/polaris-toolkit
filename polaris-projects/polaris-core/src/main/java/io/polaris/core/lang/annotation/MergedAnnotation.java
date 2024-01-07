package io.polaris.core.lang.annotation;

import io.polaris.core.reflect.Reflects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Jan 06, 2024
 */
@Getter
@ToString
@EqualsAndHashCode
public class MergedAnnotation {

	private final int distance;
	private final AnnotatedElement annotatedElement;
	private final Annotation annotation;
	private final Class<? extends Annotation> annotationType;
	private final MergedAnnotation aliasSource;
	private final Map<String, Method> aliasMethods;
	private final Class<? extends Annotation> repeatedAnnotationType;
	private final MergedAnnotation[] repeatedAnnotations;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	private SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations;

	static MergedAnnotation of(int distance, @Nonnull AnnotatedElement annotatedElement, @Nonnull Annotation annotation) {
		MergedAnnotation mergedAnnotation = new MergedAnnotation(distance, annotatedElement, annotation.annotationType(), annotation, null, null);
		return mergedAnnotation;
	}

	static MergedAnnotation of(int distance, AnnotatedElement annotatedElement, @Nonnull Class<? extends Annotation> annotationType, @Nonnull MergedAnnotation aliasSource, @Nonnull Map<String, Method> aliasMethods) {
		MergedAnnotation mergedAnnotation = new MergedAnnotation(distance, annotatedElement, annotationType, null, aliasSource, aliasMethods);
		return mergedAnnotation;
	}


	private MergedAnnotation(int distance, AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType, Annotation annotation, MergedAnnotation aliasSource, Map<String, Method> aliasMethods) {
		this.distance = distance;
		this.annotatedElement = annotatedElement;
		this.annotationType = annotationType;
		this.aliasSource = aliasSource;
		this.aliasMethods = aliasMethods;

		if (aliasSource != null && aliasMethods != null) {
			this.repeatedAnnotationType = null;
			this.repeatedAnnotations = null;
			this.annotation = null;
		} else {
			this.annotation = annotation;

			this.repeatedAnnotationType = Annotations.getRepeatedAnnotationType(annotationType);
			if (this.repeatedAnnotationType != null) {
				Annotation[] annotations = Reflects.invokeQuietly(annotation, annotationType.getDeclaredMethods()[0]);
				MergedAnnotation[] repeatedAnnotations = new MergedAnnotation[annotations.length];
				for (int i = 0; i < annotations.length; i++) {
					Annotation repeatedAnnotation = annotations[i];
					repeatedAnnotations[i] = MergedAnnotation.of(this.distance + 1, annotatedElement, repeatedAnnotation);
				}
				this.repeatedAnnotations = repeatedAnnotations;
			} else {
				this.repeatedAnnotations = null;
			}
		}
	}


	public <A extends Annotation> MatchedMergedAnnotation<A> getMatchedAnnotation(Class<A> annotationType) {
		if (!this.isAlias() && this.annotationType == annotationType) {
			return MatchedMergedAnnotation.of(annotationType, this);
		}

		MergedAnnotation matchedOne = null;
		List<MergedAnnotation> aliasList = new ArrayList<>();

		SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
		loop:
		for (Map.Entry<Integer, Set<MergedAnnotation>> entry : hierarchyAnnotations.entrySet()) {
			Set<MergedAnnotation> set = entry.getValue();
			for (MergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.annotationType == annotationType) {
					// match
					if (mergedAnnotation.isAlias()) {
						aliasList.add(mergedAnnotation);
					} else {
						matchedOne = mergedAnnotation;
						break loop;
					}
				}
			}
		}

		if (matchedOne == null && aliasList.isEmpty()) {
			if (isRepeatable()) {
				if (this.repeatedAnnotations.length > 0) {
					MatchedMergedAnnotation<A> matchedAnnotation = this.repeatedAnnotations[0].getMatchedAnnotation(annotationType);
					if (matchedAnnotation != null) {
						matchedOne = matchedAnnotation.getMatched();
						aliasList.addAll(matchedAnnotation.getAliases());
					}
				}
			}
		}
		if (matchedOne == null && aliasList.isEmpty()) {
			// not match
			return null;
		}
		return MatchedMergedAnnotation.of(annotationType, matchedOne, aliasList);
	}

	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> getMatchedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> matchedSet = new LinkedHashSet<>();
		if (this.isRepeatable()) {
			for (MergedAnnotation repeatedAnnotation : this.repeatedAnnotations) {
				MatchedMergedAnnotation<A> matchedAnnotation = repeatedAnnotation.getMatchedAnnotation(annotationType);
				if (matchedAnnotation != null) {
					matchedSet.add(matchedAnnotation);
				}
			}
		}
		if (!this.isAlias() && this.annotationType == annotationType) {
			matchedSet.add(MatchedMergedAnnotation.of(annotationType, this));
		}

		SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
		for (Map.Entry<Integer, Set<MergedAnnotation>> entry : hierarchyAnnotations.entrySet()) {
			Set<MergedAnnotation> set = entry.getValue();
			for (MergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.isRepeatable()) {
					Set<MatchedMergedAnnotation<A>> matched1 = mergedAnnotation.getMatchedRepeatableAnnotation(annotationType);
					if (matched1 != null) {
						matchedSet.addAll(matched1);
					}
				}else{
					MatchedMergedAnnotation<A> matched2 = mergedAnnotation.getMatchedAnnotation(annotationType);
					if (matched2 != null) {
						matchedSet.add(matched2);
					}
				}
			}
		}
		if (matchedSet.isEmpty()){
			return null;
		}
		return matchedSet;
	}

	public SortedMap<Integer, Set<MergedAnnotation>> getHierarchyAnnotations() {
		if (this.hierarchyAnnotations == null) {
			this.hierarchyAnnotations = Collections.unmodifiableSortedMap(MergedAnnotations.scanHierarchyAnnotations(this));
		}
		return this.hierarchyAnnotations;
	}

	public Annotation getAnnotation() {
		if (this.annotation != null) {
			return this.annotation;
		}
		return AnnotationAttributes.of(this.annotationType).asAnnotation();
	}

	public boolean isRepeatable() {
		return this.repeatedAnnotationType != null && this.repeatedAnnotations != null;
	}


	public boolean isAlias() {
		return aliasSource != null && aliasMethods != null;
	}

	public Map<String, Object> getAliasValues() {
		Map<String, Object> aliasValues = new HashMap<>();
		if (aliasSource.isAlias()) {
			AnnotationAttributes aliasAnnotationAttributes = aliasSource.getAliasAnnotationAttributes();
			for (Map.Entry<String, Method> entry : aliasMethods.entrySet()) {
				aliasValues.put(entry.getKey(), aliasAnnotationAttributes.get(entry.getValue().getName()));
			}
		} else {
			for (Map.Entry<String, Method> entry : aliasMethods.entrySet()) {
				aliasValues.put(entry.getKey(), Reflects.invokeQuietly(aliasSource.getAnnotation(), entry.getValue()));
			}
		}
		return aliasValues;
	}


	AnnotationAttributes getAliasAnnotationAttributes() {
		Map<String, Object> aliasValues = getAliasValues();
		AnnotationAttributes attributes = AnnotationAttributes.of(annotationType);
		attributes.set(aliasValues);
		return attributes;
	}

}

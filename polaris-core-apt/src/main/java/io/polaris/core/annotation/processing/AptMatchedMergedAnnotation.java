package io.polaris.core.annotation.processing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;


/**
 * @author Qt
 * @since Oct 07, 2025
 */
@EqualsAndHashCode
@ToString
public class AptMatchedMergedAnnotation {
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private final ProcessingEnvironment env;
	private final TypeElement annotationType;
	private final AptMergedAnnotation matchedAnnotation;
	private final List<AptMergedAnnotation> aliasAnnotations;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	private transient AptAnnotationAttributes annotationAttributes;

	public AptMatchedMergedAnnotation(ProcessingEnvironment env, TypeElement annotationType, AptMergedAnnotation matchedAnnotation, List<AptMergedAnnotation> aliasAnnotations) {
		this.env = env;
		this.annotationType = annotationType;
		this.matchedAnnotation = matchedAnnotation;
		this.aliasAnnotations = aliasAnnotations == null ? Collections.emptyList() : aliasAnnotations;
	}

	public static AptMatchedMergedAnnotation of(ProcessingEnvironment env,TypeElement annotationType, AptMergedAnnotation matchedAnnotation, List<AptMergedAnnotation> aliasAnnotations) {
		return new AptMatchedMergedAnnotation(env, annotationType, matchedAnnotation, aliasAnnotations);
	}

	public static AptMatchedMergedAnnotation of(ProcessingEnvironment env,TypeElement annotationType, AptMergedAnnotation matchedAnnotation) {
		return new AptMatchedMergedAnnotation(env, annotationType, matchedAnnotation, Collections.emptyList());
	}


	public AptAnnotationAttributes getAnnotationAttributes() {
		if (this.annotationAttributes == null) {
			this.annotationAttributes = newAnnotationAttributes();
		}
		return this.annotationAttributes;
	}


	public AptAnnotationAttributes newAnnotationAttributes() {
		AptMergedAnnotation matchedAnnotation = this.matchedAnnotation;
		List<AptMergedAnnotation> aliasAnnotations = this.aliasAnnotations;

		if (aliasAnnotations.isEmpty()) {
			if (matchedAnnotation != null) {
				return matchedAnnotation.getAnnotationAttributes().clone();
			} else {
				// no match
				return null;
			}
		}
		// 合并直接注解与别名属性，合并时只接受第一个非默认值的别名属性
		AptAnnotationAttributes annotationAttributes;
		Set<String> pending;
		if (matchedAnnotation != null) {
			annotationAttributes = matchedAnnotation.getAnnotationAttributes().clone();
			Set<String> memberNames = annotationAttributes.getMemberNames();
			if (!memberNames.isEmpty()) {
				pending = new HashSet<>(memberNames);
				for (Iterator<String> it = pending.iterator(); it.hasNext(); ) {
					String next = it.next();
					AptAnnotationAttributes.Member member = annotationAttributes.getMember(next);
					if (!member.isDefault()) {
						it.remove();
					}
				}
			} else {
				pending = Collections.emptySet();
			}
		} else {
			annotationAttributes = AptAnnotationAttributes.of(env,annotationType);
			Set<String> memberNames = annotationAttributes.getMemberNames();
			if (!memberNames.isEmpty()) {
				pending = new HashSet<>(memberNames);
			} else {
				pending = Collections.emptySet();
			}
		}
		if (!pending.isEmpty()) {
			for (Iterator<AptMergedAnnotation> iter = aliasAnnotations.iterator(); !pending.isEmpty() && iter.hasNext(); ) {
				AptMergedAnnotation mergedAnnotation = iter.next();
				Map<String, AnnotationValue> aliasValues = mergedAnnotation.getAnnotationAttributes().asMap();
				for (Map.Entry<String, AnnotationValue> entry : aliasValues.entrySet()) {
					String aliasName = entry.getKey();
					AnnotationValue aliasValue = entry.getValue();
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


	public TypeElement getAnnotationType() {
		return annotationType;
	}


	public AptMergedAnnotation getMatchedAnnotation() {
		return matchedAnnotation;
	}


	public List<AptMergedAnnotation> getAliasAnnotations() {
		return aliasAnnotations;
	}
}

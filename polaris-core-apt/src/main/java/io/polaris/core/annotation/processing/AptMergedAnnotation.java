package io.polaris.core.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import io.polaris.core.lang.annotation.Alias;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
@ToString
@EqualsAndHashCode
public class AptMergedAnnotation {
	@Getter
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private final ProcessingEnvironment env;
	/** 注解层级 */
	@Getter
	private final int level;
	/** 注解类型 */
	@Getter
	private final TypeElement annotationType;
	/** 注解实例 */
	private final AnnotationMirror annotation;
	/** 别名的源注解实例 */
	private final AptMergedAnnotation aliasSourceAnnotation;
	/** 别名的源注解属性映射 */
	private final Map<String, String> aliasSourceMembers;
	/** 关联的可重复配置的目标注解 */
	private final TypeElement repeatedAnnotationType;
	/** 关联的可重复配置的目标注解实例 */
	private final AptMergedAnnotation[] repeatedAnnotations;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private AptAnnotationAttributes annotationAttributes;
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Set<AptMergedAnnotation>> hierarchyAnnotations;

	static AptMergedAnnotation of(ProcessingEnvironment env, int level, AnnotationMirror annotation) {
		return new AptMergedAnnotation(env, level, (TypeElement) annotation.getAnnotationType().asElement(), annotation, null, null);
	}

	static AptMergedAnnotation of(ProcessingEnvironment env, int level, TypeElement annotationType, AnnotationMirror annotation, AptMergedAnnotation aliasSource, Map<String, String> aliasSourceMembers) {
		return new AptMergedAnnotation(env, level, annotationType, annotation, aliasSource, aliasSourceMembers);
	}

	private AptMergedAnnotation(ProcessingEnvironment env, int level, TypeElement annotationType, AnnotationMirror annotation, AptMergedAnnotation aliasSourceAnnotation, Map<String, String> aliasSourceMembers) {

		this.env = env;
		this.level = level;
		this.annotationType = annotationType;
		this.annotation = annotation;
		this.aliasSourceAnnotation = aliasSourceAnnotation;
		this.aliasSourceMembers = aliasSourceMembers;
		this.repeatedAnnotationType = AptAnnotations.getRepeatedAnnotationType(annotationType);
		if (this.repeatedAnnotationType != null && annotation != null) {

			List<? extends AnnotationValue> arrayValues = null;
			try {
				ExecutableElement method = (ExecutableElement) annotationType.getEnclosedElements().get(0);
				AnnotationValue annotationValue = annotation.getElementValues().get(method);
				Object valueObject = annotationValue.getValue();
				if (valueObject instanceof List) {
					arrayValues = (List<? extends AnnotationValue>) valueObject;
				}
			} catch (Throwable ignore) {
			}

			if (arrayValues != null) {
				List<AptMergedAnnotation> repeatedAnnotations = new ArrayList<>(arrayValues.size());
				for (AnnotationValue arrayItem : arrayValues) {
					if (arrayItem instanceof AnnotationMirror) {
						repeatedAnnotations.add(AptMergedAnnotation.of(this.env, this.level + 1, (AnnotationMirror) arrayItem));
					}
				}
				this.repeatedAnnotations = repeatedAnnotations.toArray(new AptMergedAnnotation[0]);
			} else {
				this.repeatedAnnotations = null;
			}
		} else {
			this.repeatedAnnotations = null;
		}
	}

	public AptMatchedMergedAnnotation getMatchedAnnotation(TypeElement annotationType) {
		AptMergedAnnotation matchedAnnotation = null;
		List<AptMergedAnnotation> aliasAnnotations = new ArrayList<>();

		// 遍历所有层级的注解，第一个匹配的注解作为基础匹配注解，其他的匹配的注解作为别名注解
		if (env.getTypeUtils().isSameType(this.annotationType.asType(), annotationType.asType())) {
			if (this.isAliasOnly()) {
				aliasAnnotations.add(this);
			} else {
				matchedAnnotation = this;
			}
		}

		List<Set<AptMergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
		for (Set<AptMergedAnnotation> set : hierarchyAnnotations) {
			for (AptMergedAnnotation mergedAnnotation : set) {
				if (env.getTypeUtils().isSameType(mergedAnnotation.annotationType.asType(), annotationType.asType())) {
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
		return AptMatchedMergedAnnotation.of(env, annotationType, matchedAnnotation, aliasAnnotations);
	}

	public Set<AptMatchedMergedAnnotation> getMatchedRepeatableAnnotation(TypeElement annotationType) {

		Set<AptMatchedMergedAnnotation> matchedSet = newMatchedSet(annotationType);

		List<Set<AptMergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
		for (Set<AptMergedAnnotation> set : hierarchyAnnotations) {
			for (AptMergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.isRepeatable()) {
					Set<AptMatchedMergedAnnotation> matched1 = mergedAnnotation.getMatchedRepeatableAnnotation(annotationType);
					if (matched1 != null) {
						matchedSet.addAll(matched1);
					}
				} else {
					AptMatchedMergedAnnotation matched2 = mergedAnnotation.getMatchedAnnotation(annotationType);
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

	public Set<AptMatchedMergedAnnotation> getTopMatchedRepeatableAnnotation(TypeElement annotationType) {
		Set<AptMatchedMergedAnnotation> matchedSet = newMatchedSet(annotationType);

		List<Set<AptMergedAnnotation>> hierarchyAnnotations = this.getHierarchyAnnotations();
		for (Set<AptMergedAnnotation> set : hierarchyAnnotations) {
			boolean found = false;
			for (AptMergedAnnotation mergedAnnotation : set) {
				if (mergedAnnotation.isRepeatable()) {
					Set<AptMatchedMergedAnnotation> matched1 = mergedAnnotation.getMatchedRepeatableAnnotation(annotationType);
					if (matched1 != null && !matched1.isEmpty()) {
						matchedSet.addAll(matched1);
						found = true;
					}
				} else {
					AptMatchedMergedAnnotation matched2 = mergedAnnotation.getMatchedAnnotation(annotationType);
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

	private Set<AptMatchedMergedAnnotation> newMatchedSet(TypeElement annotationType) {
		Set<AptMatchedMergedAnnotation> matchedSet = new LinkedHashSet<>();
		if (this.isRepeatable()) {
			for (AptMergedAnnotation repeatedAnnotation : this.repeatedAnnotations) {
				AptMatchedMergedAnnotation matchedAnnotation = repeatedAnnotation.getMatchedAnnotation(annotationType);
				if (matchedAnnotation != null) {
					matchedSet.add(matchedAnnotation);
				}
			}
		} else {
			// 可重复注解与元注解必然不相同
			if (this.annotationType == annotationType) {
				matchedSet.add(AptMatchedMergedAnnotation.of(env, annotationType, this));
			}
		}
		return matchedSet;
	}

	public AptAnnotationAttributes getAnnotationAttributes() {
		if (this.annotationAttributes == null) {
			AptAnnotationAttributes annotationAttributes = annotation != null ? AptAnnotationAttributes.of(env, annotation) : AptAnnotationAttributes.of(env, annotationType);
			if (aliasSourceAnnotation != null && aliasSourceMembers != null) {
				AptAnnotationAttributes aliasAnnotationAttributes = aliasSourceAnnotation.getAnnotationAttributes();
				for (Map.Entry<String, String> entry : aliasSourceMembers.entrySet()) {
					if (annotationAttributes.hasMember(entry.getKey())) {
						AnnotationValue val = aliasAnnotationAttributes.get(entry.getValue());
						if (val != null) {
							annotationAttributes.setIfNotDefault(entry.getKey(), val);
						}
					}
				}
			}
			this.annotationAttributes = annotationAttributes;
		}
		return this.annotationAttributes;
	}

	public List<Set<AptMergedAnnotation>> getHierarchyAnnotations() {
		if (this.hierarchyAnnotations == null) {
			this.hierarchyAnnotations = scanHierarchyAnnotations(this);
		}
		return this.hierarchyAnnotations;
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


	/**
	 * 扫描注解的层次结构
	 *
	 * @param annotation 合并后的注解
	 * @return 按距离排序的注解映射
	 */
	private List<Set<AptMergedAnnotation>> scanHierarchyAnnotations(AptMergedAnnotation annotation) {
		List<Set<AptMergedAnnotation>> hierarchyAnnotations = new ArrayList<>();
		Set<TypeElement> visitedAnnotation = new HashSet<>();
		Collection<AptMergedAnnotation> candidates = Collections.singletonList(annotation);
		while (!candidates.isEmpty()) {
			candidates = scanHierarchyAnnotation(hierarchyAnnotations, candidates, visitedAnnotation);
		}
		return hierarchyAnnotations;
	}

	/**
	 * 将合并后的注解添加到层次结构映射中
	 *
	 * @param hierarchyAnnotations 层次结构注解映射
	 * @param mergedAnnotation     合并后的注解
	 */
	private void addHierarchyAnnotation(List<Set<AptMergedAnnotation>> hierarchyAnnotations, AptMergedAnnotation mergedAnnotation) {
		int level = mergedAnnotation.getLevel();
		while (hierarchyAnnotations.size() <= level) {
			hierarchyAnnotations.add(new LinkedHashSet<>());
		}
		hierarchyAnnotations.get(level).add(mergedAnnotation);
	}


	/**
	 * 扫描注解的层次结构
	 *
	 * @param hierarchyAnnotations 层次结构注解映射
	 * @param lastCandidates       上一轮的候选注解集合
	 * @param visited              已访问的注解类型集合
	 * @return 下一轮的候选注解集合
	 */
	private Collection<AptMergedAnnotation> scanHierarchyAnnotation(List<Set<AptMergedAnnotation>> hierarchyAnnotations, Collection<AptMergedAnnotation> lastCandidates, Set<TypeElement> visited) {
		Collection<AptMergedAnnotation> candidates = new LinkedHashSet<>();
		for (AptMergedAnnotation mergedAnnotation : lastCandidates) {
			TypeElement annotationType = mergedAnnotation.getAnnotationType();
			String packageName = env.getElementUtils().getPackageOf(annotationType).getQualifiedName().toString();
			if (packageName.equals("java.lang.annotation")) {
				continue;
			}
			if (visited.contains(annotationType)) {
				continue;
			}
			visited.add(annotationType);

			Map<TypeElement, Map<String, String>> aliasMap = null;
			if (!mergedAnnotation.isRepeatable()) {
				// alias
				ExecutableElement[] annotationMembers = AptAnnotationAttributes.getAnnotationMembers(annotationType);
				aliasMap = new LinkedHashMap<>();

				TypeMirror defaultAnnotationType = env.getElementUtils().getTypeElement(Annotation.class.getCanonicalName()).asType();
				for (ExecutableElement method : annotationMembers) {
					List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
					if (annotationMirrors!=null) {
						for (AnnotationMirror annotationMirror : annotationMirrors) {
							TypeElement element = (TypeElement) annotationMirror.getAnnotationType().asElement();
							if (element.getQualifiedName().toString().equals(Alias.class.getCanonicalName())) {
								Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
								String value = null;
								DeclaredType annotation = null;
								for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
									ExecutableElement key = entry.getKey();
									if (key.getSimpleName().toString().equals("annotation")){
										Object v = entry.getValue().getValue();
										if (v instanceof DeclaredType) {
											annotation = (DeclaredType) v;
										}
									}else if (key.getSimpleName().toString().equals("value")){
										Object v = entry.getValue().getValue();
										if (v instanceof String) {
											value = (String) v;
										}
									}
								}
								if (annotation != null && value != null){
									if (!AptAnnotations.equals(env,annotation, annotationType.asType())
									&& !AptAnnotations.equals(env,annotation, defaultAnnotationType)) {
										Map<String, String> aliasMethods = aliasMap.computeIfAbsent((TypeElement)annotation.asElement(), k -> new LinkedHashMap<>());
										aliasMethods.putIfAbsent(value, method.getSimpleName().toString());
									}
								}
							}
						}
					}

					/* Alias alias = method.getAnnotation(Alias.class);
					if (alias != null && !alias.annotation().getCanonicalName().equals(annotationType.getQualifiedName().toString()) && alias.annotation() != Annotation.class) {
						Map<String, String> aliasMethods = aliasMap.computeIfAbsent(env.getElementUtils().getTypeElement(alias.annotation().getCanonicalName()), k -> new LinkedHashMap<>());
						aliasMethods.putIfAbsent(alias.value(), method.getSimpleName().toString());
					} */
				}
			}
			List<? extends AnnotationMirror> annotationMirrors = annotationType.getAnnotationMirrors();
			if (annotationMirrors != null) {
				for (AnnotationMirror annotation : annotationMirrors) {
					// 合并元注解与别名注解方法
					Map<String, String> aliasMethods;
					if (aliasMap == null || aliasMap.isEmpty()) {aliasMethods = null;} else {
						aliasMethods = aliasMap.remove((TypeElement) (annotation.getAnnotationType().asElement()));
					}
					if (aliasMethods != null) {
						AptMergedAnnotation aliasMergeAnnotation = AptMergedAnnotation.of(mergedAnnotation.getEnv(), mergedAnnotation.getLevel() + 1, (TypeElement) (annotation.getAnnotationType().asElement()), annotation, mergedAnnotation, aliasMethods);
						addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
						candidates.add(aliasMergeAnnotation);
					} else {
						AptMergedAnnotation relation = AptMergedAnnotation.of(mergedAnnotation.getEnv(), mergedAnnotation.getLevel() + 1, annotation);
						addHierarchyAnnotation(hierarchyAnnotations, relation);
						candidates.add(relation);
					}
				}
			}
			// 处理无元注解的别名注解方法
			if (aliasMap != null && !aliasMap.isEmpty()) {
				for (Map.Entry<TypeElement, Map<String, String>> entry : aliasMap.entrySet()) {
					TypeElement aliasAnnotationType = entry.getKey();
					Map<String, String> aliasMethods = entry.getValue();

					AptMergedAnnotation aliasMergeAnnotation = AptMergedAnnotation.of(mergedAnnotation.getEnv(), mergedAnnotation.getLevel() + 1, aliasAnnotationType, null, mergedAnnotation, aliasMethods);
					addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
					candidates.add(aliasMergeAnnotation);
				}
			}
		}
		return candidates;
	}
}

package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
public class MergedAnnotations {

	@Nullable
	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getMergedAnnotation(annotationType);
	}

	@Nonnull
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getMergedRepeatableAnnotation(annotationType);
	}

	@Nonnull
	public static <A extends Annotation> Set<A> getTopMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getTopMergedRepeatableAnnotation(annotationType);
	}

	/**
	 * 扫描注解的层次结构
	 *
	 * @param annotation 合并后的注解
	 * @return 按距离排序的注解映射
	 */
	static List<Set<MergedAnnotation>> scanHierarchyAnnotations(MergedAnnotation annotation) {
		List<Set<MergedAnnotation>> hierarchyAnnotations = new ArrayList<>();
		Set<Class<? extends Annotation>> visitedAnnotation = new HashSet<>();
		Collection<MergedAnnotation> candidates = Collections.singletonList(annotation);
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
	private static void addHierarchyAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations, MergedAnnotation mergedAnnotation) {
		int level = mergedAnnotation.level();
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
	private static Collection<MergedAnnotation> scanHierarchyAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations, Collection<MergedAnnotation> lastCandidates, Set<Class<? extends Annotation>> visited) {
		Collection<MergedAnnotation> candidates = new LinkedHashSet<>();
		for (MergedAnnotation mergedAnnotation : lastCandidates) {
			Class<? extends Annotation> annotationType = mergedAnnotation.annotationType();
			if (annotationType.getPackage().getName().equals("java.lang.annotation")) {
				continue;
			}
			if (visited.contains(annotationType)) {
				continue;
			}
			visited.add(annotationType);

			// alias-attribute -> source-member
			Map<Class<? extends Annotation>, Map<String, String>> aliasSourceMembersMap = null;
			if (!mergedAnnotation.isRepeatable()) {
				// 除可重复注解外，扫描别名属性
				Method[] annotationMembers = AnnotationAttributes.getAnnotationMembers(annotationType);
				aliasSourceMembersMap = new LinkedHashMap<>();

				for (Method method : annotationMembers) {
					Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(method);
					if (aliasAttributes != null) {
						for (AliasAttribute alias : aliasAttributes) {
							if (alias != null && alias.annotationType() != annotationType && alias.annotationType() != Annotation.class) {
								Map<String, String> aliasSourceMembers = aliasSourceMembersMap.computeIfAbsent(alias.annotationType(), k -> new LinkedHashMap<>());
								aliasSourceMembers.putIfAbsent(alias.attribute(), method.getName());
							}
						}
					}
				}
			}

			for (Annotation annotation : annotationType.getAnnotations()) {
				// 合并元注解与别名注解方法
				Map<String, String> aliasSourceMembers = aliasSourceMembersMap == null || aliasSourceMembersMap.isEmpty() ? null : aliasSourceMembersMap.remove(annotation.annotationType());
				if (aliasSourceMembers != null) {
					Class<? extends Annotation> annotationType1 = annotation.annotationType();
					MergedAnnotation aliasMergeAnnotation = MergedAnnotation.of(mergedAnnotation.level() + 1, annotationType1, annotation, mergedAnnotation, aliasSourceMembers);
					addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
					candidates.add(aliasMergeAnnotation);
				} else {
					MergedAnnotation relation = MergedAnnotation.of(mergedAnnotation.level() + 1, annotation);
					addHierarchyAnnotation(hierarchyAnnotations, relation);
					candidates.add(relation);
				}
			}
			// 处理无元注解的别名注解方法
			if (aliasSourceMembersMap != null && !aliasSourceMembersMap.isEmpty()) {
				for (Map.Entry<Class<? extends Annotation>, Map<String, String>> entry : aliasSourceMembersMap.entrySet()) {
					Class<? extends Annotation> aliasAnnotationType = entry.getKey();
					Map<String, String> aliasMethods = entry.getValue();

					MergedAnnotation aliasMergeAnnotation = MergedAnnotation.of(mergedAnnotation.level() + 1, aliasAnnotationType, null, mergedAnnotation, aliasMethods);
					addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
					candidates.add(aliasMergeAnnotation);
				}
			}
		}
		return candidates;
	}

}

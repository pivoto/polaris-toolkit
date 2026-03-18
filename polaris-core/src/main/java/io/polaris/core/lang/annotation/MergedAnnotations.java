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
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
public class MergedAnnotations {


	/**
	 * 获取指定元素上的合并注解
	 * <p>
	 * 该方法用于获取经过合并处理后的注解实例，支持注解的层次结构查找和属性合并。
	 * 通过 HierarchyMergedAnnotation 类实现完整的注解合并逻辑，包括元注解和别名处理。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解实例，如果未找到则返回 null
	 */
	@Nullable
	public static <A extends Annotation> A getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getMergedAnnotation(annotationType);
	}

	/**
	 * 获取指定元素上的合并注解属性
	 * <p>
	 * 该方法用于获取经过合并处理后的注解属性，支持注解的层次结构查找和属性合并。
	 * 通过 HierarchyMergedAnnotation 类实现完整的注解合并逻辑，包括元注解和别名处理。
	 * 返回的是 AnnotationAttributes 对象，可以方便地访问注解的各种属性值。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解属性，如果未找到则返回 null
	 */
	@Nullable
	public static <A extends Annotation> AnnotationAttributes getMergedAnnotationAttributes(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getMergedAnnotationAttributes(annotationType);
	}

	/**
	 * 获取指定元素上的合并可重复注解集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解集合，支持在注解层次结构中查找所有匹配的可重复注解。
	 * 通过 HierarchyMergedAnnotation 类实现完整的注解合并逻辑，包括元注解和别名处理。
	 * 返回的集合包含所有层级中找到的可重复注解实例。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 合并后的可重复注解集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<A> getMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getMergedRepeatableAnnotation(annotationType);
	}

	/**
	 * 获取指定元素上的合并可重复注解属性集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解属性集合，支持在注解层次结构中查找所有匹配的可重复注解。
	 * 通过 HierarchyMergedAnnotation 类实现完整的注解合并逻辑，包括元注解和别名处理。
	 * 返回的集合包含所有层级中找到的可重复注解的 AnnotationAttributes 对象。
	 * 与 {@link #getMergedRepeatableAnnotation(AnnotatedElement, Class)} 不同，该方法返回的是注解属性而非注解实例。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解属性集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<AnnotationAttributes> getMergedRepeatableAnnotationAttributes(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getMergedRepeatableAnnotationAttributes(annotationType);
	}

	/**
	 * 获取指定元素上的顶层合并可重复注解集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解集合，但只返回最顶层（最近距离）的注解实例。
	 * 与 {@link #getMergedRepeatableAnnotation(AnnotatedElement, Class)} 不同，该方法不会返回继承层级中的所有注解，
	 * 而是只返回距离元素最近的注解层中的可重复注解。
	 * 通过 HierarchyMergedAnnotation 类实现完整的注解合并逻辑，包括元注解和别名处理。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 顶层合并后的可重复注解集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<A> getTopMergedRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getTopMergedRepeatableAnnotation(annotationType);
	}

	/**
	 * 获取指定元素上的顶层合并可重复注解属性集合
	 * <p>
	 * 该方法用于获取经过合并处理后的可重复注解属性集合，但只返回最顶层（最近距离）的注解实例的属性。
	 * 与 {@link #getMergedRepeatableAnnotationAttributes(AnnotatedElement, Class)} 不同，该方法不会返回继承层级中的所有注解属性，
	 * 而是只返回距离元素最近的注解层中的可重复注解属性。
	 * 通过 HierarchyMergedAnnotation 类实现完整的注解合并逻辑，包括元注解和别名处理。
	 * 返回的是 AnnotationAttributes 集合，可以方便地访问每个注解的各种属性值。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 顶层合并后的注解属性集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> Set<AnnotationAttributes> getTopMergedRepeatableAnnotationAttributes(AnnotatedElement element, Class<A> annotationType) {
		return HierarchyMergedAnnotation.of(element).getTopMergedRepeatableAnnotationAttributes(annotationType);
	}

	/**
	 * 查找指定元素上满足条件的合并注解属性
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并注解属性。
	 * 委托给 {@link HierarchyMergedAnnotation#findMergedAnnotationAttributes(Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的合并注解属性。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并注解属性；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, AnnotationAttributes> findMergedAnnotationAttributes(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return HierarchyMergedAnnotation.of(element).findMergedAnnotationAttributes(predicate);
	}

	/**
	 * 查找指定元素上满足条件的合并注解
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并注解实例。
	 * 委托给 {@link HierarchyMergedAnnotation#findMergedAnnotation(Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的合并注解实例。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并注解实例；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Annotation> findMergedAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return HierarchyMergedAnnotation.of(element).findMergedAnnotation(predicate);
	}

	/**
	 * 查找指定元素上满足条件的合并可重复注解属性集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并可重复注解属性集合。
	 * 委托给 {@link HierarchyMergedAnnotation#findMergedRepeatableAnnotationAttributes(Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的所有合并可重复注解属性。
	 * 即使某个注解类型没有可重复注解，也会在结果中包含该类型（值为空集合）。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并可重复注解属性集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<AnnotationAttributes>> findMergedRepeatableAnnotationAttributes(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return HierarchyMergedAnnotation.of(element).findMergedRepeatableAnnotationAttributes(predicate);
	}

	/**
	 * 查找指定元素上满足条件的合并可重复注解集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的合并可重复注解实例集合。
	 * 委托给 {@link HierarchyMergedAnnotation#findMergedRepeatableAnnotation(Predicate)} 实现完整的查找逻辑。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的所有合并可重复注解实例。
	 * 即使某个注解类型没有可重复注解，也会在结果中包含该类型（值为空集合）。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的合并可重复注解实例集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<? extends Annotation>> findMergedRepeatableAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return HierarchyMergedAnnotation.of(element).findMergedRepeatableAnnotation(predicate);
	}

	/**
	 * 查找指定元素上满足条件的顶层合并可重复注解属性集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的顶层（最近距离）合并可重复注解属性集合。
	 * 委托给 {@link HierarchyMergedAnnotation#findTopMergedRepeatableAnnotationAttributes(Predicate)} 实现完整的查找逻辑。
	 * 与 {@link #findMergedRepeatableAnnotationAttributes(AnnotatedElement, Predicate)} 不同，该方法只返回最近层级的注解属性，
	 * 而不包含继承层级中的注解属性。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的顶层合并可重复注解属性。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的顶层合并可重复注解属性集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<AnnotationAttributes>> findTopMergedRepeatableAnnotationAttributes(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return HierarchyMergedAnnotation.of(element).findTopMergedRepeatableAnnotationAttributes(predicate);
	}

	/**
	 * 查找指定元素上满足条件的顶层合并可重复注解集合
	 * <p>
	 * 该方法用于从注解元素上查找所有满足给定条件的注解类型，并获取它们的顶层（最近距离）合并可重复注解实例集合。
	 * 委托给 {@link HierarchyMergedAnnotation#findTopMergedRepeatableAnnotation(Predicate)} 实现完整的查找逻辑。
	 * 与 {@link #findMergedRepeatableAnnotation(AnnotatedElement, Predicate)} 不同，该方法只返回最近层级的注解实例，
	 * 而不包含继承层级中的注解实例。
	 * 返回的 Map 包含所有满足条件的注解类型及其对应的顶层合并可重复注解实例。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素，不能为 null
	 * @param predicate 条件判断器，用于测试注解类型是否应该被查找；不能为 null
	 * @return Map，键为满足条件的注解类型，值为对应的顶层合并可重复注解实例集合；如果没有任何注解满足条件则返回空映射
	 */
	@Nonnull
	public static Map<Class<? extends Annotation>, Set<? extends Annotation>> findTopMergedRepeatableAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		return HierarchyMergedAnnotation.of(element).findTopMergedRepeatableAnnotation(predicate);
	}


	// region private

	/**
	 * 扫描注解的层次结构
	 * <p>
	 * 该方法用于遍历注解的完整层次结构，从给定的合并注解开始，逐层向上扫描所有的元注解和别名注解。
	 * 使用迭代方式处理每一层的候选注解，直到没有新的候选注解为止。
	 * 通过 visited 集合避免循环依赖和重复处理，确保算法能够终止。
	 * 返回的结果是按层级（距离）排序的注解集合列表，索引越小表示距离原始注解越近。
	 *
	 * @param annotation 合并后的注解，作为扫描层次结构的起始点
	 * @return 按距离排序的注解映射列表，列表的每个元素代表一个层级的注解集合
	 */
	static List<Set<MergedAnnotation>> scanHierarchyAnnotations(MergedAnnotation annotation) {
		// 创建层次结构注解列表，每个层级使用 LinkedHashSet 保持插入顺序
		List<Set<MergedAnnotation>> hierarchyAnnotations = new ArrayList<>();
		// 使用 HashSet 记录已访问的注解类型，避免循环依赖和重复处理
		Set<Class<? extends Annotation>> visitedAnnotation = new HashSet<>();
		// 初始化候选注解集合，初始时只包含起始注解
		Collection<MergedAnnotation> candidates = Collections.singletonList(annotation);
		// 迭代处理所有候选注解，直到没有新的候选注解为止
		while (!candidates.isEmpty()) {
			// 扫描当前层级的注解并获取下一层级的候选注解
			candidates = scanHierarchyAnnotation(hierarchyAnnotations, candidates, visitedAnnotation);
		}
		return hierarchyAnnotations;
	}

	/**
	 * 将合并后的注解添加到层次结构映射中
	 * <p>
	 * 该方法根据注解的层级（level）将其添加到对应的层级集合中。
	 * 如果层级列表的大小不足以容纳当前层级，会自动扩展列表并创建新的层级集合。
	 * 使用 LinkedHashSet 存储每个层级的注解以保持插入顺序。
	 *
	 * @param hierarchyAnnotations 层次结构注解映射，列表的索引表示层级，集合包含该层级的所有注解
	 * @param mergedAnnotation     合并后的注解，包含层级信息和注解内容
	 */
	private static void addHierarchyAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations, MergedAnnotation mergedAnnotation) {
		// 获取当前注解的层级
		int level = mergedAnnotation.level();
		// 如果层级列表的大小不足以容纳当前层级，自动扩展列表
		while (hierarchyAnnotations.size() <= level) {
			// 为每个新层级创建 LinkedHashSet 以保持插入顺序
			hierarchyAnnotations.add(new LinkedHashSet<>());
		}
		// 将注解添加到对应层级的集合中
		hierarchyAnnotations.get(level).add(mergedAnnotation);
	}

	/**
	 * 扫描注解的层次结构
	 * <p>
	 * 该方法是注解层次结构扫描的核心实现，负责处理每一层的候选注解并生成下一层的候选注解。
	 * 主要功能包括：
	 * <br>1. 跳过 Java 标准注解包（java.lang.annotation）中的注解
	 * <br>2. 使用 visited 集合避免重复处理和循环依赖
	 * <br>3. 扫描别名属性（仅针对非可重复注解）
	 * <br>4. 处理元注解和别名注解的合并
	 * <br>5. 处理无元注解的别名注解方法
	 * <p>
	 * 对于每个候选注解，会检查其元注解和别名注解，并将它们添加到层次结构和下一轮候选集合中。
	 * 所有新生成的注解层级都会比当前注解的层级加 1。
	 *
	 * @param hierarchyAnnotations 层次结构注解映射，用于存储按层级组织的注解集合
	 * @param lastCandidates       上一轮的候选注解集合，表示当前需要处理的注解
	 * @param visited              已访问的注解类型集合，用于避免重复处理和循环依赖
	 * @return 下一轮的候选注解集合，包含所有需要在下一轮处理的注解
	 */
	private static Collection<MergedAnnotation> scanHierarchyAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations, Collection<MergedAnnotation> lastCandidates, Set<Class<? extends Annotation>> visited) {
		// 使用 LinkedHashSet 收集下一层级的候选注解，保持插入顺序并去重
		Collection<MergedAnnotation> candidates = new LinkedHashSet<>();
		// 遍历上一轮的所有候选注解
		for (MergedAnnotation mergedAnnotation : lastCandidates) {
			Class<? extends Annotation> annotationType = mergedAnnotation.annotationType();
			// 跳过 Java 标准注解包中的注解，不处理标准元注解
			if (annotationType.getPackage().getName().equals("java.lang.annotation")) {
				continue;
			}
			// 如果注解已被访问过，跳过以避免重复处理
			if (visited.contains(annotationType)) {
				continue;
			}
			// 标记当前注解为已访问
			visited.add(annotationType);

			// alias-attribute -> source-member，用于存储别名注解与其源属性的映射关系
			Map<Class<? extends Annotation>, Map<String, String>> aliasSourceMembersMap = null;
			// 除可重复注解外，扫描别名属性
			if (!mergedAnnotation.isRepeatable()) {
				// 获取注解的所有成员方法
				Method[] annotationMembers = AnnotationAttributes.getAnnotationMembers(annotationType);
				aliasSourceMembersMap = new LinkedHashMap<>();

				// 遍历所有成员方法，查找别名注解
				for (Method method : annotationMembers) {
					Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(method);
					if (aliasAttributes != null) {
						for (AliasAttribute alias : aliasAttributes) {
							if (alias != null && alias.annotationType() != annotationType && alias.annotationType() != Annotation.class) {
								// 为每个别名注解类型建立属性映射
								Map<String, String> aliasSourceMembers = aliasSourceMembersMap.computeIfAbsent(alias.annotationType(), k -> new LinkedHashMap<>());
								aliasSourceMembers.putIfAbsent(alias.attribute(), method.getName());
							}
						}
					}
				}
			}

			// 遍历注解类型的所有元注解
			for (Annotation annotation : annotationType.getAnnotations()) {
				// 合并元注解与别名注解方法，如果存在别名映射则移除该条目
				Map<String, String> aliasSourceMembers = aliasSourceMembersMap == null || aliasSourceMembersMap.isEmpty() ? null : aliasSourceMembersMap.remove(annotation.annotationType());
				if (aliasSourceMembers != null) {
					// 如果存在别名映射，创建包含别名信息的合并注解
					Class<? extends Annotation> annotationType1 = annotation.annotationType();
					MergedAnnotation aliasMergeAnnotation = MergedAnnotation.of(mergedAnnotation.level() + 1, annotationType1, annotation, mergedAnnotation, aliasSourceMembers);
					addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
					candidates.add(aliasMergeAnnotation);
				} else {
					// 否则创建普通的合并注解
					MergedAnnotation relation = MergedAnnotation.of(mergedAnnotation.level() + 1, annotation);
					addHierarchyAnnotation(hierarchyAnnotations, relation);
					candidates.add(relation);
				}
			}
			// 处理无元注解的别名注解方法，即只有别名声明但没有实际元注解的情况
			if (aliasSourceMembersMap != null && !aliasSourceMembersMap.isEmpty()) {
				for (Map.Entry<Class<? extends Annotation>, Map<String, String>> entry : aliasSourceMembersMap.entrySet()) {
					Class<? extends Annotation> aliasAnnotationType = entry.getKey();
					Map<String, String> aliasMethods = entry.getValue();

					// 为这些别名注解创建合并注解，注解对象为 null
					MergedAnnotation aliasMergeAnnotation = MergedAnnotation.of(mergedAnnotation.level() + 1, aliasAnnotationType, null, mergedAnnotation, aliasMethods);
					addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
					candidates.add(aliasMergeAnnotation);
				}
			}
		}
		return candidates;
	}

	// endregion private

}

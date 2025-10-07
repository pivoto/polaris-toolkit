package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
class HierarchyMergedAnnotation {
	private final List<Set<MergedAnnotation>> sortedAnnotations = new ArrayList<>();


	HierarchyMergedAnnotation(AnnotatedElement element) {
		scanAnnotations(0, element);
	}

	static HierarchyMergedAnnotation of(AnnotatedElement element) {
		return new HierarchyMergedAnnotation(element);
	}

	/**
	 * 获取按距离排序的注解映射
	 *
	 * @return 按距离排序的注解映射的不可变视图
	 */
	public List<Set<MergedAnnotation>> getSortedAnnotations() {
		return Collections.unmodifiableList(sortedAnnotations);
	}


	/**
	 * 获取合并后的注解
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要获取的注解类型
	 * @return 合并后的注解实例，如果未找到则返回null
	 */
	@Nullable
	public <A extends Annotation> A getMergedAnnotation(Class<A> annotationType) {
		MergedAnnotation matchedAnnotation = null;
		List<MergedAnnotation> aliasAnnotations = new ArrayList<>();

		// 遍历所有层级，尽可能合并所有别名属性
		for (Set<MergedAnnotation> set : sortedAnnotations) {
			for (MergedAnnotation annotation : set) {
				MatchedMergedAnnotation<A> target = annotation.getMatchedAnnotation(annotationType);
				if (target != null) {
					// match
					MergedAnnotation matched = target.getMatchedAnnotation();
					if (matched != null) {
						if (matchedAnnotation == null) {
							matchedAnnotation = matched;
						} else {
							aliasAnnotations.add(matched);
						}
					}
					aliasAnnotations.addAll(target.getAliasAnnotations());
				}
			}
		}
		// 如果存在匹配的注解，则合并本级别名属性并返回
		if (matchedAnnotation != null) {
			return MatchedMergedAnnotation.of(annotationType, matchedAnnotation, aliasAnnotations).asAnnotation();
		}

		// 如果只存在纯别名配置，则汇总所有别名属性并合并成一个注解实例
		if (!aliasAnnotations.isEmpty()) {
			return MatchedMergedAnnotation.of(annotationType, null, aliasAnnotations).asAnnotation();
		}
		return null;
	}

	/**
	 * 获取合并后的可重复注解集合
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要获取的注解类型
	 * @return 合并后的注解集合
	 */
	@Nonnull
	public <A extends Annotation> Set<A> getMergedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> result = new LinkedHashSet<>();
		// 遍历所有层级，尽可能找到所有匹配的注解
		for (Set<MergedAnnotation> set : sortedAnnotations) {
			for (MergedAnnotation annotation : set) {
				Set<MatchedMergedAnnotation<A>> matchedSet = annotation.getMatchedRepeatableAnnotation(annotationType);
				if (matchedSet != null) {
					// match
					result.addAll(matchedSet);
				}
			}
		}
		Set<A> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.asAnnotation());
		}
		return annotationSet;
	}

	/**
	 * 获取合并后的可重复注解集合
	 *
	 * @param <A>            注解类型
	 * @param annotationType 要获取的注解类型
	 * @return 合并后的注解集合
	 */
	@Nonnull
	public <A extends Annotation> Set<A> getTopMergedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> result = new LinkedHashSet<>();
		// 遍历所有层级，找到存在匹配的注解的层级后跳出
		for (Set<MergedAnnotation> set : sortedAnnotations) {
			boolean found = false;
			for (MergedAnnotation annotation : set) {
				Set<MatchedMergedAnnotation<A>> matchedSet = annotation.getTopMatchedRepeatableAnnotation(annotationType);
				if (matchedSet != null && !matchedSet.isEmpty()) {
					// match
					result.addAll(matchedSet);
					found = true;
				}
			}
			if (found) {
				break;
			}
		}
		Set<A> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.asAnnotation());
		}
		return annotationSet;
	}

	private void addMergedAnnotation(MergedAnnotation annotation) {
		int level = annotation.getLevel();
		while (sortedAnnotations.size() <= level) {
			sortedAnnotations.add(new LinkedHashSet<>());
		}
		sortedAnnotations.get(level).add(annotation);
	}

	/**
	 * 扫描指定元素上的所有注解
	 *
	 * @param level   层次深度
	 * @param element 注解元素（类、方法、参数等）
	 */
	private void scanAnnotations(int level, AnnotatedElement element) {

		Annotation[] annotations = element.getAnnotations();
		for (Annotation annotation : annotations) {
			MergedAnnotation mergedAnnotation = MergedAnnotation.of(level, element, annotation);
			addMergedAnnotation(mergedAnnotation);
		}

		if (element instanceof Class) {
			scanHierarchyClass(level + 1, (Class<?>) element);
		} else if (element instanceof Method) {
			scanHierarchyMethod(level + 1, (Method) element);
		} else if (element instanceof Parameter) {
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						scanHierarchyParameter(level + 1, ((Parameter) element), (Method) executable, i);
					}
				}
			}
		}
	}

	/**
	 * 扫描类的继承结构中的注解
	 *
	 * @param level   层次深度
	 * @param element 类元素
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyClass(int level, Class<?> element) {
		Set<Class<?>> visitedClass = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visitedClass);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		int nextLevel = level;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				scanAnnotations(nextLevel, superclass);
			}
			for (Class<?> anInterface : interfaces) {
				scanAnnotations(nextLevel, anInterface);
			}

			// next level
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visitedClass);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 扫描方法的继承结构中的注解
	 *
	 * @param level   层次深度
	 * @param element 方法元素
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyMethod(int level, Method element) {
		Class<?> declaringClass = element.getDeclaringClass();
		Set<Class<?>> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		int nextLevel = level;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				try {
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					scanAnnotations(nextLevel, method);
				} catch (NoSuchMethodException ignored) {
				}
			}
			for (Class<?> anInterface : interfaces) {
				try {
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					scanAnnotations(nextLevel, method);
				} catch (NoSuchMethodException ignored) {
				}
			}

			// next level
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 扫描参数的继承结构中的注解
	 *
	 * @param level           层次深度
	 * @param element         参数元素
	 * @param declaringMethod 声明该参数的方法
	 * @param position        参数在方法参数列表中的位置
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyParameter(int level, Parameter element, Method declaringMethod, int position) {
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		Set<Class<?>> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		int nextLevel = level;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				try {
					Parameter parameter = superclass.getDeclaredMethod(declaringMethod.getName(), declaringMethod.getParameterTypes()).getParameters()[position];
					scanAnnotations(nextLevel, parameter);
				} catch (NoSuchMethodException ignored) {
				}
			}
			for (Class<?> anInterface : interfaces) {
				try {
					Parameter parameter = anInterface.getDeclaredMethod(declaringMethod.getName(), declaringMethod.getParameterTypes()).getParameters()[position];
					scanAnnotations(nextLevel, parameter);
				} catch (NoSuchMethodException ignored) {
				}
			}
			// next level
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 获取类继承结构中的候选类
	 *
	 * @param superclass   父类
	 * @param interfaces   接口数组
	 * @param visitedClass 已访问的类集合
	 * @return 包含下一个父类和接口数组的元组
	 */
	@SuppressWarnings("DuplicatedCode")
	private Tuple2<Class<?>, Class<?>[]> getHierarchyClassCandidates(Class<?> superclass, Class<?>[] interfaces, Set<Class<?>> visitedClass) {
		Set<Class<?>> candidates = new LinkedHashSet<>();
		if (interfaces == null) {
			// first level
			if (superclass != null && superclass != Object.class) {
				interfaces = superclass.getInterfaces();
				for (Class<?> anInterface : interfaces) {
					candidates.add(anInterface);
					visitedClass.add(anInterface);
				}
				superclass = superclass.getSuperclass();
			}
		} else {
			// next level
			if (superclass != null && superclass != Object.class) {
				for (Class<?> anInterface : superclass.getInterfaces()) {
					if (visitedClass.contains(anInterface)) {
						continue;
					}
					candidates.add(anInterface);
					visitedClass.add(anInterface);
				}
				superclass = superclass.getSuperclass();
			}
			for (Class<?> anInterface : interfaces) {
				for (Class<?> anInterfaceInterface : anInterface.getInterfaces()) {
					if (visitedClass.contains(anInterfaceInterface)) {
						continue;
					}
					candidates.add(anInterfaceInterface);
					visitedClass.add(anInterfaceInterface);
				}
			}
		}
		return Tuple2.of(superclass, candidates.toArray(new Class[0]));
	}
}

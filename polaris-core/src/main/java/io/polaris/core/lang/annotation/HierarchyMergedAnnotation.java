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
public class HierarchyMergedAnnotation {
	private final List<Set<MergedAnnotation>> hierarchyAnnotations;


	HierarchyMergedAnnotation(AnnotatedElement element) {
		this.hierarchyAnnotations = new ArrayList<>();
		scanAnnotations(0, element);
	}

	HierarchyMergedAnnotation(List<Set<MergedAnnotation>> hierarchyAnnotations) {
		this.hierarchyAnnotations = hierarchyAnnotations;
	}

	public static HierarchyMergedAnnotation of(List<Set<MergedAnnotation>> hierarchyAnnotations) {
		return new HierarchyMergedAnnotation(hierarchyAnnotations);
	}

	public static HierarchyMergedAnnotation of(AnnotatedElement element) {
		return new HierarchyMergedAnnotation(element);
	}

	/**
	 * 获取按距离排序的注解映射
	 *
	 * @return 按距离排序的注解映射的不可变视图
	 */
	public List<Set<MergedAnnotation>> hierarchyAnnotations() {
		return Collections.unmodifiableList(hierarchyAnnotations);
	}

	/**
	 * 匹配合并后的注解
	 * <p>
	 * 该方法用于从注解层次结构中查找并匹配指定类型的注解，返回包含完整匹配信息的 MatchedMergedAnnotation 对象。
	 * 遍历所有层级的注解，收集所有匹配的注解实例和别名注解：
	 * 1. 第一个匹配的直接注解作为主注解
	 * 2. 后续匹配的直接注解作为别名注解
	 * 3. 所有通过别名配置找到的注解也作为别名注解
	 * 如果存在多个匹配的注解，会正确区分主注解和别名注解的关系。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要匹配的注解类型，必须是 Annotation 的子类
	 * @return MatchedMergedAnnotation 对象，封装了匹配的注解及其别名关系；如果未找到任何匹配则返回 null
	 */
	@Nullable
	public <A extends Annotation> MatchedMergedAnnotation<A> matchMergedAnnotation(Class<A> annotationType) {
		// 记录第一个匹配的注解作为主注解
		MergedAnnotation matchedAnnotation = null;
		// 收集所有别名注解，包括匹配注解的别名和纯别名配置
		List<MergedAnnotation> aliasAnnotations = new ArrayList<>();

		// 遍历所有层级，尽可能合并所有别名属性
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			for (MergedAnnotation annotation : set) {
				MatchedMergedAnnotation<A> target = annotation.getMatchedAnnotation(annotationType);
				if (target != null) {
					// match
					MergedAnnotation matched = target.directMatched();
					if (matched != null) {
						// 如果还没有主注解，将当前匹配注解设为主注解
						if (matchedAnnotation == null) {
							matchedAnnotation = matched;
						} else {
							// 否则将当前匹配注解添加到别名列表
							aliasAnnotations.add(matched);
						}
					}
					// 收集所有的别名注解
					aliasAnnotations.addAll(target.aliasMatchedList());
				}
			}
		}
		// 如果存在匹配的注解，则合并本级别名属性并返回
		if (matchedAnnotation != null) {
			return MatchedMergedAnnotation.of(annotationType, matchedAnnotation, aliasAnnotations);
		}

		// 如果只存在纯别名配置，则汇总所有别名属性并合并成一个注解实例
		if (!aliasAnnotations.isEmpty()) {
			return MatchedMergedAnnotation.of(annotationType, null, aliasAnnotations);
		}
		return null;
	}

	/**
	 * 获取合并后的注解属性
	 * <p>
	 * 该方法用于从注解层次结构中查找并合并指定类型的注解属性。
	 * 首先调用 {@link #matchMergedAnnotation(Class)} 获取匹配的注解对象，
	 * 然后从中提取注解属性返回。
	 * 如果未找到匹配的注解，则返回 null。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解属性，如果未找到任何匹配的注解则返回 null
	 */
	@Nullable
	public <A extends Annotation> AnnotationAttributes getMergedAnnotationAttributes(Class<A> annotationType) {
		// 获取匹配的合并注解对象
		MatchedMergedAnnotation<A> matchMergedAnnotation = matchMergedAnnotation(annotationType);
		if (matchMergedAnnotation != null) {
			// 返回匹配注解的属性
			return matchMergedAnnotation.annotationAttributes();
		}
		return null;
	}

	/**
	 * 获取合并后的注解
	 * <p>
	 * 该方法用于从注解层次结构中查找并合并指定类型的注解。
	 * 遍历所有层级的注解，收集所有匹配的注解实例和别名注解，然后合并成一个完整的注解实例。
	 * 如果存在多个匹配的注解，第一个作为主注解，其余的作为别名注解处理。
	 * 即使没有直接匹配的注解，如果存在别名注解配置，也会汇总所有别名属性并合并成一个注解实例。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解实例，如果未找到任何匹配的注解或别名配置则返回 null
	 */

	@Nullable
	public <A extends Annotation> A getMergedAnnotation(Class<A> annotationType) {
		MatchedMergedAnnotation<A> matchMergedAnnotation = matchMergedAnnotation(annotationType);
		if (matchMergedAnnotation != null) {
			return matchMergedAnnotation.asAnnotation();
		}
		return null;
	}

	/**
	 * 匹配合并后的可重复注解集合
	 * <p>
	 * 该方法用于从注解层次结构中查找并匹配所有指定类型的可重复注解。
	 * 遍历所有层级的注解，收集所有匹配的可重复注解实例，包括直接匹配和通过别名匹配的注解。
	 * 使用 LinkedHashSet 确保结果不重复并保持插入顺序。
	 * 返回的是 MatchedMergedAnnotation 集合，包含了每个匹配注解的完整信息。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要匹配的可重复注解类型，必须是 Annotation 的子类
	 * @return MatchedMergedAnnotation 集合，包含所有匹配的可重复注解信息
	 */
	@Nonnull
	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> matchMergedRepeatableAnnotation(Class<A> annotationType) {
		// 使用 LinkedHashSet 存储匹配的注解，确保不重复并保持插入顺序
		Set<MatchedMergedAnnotation<A>> result = new LinkedHashSet<>();
		// 遍历所有层级，尽可能找到所有匹配的注解
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			for (MergedAnnotation annotation : set) {
				Set<MatchedMergedAnnotation<A>> matchedSet = annotation.getMatchedRepeatableAnnotation(annotationType);
				if (matchedSet != null) {
					// match
					result.addAll(matchedSet);
				}
			}
		}
		return result;
	}

	/**
	 * 获取合并后的可重复注解属性集合
	 * <p>
	 * 该方法用于从注解层次结构中查找并合并所有指定类型的可重复注解属性。
	 * 首先调用 {@link #matchMergedRepeatableAnnotation(Class)} 获取所有匹配的注解对象，
	 * 然后从中提取注解属性并返回。
	 * 使用 LinkedHashSet 确保结果不重复并保持插入顺序。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 合并后的注解属性集合，包含所有匹配的可重复注解属性
	 */
	@Nonnull
	public <A extends Annotation> Set<AnnotationAttributes> getMergedRepeatableAnnotationAttributes(Class<A> annotationType) {
		// 获取所有匹配的合并注解对象
		Set<MatchedMergedAnnotation<A>> result = matchMergedRepeatableAnnotation(annotationType);
		// 将所有匹配的注解转换为实际的注解实例
		Set<AnnotationAttributes> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.annotationAttributes());
		}
		return annotationSet;
	}

	/**
	 * 获取合并后的可重复注解集合
	 * <p>
	 * 该方法用于从注解层次结构中查找并合并所有指定类型的可重复注解。
	 * 遍历所有层级的注解，收集所有匹配的可重复注解实例，包括元注解和别名注解中的可重复注解。
	 * 使用 LinkedHashSet 确保结果不重复并保持插入顺序。
	 * 与 {@link #getTopMergedRepeatableAnnotation(Class)} 不同，该方法会返回所有层级中找到的注解。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 合并后的可重复注解集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public <A extends Annotation> Set<A> getMergedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> result = matchMergedRepeatableAnnotation(annotationType);
		// 将所有匹配的注解转换为实际的注解实例
		Set<A> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.asAnnotation());
		}
		return annotationSet;
	}

	/**
	 * 匹配顶层合并后的可重复注解集合
	 * <p>
	 * 该方法用于从注解层次结构中查找并匹配最顶层（最近距离）的可重复注解。
	 * 与 {@link #matchMergedRepeatableAnnotation(Class)} 不同，该方法在找到第一个包含匹配注解的层级后就会停止查找。
	 * 这确保了只返回距离元素最近的注解层中的可重复注解，而不会包含继承层级中的注解。
	 * 使用 LinkedHashSet 确保结果不重复并保持插入顺序。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要匹配的可重复注解类型，必须是 Annotation 的子类
	 * @return MatchedMergedAnnotation 集合，包含顶层匹配的所有可重复注解信息
	 */
	@Nonnull
	public <A extends Annotation> Set<MatchedMergedAnnotation<A>> matchTopMergedRepeatableAnnotation(Class<A> annotationType) {
		// 使用 LinkedHashSet 存储匹配的注解，确保不重复并保持插入顺序
		Set<MatchedMergedAnnotation<A>> result = new LinkedHashSet<>();
		// 遍历所有层级，找到存在匹配的注解的层级后跳出
		for (Set<MergedAnnotation> set : hierarchyAnnotations) {
			boolean found = false;
			for (MergedAnnotation annotation : set) {
				Set<MatchedMergedAnnotation<A>> matchedSet = annotation.getTopMatchedRepeatableAnnotation(annotationType);
				if (matchedSet != null && !matchedSet.isEmpty()) {
					// match
					result.addAll(matchedSet);
					found = true;
				}
			}
			// 如果在当前层级找到了匹配的注解，立即跳出循环，不再检查更深层级
			if (found) {
				break;
			}
		}
		return result;
	}

	/**
	 * 获取顶层合并后的可重复注解属性集合
	 * <p>
	 * 该方法用于从注解层次结构中查找并获取最顶层（最近距离）的可重复注解属性。
	 * 首先调用 {@link #matchTopMergedRepeatableAnnotation(Class)} 获取顶层匹配的注解对象，
	 * 然后从中提取注解属性并返回。
	 * 使用 LinkedHashSet 确保结果不重复并保持插入顺序。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 顶层合并后的注解属性集合，包含最近层级的所有可重复注解属性
	 */
	@Nonnull
	public <A extends Annotation> Set<AnnotationAttributes> getTopMergedRepeatableAnnotationAttributes(Class<A> annotationType) {
		// 使用 LinkedHashSet 存储匹配的注解，确保不重复并保持插入顺序
		Set<MatchedMergedAnnotation<A>> result = matchTopMergedRepeatableAnnotation(annotationType);
		// 将所有匹配的注解转换为实际的注解实例
		Set<AnnotationAttributes> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.annotationAttributes());
		}
		return annotationSet;
	}

	/**
	 * 获取最近层级的合并后的可重复注解集合
	 * <p>
	 * 该方法用于从注解层次结构中查找并合并最顶层（最近距离）的可重复注解。
	 * 与 {@link #getMergedRepeatableAnnotation(Class)} 不同，该方法在找到第一个包含匹配注解的层级后就会停止查找。
	 * 这确保了只返回距离元素最近的注解层中的可重复注解，而不会包含继承层级中的注解。
	 * 使用 LinkedHashSet 确保结果不重复并保持插入顺序。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 要获取的可重复注解类型，必须是 Annotation 的子类
	 * @return 顶层合并后的可重复注解集合，如果未找到则返回空集合而不是 null
	 */
	@Nonnull
	public <A extends Annotation> Set<A> getTopMergedRepeatableAnnotation(Class<A> annotationType) {
		// 使用 LinkedHashSet 存储匹配的注解，确保不重复并保持插入顺序
		Set<MatchedMergedAnnotation<A>> result = matchTopMergedRepeatableAnnotation(annotationType);
		// 将所有匹配的注解转换为实际的注解实例
		Set<A> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.asAnnotation());
		}
		return annotationSet;
	}

	// region private

	private void addMergedAnnotation(MergedAnnotation annotation) {
		int level = annotation.level();
		while (hierarchyAnnotations.size() <= level) {
			hierarchyAnnotations.add(new LinkedHashSet<>());
		}
		hierarchyAnnotations.get(level).add(annotation);
	}

	/**
	 * 扫描指定元素上的所有注解
	 * <p>
	 * 该方法是注解扫描的核心入口，负责在给定元素上扫描所有直接注解，并根据元素类型递归扫描继承结构中的注解。
	 * 对于类元素，会扫描其父类和接口的注解；对于方法元素，会扫描父类和接口中同名方法的注解；
	 * 对于参数元素，会扫描父类和接口中对应位置参数的注解。
	 * 所有扫描到的注解都会被转换为 MergedAnnotation 并按层级添加到层次结构中。
	 *
	 * @param level   层次深度，表示当前处理的注解所在的层级，初始调用通常为 0
	 * @param element 注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 */
	private void scanAnnotations(int level, AnnotatedElement element) {

		// 获取元素上的所有直接注解
		Annotation[] annotations = element.getAnnotations();
		// 将每个直接注解转换为 MergedAnnotation 并添加到层次结构中
		for (Annotation annotation : annotations) {
			MergedAnnotation mergedAnnotation = MergedAnnotation.of(level, annotation);
			addMergedAnnotation(mergedAnnotation);
		}

		// 根据元素类型进行特定的继承结构扫描
		if (element instanceof Class) {
			// 扫描类的继承结构中的注解
			scanHierarchyClass(level + 1, (Class<?>) element);
		} else if (element instanceof Method) {
			// 扫描方法的继承结构中的注解
			scanHierarchyMethod(level + 1, (Method) element);
		} else if (element instanceof Parameter) {
			// 获取参数所属的方法或构造函数
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				// 获取方法的所有参数，确定当前参数的位置
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						// 扫描参数的继承结构中的注解
						scanHierarchyParameter(level + 1, ((Parameter) element), (Method) executable, i);
						break;
					}
				}
			}
		}
	}

	/**
	 * 扫描类的继承结构中的注解
	 * <p>
	 * 该方法负责在类的继承层次结构中扫描所有注解，包括父类链和接口层次结构。
	 * 使用 visitedClass 集合避免重复处理和循环依赖。
	 * 逐层遍历类的继承结构，每一层的注解都会被添加到对应的层级中。
	 * 通过 getHierarchyClassCandidates 方法获取下一层的候选类（父类的父类、接口的父接口）。
	 *
	 * @param level   层次深度，表示当前处理的类所在的层级
	 * @param element 类元素，表示要扫描注解的起始类
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyClass(int level, Class<?> element) {
		// 使用 LinkedHashSet 记录已访问的类，保持插入顺序并避免重复处理
		Set<Class<?>> visitedClass = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visitedClass);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		// 从当前层级开始遍历类的继承结构
		int nextLevel = level;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 扫描父类上的注解
			if (superclass != null && superclass != Object.class) {
				scanAnnotations(nextLevel, superclass);
			}
			// 扫描所有接口上的注解
			for (Class<?> anInterface : interfaces) {
				scanAnnotations(nextLevel, anInterface);
			}

			// 准备下一层级的遍历
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visitedClass);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 扫描方法的继承结构中的注解
	 * <p>
	 * 该方法负责在方法的继承层次结构中扫描所有注解。
	 * 通过方法签名（方法名 + 参数类型）精确定位父类或接口中的对应方法，然后在这些方法上扫描注解。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 逐层遍历类的继承结构，每一层找到的对应方法上的注解都会被添加到对应的层级中。
	 * NoSuchMethodException 会被忽略，因为不是所有父类或接口都必须有同名方法。
	 *
	 * @param level   层次深度，表示当前处理的方法所在的层级
	 * @param element 方法元素，表示要扫描注解的起始方法
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyMethod(int level, Method element) {
		// 获取声明该方法的类
		Class<?> declaringClass = element.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的类，保持插入顺序并避免重复处理
		Set<Class<?>> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		// 从当前层级开始遍历类的继承结构
		int nextLevel = level;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法并扫描注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 根据方法名和参数类型精确查找父类中的对应方法
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					scanAnnotations(nextLevel, method);
				} catch (NoSuchMethodException ignored) {
					// 父类中不存在该方法，忽略
				}
			}
			// 在接口中查找同名方法并扫描注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 根据方法名和参数类型精确查找接口中的对应方法
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					scanAnnotations(nextLevel, method);
				} catch (NoSuchMethodException ignored) {
					// 接口中不存在该方法，忽略
				}
			}

			// 准备下一层级的遍历
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 扫描参数的继承结构中的注解
	 * <p>
	 * 该方法负责在参数的继承层次结构中扫描所有注解。
	 * 首先定位到父类或接口中的同名方法，然后获取相同位置的参数进行注解扫描。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 逐层遍历类的继承结构，每一层找到的对应位置参数上的注解都会被添加到对应的层级中。
	 * NoSuchMethodException 会被忽略，因为不是所有父类或接口都必须有同名方法。
	 *
	 * @param level           层次深度，表示当前处理的参数所在的层级
	 * @param element         参数元素，表示要扫描注解的起始参数
	 * @param declaringMethod 声明该参数的方法，用于定位父类或接口中的对应方法
	 * @param position        参数在方法参数列表中的位置索引，用于获取对应位置的参数
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyParameter(int level, Parameter element, Method declaringMethod, int position) {
		// 获取声明该方法的类
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的类，保持插入顺序并避免重复处理
		Set<Class<?>> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		// 从当前层级开始遍历类的继承结构
		int nextLevel = level;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法的对应参数并扫描注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 获取父类中同名方法的对应位置参数
					Parameter parameter = superclass.getDeclaredMethod(declaringMethod.getName(), declaringMethod.getParameterTypes()).getParameters()[position];
					scanAnnotations(nextLevel, parameter);
				} catch (NoSuchMethodException ignored) {
					// 父类中不存在该方法，忽略
				}
			}
			// 在接口中查找同名方法的对应参数并扫描注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 获取接口中同名方法的对应位置参数
					Parameter parameter = anInterface.getDeclaredMethod(declaringMethod.getName(), declaringMethod.getParameterTypes()).getParameters()[position];
					scanAnnotations(nextLevel, parameter);
				} catch (NoSuchMethodException ignored) {
					// 接口中不存在该方法，忽略
				}
			}
			// 准备下一层级的遍历
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 获取类继承结构中的候选类
	 * <p>
	 * 该方法用于在类的继承层级结构中获取下一层级的候选类，包括父类和接口。
	 * 支持两种处理模式：
	 * 1. 初始调用（interfaces 为 null）：处理直接父类的接口和父类的父类
	 * 2. 后续调用（interfaces 不为 null）：处理当前父类的接口以及所有接口的父接口
	 * 通过 visitedClass 集合避免重复处理和循环依赖，确保遍历的终止性。
	 * 与 RawAnnotations.getHierarchyClassCandidates 类似，但不检查已访问集合，因为这里由调用方负责管理。
	 *
	 * @param superclass   父类，表示当前要处理的类的直接父类，如果为 null 或 Object.class 表示已到达继承树顶端
	 * @param interfaces   接口数组，表示当前要处理的类或接口所实现的接口，首次调用时可为 null
	 * @param visitedClass 已访问的类集合，用于避免重复处理，方法执行过程中会将新发现的候选类添加到此集合
	 * @return 包含下一个父类和接口数组的元组，第一个元素为下一个父类，第二个元素为候选接口数组
	 */
	@SuppressWarnings("DuplicatedCode")
	private Tuple2<Class<?>, Class<?>[]> getHierarchyClassCandidates(Class<?> superclass, Class<?>[] interfaces, Set<Class<?>> visitedClass) {
		// 使用 LinkedHashSet 保持插入顺序，收集下一个层级的所有候选类
		Set<Class<?>> candidates = new LinkedHashSet<>();
		// 判断是否为首次调用（第一层处理）
		if (interfaces == null) {
			// 第一层处理：处理直接父类的接口和父类的父类
			if (superclass != null && superclass != Object.class) {
				interfaces = superclass.getInterfaces();
				// 将直接父类的所有接口添加到候选集合
				for (Class<?> anInterface : interfaces) {
					candidates.add(anInterface);
					visitedClass.add(anInterface);
				}
				// 更新 superclass 为当前父类的父类，准备下一次迭代
				superclass = superclass.getSuperclass();
			}
		} else {
			// 后续层级处理：处理当前父类的接口以及所有接口的父接口
			if (superclass != null && superclass != Object.class) {
				// 将当前父类实现的未访问接口添加到候选集合
				for (Class<?> anInterface : superclass.getInterfaces()) {
					if (visitedClass.contains(anInterface)) {
						continue;
					}
					candidates.add(anInterface);
					visitedClass.add(anInterface);
				}
				// 更新 superclass 为当前父类的父类，准备下一次迭代
				superclass = superclass.getSuperclass();
			}
			// 遍历当前层级的所有接口，获取它们的父接口
			for (Class<?> anInterface : interfaces) {
				// 获取当前接口的所有父接口并添加到候选集合
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

	// endregion private
}

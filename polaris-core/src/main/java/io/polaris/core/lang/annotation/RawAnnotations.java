package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.io.Consoles;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
@SuppressWarnings("ALL")
public class RawAnnotations {

	/**
	 * 获取层级注解候选元素数组
	 * <p>
	 * 该方法用于在注解层级查找过程中，从当前候选元素集合中提取下一层级的候选元素。
	 * 它会收集所有当前候选元素上的注解类型，并将这些注解类型作为下一轮查找的候选元素。
	 * 通过已访问集合来避免重复处理和循环依赖。
	 *
	 * @param candidates 当前候选元素数组，包含需要检查注解的元素
	 * @param visited    已访问过的元素集合，用于避免重复处理，方法执行过程中会将新发现的元素添加到此集合
	 * @return 下一层级的候选元素数组，包含从当前候选元素的注解中提取的所有未访问过的注解类型
	 */
	private static Set<AnnotatedElement> getHierarchyAnnotationCandidates(Set<AnnotatedElement> candidates, Set<AnnotatedElement> visited) {
		// 使用 LinkedHashSet 保持插入顺序，收集当前候选元素上的所有注解类型作为下一轮的候选元素
		Set<AnnotatedElement> candidateSet = new LinkedHashSet<>();
		// 遍历所有当前候选元素
		for (AnnotatedElement candidate : candidates) {
			// 获取当前元素上的所有注解
			Annotation[] annotations = candidate.getAnnotations();
			// 遍历所有注解
			for (Annotation annotation : annotations) {
				// 获取注解类型
				Class<? extends Annotation> type = annotation.annotationType();
				// 如果该注解类型已被访问过，则跳过以避免重复处理
				if (visited.contains(type)) {
					continue;
				}
				// 将未访问过的注解类型添加到候选集合中
				candidateSet.add(type);
				// 标记该注解类型为已访问
				visited.add(type);
			}
		}
		return candidateSet;
	}

	/**
	 * 获取类继承结构中的候选类
	 * <p>
	 * 该方法用于在类的继承层级结构中查找下一个层级的候选类，包括父类和接口。
	 * 支持两种处理模式：
	 * 1. 初始调用（interfaces 为 null）：处理直接父类的接口和父类的父类
	 * 2. 后续调用（interfaces 不为 null）：处理当前父类的接口以及所有接口的父接口
	 * 通过 visited 集合避免重复处理和循环依赖，确保遍历的终止性。
	 *
	 * @param superclass 父类，表示当前要处理的类的直接父类，如果为 null 或 Object.class 表示已到达继承树顶端
	 * @param interfaces 接口数组，表示当前要处理的类或接口所实现的接口，首次调用时可为 null
	 * @param visited    已访问的类集合，用于避免重复处理，方法执行过程中会将新发现的候选类添加到此集合
	 * @return 包含下一个父类和接口数组的元组，第一个元素为下一个父类，第二个元素为候选接口数组
	 */
	private static Tuple2<Class<?>, Class<?>[]> getHierarchyClassCandidates(Class<?> superclass, Class<?>[] interfaces, Set<AnnotatedElement> visited) {
		// 使用 LinkedHashSet 保持插入顺序，收集下一个层级的所有候选类
		Set<Class<?>> candidates = new LinkedHashSet<>();
		// 判断是否为首次调用（第一层处理）
		if (interfaces == null) {
			// 第一层处理：处理直接父类的接口和父类的父类
			if (superclass != null && superclass != Object.class) {
				// 获取当前父类实现的所有接口
				interfaces = superclass.getInterfaces();
				// 将所有未访问过的接口添加到候选集合
				for (Class<?> anInterface : interfaces) {
					if (visited.contains(anInterface)) {
						continue;
					}
					candidates.add(anInterface);
					visited.add(anInterface);
				}
				// 更新 superclass 为当前父类的父类，准备下一次迭代
				superclass = superclass.getSuperclass();
			}
			// 返回第一层处理结果：更新后的父类和候选接口数组
			return Tuple2.of(superclass, candidates.toArray(new Class[0]));
		} else {
			// 后续层级处理：处理当前父类的接口以及所有接口的父接口
			if (superclass != null && superclass != Object.class) {
				// 将当前父类实现的未访问接口添加到候选集合
				for (Class<?> anInterface : superclass.getInterfaces()) {
					if (visited.contains(anInterface)) {
						continue;
					}
					candidates.add(anInterface);
					visited.add(anInterface);
				}
				// 更新 superclass 为当前父类的父类，准备下一次迭代
				superclass = superclass.getSuperclass();
			}
			// 遍历当前层级的所有接口，获取它们的父接口
			for (Class<?> anInterface : interfaces) {
				// 获取当前接口的所有父接口并添加到候选集合
				for (Class<?> anInterfaceInterface : anInterface.getInterfaces()) {
					if (visited.contains(anInterfaceInterface)) {
						continue;
					}
					candidates.add(anInterfaceInterface);
					visited.add(anInterfaceInterface);
				}
			}
			// 返回后续层级处理结果：更新后的父类和候选接口数组
			return Tuple2.of(superclass, candidates.toArray(new Class[0]));
		}
	}


	/**
	 * 获取指定元素上的注解，支持继承查找
	 * <p>
	 * 该方法是注解查找的入口方法，支持在类、方法、参数等元素上进行全面的注解查找。
	 * 查找过程包括：
	 * 1. 直接查找：在当前元素上直接查找匹配的注解
	 * 2. 注解层级查找：通过元注解（注解的注解）进行间接查找
	 * 3. 继承结构查找：根据元素类型（类、方法、参数）在其继承结构中查找注解
	 *    - 类：在父类和接口中查找
	 *    - 方法：在父类和接口的同名方法中查找
	 *    - 参数：在父类和接口的对应位置参数中查找
	 * 所有找到的注解会去重后返回（使用 LinkedHashSet 保持插入顺序）。
	 *
	 * @param element   注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param predicate 注解过滤条件，用于筛选符合条件的注解，只有使 predicate.test() 返回 true 的注解才会被收集
	 * @return 找到的所有匹配的注解实例集合，如果未找到则返回空集合，集合已自动去重并保持插入顺序
	 */
	public static Set<Annotation> findAnnotation(AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		// 使用 LinkedHashSet 存储结果以保持插入顺序并自动去重
		Set<Annotation> result = new LinkedHashSet<>();
		// 直接在当前元素上查找匹配的注解
		seekDirectly(result, element, predicate);
		// 通过注解的层级结构（元注解）间接查找匹配的注解
		seekByHierarchyAnnotation(result, element, predicate);

		// 根据元素类型进行特定的继承查找
		if (element instanceof Class) {
			seekByHierarchyClass(result, (Class<?>) element, predicate);
		} else if (element instanceof Method) {
			seekByHierarchyMethod(result, (Method) element, predicate);
		} else if (element instanceof Parameter) {
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						seekByHierarchyParameter(result, ((Parameter) element), (Method) executable, i, predicate);
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 直接在指定元素上查找注解，不进行继承层级的搜索
	 * <p>
	 * 该方法会检查元素上的所有直接注解，包括普通注解和可重复注解。
	 * 对于每个注解，使用提供的谓词进行过滤，匹配的注解会被添加到结果集合中。
	 * 如果注解是可重复注解的容器注解，还会提取出其中包含的所有重复注解实例。
	 *
	 * @param result    结果集合，用于收集所有匹配的注解实例，方法执行过程中会将匹配的注解添加到此集合
	 * @param element   注解元素，表示要检查的类、方法、参数等反射元素
	 * @param predicate 注解过滤条件，用于判断注解是否应该被收集到结果集合中
	 */
	private static void seekDirectly(Set<Annotation> result, AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		// 获取元素上的所有直接注解
		Annotation[] annotations = element.getAnnotations();
		// 遍历所有直接注解
		for (Annotation annotation : annotations) {
			// 使用谓词测试当前注解，如果匹配则添加到结果集合
			if (predicate.test(annotation.annotationType())) {
				result.add(annotation);
			}
			// 检查当前注解是否为可重复注解的容器注解，如果是则获取其内部的重复注解类型
			Class<? extends Annotation> repeatedAnnotationType = Annotations.getRepeatedAnnotationType(annotation.annotationType());
			// 如果存在可重复注解类型，说明当前注解是容器注解，需要提取其中的所有重复注解
			if (repeatedAnnotationType != null) {
				// 获取元素上该类型的所有重复注解实例
				Annotation[] annotationsByType = element.getAnnotationsByType(repeatedAnnotationType);
				// 遍历所有重复注解实例并进行过滤
				for (Annotation repeatedAnnotation : annotationsByType) {
					if (predicate.test(repeatedAnnotation.annotationType())) {
						result.add(repeatedAnnotation);
					}
				}
			}
		}
	}

	/**
	 * 通过注解的继承层级查找指定类型的注解
	 * <p>
	 * 该方法使用迭代方式在注解的层级结构中查找匹配的注解。
	 * 它从给定元素开始，通过元注解（注解上的注解）逐层向上查找，直到没有更多的候选元素为止。
	 * 使用 visited 集合来避免循环依赖和重复处理，确保算法能够终止。
	 * 每次迭代都会在当前候选元素上执行直接查找，然后获取下一层级的候选元素。
	 *
	 * @param result    结果集合，用于收集所有匹配的注解实例，方法执行过程中会将匹配的注解添加到此集合
	 * @param element   注解元素，表示开始查找的起始元素，通常是类、方法、参数等反射元素
	 * @param predicate 注解过滤条件，用于判断注解是否应该被收集到结果集合中
	 */
	private static void seekByHierarchyAnnotation(Set<Annotation> result, AnnotatedElement element, Predicate<Class<? extends Annotation>> predicate) {
		// 使用 HashSet 记录已访问的元素，避免循环依赖和重复处理
		Set<AnnotatedElement> visited = new HashSet<>();
		// 获取第一层候选元素，初始时只包含起始元素
		Set<AnnotatedElement> candidates = getHierarchyAnnotationCandidates(Collections.singleton(element), visited);
		// 迭代处理所有候选元素，直到没有新的候选元素为止
		while (candidates.size() > 0) {
			// 在当前层级的所有候选元素上执行直接查找
			for (AnnotatedElement candidate : candidates) {
				seekDirectly(result, candidate, predicate);
			}
			// 获取下一层级的候选元素，继续迭代查找
			candidates = getHierarchyAnnotationCandidates(candidates, visited);
		}
	}


	/**
	 * 通过类层级结构查找注解
	 * <p>
	 * 该方法在类的继承结构中查找匹配的注解，包括父类链和接口层次结构。
	 * 它会遍历类的所有父类（直到 Object.class）以及所有实现的接口（包括接口的父接口）。
	 * 对于每个找到的类或接口，会执行直接查找和注解层级查找两种方式。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 *
	 * @param result    结果集合，用于收集所有匹配的注解实例，方法执行过程中会将匹配的注解添加到此集合
	 * @param element   类元素，表示要查找注解的起始类
	 * @param predicate 注解过滤条件，用于判断注解是否应该被收集到结果集合中
	 */
	private static void seekByHierarchyClass(Set<Annotation> result, Class<?> element, Predicate<Class<? extends Annotation>> predicate) {
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		// 遍历类层级结构查找注解，直到没有更多的父类和接口
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 检查父类上的注解，包括直接注解和元注解
			if (superclass != null && superclass != Object.class) {
				seekDirectly(result, superclass, predicate);
				seekByHierarchyAnnotation(result, superclass, predicate);
			}

			// 检查所有接口上的注解，包括直接注解和元注解
			for (Class<?> anInterface : interfaces) {
				seekDirectly(result, anInterface, predicate);
				seekByHierarchyAnnotation(result, anInterface, predicate);
			}

			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 通过方法层级结构查找注解
	 * <p>
	 * 该方法在方法的继承结构中查找匹配的注解。
	 * 它会遍历方法所在类的父类链和接口层次结构，在对应的同名方法上查找注解。
	 * 通过方法签名（方法名 + 参数类型）精确定位父类或接口中的对应方法。
	 * 对于每个找到的方法，会执行直接查找和注解层级查找两种方式。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 *
	 * @param result    结果集合，用于收集所有匹配的注解实例，方法执行过程中会将匹配的注解添加到此集合
	 * @param element   方法元素，表示要查找注解的起始方法
	 * @param predicate 注解过滤条件，用于判断注解是否应该被收集到结果集合中
	 */
	private static void seekByHierarchyMethod(Set<Annotation> result, Method element, Predicate<Class<? extends Annotation>> predicate) {
		// 获取声明该方法的类
		Class<?> declaringClass = element.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		// 遍历类层级结构，在对应方法上查找注解，直到没有更多的父类和接口
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法并检查注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 根据方法名和参数类型精确查找父类中的对应方法
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					seekDirectly(result, method, predicate);
					seekByHierarchyAnnotation(result, method, predicate);
				} catch (NoSuchMethodException e) {
					// 父类中不存在该方法，忽略
				}
			}

			// 在接口中查找同名方法并检查注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 根据方法名和参数类型精确查找接口中的对应方法
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					seekDirectly(result, method, predicate);
					seekByHierarchyAnnotation(result, method, predicate);
				} catch (NoSuchMethodException e) {
					// 接口中不存在该方法，忽略
				}
			}

			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}


	/**
	 * 通过参数层级结构查找注解
	 * <p>
	 * 该方法在参数的继承结构中查找匹配的注解。
	 * 它会遍历方法所在类的父类链和接口层次结构，在对应位置的方法参数上查找注解。
	 * 首先定位到父类或接口中的同名方法，然后获取相同位置的参数进行注解查找。
	 * 对于每个找到的参数，会执行直接查找和注解层级查找两种方式。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 *
	 * @param result          结果集合，用于收集所有匹配的注解实例，方法执行过程中会将匹配的注解添加到此集合
	 * @param element         参数元素，表示要查找注解的起始参数
	 * @param declaringMethod 声明该参数的方法，用于定位父类或接口中的对应方法
	 * @param position        参数在方法参数列表中的位置索引，用于获取对应位置的参数
	 * @param predicate       注解过滤条件，用于判断注解是否应该被收集到结果集合中
	 */
	private static void seekByHierarchyParameter(Set<Annotation> result, Parameter element, Method declaringMethod, int position, Predicate<Class<? extends Annotation>> predicate) {
		// 获取声明该方法的类
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		// 遍历类层级结构，在对应参数上查找注解，直到没有更多的父类和接口
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法的对应参数并检查注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 获取父类中同名方法的对应位置参数
					Parameter parameter = superclass.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					seekDirectly(result, parameter, predicate);
					seekByHierarchyAnnotation(result, parameter, predicate);
				} catch (NoSuchMethodException e) {
					// 父类中不存在该方法，忽略
				}
			}

			// 在接口中查找同名方法的对应参数并检查注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 获取接口中同名方法的对应位置参数
					Parameter parameter = anInterface.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					seekDirectly(result, parameter, predicate);
					seekByHierarchyAnnotation(result, parameter, predicate);
				} catch (NoSuchMethodException e) {
					// 接口中不存在该方法，忽略
				}
			}
			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	/**
	 * 获取指定元素上的注解，支持继承查找
	 * <p>
	 * 该方法是获取单个注解的主要入口方法，返回第一个匹配的注解实例。
	 * 查找策略按照以下优先级进行：
	 * 1. 直接查找：在当前元素上直接获取指定类型的注解
	 * 2. 注解层级查找：通过元注解（注解的注解）间接查找
	 * 3. 继承结构查找：根据元素类型（类、方法、参数）在其继承结构中查找
	 *    - 类：在父类和接口中查找
	 *    - 方法：在父类和接口的同名方法中查找
	 *    - 参数：在父类和接口的对应位置参数中查找
	 * 一旦找到匹配的注解就立即返回，不会继续查找。
	 *
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 找到的注解实例，如果未找到则返回 null
	 */
	@Nullable
	public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType) {
		// 直接在当前元素上获取指定类型的注解
		A rs = element.getAnnotation(annotationType);
		if (rs != null) {
			return rs;
		}
		// 通过注解的层级结构（元注解）间接查找指定类型的注解
		rs = seekByHierarchyAnnotation(element, annotationType);
		if (rs != null) {
			return rs;
		}

		// 根据元素类型进行特定的继承结构查找
		if (element instanceof Class) {
			// 在类的继承结构（父类和接口）中查找
			rs = seekByHierarchyClass((Class<?>) element, annotationType);
			return rs;
		} else if (element instanceof Method) {
			// 在方法的继承结构（父类和接口的同名方法）中查找
			return seekByHierarchyMethod((Method) element, annotationType);
		} else if (element instanceof Parameter) {
			// 获取参数所属的方法或构造函数
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				// 获取方法的所有参数，确定当前参数的位置
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						// 在参数的继承结构（父类和接口的对应位置参数）中查找
						return seekByHierarchyParameter(((Parameter) element), (Method) executable, i, annotationType);
					}
				}
			}
		}
		return null;
	}


	/**
	 * 通过注解的继承层级查找指定类型的注解
	 * <p>
	 * 该方法使用迭代方式在注解的层级结构中查找指定类型的单个注解。
	 * 它从给定元素开始，通过元注解（注解上的注解）逐层向上查找，直到找到目标注解或没有更多的候选元素为止。
	 * 使用 visited 集合来避免循环依赖和重复处理，确保算法能够终止。
	 * 与 {@link #seekByHierarchyAnnotation(Set, AnnotatedElement, Predicate)} 不同，该方法只返回第一个匹配的注解。
	 *
	 * @param element        注解元素，表示开始查找的起始元素，通常是类、方法、参数等反射元素
	 * @param annotationType 要查找的注解类型，必须是 Annotation 的子类
	 * @param <A>            注解类型泛型参数
	 * @return 找到的注解实例，如果未找到则返回 null
	 */
	private static <A extends Annotation> A seekByHierarchyAnnotation(AnnotatedElement element, Class<A> annotationType) {
		// 使用 HashSet 记录已访问的元素，避免循环依赖和重复处理
		Set<AnnotatedElement> visited = new HashSet<>();
		// 获取第一层候选元素，初始时只包含起始元素
		Set<AnnotatedElement> candidates = getHierarchyAnnotationCandidates(Collections.singleton(element), visited);
		// 迭代处理所有候选元素，直到没有新的候选元素为止
		while (candidates.size() > 0) {
			// 在当前层级的所有候选元素上查找目标注解
			for (AnnotatedElement candidate : candidates) {
				A rs = candidate.getAnnotation(annotationType);
				if (rs != null) {
					// 找到目标注解，立即返回
					return rs;
				}
			}
			// 获取下一层级的候选元素，继续迭代查找
			candidates = getHierarchyAnnotationCandidates(candidates, visited);
		}
		return null;
	}

	/**
	 * 通过类层级结构查找注解
	 * <p>
	 * 该方法在类的继承结构中查找指定类型的单个注解。
	 * 它会遍历类的所有父类（直到 Object.class）以及所有实现的接口（包括接口的父接口）。
	 * 对于每个找到的类或接口，会先尝试直接获取注解，如果没有则继续在注解层级中查找。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 一旦找到匹配的注解就立即返回，不会继续查找。
	 *
	 * @param element        类元素，表示要查找注解的起始类
	 * @param annotationType 要查找的注解类型，必须是 Annotation 的子类
	 * @param <A>            注解类型泛型参数
	 * @return 找到的注解实例，如果未找到则返回 null
	 */
	private static <A extends Annotation> A seekByHierarchyClass(Class<?> element, Class<A> annotationType) {
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回 null
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构查找注解，直到没有更多的父类和接口或找到目标注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 检查父类上的注解，包括直接注解和元注解
			if (superclass != null && superclass != Object.class) {
				// 尝试直接在父类上获取目标注解
				A rs = superclass.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
				// 在父类的注解层级中间接查找目标注解
				rs = seekByHierarchyAnnotation(superclass, annotationType);
				if (rs != null) {
					return rs;
				}
			}

			// 检查所有接口上的注解，包括直接注解和元注解
			for (Class<?> anInterface : interfaces) {
				// 尝试直接在接口上获取目标注解
				A rs = anInterface.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
				// 在接口的注解层级中间接查找目标注解
				rs = seekByHierarchyAnnotation(anInterface, annotationType);
				if (rs != null) {
					return rs;
				}
			}

			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 通过方法层级结构查找注解
	 * <p>
	 * 该方法在方法的继承结构中查找指定类型的单个注解。
	 * 它会遍历方法所在类的父类链和接口层次结构，在对应的同名方法上查找注解。
	 * 通过方法签名（方法名 + 参数类型）精确定位父类或接口中的对应方法。
	 * 对于每个找到的方法，会先尝试直接获取注解，如果没有则继续在注解层级中查找。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 一旦找到匹配的注解就立即返回，不会继续查找。
	 *
	 * @param element        方法元素，表示要查找注解的起始方法
	 * @param annotationType 要查找的注解类型，必须是 Annotation 的子类
	 * @param <A>            注解类型泛型参数
	 * @return 找到的注解实例，如果未找到则返回 null
	 */
	private static <A extends Annotation> A seekByHierarchyMethod(Method element, Class<A> annotationType) {
		// 获取声明该方法的类
		Class<?> declaringClass = element.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回 null
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构，在对应方法上查找注解，直到没有更多的父类和接口或找到目标注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法并检查注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 根据方法名和参数类型精确查找父类中的对应方法
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					// 尝试直接在方法上获取目标注解
					A rs = method.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					// 在方法的注解层级中间接查找目标注解
					rs = seekByHierarchyAnnotation(method, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 父类中不存在该方法，忽略
				}
			}

			// 在接口中查找同名方法并检查注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 根据方法名和参数类型精确查找接口中的对应方法
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					// 尝试直接在方法上获取目标注解
					A rs = method.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					// 在方法的注解层级中间接查找目标注解
					rs = seekByHierarchyAnnotation(method, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 接口中不存在该方法，忽略
				}
			}

			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 通过参数层级结构查找注解
	 * <p>
	 * 该方法在参数的继承结构中查找指定类型的单个注解。
	 * 它会遍历方法所在类的父类链和接口层次结构，在对应位置的方法参数上查找注解。
	 * 首先定位到父类或接口中的同名方法，然后获取相同位置的参数进行注解查找。
	 * 对于每个找到的参数，会先尝试直接获取注解，如果没有则继续在注解层级中查找。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 一旦找到匹配的注解就立即返回，不会继续查找。
	 *
	 * @param element         参数元素，表示要查找注解的起始参数
	 * @param declaringMethod 声明该参数的方法，用于定位父类或接口中的对应方法
	 * @param position        参数在方法参数列表中的位置索引，用于获取对应位置的参数
	 * @param annotationType  要查找的注解类型，必须是 Annotation 的子类
	 * @param <A>             注解类型泛型参数
	 * @return 找到的注解实例，如果未找到则返回 null
	 */
	private static <A extends Annotation> A seekByHierarchyParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
		// 获取声明该方法的类
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回 null
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构，在对应参数上查找注解，直到没有更多的父类和接口或找到目标注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法的对应参数并检查注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 获取父类中同名方法的对应位置参数
					Parameter parameter = superclass.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					// 尝试直接在参数上获取目标注解
					A rs = parameter.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					// 在参数的注解层级中间接查找目标注解
					rs = seekByHierarchyAnnotation(parameter, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 父类中不存在该方法，忽略
				}
			}

			// 在接口中查找同名方法的对应参数并检查注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 获取接口中同名方法的对应位置参数
					Parameter parameter = anInterface.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					// 尝试直接在参数上获取目标注解
					A rs = parameter.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					// 在参数的注解层级中间接查找目标注解
					rs = seekByHierarchyAnnotation(parameter, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 接口中不存在该方法，忽略
				}
			}
			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 获取可重复注解数组
	 * <p>
	 * 该方法用于获取指定元素上的可重复注解，支持在继承结构中查找。
	 * 查找策略按照以下优先级进行：
	 * 1. 直接查找：在当前元素上直接获取指定类型的注解数组
	 * 2. 注解层级查找：通过元注解（注解的注解）间接查找可重复注解
	 * 3. 继承结构查找：根据元素类型（类、方法、参数）在其继承结构中查找
	 *    - 类：在父类和接口中查找
	 *    - 方法：在父类和接口的同名方法中查找
	 *    - 参数：在父类和接口的对应位置参数中查找
	 * 一旦在任何层级找到匹配的注解就立即返回，不会继续查找。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，可以是类、方法、参数等任何带有注解的程序元素
	 * @param annotationType 要获取的注解类型，必须是 Annotation 的子类
	 * @return 注解数组，如果未找到则返回空数组而不是 null
	 */
	@Nonnull
	public static <A extends Annotation> A[] getRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		// 直接在当前元素上获取指定类型的注解数组
		A[] rs = element.getAnnotationsByType(annotationType);
		if (rs.length == 0) {
			// 如果直接查找未找到，尝试通过注解层级结构间接查找
			{
				A[] arr = seekRepeatableByHierarchyAnnotation(element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}
			}

			// 根据元素类型进行特定的继承结构查找
			if (element instanceof Class) {
				// 在类的继承结构（父类和接口）中查找可重复注解
				A[] arr = seekRepeatableByHierarchyClass((Class<?>) element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}

			} else if (element instanceof Method) {
				// 在方法的继承结构（父类和接口的同名方法）中查找可重复注解
				A[] arr = seekRepeatableByHierarchyMethod((Method) element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}
			} else if (element instanceof Parameter) {
				// 获取参数所属的方法或构造函数
				Executable executable = ((Parameter) element).getDeclaringExecutable();
				if (executable instanceof Method) {
					// 获取方法的所有参数，确定当前参数的位置
					Parameter[] parameters = executable.getParameters();
					for (int i = 0; i < parameters.length; i++) {
						if (parameters[i] == element) {
							// 在参数的继承结构（父类和接口的对应位置参数）中查找可重复注解
							A[] arr = seekRepeatableByHierarchyParameter(((Parameter) element), (Method) executable, i, annotationType);
							if (arr != null && arr.length > 0) {
								return arr;
							}
							break;
						}
					}
				}
			}
		}
		return rs;
	}

	/**
	 * 通过注解层级结构查找可重复注解
	 * <p>
	 * 该方法使用迭代方式在注解的层级结构中查找可重复注解数组。
	 * 它从给定元素开始，通过元注解（注解上的注解）逐层向上查找，直到找到可重复注解或没有更多的候选元素为止。
	 * 使用 visited 集合来避免循环依赖和重复处理，确保算法能够终止。
	 * 一旦在某层找到可重复注解就立即返回，不会继续查找更深层级。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        注解元素，表示开始查找的起始元素，通常是类、方法、参数等反射元素
	 * @param annotationType 要查找的可重复注解类型，必须是 Annotation 的子类
	 * @return 注解数组，如果未找到则返回 null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyAnnotation(AnnotatedElement element, Class<A> annotationType) {
		// 使用 HashSet 记录已访问的元素，避免循环依赖和重复处理
		Set<AnnotatedElement> visited = new HashSet<>();
		// 获取第一层候选元素，初始时只包含起始元素
		Set<AnnotatedElement> candidates = getHierarchyAnnotationCandidates(Collections.singleton(element), visited);
		// 迭代处理所有候选元素，直到没有新的候选元素为止
		while (candidates.size() > 0) {
			// 在当前层级的所有候选元素上查找可重复注解
			for (AnnotatedElement candidate : candidates) {
				A[] rs = candidate.getAnnotationsByType(annotationType);
				if (rs.length > 0) {
					// 找到可重复注解，立即返回
					return rs;
				}
			}
			// 获取下一层级的候选元素，继续迭代查找
			candidates = getHierarchyAnnotationCandidates(candidates, visited);
		}
		return null;
	}

	/**
	 * 通过类层级结构查找可重复注解
	 * <p>
	 * 该方法在类的继承结构中查找可重复注解数组。
	 * 它会遍历类的所有父类（直到 Object.class）以及所有实现的接口（包括接口的父接口）。
	 * 对于每个找到的类或接口，会先尝试直接获取可重复注解，如果没有则继续在注解层级中查找。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 一旦找到匹配的可重复注解就立即返回，不会继续查找。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        类元素，表示要查找可重复注解的起始类
	 * @param annotationType 要查找的可重复注解类型，必须是 Annotation 的子类
	 * @return 注解数组，如果未找到则返回 null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyClass(Class<?> element, Class<A> annotationType) {
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回 null
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}
		// 遍历类层级结构查找可重复注解，直到没有更多的父类和接口或找到目标注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 检查父类上的可重复注解，包括直接注解和元注解
			if (superclass != null && superclass != Object.class) {
				// 尝试直接在父类上获取可重复注解
				A[] rs = superclass.getAnnotationsByType(annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
				// 在父类的注解层级中间接查找可重复注解
				rs = seekRepeatableByHierarchyAnnotation(superclass, annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
			}
			// 检查所有接口上的可重复注解，包括直接注解和元注解
			for (Class<?> anInterface : interfaces) {
				// 尝试直接在接口上获取可重复注解
				A[] rs = anInterface.getAnnotationsByType(annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
				// 在接口的注解层级中间接查找可重复注解
				rs = seekRepeatableByHierarchyAnnotation(anInterface, annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
			}

			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 通过方法层级结构查找可重复注解
	 * <p>
	 * 该方法在方法的继承结构中查找可重复注解数组。
	 * 它会遍历方法所在类的父类链和接口层次结构，在对应的同名方法上查找可重复注解。
	 * 通过方法签名（方法名 + 参数类型）精确定位父类或接口中的对应方法。
	 * 对于每个找到的方法，会先尝试直接获取可重复注解，如果没有则继续在注解层级中查找。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 一旦找到匹配的可重复注解就立即返回，不会继续查找。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param element        方法元素，表示要查找可重复注解的起始方法
	 * @param annotationType 要查找的可重复注解类型，必须是 Annotation 的子类
	 * @return 注解数组，如果未找到则返回 null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyMethod(Method element, Class<A> annotationType) {
		// 获取声明该方法的类
		Class<?> declaringClass = element.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回 null
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}
		// 遍历类层级结构，在对应方法上查找可重复注解，直到没有更多的父类和接口或找到目标注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法并检查可重复注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 根据方法名和参数类型精确查找父类中的对应方法
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					// 尝试直接在方法上获取可重复注解
					A[] rs = method.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					// 在方法的注解层级中间接查找可重复注解
					rs = seekRepeatableByHierarchyAnnotation(method, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 父类中不存在该方法，忽略
				}
			}

			// 在接口中查找同名方法并检查可重复注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 根据方法名和参数类型精确查找接口中的对应方法
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					// 尝试直接在方法上获取可重复注解
					A[] rs = method.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					// 在方法的注解层级中间接查找可重复注解
					rs = seekRepeatableByHierarchyAnnotation(method, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 接口中不存在该方法，忽略
				}
			}

			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}


	/**
	 * 通过参数层级结构查找可重复注解
	 * <p>
	 * 该方法在参数的继承结构中查找可重复注解数组。
	 * 它会遍历方法所在类的父类链和接口层次结构，在对应位置的方法参数上查找可重复注解。
	 * 首先定位到父类或接口中的同名方法，然后获取相同位置的参数进行注解查找。
	 * 对于每个找到的参数，会先尝试直接获取可重复注解，如果没有则继续在注解层级中查找。
	 * 使用 visited 集合避免重复处理和循环依赖。
	 * 一旦找到匹配的可重复注解就立即返回，不会继续查找。
	 *
	 * @param <A>             注解类型泛型参数，必须是 Annotation 的子类
	 * @param element         参数元素，表示要查找可重复注解的起始参数
	 * @param declaringMethod 声明该参数的方法，用于定位父类或接口中的对应方法
	 * @param position        参数在方法参数列表中的位置索引，用于获取对应位置的参数
	 * @param annotationType  要查找的可重复注解类型，必须是 Annotation 的子类
	 * @return 注解数组，如果未找到则返回 null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
		// 获取声明该方法的类
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		// 使用 LinkedHashSet 记录已访问的元素，保持插入顺序并避免重复处理
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		// 获取第一层候选类（直接父类和直接接口）
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		// 如果没有父类和接口，说明已经到达继承树顶端，直接返回 null
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构，在对应参数上查找可重复注解，直到没有更多的父类和接口或找到目标注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法的对应参数并检查可重复注解
			if (superclass != null && superclass != Object.class) {
				try {
					// 获取父类中同名方法的对应位置参数
					Parameter parameter = superclass.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					// 尝试直接在参数上获取可重复注解
					A[] rs = parameter.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					// 在参数的注解层级中间接查找可重复注解
					rs = seekRepeatableByHierarchyAnnotation(parameter, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 父类中不存在该方法，忽略
				}
			}

			// 在接口中查找同名方法的对应参数并检查可重复注解
			for (Class<?> anInterface : interfaces) {
				try {
					// 获取接口中同名方法的对应位置参数
					Parameter parameter = anInterface.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					// 尝试直接在参数上获取可重复注解
					A[] rs = parameter.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					// 在参数的注解层级中间接查找可重复注解
					rs = seekRepeatableByHierarchyAnnotation(parameter, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
					// 接口中不存在该方法，忽略
				}
			}
			// 获取下一层级的候选类（父类的父类、接口的父接口）
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}
}

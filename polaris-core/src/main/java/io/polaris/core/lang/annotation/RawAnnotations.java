package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
@SuppressWarnings("ALL")
public class RawAnnotations {

	/**
	 * 获取指定元素上的注解，支持继承查找
	 *
	 * @param element        注解元素（类、方法、参数等）
	 * @param annotationType 要获取的注解类型
	 * @return 找到的注解实例，如果未找到则返回null
	 */
	@Nullable
	public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType) {
		// directly
		A rs = element.getAnnotation(annotationType);
		if (rs != null) {
			return rs;
		}
		// indirectly
		rs = seekByHierarchyAnnotation(element, annotationType);
		if (rs != null) {
			return rs;
		}

		// 根据元素类型进行特定的继承查找
		if (element instanceof Class) {
			rs = seekByHierarchyClass((Class<?>) element, annotationType);
			return rs;
		} else if (element instanceof Method) {
			return seekByHierarchyMethod((Method) element, annotationType);
		} else if (element instanceof Parameter) {
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						return seekByHierarchyParameter(((Parameter) element), (Method) executable, i, annotationType);
					}
				}
			}
		}
		return null;
	}


	/**
	 * 通过注解的继承层级查找指定类型的注解
	 *
	 * @param element        注解元素
	 * @param annotationType 要查找的注解类型
	 * @return 找到的注解实例，如果未找到则返回null
	 */
	private static <A extends Annotation> A seekByHierarchyAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new HashSet<>();
		AnnotatedElement[] candidates = getHierarchyAnnotationCandidates(new AnnotatedElement[]{element}, visited);
		while (candidates.length > 0) {
			for (AnnotatedElement candidate : candidates) {
				A rs = candidate.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
			}
			candidates = getHierarchyAnnotationCandidates(candidates, visited);
		}
		return null;
	}

	/**
	 * 通过类层级结构查找注解
	 *
	 * @param element        类元素
	 * @param annotationType 要查找的注解类型
	 * @return 找到的注解实例，如果未找到则返回null
	 */
	private static <A extends Annotation> A seekByHierarchyClass(Class<?> element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构查找注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 检查父类上的注解
			if (superclass != null && superclass != Object.class) {
				A rs = superclass.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
				rs = seekByHierarchyAnnotation(superclass, annotationType);
				if (rs != null) {
					return rs;
				}
			}

			// 检查接口上的注解
			for (Class<?> anInterface : interfaces) {
				A rs = anInterface.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
				rs = seekByHierarchyAnnotation(anInterface, annotationType);
				if (rs != null) {
					return rs;
				}
			}

			// next level
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 通过方法层级结构查找注解
	 *
	 * @param element        方法元素
	 * @param annotationType 要查找的注解类型
	 * @return 找到的注解实例，如果未找到则返回null
	 */
	private static <A extends Annotation> A seekByHierarchyMethod(Method element, Class<A> annotationType) {
		Class<?> declaringClass = element.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构，在对应方法上查找注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法并检查注解
			if (superclass != null && superclass != Object.class) {
				try {
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					A rs = method.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					rs = seekByHierarchyAnnotation(method, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}

			// 在接口中查找同名方法并检查注解
			for (Class<?> anInterface : interfaces) {
				try {
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					A rs = method.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					rs = seekByHierarchyAnnotation(method, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}

			// next level
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 通过参数层级结构查找注解
	 *
	 * @param element         参数元素
	 * @param declaringMethod 声明该参数的方法
	 * @param position        参数在方法参数列表中的位置
	 * @param annotationType  要查找的注解类型
	 * @return 找到的注解实例，如果未找到则返回null
	 */
	private static <A extends Annotation> A seekByHierarchyParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		// 遍历类层级结构，在对应参数上查找注解
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			// 在父类中查找同名方法的对应参数并检查注解
			if (superclass != null && superclass != Object.class) {
				try {
					Parameter parameter = superclass.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					A rs = parameter.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					rs = seekByHierarchyAnnotation(parameter, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}

			// 在接口中查找同名方法的对应参数并检查注解
			for (Class<?> anInterface : interfaces) {
				try {
					Parameter parameter = anInterface.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					A rs = parameter.getAnnotation(annotationType);
					if (rs != null) {
						return rs;
					}
					rs = seekByHierarchyAnnotation(parameter, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}
			// next level
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 获取层级注解候选元素数组
	 *
	 * @param candidates 当前候选元素数组
	 * @param visited    已访问过的元素集合，用于避免重复处理
	 * @return 下一层级的候选元素数组
	 */
	private static AnnotatedElement[] getHierarchyAnnotationCandidates(AnnotatedElement[] candidates, Set<AnnotatedElement> visited) {
		// 收集当前候选元素上的所有注解类型作为下一轮的候选元素
		Set<AnnotatedElement> candidateSet = new LinkedHashSet<>();
		for (AnnotatedElement candidate : candidates) {
			Annotation[] annotations = candidate.getAnnotations();
			for (Annotation annotation : annotations) {
				Class<? extends Annotation> type = annotation.annotationType();
				if (visited.contains(type)) {
					continue;
				}
				candidateSet.add(type);
				visited.add(type);
			}
		}
		candidates = candidateSet.toArray(new AnnotatedElement[0]);
		candidateSet.clear();
		return candidates;
	}

	/**
	 * 获取类继承结构中的候选类
	 *
	 * @param superclass 父类
	 * @param interfaces 接口数组
	 * @param visited    已访问的类集合，用于避免重复处理
	 * @return 包含下一个父类和接口数组的元组
	 */
	private static Tuple2<Class<?>, Class<?>[]> getHierarchyClassCandidates(Class<?> superclass, Class<?>[] interfaces, Set<AnnotatedElement> visited) {
		Set<Class<?>> candidates = new LinkedHashSet<>();
		if (interfaces == null) {
			// first level
			if (superclass != null && superclass != Object.class) {
				interfaces = superclass.getInterfaces();
				for (Class<?> anInterface : interfaces) {
					if (visited.contains(anInterface)) {
						continue;
					}
					candidates.add(anInterface);
					visited.add(anInterface);
				}
				superclass = superclass.getSuperclass();
			}
			return Tuple2.of(superclass, candidates.toArray(new Class[0]));
		} else {
			// next level
			if (superclass != null && superclass != Object.class) {
				for (Class<?> anInterface : superclass.getInterfaces()) {
					if (visited.contains(anInterface)) {
						continue;
					}
					candidates.add(anInterface);
					visited.add(anInterface);
				}
				superclass = superclass.getSuperclass();
			}
			for (Class<?> anInterface : interfaces) {
				for (Class<?> anInterfaceInterface : anInterface.getInterfaces()) {
					if (visited.contains(anInterfaceInterface)) {
						continue;
					}
					candidates.add(anInterfaceInterface);
					visited.add(anInterfaceInterface);
				}
			}
			return Tuple2.of(superclass, candidates.toArray(new Class[0]));
		}
	}


	/**
	 * 获取可重复注解数组
	 *
	 * @param <A>            注解类型
	 * @param element        注解元素（类、方法、参数等）
	 * @param annotationType 要获取的注解类型
	 * @return 注解数组，如果未找到则返回空数组
	 */
	@Nonnull
	public static <A extends Annotation> A[] getRepeatableAnnotation(AnnotatedElement element, Class<A> annotationType) {
		A[] rs = element.getAnnotationsByType(annotationType);
		if (rs.length == 0) {
			// indirectly
			{
				A[] arr = seekRepeatableByHierarchyAnnotation(element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}
			}

			if (element instanceof Class) {
				A[] arr = seekRepeatableByHierarchyClass((Class<?>) element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}

			} else if (element instanceof Method) {
				A[] arr = seekRepeatableByHierarchyMethod((Method) element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}
			} else if (element instanceof Parameter) {
				Executable executable = ((Parameter) element).getDeclaringExecutable();
				if (executable instanceof Method) {
					Parameter[] parameters = executable.getParameters();
					for (int i = 0; i < parameters.length; i++) {
						if (parameters[i] == element) {
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
	 *
	 * @param <A>            注解类型
	 * @param element        注解元素
	 * @param annotationType 要查找的注解类型
	 * @return 注解数组，如果未找到则返回null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new HashSet<>();
		AnnotatedElement[] candidates = getHierarchyAnnotationCandidates(new AnnotatedElement[]{element}, visited);
		while (candidates.length > 0) {
			for (AnnotatedElement candidate : candidates) {
				A[] rs = candidate.getAnnotationsByType(annotationType);
				if (rs.length > 0) {
					return rs;
				}
			}
			candidates = getHierarchyAnnotationCandidates(candidates, visited);
		}
		return null;
	}

	/**
	 * 通过类层级结构查找可重复注解
	 *
	 * @param <A>            注解类型
	 * @param element        类元素
	 * @param annotationType 要查找的注解类型
	 * @return 注解数组，如果未找到则返回null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyClass(Class<?> element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				A[] rs = superclass.getAnnotationsByType(annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
				rs = seekRepeatableByHierarchyAnnotation(superclass, annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
			}
			for (Class<?> anInterface : interfaces) {
				A[] rs = anInterface.getAnnotationsByType(annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
				rs = seekRepeatableByHierarchyAnnotation(anInterface, annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
			}

			// next level
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	/**
	 * 通过方法层级结构查找可重复注解
	 *
	 * @param <A>            注解类型
	 * @param element        方法元素
	 * @param annotationType 要查找的注解类型
	 * @return 注解数组，如果未找到则返回null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyMethod(Method element, Class<A> annotationType) {
		Class<?> declaringClass = element.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				try {
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					A[] rs = method.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					rs = seekRepeatableByHierarchyAnnotation(method, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}
			for (Class<?> anInterface : interfaces) {
				try {
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					A[] rs = method.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					rs = seekRepeatableByHierarchyAnnotation(method, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}

			// next level
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}


	/**
	 * 通过参数层级结构查找可重复注解
	 *
	 * @param <A>             注解类型
	 * @param element         参数元素
	 * @param declaringMethod 声明该参数的方法
	 * @param position        参数在方法参数列表中的位置
	 * @param annotationType  要查找的注解类型
	 * @return 注解数组，如果未找到则返回null
	 */
	private static <A extends Annotation> A[] seekRepeatableByHierarchyParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}

		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				try {
					Parameter parameter = superclass.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					A[] rs = parameter.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					rs = seekRepeatableByHierarchyAnnotation(parameter, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}
			for (Class<?> anInterface : interfaces) {
				try {
					Parameter parameter = anInterface.getDeclaredMethod(
							declaringMethod.getName(), declaringMethod.getParameterTypes())
						.getParameters()[position];
					A[] rs = parameter.getAnnotationsByType(annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
					rs = seekRepeatableByHierarchyAnnotation(parameter, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}
			// next level
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}
}

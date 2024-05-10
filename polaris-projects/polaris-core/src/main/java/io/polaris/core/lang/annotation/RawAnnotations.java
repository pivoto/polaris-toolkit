package io.polaris.core.lang.annotation;

import io.polaris.core.tuple.Tuple2;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Qt
 * @since  Jan 06, 2024
 */
@SuppressWarnings("ALL")
class RawAnnotations {

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

	private static <A extends Annotation> A seekByHierarchyClass(Class<?> element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return null;
		}
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
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

	private static <A extends Annotation> A seekByHierarchyMethod(Method element, Class<A> annotationType) {
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

	private static <A extends Annotation> A seekByHierarchyParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
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

	private static AnnotatedElement[] getHierarchyAnnotationCandidates(AnnotatedElement[] candidates, Set<AnnotatedElement> visited) {
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

	private static Tuple2<Class<?>, Class<?>[]> getHierarchyClassCandidates(Class<?> superclass, Class<?>[] interfaces, Set<AnnotatedElement> visited) {
		Set<Class<?>> candidates = new LinkedHashSet<>();
		if (interfaces == null) {
			// first level
			if (superclass != null && superclass != Object.class) {
				interfaces = superclass.getInterfaces();
				for (Class<?> anInterface : interfaces) {
					candidates.add(anInterface);
					visited.add(anInterface);
				}
			}
			superclass = superclass.getSuperclass();
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

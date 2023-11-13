package io.polaris.core.lang;

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
 * @since 1.8,  Nov 12, 2023
 */
@SuppressWarnings("ALL")
public class Annotations {


	public static <A extends Annotation> A get(AnnotatedElement element, Class<A> annotationType) {
		// directly
		A rs = element.getAnnotation(annotationType);
		if (rs != null) {
			return rs;
		}
		// indirectly
		rs = seekByAnnotation(element, annotationType);
		if (rs != null) {
			return rs;
		}

		if (element instanceof Class) {
			rs = seekByClass((Class<?>) element, annotationType);
			return rs;
		} else if (element instanceof Method) {
			return seekByMethod((Method) element, annotationType);
		} else if (element instanceof Parameter) {
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						return seekByParameter(((Parameter) element), (Method) executable, i, annotationType);
					}
				}
			}
		}
		return null;
	}


	private static <A extends Annotation> A seekByAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new HashSet<>();
		AnnotatedElement[] candidates = getAnnotationCandidates(new AnnotatedElement[]{element}, visited);
		while (candidates.length > 0) {
			for (AnnotatedElement candidate : candidates) {
				A rs = candidate.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
			}
			candidates = getAnnotationCandidates(candidates, visited);
		}
		return null;
	}

	private static <A extends Annotation> A seekByClass(Class<?> element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getClassCandidates(element, null, visited);
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
				rs = seekByAnnotation(superclass, annotationType);
				if (rs != null) {
					return rs;
				}
			}
			for (Class<?> anInterface : interfaces) {
				A rs = anInterface.getAnnotation(annotationType);
				if (rs != null) {
					return rs;
				}
				rs = seekByAnnotation(anInterface, annotationType);
				if (rs != null) {
					return rs;
				}
			}

			// next level
			classCandidates = getClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	private static <A extends Annotation> A seekByMethod(Method element, Class<A> annotationType) {
		Class<?> declaringClass = element.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getClassCandidates(declaringClass, null, visited);
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
					rs = seekByAnnotation(method, annotationType);
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
					rs = seekByAnnotation(method, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}

			// next level
			classCandidates = getClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	private static <A extends Annotation> A seekByParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getClassCandidates(declaringClass, null, visited);
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
					rs = seekByAnnotation(parameter, annotationType);
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
					rs = seekByAnnotation(parameter, annotationType);
					if (rs != null) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}
			// next level
			classCandidates = getClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	private static AnnotatedElement[] getAnnotationCandidates(AnnotatedElement[] candidates, Set<AnnotatedElement> visited) {
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

	private static Tuple2<Class<?>, Class<?>[]> getClassCandidates(Class<?> superclass, Class<?>[] interfaces, Set<AnnotatedElement> visited) {
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


	public static <A extends Annotation> A[] getRepeatable(AnnotatedElement element, Class<A> annotationType) {
		A[] rs = element.getAnnotationsByType(annotationType);
		if (rs.length == 0) {
			// indirectly
			{
				A[] arr = seekRepeatableByAnnotation(element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}
			}

			if (element instanceof Class) {
				A[] arr = seekRepeatableByClass((Class<?>) element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}

			} else if (element instanceof Method) {
				A[] arr = seekRepeatableByMethod((Method) element, annotationType);
				if (arr != null && arr.length > 0) {
					return arr;
				}
			} else if (element instanceof Parameter) {
				Executable executable = ((Parameter) element).getDeclaringExecutable();
				if (executable instanceof Method) {
					Parameter[] parameters = executable.getParameters();
					for (int i = 0; i < parameters.length; i++) {
						if (parameters[i] == element) {
							A[] arr = seekRepeatableByParameter(((Parameter) element), (Method) executable, i, annotationType);
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

	private static <A extends Annotation> A[] seekRepeatableByAnnotation(AnnotatedElement element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new HashSet<>();
		AnnotatedElement[] candidates = getAnnotationCandidates(new AnnotatedElement[]{element}, visited);
		while (candidates.length > 0) {
			for (AnnotatedElement candidate : candidates) {
				A[] rs = candidate.getAnnotationsByType(annotationType);
				if (rs.length > 0) {
					return rs;
				}
			}
			candidates = getAnnotationCandidates(candidates, visited);
		}
		return null;
	}

	private static <A extends Annotation> A[] seekRepeatableByClass(Class<?> element, Class<A> annotationType) {
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getClassCandidates(element, null, visited);
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
				rs = seekRepeatableByAnnotation(superclass, annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
			}
			for (Class<?> anInterface : interfaces) {
				A[] rs = anInterface.getAnnotationsByType(annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
				rs = seekRepeatableByAnnotation(anInterface, annotationType);
				if (rs != null && rs.length > 0) {
					return rs;
				}
			}

			// next level
			classCandidates = getClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}

	private static <A extends Annotation> A[] seekRepeatableByMethod(Method element, Class<A> annotationType) {
		Class<?> declaringClass = element.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getClassCandidates(declaringClass, null, visited);
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
					rs = seekRepeatableByAnnotation(method, annotationType);
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
					rs = seekRepeatableByAnnotation(method, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}

			// next level
			classCandidates = getClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}


	private static <A extends Annotation> A[] seekRepeatableByParameter(Parameter element, Method declaringMethod, int position, Class<A> annotationType) {
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		Set<AnnotatedElement> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getClassCandidates(declaringClass, null, visited);
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
					rs = seekRepeatableByAnnotation(parameter, annotationType);
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
					rs = seekRepeatableByAnnotation(parameter, annotationType);
					if (rs != null && rs.length > 0) {
						return rs;
					}
				} catch (NoSuchMethodException e) {
				}
			}
			// next level
			classCandidates = getClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
		return null;
	}
}

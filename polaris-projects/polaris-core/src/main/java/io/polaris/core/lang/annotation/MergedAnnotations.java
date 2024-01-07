package io.polaris.core.lang.annotation;

import io.polaris.core.tuple.Tuple2;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Jan 06, 2024
 */
class MergedAnnotations {
	private SortedMap<Integer, Set<MergedAnnotation>> sortedAnnotations = new TreeMap<>();

	public MergedAnnotations(AnnotatedElement element) {
		scanAnnotations(0, element);
	}

	public static MergedAnnotations of(AnnotatedElement element) {
		return new MergedAnnotations(element);
	}

	static SortedMap<Integer, Set<MergedAnnotation>> scanHierarchyAnnotations(MergedAnnotation annotation) {
		SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations = new TreeMap<>();
		Set<Class<? extends Annotation>> visitedAnnotation = new HashSet<>();
		Collection<MergedAnnotation> candidates = Collections.singletonList(annotation);
		while (!candidates.isEmpty()) {
			candidates = scanHierarchyAnnotation(hierarchyAnnotations, candidates, visitedAnnotation);
		}
		return hierarchyAnnotations;
	}

	private static void addHierarchyAnnotation(SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations, MergedAnnotation mergedAnnotation) {
		Set<MergedAnnotation> annotations = hierarchyAnnotations.computeIfAbsent(mergedAnnotation.getDistance(), k -> new LinkedHashSet<>());
		annotations.add(mergedAnnotation);
	}

	private static Collection<MergedAnnotation> scanHierarchyAnnotation(SortedMap<Integer, Set<MergedAnnotation>> hierarchyAnnotations, Collection<MergedAnnotation> lastCandidates, Set<Class<? extends Annotation>> visited) {
		Collection<MergedAnnotation> candidates = new LinkedHashSet<>();
		for (MergedAnnotation mergedAnnotation : lastCandidates) {
			Class<? extends Annotation> annotationType = mergedAnnotation.getAnnotationType();
			if (annotationType.getPackage().getName().equals("java.lang.annotation")) {
				continue;
			}
			if (visited.contains(annotationType)) {
				continue;
			}
			visited.add(annotationType);

			if (!mergedAnnotation.isRepeatable()) {
				// alias
				Method[] annotationMembers = AnnotationAttributes.getAnnotationMembers(annotationType);
				Map<Class<? extends Annotation>, Map<String, Method>> aliasMap = new LinkedHashMap<>();

				for (Method method : annotationMembers) {
					Alias alias = method.getAnnotation(Alias.class);
					if (alias != null && alias.annotation() != annotationType && alias.annotation() != Alias.DEFAULT_ANNOTATION) {
						Map<String, Method> aliasMethods = aliasMap.computeIfAbsent(alias.annotation(), k -> new LinkedHashMap<>());
						aliasMethods.put(alias.value(), method);
					}
				}
				if (!aliasMap.isEmpty()) {
					for (Map.Entry<Class<? extends Annotation>, Map<String, Method>> entry : aliasMap.entrySet()) {
						Class<? extends Annotation> aliasAnnotationType = entry.getKey();
						Map<String, Method> aliasMethods = entry.getValue();

						MergedAnnotation aliasMergeAnnotation = MergedAnnotation.of(mergedAnnotation.getDistance() + 1, annotationType, aliasAnnotationType, mergedAnnotation, aliasMethods);
						addHierarchyAnnotation(hierarchyAnnotations, aliasMergeAnnotation);
						candidates.add(aliasMergeAnnotation);
					}
				}
			}

			for (Annotation annotation : annotationType.getAnnotations()) {
				MergedAnnotation relation = MergedAnnotation.of(mergedAnnotation.getDistance() + 1, annotationType, annotation);
				addHierarchyAnnotation(hierarchyAnnotations, relation);
				candidates.add(relation);
			}
		}
		return candidates;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <A extends Annotation> A getMergedAnnotation(Class<A> annotationType) {
		MergedAnnotation matchedOne = null;
		List<MergedAnnotation> aliasList = new ArrayList<>();

		loop:
		for (Map.Entry<Integer, Set<MergedAnnotation>> entry : sortedAnnotations.entrySet()) {
			Set<MergedAnnotation> set = entry.getValue();
			for (MergedAnnotation annotation : set) {
				MatchedMergedAnnotation<A> matchedAnnotation = annotation.getMatchedAnnotation(annotationType);
				if (matchedAnnotation != null) {
					// match
					MergedAnnotation matched = matchedAnnotation.getMatched();
					if (matched != null) {
						matchedOne = matched;
						break loop;
					}
					aliasList.addAll(matchedAnnotation.getAliases());
				}
			}
		}
		if (matchedOne == null && aliasList.isEmpty()) {
			// not match
			return null;
		}

		return MatchedMergedAnnotation.of(annotationType, matchedOne, aliasList).getAnnotation();
	}

	public <A extends Annotation> Set<A> getMergedRepeatableAnnotation(Class<A> annotationType) {
		Set<MatchedMergedAnnotation<A>> matchedSet = null;
		loop:
		for (Map.Entry<Integer, Set<MergedAnnotation>> entry : sortedAnnotations.entrySet()) {
			Set<MergedAnnotation> set = entry.getValue();
			for (MergedAnnotation annotation : set) {
				matchedSet = annotation.getMatchedRepeatableAnnotation(annotationType);
				if (matchedSet != null) {
					// match
					break loop;
				}
			}
		}
		if (matchedSet == null) {
			return Collections.emptySet();
		}
		Set<A> annotationSet = new LinkedHashSet<>();
		for (MatchedMergedAnnotation<A> matchedMergedAnnotation : matchedSet) {
			annotationSet.add(matchedMergedAnnotation.getAnnotation());
		}
		return annotationSet;
	}


	private void addMergedAnnotation(MergedAnnotation mergedAnnotation) {
		Set<MergedAnnotation> annotations = sortedAnnotations.computeIfAbsent(mergedAnnotation.getDistance(), k -> new LinkedHashSet<>());
		annotations.add(mergedAnnotation);
	}

	private void scanAnnotations(int distance, AnnotatedElement element) {
		Set<MergedAnnotation> first = new LinkedHashSet<>();

		Annotation[] annotations = element.getAnnotations();
		for (Annotation annotation : annotations) {
			MergedAnnotation mergedAnnotation = MergedAnnotation.of(distance, element, annotation);
			first.add(mergedAnnotation);
			addMergedAnnotation(mergedAnnotation);
		}

		if (element instanceof Class) {
			scanHierarchyClass(distance + 1, (Class<?>) element);
		} else if (element instanceof Method) {
			scanHierarchyMethod(distance + 1, (Method) element);
		} else if (element instanceof Parameter) {
			Executable executable = ((Parameter) element).getDeclaringExecutable();
			if (executable instanceof Method) {
				Parameter[] parameters = executable.getParameters();
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i] == element) {
						scanHierarchyParameter(distance + 1, ((Parameter) element), (Method) executable, i);
					}
				}
			}
		}
	}

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
			}
			superclass = superclass.getSuperclass();
			return Tuple2.of(superclass, candidates.toArray(new Class[0]));
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
			return Tuple2.of(superclass, candidates.toArray(new Class[0]));
		}
	}

	private void scanHierarchyClass(int distance, Class<?> element) {
		Set<Class<?>> visitedClass = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(element, null, visitedClass);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		int nextDistance = distance;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				scanAnnotations(nextDistance, superclass);
			}
			for (Class<?> anInterface : interfaces) {
				scanAnnotations(nextDistance, anInterface);
			}

			// next level
			nextDistance++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visitedClass);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	private void scanHierarchyMethod(int distance, Method element) {
		Class<?> declaringClass = element.getDeclaringClass();
		Set<Class<?>> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		int nextDistance = distance;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				try {
					Method method = superclass.getDeclaredMethod(element.getName(), element.getParameterTypes());
					scanAnnotations(nextDistance, method);
				} catch (NoSuchMethodException e) {
				}
			}
			for (Class<?> anInterface : interfaces) {
				try {
					Method method = anInterface.getDeclaredMethod(element.getName(), element.getParameterTypes());
					scanAnnotations(nextDistance, method);
				} catch (NoSuchMethodException e) {
				}
			}

			// next level
			nextDistance++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}


	private void scanHierarchyParameter(int distance, Parameter element, Method declaringMethod, int position) {
		Class<?> declaringClass = declaringMethod.getDeclaringClass();
		Set<Class<?>> visited = new LinkedHashSet<>();
		Tuple2<Class<?>, Class<?>[]> classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		Class<?> superclass = classCandidates.getFirst();
		Class<?>[] interfaces = classCandidates.getSecond();
		if ((superclass == null || superclass == Object.class) && interfaces.length == 0) {
			return;
		}

		int nextDistance = distance;
		while (superclass != null && superclass != Object.class || interfaces.length > 0) {
			if (superclass != null && superclass != Object.class) {
				try {
					Parameter parameter = superclass.getDeclaredMethod(declaringMethod.getName(), declaringMethod.getParameterTypes()).getParameters()[position];
					scanAnnotations(nextDistance, parameter);
				} catch (NoSuchMethodException e) {
				}
			}
			for (Class<?> anInterface : interfaces) {
				try {
					Parameter parameter = anInterface.getDeclaredMethod(declaringMethod.getName(), declaringMethod.getParameterTypes()).getParameters()[position];
					scanAnnotations(nextDistance, parameter);
				} catch (NoSuchMethodException e) {
				}
			}
			// next level
			nextDistance++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getFirst();
			interfaces = classCandidates.getSecond();
		}
	}

	public SortedMap<Integer, Set<MergedAnnotation>> getSortedAnnotations() {
		return Collections.unmodifiableSortedMap(sortedAnnotations);
	}

}

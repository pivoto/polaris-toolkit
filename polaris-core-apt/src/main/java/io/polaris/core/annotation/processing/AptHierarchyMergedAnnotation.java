package io.polaris.core.annotation.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * @author Qt
 * @since Oct 07, 2025
 */
public class AptHierarchyMergedAnnotation {
	private final List<Set<AptMergedAnnotation>> sortedAnnotations = new ArrayList<>();
	private final ProcessingEnvironment env;


	AptHierarchyMergedAnnotation(ProcessingEnvironment env, Element element) {
		this.env = env;
		scanAnnotations(0, env, element);
	}

	static AptHierarchyMergedAnnotation of(ProcessingEnvironment env, Element element) {
		return new AptHierarchyMergedAnnotation(env, element);
	}

	/**
	 * 获取按距离排序的注解映射
	 *
	 * @return 按距离排序的注解映射的不可变视图
	 */
	public List<Set<AptMergedAnnotation>> getSortedAnnotations() {
		return Collections.unmodifiableList(sortedAnnotations);
	}


	/**
	 * 获取合并后的注解
	 *
	 * @param annotationType 要获取的注解类型
	 * @return 合并后的注解实例，如果未找到则返回null
	 */
	public AptAnnotationAttributes getMergedAnnotation(TypeElement annotationType) {
		AptMergedAnnotation matchedAnnotation = null;
		List<AptMergedAnnotation> aliasAnnotations = new ArrayList<>();
		// 遍历所有层级，尽可能合并所有别名属性
		for (Set<AptMergedAnnotation> set : sortedAnnotations) {
			for (AptMergedAnnotation annotation : set) {
				AptMatchedMergedAnnotation target = annotation.getMatchedAnnotation(annotationType);
				if (target != null) {
					// match
					AptMergedAnnotation matched = target.getMatchedAnnotation();
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
			return AptMatchedMergedAnnotation.of(env, annotationType, matchedAnnotation, aliasAnnotations).getAnnotationAttributes();
		}

		// 如果只存在纯别名配置，则汇总所有别名属性并合并成一个注解实例
		if (!aliasAnnotations.isEmpty()) {
			return AptMatchedMergedAnnotation.of(env, annotationType, null, aliasAnnotations).getAnnotationAttributes();
		}
		return null;
	}

	/**
	 * 获取合并后的可重复注解集合
	 *
	 * @param annotationType 要获取的注解类型
	 * @return 合并后的注解集合
	 */
	public Set<AptAnnotationAttributes> getMergedRepeatableAnnotation(TypeElement annotationType) {
		Set<AptMatchedMergedAnnotation> result = new LinkedHashSet<>();
		// 遍历所有层级，尽可能找到所有匹配的注解
		for (Set<AptMergedAnnotation> set : sortedAnnotations) {
			for (AptMergedAnnotation annotation : set) {
				Set<AptMatchedMergedAnnotation> matchedSet = annotation.getMatchedRepeatableAnnotation(annotationType);
				if (matchedSet != null) {
					// match
					result.addAll(matchedSet);
				}
			}
		}
		Set<AptAnnotationAttributes> annotationSet = new LinkedHashSet<>();
		for (AptMatchedMergedAnnotation matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.getAnnotationAttributes());
		}
		return annotationSet;
	}

	/**
	 * 获取合并后的可重复注解集合
	 *
	 * @param annotationType 要获取的注解类型
	 * @return 合并后的注解集合
	 */
	public Set<AptAnnotationAttributes> getTopMergedRepeatableAnnotation(TypeElement annotationType) {
		Set<AptMatchedMergedAnnotation> result = new LinkedHashSet<>();
		// 遍历所有层级，找到存在匹配的注解的层级后跳出
		for (Set<AptMergedAnnotation> set : sortedAnnotations) {
			boolean found = false;
			for (AptMergedAnnotation annotation : set) {
				Set<AptMatchedMergedAnnotation> matchedSet = annotation.getTopMatchedRepeatableAnnotation(annotationType);
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
		Set<AptAnnotationAttributes> annotationSet = new LinkedHashSet<>();
		for (AptMatchedMergedAnnotation matchedMergedAnnotation : result) {
			annotationSet.add(matchedMergedAnnotation.getAnnotationAttributes());
		}
		return annotationSet;
	}

	private void addMergedAnnotation(AptMergedAnnotation annotation) {
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
	private void scanAnnotations(int level, ProcessingEnvironment env, Element element) {
		List<? extends AnnotationMirror> annotationMirrors = env.getElementUtils().getAllAnnotationMirrors(element);
		if (annotationMirrors != null && !annotationMirrors.isEmpty()) {
			for (AnnotationMirror annotation : annotationMirrors) {
				AptMergedAnnotation mergedAnnotation = AptMergedAnnotation.of(env, level, annotation);
				addMergedAnnotation(mergedAnnotation);
			}
		}

		if (element instanceof TypeElement) {
			scanHierarchyClass(level + 1, (TypeElement) element);
		} else if (element.getKind() == ElementKind.METHOD && element instanceof ExecutableElement) {
			scanHierarchyMethod(level + 1, (ExecutableElement) element);
		}

	}

	/**
	 * 扫描类的继承结构中的注解
	 *
	 * @param level   层次深度
	 * @param element 类元素
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyClass(int level, TypeElement element) {
		Set<TypeElement> visitedClass = new LinkedHashSet<>();
		ClassCandidates classCandidates = getHierarchyClassCandidates(element, null, visitedClass);
		TypeElement superclass = classCandidates.getSuperclass();
		TypeElement[] interfaces = classCandidates.getInterfaces();

		TypeMirror objectType = env.getElementUtils().getTypeElement(Object.class.getName()).asType();
		if ((superclass == null || env.getTypeUtils().isSameType(objectType, superclass.asType())) && interfaces.length == 0) {
			return;
		}

		int nextLevel = level;
		while (superclass != null && !env.getTypeUtils().isSameType(objectType, superclass.asType()) || interfaces.length > 0) {
			if (superclass != null && !env.getTypeUtils().isSameType(objectType, superclass.asType())) {
				scanAnnotations(nextLevel, env, superclass);
			}
			for (TypeElement anInterface : interfaces) {
				scanAnnotations(nextLevel, env, anInterface);
			}

			// next level
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visitedClass);
			superclass = classCandidates.getSuperclass();
			interfaces = classCandidates.getInterfaces();
		}
	}

	/**
	 * 扫描方法的继承结构中的注解
	 *
	 * @param level   层次深度
	 * @param element 方法元素
	 */
	@SuppressWarnings("DuplicatedCode")
	private void scanHierarchyMethod(int level, ExecutableElement element) {
		Element enclosingElement = element.getEnclosingElement();
		if (!(enclosingElement instanceof TypeElement)) {
			return;
		}

		TypeElement declaringClass = (TypeElement) enclosingElement;
		Set<TypeElement> visited = new LinkedHashSet<>();
		ClassCandidates classCandidates = getHierarchyClassCandidates(declaringClass, null, visited);
		TypeElement superclass = classCandidates.getSuperclass();
		TypeElement[] interfaces = classCandidates.getInterfaces();

		TypeMirror objectType = env.getElementUtils().getTypeElement(Object.class.getName()).asType();
		if ((superclass == null || env.getTypeUtils().isSameType(objectType, superclass.asType())) && interfaces.length == 0) {
			return;
		}

		int nextLevel = level;
		while (superclass != null && !env.getTypeUtils().isSameType(objectType, superclass.asType()) || interfaces.length > 0) {
			if (superclass != null && !env.getTypeUtils().isSameType(objectType, superclass.asType())) {
				try {
					for (Element enclosedElement : superclass.getEnclosedElements()) {
						if (enclosedElement.getKind() == ElementKind.METHOD && enclosedElement instanceof ExecutableElement) {
							if (AptAnnotations.equals(env, element, (ExecutableElement) enclosedElement)) {
								scanAnnotations(nextLevel, env, enclosedElement);
								break;
							}
						}
					}
				} catch (Throwable ignored) {
				}
			}
			for (TypeElement anInterface : interfaces) {
				try {
					for (Element enclosedElement : anInterface.getEnclosedElements()) {
						if (enclosedElement.getKind() == ElementKind.METHOD && enclosedElement instanceof ExecutableElement) {
							if (AptAnnotations.equals(env, element, (ExecutableElement) enclosedElement)) {
								scanAnnotations(nextLevel, env, enclosedElement);
								break;
							}
						}
					}
				} catch (Throwable ignored) {
				}
			}

			// next level
			nextLevel++;
			classCandidates = getHierarchyClassCandidates(superclass, interfaces, visited);
			superclass = classCandidates.getSuperclass();
			interfaces = classCandidates.getInterfaces();
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
	private ClassCandidates getHierarchyClassCandidates(TypeElement superclass, TypeElement[] interfaces, Set<TypeElement> visitedClass) {
		TypeMirror objectType = env.getElementUtils().getTypeElement(Object.class.getName()).asType();
		Set<TypeElement> candidates = new LinkedHashSet<>();
		if (interfaces == null) {
			// first level
			if (superclass != null && !env.getTypeUtils().isSameType(objectType, superclass.asType())) {
				List<? extends TypeMirror> interfaces0 = superclass.getInterfaces();
				for (TypeMirror o : interfaces0) {
					if (o instanceof DeclaredType) {
						TypeElement anInterface = (TypeElement) ((DeclaredType) o).asElement();
						candidates.add(anInterface);
						visitedClass.add(anInterface);
					}
				}
				TypeMirror typeMirror = superclass.getSuperclass();
				if (typeMirror instanceof DeclaredType) {
					superclass = (TypeElement) ((DeclaredType) typeMirror).asElement();
				} else {
					superclass = null;
				}
			}
		} else {
			// next level
			if (superclass != null && !env.getTypeUtils().isSameType(objectType, superclass.asType())) {
				for (TypeMirror o : superclass.getInterfaces()) {
					if (o instanceof DeclaredType) {
						TypeElement anInterface = (TypeElement) ((DeclaredType) o).asElement();
						if (visitedClass.contains(anInterface)) {
							continue;
						}
						candidates.add(anInterface);
						visitedClass.add(anInterface);
					}
				}
				TypeMirror typeMirror = superclass.getSuperclass();
				if (typeMirror instanceof DeclaredType) {
					superclass = (TypeElement) ((DeclaredType) typeMirror).asElement();
				} else {
					superclass = null;
				}
			}
			for (TypeElement anInterface : interfaces) {
				for (TypeMirror o1 : anInterface.getInterfaces()) {
					if (o1 instanceof DeclaredType) {
						TypeElement anInterfaceInterface = (TypeElement) ((DeclaredType) o1).asElement();
						if (visitedClass.contains(anInterfaceInterface)) {
							continue;
						}
						candidates.add(anInterfaceInterface);
						visitedClass.add(anInterfaceInterface);
					}
				}
			}
		}
		return new ClassCandidates(superclass, candidates.toArray(new TypeElement[0]));
	}


	@RequiredArgsConstructor
	@Getter
	static class ClassCandidates {
		private final TypeElement superclass;
		private final TypeElement[] interfaces;
	}
}

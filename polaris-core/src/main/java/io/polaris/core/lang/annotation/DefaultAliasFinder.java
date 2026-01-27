package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
public class DefaultAliasFinder implements AliasFinder {

	public static final DefaultAliasFinder INSTANCE = new DefaultAliasFinder();

	/**
	 * 查找指定注解元素上的所有别名属性
	 * 此方法会遍历元素的所有注解及其层级结构，查找所有符合别名规则的属性
	 *
	 * @param element 需要查找别名属性的注解元素，可以是类、方法、字段等具有注解的元素
	 * @return 包含所有找到的别名属性的集合，如果没有找到则返回空集合
	 */
	@Override
	public Set<AliasAttribute> findAliasAttributes(AnnotatedElement element) {
		// 创建已访问注解类型的集合，用于防止循环遍历
		Set<Class<? extends Annotation>> visited = new HashSet<>();
		// 创建存储别名属性的有序集合
		Set<AliasAttribute> attributes = new LinkedHashSet<>();
		// 扫描注解层级结构以查找别名属性
		scanHierarchyAnnotation(attributes, element.getAnnotations(), visited);
		return attributes;
		/* Alias alias = element.getAnnotation(Alias.class);
		if (alias == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(new AliasAttribute(alias.value(), alias.annotation())); */
	}

	/**
	 * 扫描注解层次结构以查找别名属性
	 * 遍历给定的注解数组，查找其中的别名注解，并递归扫描注解的元注解
	 *
	 * @param attributes 存储找到的别名属性的集合
	 * @param annotations 需要扫描的注解数组
	 * @param visited 已访问过的注解类型集合，用于防止循环引用
	 * @return 包含所有找到的别名属性的集合
	 */
	private Set<AliasAttribute> scanHierarchyAnnotation(Set<AliasAttribute> attributes, Annotation[] annotations, Set<Class<? extends Annotation>> visited) {
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (Alias.class.isAssignableFrom(annotationType)) {
				Alias alias = (Alias) annotation;
				attributes.add(new AliasAttribute(alias.value(), alias.annotation()));
			} else {
				String simpleName = annotationType.getSimpleName();
				if (simpleName.equals("Alias") || simpleName.equals("AliasFor")) {
					// 尝试匹配其他类型的Alias
					Method[] methods = annotationType.getDeclaredMethods();
					String value = null;
					Class<? extends Annotation> anno = null;
					for (Method method : methods) {
						if (method.getParameterCount() == 0 && method.getReturnType() != void.class
							&& !method.isSynthetic()
							&& !method.isBridge()
							&& !Modifier.isStatic(method.getModifiers())) {
							String name = method.getName();
							if (method.getReturnType() == String.class) {
								if (value == null) {
									if (name.equals("value") || name.equals("attribute") || name.equals("name")) {
										String v = Reflects.invokeQuietly(annotation, method);
										if (v != null && !(v = v.trim()).isEmpty()) {
											value = v;
										}
									}
								}
							} else if (method.getReturnType() == Class.class) {
								if (anno == null) {
									if (name.equals("annotation") || name.equals("annotationType") || name.equals("type")) {
										Class<?> v = Reflects.invokeQuietly(annotation, method);
										if (v != null && Annotation.class.isAssignableFrom(v)) {
											//noinspection unchecked
											anno = (Class<? extends Annotation>) v;
										}
									}
								}
							}
							if (value != null && anno != null) {
								break;
							}
						}
					}

					if (value != null && anno != null) {
						attributes.add(new AliasAttribute(value, anno));
					}
				}

				// 检查是否已访问过此注解类型，避免循环引用
				if (visited.contains(annotationType)) {
					continue;
				}
				visited.add(annotationType);

				// 递归扫描非java.lang.annotation包下的注解的元注解
				if (!annotationType.getPackage().getName().equals("java.lang.annotation")) {
					scanHierarchyAnnotation(attributes, annotationType.getAnnotations(), visited);
				}
			}
		}
		return attributes;
	}


}
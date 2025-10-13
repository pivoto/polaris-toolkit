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

	@Override
	public Set<AliasAttribute> findAliasAttributes(AnnotatedElement element) {
		Set<Class<? extends Annotation>> visited = new HashSet<>();
		Set<AliasAttribute> attributes = new LinkedHashSet<>();
		scanHierarchyAnnotation(attributes, element.getAnnotations(), visited);
		return attributes;
		/* Alias alias = element.getAnnotation(Alias.class);
		if (alias == null) {
			return Collections.emptySet();
		}
		return Collections.singleton(new AliasAttribute(alias.value(), alias.annotation())); */
	}

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

				if (visited.contains(annotationType)) {
					return attributes;
				}
				visited.add(annotationType);

				if (!annotationType.getPackage().getName().equals("java.lang.annotation")) {
					scanHierarchyAnnotation(attributes, annotationType.getAnnotations(), visited);
				}
			}
		}
		return attributes;
	}


}

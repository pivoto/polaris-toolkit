package io.polaris.core.annotation.processing;

import java.lang.annotation.Repeatable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
public class AptAnnotations {


	public static AptAnnotationAttributes getMergedAnnotation(ProcessingEnvironment env, Element element, TypeElement annotationType) {
		return AptHierarchyMergedAnnotation.of(env, element).getMergedAnnotation(annotationType);
	}

	public static Set<AptAnnotationAttributes> getMergedRepeatableAnnotation(ProcessingEnvironment env, Element element, TypeElement annotationType) {
		return AptHierarchyMergedAnnotation.of(env, element).getMergedRepeatableAnnotation(annotationType);
	}

	public static Set<AptAnnotationAttributes> getTopMergedRepeatableAnnotation(ProcessingEnvironment env, Element element, TypeElement annotationType) {
		return AptHierarchyMergedAnnotation.of(env, element).getTopMergedRepeatableAnnotation(annotationType);
	}

	public static TypeElement getRepeatedAnnotationType(TypeElement annotationType) {
		List<? extends Element> methods = annotationType.getEnclosedElements();
		if (methods.size() != 1) {
			return null;
		}
		ExecutableElement method = (ExecutableElement) methods.get(0);
		String name = method.getSimpleName().toString();
		if (!name.equals("value")) {
			return null;
		}
		TypeMirror returnType = method.getReturnType();
		if (returnType.getKind() != TypeKind.ARRAY) {
			return null;
		}

		TypeMirror componentType = ((ArrayType) returnType).getComponentType();
		if (componentType.getKind() != TypeKind.DECLARED) {
			return null;
		}
		if (componentType instanceof DeclaredType) {
			DeclaredType declaredType = (DeclaredType) componentType;
			Repeatable annotation = componentType.getAnnotation(Repeatable.class);
			if (annotation == null) {
				return null;
			}
			String qualifiedName = annotationType.getQualifiedName().toString();
			if (annotation.value().getCanonicalName().equals(qualifiedName)) {
				return (TypeElement) declaredType.asElement();
			}
		}
		return null;
	}

	public static AnnotationValue asTargetTypeIfNecessary(ProcessingEnvironment env, AnnotationValue value, TypeMirror typeMirror) {
		if (value == null) {
			return null;
		}
		Object obj = value.getValue();
		if (obj == null) {
			return value;
		}
		if (AptAnnotations.isInstance(env, typeMirror, obj)) {
			return value;
		}

		obj = convertAnnotationValue(env, obj, typeMirror);
		return new InternalAnnotationValue(value, obj);
	}

	@Nullable
	private static Object convertAnnotationValue(ProcessingEnvironment env, Object obj, TypeMirror typeMirror) {
		// 尝试转换为目标类型，这里并不完善，现仅支持常用的功能
		try {
			TypeKind kind = typeMirror.getKind();
			if (kind == TypeKind.BOOLEAN) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj);
				}
				if (obj instanceof String) {
					return Boolean.parseBoolean((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).intValue() != 0;
				}
				if (obj instanceof Character) {
					return ((Character) obj).charValue() != 0;
				}
				if (obj instanceof List<?>) {
					return !((List<?>) obj).isEmpty();
				}
			} else if (kind == TypeKind.BYTE) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (byte) 1 : (byte) 0;
				}
				if (obj instanceof String) {
					return Byte.parseByte((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).byteValue();
				}
				if (obj instanceof Character) {
					return (byte) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.SHORT) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (short) 1 : (short) 0;
				}
				if (obj instanceof String) {
					return Short.parseShort((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).shortValue();
				}
				if (obj instanceof Character) {
					return (short) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.INT) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (int) 1 : (int) 0;
				}
				if (obj instanceof String) {
					return Integer.parseInt((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).intValue();
				}
				if (obj instanceof Character) {
					return (int) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.LONG) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (long) 1 : (long) 0;
				}
				if (obj instanceof String) {
					return Long.parseLong((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).longValue();
				}
				if (obj instanceof Character) {
					return (long) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.CHAR) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (char) 1 : (char) 0;
				}
				if (obj instanceof String) {
					return !((String) obj).isEmpty() ? ((String) obj).charAt(0) : '\0';
				}
				if (obj instanceof Number) {
					return (char) ((Number) obj).intValue();
				}
				if (obj instanceof Character) {
					return (char) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.FLOAT) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (float) 1 : (float) 0;
				}
				if (obj instanceof String) {
					return Float.parseFloat((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).floatValue();
				}
				if (obj instanceof Character) {
					return (float) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.DOUBLE) {
				if (obj instanceof Boolean) {
					return ((Boolean) obj) ? (double) 1 : (double) 0;
				}
				if (obj instanceof String) {
					return Double.parseDouble((String) obj);
				}
				if (obj instanceof Number) {
					return ((Number) obj).doubleValue();
				}
				if (obj instanceof Character) {
					return (double) ((Character) obj).charValue();
				}
			} else if (kind == TypeKind.DECLARED) {
				Element element = ((DeclaredType) typeMirror).asElement();
				if (element instanceof TypeElement) {
					ElementKind elementKind = element.getKind();
					TypeElement typeElement = (TypeElement) element;
					if (typeElement.getQualifiedName().toString().equals("java.lang.String")) {
						if (obj instanceof String) {
							return ((String) obj);
						} else if (obj instanceof Number || obj instanceof Boolean || obj instanceof Character) {
							return obj.toString();
						} else if (obj instanceof TypeMirror) {
							return ((TypeElement) ((DeclaredType) obj).asElement()).getQualifiedName();
						} else if (obj instanceof VariableElement) {
							return ((VariableElement) obj).getSimpleName();
						}
					} else if (typeElement.getQualifiedName().toString().equals("java.lang.Class")) {
						if (obj instanceof TypeMirror) {
							return obj;
						}
						if (obj instanceof AnnotationMirror) {
							return ((AnnotationMirror) obj).getAnnotationType();
						}
					} else if (elementKind == ElementKind.ENUM) {
						if (obj instanceof VariableElement) {
							return ((VariableElement) obj);
						}
					} else if (elementKind == ElementKind.ANNOTATION_TYPE) {
						if (obj instanceof AnnotationMirror) {
							return (AnnotationMirror) obj;
						}
					}
				}
			} else if (kind == TypeKind.ARRAY) {
				TypeMirror componentType = ((ArrayType) typeMirror).getComponentType();
				if (obj instanceof List<?>) {
					return obj;
				}
			}
		} catch (Throwable ignored) {
		}
		return null;
	}

	public static boolean isInstance(ProcessingEnvironment env, TypeMirror mirror, Object obj) {
		TypeElement objType = env.getElementUtils().getTypeElement(obj.getClass().getCanonicalName());
		return env.getTypeUtils().isAssignable(objType.asType(), mirror);
	}


	public static boolean equals(ProcessingEnvironment env, TypeMirror value1, TypeMirror value2) {
		if (value1 == value2) {
			return true;
		}
		if (value1 == null || value2 == null) {
			return false;
		}
		return env.getTypeUtils().isSameType(value1, value2);
	}


	public static boolean equals(ProcessingEnvironment env, ExecutableElement value1, ExecutableElement value2) {
		if (value1 == value2) {
			return true;
		}
		if (value1 == null || value2 == null) {
			return false;
		}
		if (value1.getKind() != value2.getKind()) {
			return false;
		}

		String name1 = value1.getSimpleName().toString();
		String name2 = value2.getSimpleName().toString();
		if (name1.equals(name2)) {
			List<? extends VariableElement> parameters1 = value1.getParameters();
			List<? extends VariableElement> parameters2 = value2.getParameters();
			int size = parameters1.size();
			if (size == parameters2.size()) {
				for (int i = 0; i < size; i++) {
					VariableElement parameter1 = parameters1.get(i);
					VariableElement parameter2 = parameters2.get(i);
					if (!env.getTypeUtils().isSameType(parameter1.asType(), parameter2.asType())) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public static boolean equals(ProcessingEnvironment env, AnnotationValue value1, AnnotationValue value2) {
		if (value1 == value2) {
			return true;
		}
		if (value1 == null || value2 == null) {
			return false;
		}
		Object obj1 = value1.getValue();
		Object obj2 = value2.getValue();

		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		// 根据 obj1 的类型进行比较
		if (obj1 instanceof String) {
			return obj1.equals(obj2);
		}
		if (obj1 instanceof Number || obj1 instanceof Boolean || obj1 instanceof Character) {
			// Number 和 Boolean 可以直接用 equals
			return obj1.equals(obj2);
		}
		if (obj1 instanceof VariableElement) {
			if (!(obj2 instanceof VariableElement)) {
				return false;
			}
			// 处理枚举常量
			// 比较枚举常量的名称
			VariableElement enumConstant1 = (VariableElement) obj1;
			VariableElement enumConstant2 = (VariableElement) obj2;
			return enumConstant1.getSimpleName().equals(enumConstant2.getSimpleName());
		}
		if (obj1 instanceof AnnotationMirror) {
			if (!(obj2 instanceof AnnotationMirror)) {
				return false;
			}
			// 处理嵌套注解，递归比较
			AnnotationMirror mirror1 = (AnnotationMirror) obj1;
			AnnotationMirror mirror2 = (AnnotationMirror) obj2;
			return equals(env, mirror1, mirror2);
		}
		if (obj1 instanceof List<?>) {
			if (!(obj2 instanceof List<?>)) {
				return false;
			}

			List<?> list1 = (List<?>) obj1;
			List<?> list2 = (List<?>) obj2;
			if (list1.size() != list2.size()) {
				return false;
			}

			for (int i = 0; i < list1.size(); i++) {
				Object item1 = list1.get(i);
				Object item2 = list2.get(i);
				if (!(item1 instanceof AnnotationValue && item2 instanceof AnnotationValue)) {
					return false;
				}
				if (!equals(env, (AnnotationValue) item1, (AnnotationValue) item2)) {
					return false;
				}
			}
			return true;
		}
		if (obj1 instanceof TypeMirror) {
			if (!(obj2 instanceof TypeMirror)) {
				return false;
			}
			// 处理 Class 类型的注解属性
			TypeMirror typeMirror1 = (TypeMirror) obj1;
			TypeMirror typeMirror2 = (TypeMirror) obj2;
			return env.getTypeUtils().isSameType(typeMirror1, typeMirror2);
		}

		// 对于未知类型，使用 equals 作为最后的尝试
		return obj1.equals(obj2);
	}

	public static boolean equals(ProcessingEnvironment env, AnnotationMirror mirror1, AnnotationMirror mirror2) {
		if (mirror1 == mirror2) {
			return true;
		}
		if (mirror1 == null || mirror2 == null) {
			return false;
		}

		// 1. 比较注解类型是否相同
		if (!env.getTypeUtils().isSameType(mirror1.getAnnotationType(), mirror2.getAnnotationType())) {
			return false;
		}
		// 2. 比较所有属性值是否相同
		// 注意：需要处理属性顺序不同但值相同的情况
		// 因此，我们将其转换为 Map 再进行比较
		Map<? extends ExecutableElement, ? extends AnnotationValue> values1 = mirror1.getElementValues();
		Map<? extends ExecutableElement, ? extends AnnotationValue> values2 = mirror2.getElementValues();

		if (values1 == values2) {
			return true;
		}
		if (values1 == null || values2 == null) {
			return false;
		}
		if (values1.size() != values2.size()) {
			return false;
		}
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values1.entrySet()) {
			ExecutableElement key = entry.getKey();
			AnnotationValue value1 = entry.getValue();
			AnnotationValue value2 = values2.get(key);

			if (!equals(env, value1, value2)) {
				return false;
			}
		}

		return true;
	}

}

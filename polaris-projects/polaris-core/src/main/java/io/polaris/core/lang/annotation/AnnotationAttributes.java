package io.polaris.core.lang.annotation;

import io.polaris.core.converter.Converters;
import io.polaris.core.reflect.Reflects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Jan 06, 2024
 */
@SuppressWarnings({"all"})
public class AnnotationAttributes {

	private final Map<String, MemberValue> memberValues;
	private final Class<? extends Annotation> annotationType;

	private AnnotationAttributes(Annotation annotation) {
		this.annotationType = annotation.annotationType();
		Method[] methods = getAnnotationMembers(annotationType);
		Map<String, MemberValue> memberValues = new LinkedHashMap<>();
		for (Method method : methods) {
			String name = method.getName();
			Object value = Reflects.invokeQuietly(annotation, method);
			memberValues.put(name, new MemberValue(method, value, method.getDefaultValue()));
		}
		for (Method method : methods) {
			Alias alias = method.getAnnotation(Alias.class);
			if (alias != null && (alias.annotation() == this.annotationType || alias.annotation() == Alias.DEFAULT_ANNOTATION)) {
				String aliasName = alias.value();
				String name = method.getName();
				MemberValue memberValue = memberValues.get(name);
				Object value = memberValue.getValue();
				if (!Objects.equals(value, memberValue.getDefaultValue())) {
					MemberValue aliasMemberValue = memberValues.get(aliasName);
					if (aliasMemberValue != null) {
						aliasMemberValue.setValue(value);
					}
				}
			}
		}
		this.memberValues = Collections.unmodifiableMap(memberValues);
	}


	private AnnotationAttributes(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
		Method[] methods = getAnnotationMembers(annotationType);
		Map<String, MemberValue> memberValues = new LinkedHashMap<>();
		for (Method method : methods) {
			String name = method.getName();
			memberValues.put(name, new MemberValue(method, method.getDefaultValue(), method.getDefaultValue()));
		}
		this.memberValues = Collections.unmodifiableMap(memberValues);
	}


	public static <A extends Annotation> AnnotationAttributes of(A annotation) {
		return new AnnotationAttributes(annotation);
	}

	public static <A extends Annotation> AnnotationAttributes of(Class<A> annotationType) {
		return new AnnotationAttributes(annotationType);
	}

	public static <A extends Annotation> Method[] getAnnotationMembers(Class<A> annotationType) {
		Method[] methods = annotationType.getDeclaredMethods();
		List<Method> list = new ArrayList<>();
		for (Method method : methods) {
			if ((method.getParameterCount() == 0 && method.getReturnType() != void.class)) {
				list.add(method);
			}
		}
		return list.toArray(new Method[0]);
	}

	public Annotation asAnnotation() {
		return Annotations.newInstance(annotationType, Collections.unmodifiableMap(asMap()));
	}

	public Map<String, Object> asMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		memberValues.forEach((k, v) -> map.put(k, v.getValue()));
		return map;
	}

	public Map<String, MemberValue> getMemberValues() {
		return memberValues;
	}

	public MemberValue getMemberValue(String name) {
		return memberValues.get(name);
	}

	public void set(Map<String, Object> values) {
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	public boolean set(String name, Object value) {
		MemberValue memberValue = getMemberValue(name);
		if (memberValue != null) {
			return memberValue.setValue(value);
		}
		return false;
	}

	public Object get(String name) {
		MemberValue memberValue = getMemberValue(name);
		return memberValue == null ? null : memberValue.getValue();
	}

	public String getString(String name) {
		return Converters.convert(String.class, get(name));
	}

	public String[] getStringArray(String name) {
		return Converters.convert(String[].class, get(name));
	}

	public Boolean getBoolean(String name) {
		return Converters.convert(Boolean.class, get(name));
	}

	public Number getNumber(String name) {
		return Converters.convert(Number.class, get(name));
	}

	public Enum getEnum(String name) {
		return Converters.convert(Enum.class, get(name));
	}

	public Class getClass(String name) {
		return Converters.convert(Class.class, get(name));
	}

	public Class[] getClassArray(String name) {
		return Converters.convert(Class[].class, get(name));
	}

	public <V extends Annotation> V getAnnotation(String name, Class<V> annotationType) {
		return Converters.convert(annotationType, get(name));
	}

	public <V extends Annotation> AnnotationAttributes getAnnotationAttributes(String name, Class<V> annotationType) {
		V a = Converters.convert(annotationType, get(name));
		return AnnotationAttributes.of(a);
	}

	public <V extends Annotation> V[] getAnnotationArray(String name, Class<V> annotationType) {
		return Converters.convert(Array.newInstance(annotationType, 0).getClass(), get(name));
	}

	public <V extends Annotation> AnnotationAttributes[] getAnnotationAttributesArray(String name, Class<V> annotationType) {
		V[] arr = Converters.convert(Array.newInstance(annotationType, 0).getClass(), get(name));
		AnnotationAttributes[] attributes = new AnnotationAttributes[arr.length];
		for (int i = 0; i < arr.length; i++) {
			attributes[i] = AnnotationAttributes.of(arr[i]);
		}
		return attributes;
	}

	@Getter
	@AllArgsConstructor
	@ToString
	public static class MemberValue {
		private Method method;
		private Object value;
		private Object defaultValue;

		public boolean setValue(Object value) {
			if (value == null || value.equals(defaultValue)) {
				return false;
			}
			Object o = Converters.convertQuietly(method.getGenericReturnType(), value);
			if (o != null) {
				this.value = o;
				return true;
			}
			return false;
		}
	}

}

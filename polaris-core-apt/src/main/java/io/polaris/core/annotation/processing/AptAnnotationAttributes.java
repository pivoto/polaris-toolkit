package io.polaris.core.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import io.polaris.core.lang.annotation.Alias;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since Oct 07, 2025
 */
@SuppressWarnings({"all"})
@ToString
@EqualsAndHashCode
public class AptAnnotationAttributes implements Cloneable {

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private final ProcessingEnvironment env;
	private final Map<String, Member> members;
	private final TypeElement annotationType;
	private final Map<String, String> aliasMembers;

	private AptAnnotationAttributes(ProcessingEnvironment env, Map<String, Member> members, TypeElement annotationType, Map<String, String> aliasMembers) {
		this.env = env;
		this.annotationType = annotationType;
		if (members == null || members.isEmpty()) {
			this.members = Collections.unmodifiableMap(new LinkedHashMap<>());
		} else {
			Map<String, Member> map = new LinkedHashMap<>();
			members.forEach((k, v) -> {
				map.put(k, v.clone());
			});
			this.members = Collections.unmodifiableMap(map);
		}
		if (aliasMembers == null || aliasMembers.isEmpty()) {
			this.aliasMembers = Collections.unmodifiableMap(new HashMap<>());
		} else {
			this.aliasMembers = Collections.unmodifiableMap(new HashMap<String, String>(aliasMembers));
		}
	}

	private AptAnnotationAttributes(ProcessingEnvironment env, AnnotationMirror annotation) {
		this.env = env;
		this.annotationType = (TypeElement) annotation.getAnnotationType().asElement();
		ExecutableElement[] methods = getAnnotationMembers(annotationType);
		Map<String, String> aliasMembers = getAliasMembers(methods);
		Map<String, Member> memberValues = new LinkedHashMap<>();
		Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = annotation.getElementValues();
		for (ExecutableElement method : methods) {
			AnnotationValue value = annotationValues.get(method);
			String name = method.getSimpleName().toString();
			AnnotationValue defaultValue = method.getDefaultValue();
			memberValues.put(name, new Member(env, method, value, defaultValue));
		}
		aliasMembers.forEach((k, v) -> {
			Member member = memberValues.get(k);
			Member aliasMember = memberValues.get(v);
			if (member != null && aliasMember != null) {
				if (!member.isDefault()) {
					aliasMember.setValue(member.getValue());
				}
			}
		});
		this.members = Collections.unmodifiableMap(memberValues);
		this.aliasMembers = Collections.unmodifiableMap(aliasMembers);
	}

	private AptAnnotationAttributes(ProcessingEnvironment env, TypeElement annotationType) {
		this.env = env;
		this.annotationType = annotationType;
		ExecutableElement[] methods = getAnnotationMembers(annotationType);
		Map<String, String> aliasMembers = getAliasMembers(methods);
		Map<String, Member> members = new LinkedHashMap<>();
		for (ExecutableElement method : methods) {
			String name = method.getSimpleName().toString();
			AnnotationValue defaultValue = method.getDefaultValue();
			members.put(name, new Member(env, method, defaultValue, defaultValue));
		}
		aliasMembers.forEach((k, v) -> {
			Member member = members.get(k);
			Member aliasMember = members.get(v);
			if (member != null && aliasMember != null) {
				if (!member.isDefault()) {
					aliasMember.setValue(member.getValue());
				}
			}
		});
		this.members = Collections.unmodifiableMap(members);
		this.aliasMembers = Collections.unmodifiableMap(aliasMembers);
	}

	private Map<String, String> getAliasMembers(ExecutableElement[] methods) {
		Map<String, String> aliasMembers = new HashMap<>();
		TypeMirror defaultAnnotationType = env.getElementUtils().getTypeElement(Annotation.class.getCanonicalName()).asType();
		for (ExecutableElement method : methods) {
			List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
			if (annotationMirrors != null) {
				for (AnnotationMirror annotationMirror : annotationMirrors) {
					TypeElement element = (TypeElement) annotationMirror.getAnnotationType().asElement();
					if (element.getQualifiedName().toString().equals(Alias.class.getCanonicalName())) {
						Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
						String value = null;
						DeclaredType annotation = null;
						for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
							ExecutableElement key = entry.getKey();
							if (key.getSimpleName().toString().equals("annotation")) {
								Object v = entry.getValue().getValue();
								if (v instanceof DeclaredType) {
									annotation = (DeclaredType) v;
								}
							} else if (key.getSimpleName().toString().equals("value")) {
								Object v = entry.getValue().getValue();
								if (v instanceof String) {
									value = (String) v;
								}
							}
						}
						if (annotation != null && value != null) {
							if (AptAnnotations.equals(env, annotation, annotationType.asType())
								|| AptAnnotations.equals(env, annotation, defaultAnnotationType)) {
								String aliasName = value;
								String name = method.getSimpleName().toString();
								aliasMembers.putIfAbsent(name, aliasName);
							}
						}
					}
				}
			}
			/* Alias alias = method.getAnnotation(Alias.class);
			if (alias != null && (alias.annotation().getCanonicalName().equals(this.annotationType.getQualifiedName().toString()) || alias.annotation() == Annotation.class)) {
				String aliasName = alias.value();
				String name = method.getSimpleName().toString();
				aliasMembers.putIfAbsent(name, aliasName);
			} */
		}
		return aliasMembers;
	}

	public static <A extends Annotation> AptAnnotationAttributes of(ProcessingEnvironment env, AnnotationMirror annotation) {
		return new AptAnnotationAttributes(env, annotation);
	}


	public static <A extends Annotation> AptAnnotationAttributes of(ProcessingEnvironment env, TypeElement annotationType) {
		return new AptAnnotationAttributes(env, annotationType);
	}

	public static <A extends Annotation> ExecutableElement[] getAnnotationMembers(TypeElement annotationType) {
		List<ExecutableElement> list = new ArrayList<>();
		List<? extends Element> methods = annotationType.getEnclosedElements();
		for (Element o : methods) {
			if (o instanceof ExecutableElement) {
				ExecutableElement method = (ExecutableElement) o;
				if (method.getParameters().size() == 0 && method.getReturnType().getKind() != TypeKind.VOID) {
					list.add(method);
				}
			}
		}
		return list.toArray(new ExecutableElement[0]);
	}

	@Override
	public AptAnnotationAttributes clone() {
		return new AptAnnotationAttributes(env, members, annotationType, aliasMembers);
	}


	public Map<String, AnnotationValue> asMap() {
		Map<String, AnnotationValue> map = new LinkedHashMap<>();
		members.forEach((k, v) -> map.put(k, v.getValue()));
		return map;
	}

	public Set<String> getMemberNames() {
		return members.keySet();
	}

	public Map<String, Member> getMembers() {
		return members;
	}

	public boolean hasMember(String name) {
		return members.containsKey(name);
	}

	public Member getMember(String name) {
		return members.get(name);
	}

	public void set(Map<String, AnnotationValue> values) {
		for (Map.Entry<String, AnnotationValue> entry : values.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 设置属性值, 如果属性存在且赋值成功, 则返回true，否则返回false
	 *
	 * @param name  属性名
	 * @param value 属性值
	 * @return 是否设置成功
	 */
	public boolean set(String name, AnnotationValue value) {
		boolean rs = false;
		Member member = members.get(name);
		if (member != null) {
			member.setValue(value);
			rs = true;
		}
		String alias = aliasMembers.get(name);
		Member aliasMember = members.get(alias);
		if (aliasMember != null) {
			aliasMember.setValue(value);
			rs = true;
		}
		return rs;
	}

	public void setIf(Map<String, AnnotationValue> values, BiPredicate<Member, AnnotationValue> predicate) {
		for (Map.Entry<String, AnnotationValue> entry : values.entrySet()) {
			setIf(entry.getKey(), entry.getValue(), predicate);
		}
	}

	/**
	 * 根据条件设置属性值
	 * 只有当属性存在且满足给定条件时才设置新值
	 *
	 * @param name      属性名称
	 * @param value     要设置的值
	 * @param predicate 判断条件，用于决定是否设置值
	 * @return 如果成功设置了值则返回true，否则返回false
	 */
	public boolean setIf(String name, AnnotationValue value, BiPredicate<Member, AnnotationValue> predicate) {
		boolean rs = false;
		Member member = members.get(name);
		if (member != null) {
			if (predicate.test(member, value)) {
				member.setValue(value);
				rs = true;
			}
		}
		String alias = aliasMembers.get(name);
		Member aliasMember = members.get(alias);
		if (aliasMember != null) {
			if (predicate.test(member, value)) {
				aliasMember.setValue(value);
				rs = true;
			}
		}
		return rs;
	}

	public void setIfNotDefault(Map<String, AnnotationValue> values) {
		for (Map.Entry<String, AnnotationValue> entry : values.entrySet()) {
			setIfNotDefault(entry.getKey(), entry.getValue());
		}
	}

	public boolean setIfNotDefault(String name, AnnotationValue value) {
		return setIf(name, value, (member, val) -> !member.isDefault(val));
	}

	public AnnotationValue get(String name) {
		Member member = members.get(name);
		return member == null ? null : member.getValueOrDefault();
	}

	public String getString(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof String) {
				return (String) val;
			}
		}
		return null;
	}

	public Boolean getBoolean(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Boolean) {
				return (Boolean) val;
			}
		}
		return null;
	}

	public Integer getInteger(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Integer) {
				return (Integer) val;
			}
		}
		return null;
	}

	public Long getLong(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Long) {
				return (Long) val;
			}
		}
		return null;
	}

	public Byte getByte(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Byte) {
				return (Byte) val;
			}
		}
		return null;
	}

	public Character getCharacter(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Character) {
				return (Character) val;
			}
		}
		return null;
	}

	public Double getDouble(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Double) {
				return (Double) val;
			}
		}
		return null;
	}

	public Float getFloat(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Float) {
				return (Float) val;
			}
		}
		return null;
	}

	public Short getShort(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof Short) {
				return (Short) val;
			}
		}
		return null;
	}

	public DeclaredType getClass(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof DeclaredType) {
				return ((DeclaredType) val);
			}
		}
		return null;
	}

	public VariableElement getEnum(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof VariableElement) {
				return ((VariableElement) val);
			}
		}
		return null;
	}

	public AnnotationMirror getAnnotation(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof AnnotationMirror) {
				return ((AnnotationMirror) val);
			}
		}
		return null;
	}

	public List<? extends AnnotationValue> getArray(String name) {
		AnnotationValue value = get(name);
		if (value != null) {
			Object val = value.getValue();
			if (val instanceof List) {
				return (List<? extends AnnotationValue>) val;
			}
		}
		return null;
	}


	@ToString
	@EqualsAndHashCode
	public static class Member implements Cloneable {
		@ToString.Exclude
		@EqualsAndHashCode.Exclude
		private ProcessingEnvironment env;
		private ExecutableElement method;
		private AnnotationValue value;
		private AnnotationValue defaultValue;

		public Member(ProcessingEnvironment env, ExecutableElement method, AnnotationValue value, AnnotationValue defaultValue) {
			this.env = env;
			this.method = method;
			this.value = value;
			this.defaultValue = defaultValue;
		}

		@Override
		public Member clone() {
			return new Member(env, method, value, defaultValue);
		}

		public AnnotationValue getValue() {
			return value;
		}

		public void setValue(AnnotationValue value) {
			if (isDefault(value)) {
				this.value = value;
				return;
			}

			TypeMirror returnType = method.getReturnType();
			value = AptAnnotations.asTargetTypeIfNecessary(env, value, returnType);
			// 无法匹配类型则忽略
			if (value == null || value.getValue() == null) {
				return;
			}
			this.value = value;
		}

		public void reset() {
			this.value = defaultValue;
		}

		public AnnotationValue getValueOrDefault() {
			if (value == null || value.getValue() == null) {
				return defaultValue;
			}
			return value;
		}

		public boolean isDefault(AnnotationValue value) {
			return value == null || value.getValue() == null || AptAnnotations.equals(env, value, defaultValue);
		}

		public boolean isDefault() {
			return isDefault(value);
		}
	}
}

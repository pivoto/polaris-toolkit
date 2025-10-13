package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

import io.polaris.core.converter.Converters;
import io.polaris.core.reflect.Reflects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
@SuppressWarnings({"all"})
@ToString
@EqualsAndHashCode
public class AnnotationAttributes implements Cloneable {

	private final Map<String, Member> members;
	private final Class<? extends Annotation> annotationType;
	private final Map<String, String> aliasMembers;

	private AnnotationAttributes(Annotation annotation) {
		this.annotationType = annotation.annotationType();
		Method[] methods = getAnnotationMembers(annotationType);
		Map<String, String> aliasMembers = getAliasMembers(methods);
		Map<String, Member> memberValues = new LinkedHashMap<>();
		for (Method method : methods) {
			String name = method.getName();
			Object value = Reflects.invokeQuietly(annotation, method);
			memberValues.put(name, new Member(method, value, method.getDefaultValue()));
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

	private AnnotationAttributes(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
		Method[] methods = getAnnotationMembers(annotationType);
		Map<String, String> aliasMembers = getAliasMembers(methods);
		Map<String, Member> members = new LinkedHashMap<>();
		for (Method method : methods) {
			String name = method.getName();
			members.put(name, new Member(method, method.getDefaultValue(), method.getDefaultValue()));
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

	@Nonnull
	private Map<String, String> getAliasMembers(Method[] methods) {
		Map<String, String> aliasMembers = new HashMap<>();
		for (Method method : methods) {
			Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(method);
			if (aliasAttributes != null) {
				for (AliasAttribute alias : aliasAttributes) {
					if (alias != null && (alias.annotation() == this.annotationType || alias.annotation() == Annotation.class)) {
						String aliasName = alias.value();
						String name = method.getName();
						aliasMembers.putIfAbsent(name, aliasName);
					}
				}
			}
		}
		return aliasMembers;
	}

	private AnnotationAttributes(Map<String, Member> members, Class<? extends Annotation> annotationType, Map<String, String> aliasMembers) {
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

	@Override
	public AnnotationAttributes clone() {
		return new AnnotationAttributes(members, annotationType, aliasMembers);
	}

	public <A extends Annotation> A asAnnotation() {
		return (A) AnnotationInvocationHandler.createProxy(annotationType, Collections.unmodifiableMap(asMap()),  true);
	}

	public Map<String, Object> asMap() {
		Map<String, Object> map = new LinkedHashMap<>();
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

	public void set(Map<String, Object> values) {
		for (Map.Entry<String, Object> entry : values.entrySet()) {
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
	public boolean set(String name, Object value) {
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

	public void setIf(Map<String, Object> values, BiPredicate<Member, Object> predicate) {
		for (Map.Entry<String, Object> entry : values.entrySet()) {
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
	public boolean setIf(String name, Object value, BiPredicate<Member, Object> predicate) {
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

	public void setIfNotDefault(Map<String, Object> values) {
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			setIfNotDefault(entry.getKey(), entry.getValue());
		}
	}

	public boolean setIfNotDefault(String name, Object value) {
		return setIf(name, value, (member, val) -> !member.isDefault(val));
	}

	public Object get(String name) {
		Member member = members.get(name);
		return member == null ? null : member.getValueOrDefault();
	}

	public <T> T get(String name, Class<T> type) {
		return Converters.convert(type, get(name));
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

	public Annotation getAnnotation(String name) {
		return Converters.convert(Annotation.class, get(name));
	}

	public Annotation[] getAnnotationArray(String name) {
		return Converters.convert(Annotation[].class, get(name));
	}

	public AnnotationAttributes getAnnotationAttributes(String name) {
		return AnnotationAttributes.of(getAnnotation(name));
	}

	public AnnotationAttributes[] getAnnotationAttributesArray(String name) {
		Annotation[] arr = getAnnotationArray(name);
		AnnotationAttributes[] attributes = new AnnotationAttributes[arr.length];
		for (int i = 0; i < arr.length; i++) {
			attributes[i] = AnnotationAttributes.of(arr[i]);
		}
		return attributes;
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
	@ToString
	@EqualsAndHashCode
	public static class Member implements Cloneable {
		private Method method;
		private Object value;
		private Object defaultValue;

		public Member(Method method, Object value, Object defaultValue) {
			this.method = method;
			this.value = value;
			this.defaultValue = defaultValue;
		}

		@Override
		public Member clone() {
			try {
				return (Member) super.clone();
			} catch (CloneNotSupportedException e) {
				return new Member(method, value, defaultValue);
			}
		}

		public void setValue(Object value) {
			if (isDefault(value)) {
				this.value = value;
				return;
			}
			Object o = Converters.convertQuietly(method.getGenericReturnType(), value);
			if (o != null) {
				this.value = o;
				return;
			}
			return;
		}

		public void reset() {
			this.value = defaultValue;
		}

		public Object getValueOrDefault() {
			if (value == null) {
				return defaultValue;
			}
			return value;
		}

		public boolean isDefault(Object value) {
			return value == null || value.equals(defaultValue);
		}

		public boolean isDefault() {
			return isDefault(value);
		}
	}


}

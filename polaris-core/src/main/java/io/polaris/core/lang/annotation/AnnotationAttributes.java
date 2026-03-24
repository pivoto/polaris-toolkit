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
import io.polaris.core.lang.Copyable;
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
public class AnnotationAttributes implements Cloneable, Copyable<AnnotationAttributes> {

	private final Map<String, Member> members;
	private final Class<? extends Annotation> annotationType;
	private final Map<String, String> aliasMembers;

	/**
	 * 私有构造函数，从已有的成员映射、注解类型和别名映射创建 AnnotationAttributes 实例
	 * <p>
	 * 该构造函数会对传入的成员映射进行深拷贝，确保返回的对象是不可变的。
	 * 所有成员和别名映射都会被包装成不可修改的 Map。
	 *
	 * @param members        成员映射，键为属性名，值为 Member 对象
	 * @param annotationType 注解类型
	 * @param aliasMembers   别名映射，键为属性名，值为对应的别名属性名
	 */
	private AnnotationAttributes(Map<String, Member> members, Class<? extends Annotation> annotationType, Map<String, String> aliasMembers) {
		this.annotationType = annotationType;
		// 处理成员映射，进行深拷贝并包装为不可变视图
		if (members == null || members.isEmpty()) {
			this.members = Collections.unmodifiableMap(new LinkedHashMap<>());
		} else {
			Map<String, Member> map = new LinkedHashMap<>();
			members.forEach((k, v) -> {
				map.put(k, v.clone());
			});
			this.members = Collections.unmodifiableMap(map);
		}
		// 处理别名映射，包装为不可变视图
		if (aliasMembers == null || aliasMembers.isEmpty()) {
			this.aliasMembers = Collections.unmodifiableMap(new HashMap<>());
		} else {
			this.aliasMembers = Collections.unmodifiableMap(new HashMap<String, String>(aliasMembers));
		}
	}

	/**
	 * 私有构造函数，从注解实例创建 AnnotationAttributes
	 * <p>
	 * 该构造函数会通过反射获取注解的所有属性值，并处理别名属性的合并逻辑。
	 * 对于有别名定义的属性，如果主属性不是默认值，则会将主属性的值同步到别名属性。
	 *
	 * @param annotation 注解实例，不能为 null
	 */
	private AnnotationAttributes(Annotation annotation) {
		// 获取注解类型
		this.annotationType = annotation.annotationType();
		// 获取注解的所有成员方法
		Method[] methods = getAnnotationMembers(annotationType);
		// 获取别名属性映射
		Map<String, String> aliasMembers = getAliasMembers(methods);
		// 存储所有成员的值
		Map<String, Member> memberValues = new LinkedHashMap<>();
		// 遍历所有成员方法，获取属性值
		for (Method method : methods) {
			String name = method.getName();
			// 通过反射调用方法获取属性值
			Object value = Reflects.invokeQuietly(annotation, method);
			memberValues.put(name, new Member(method, value, method.getDefaultValue()));
		}
		// 处理别名属性，将主属性的值同步到别名属性
		aliasMembers.forEach((k, v) -> {
			Member member = memberValues.get(k);
			Member aliasMember = memberValues.get(v);
			if (member != null && aliasMember != null) {
				// 如果主属性不是默认值，则将主属性的值设置到别名属性
				if (!member.isDefault()) {
					aliasMember.setValue(member.getValue());
				}
			}
		});
		this.members = Collections.unmodifiableMap(memberValues);
		this.aliasMembers = Collections.unmodifiableMap(aliasMembers);
	}

	/**
	 * 私有构造函数，从注解类型创建 AnnotationAttributes（使用默认值）
	 * <p>
	 * 该构造函数会创建基于注解类型默认值的 AnnotationAttributes 实例。
	 * 所有属性的初始值都为其默认值，然后处理别名属性的合并逻辑。
	 * 对于有别名定义的属性，如果主属性不是默认值，则会将主属性的值同步到别名属性。
	 *
	 * @param annotationType 注解类型，不能为 null
	 */
	private AnnotationAttributes(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
		// 获取注解的所有成员方法
		Method[] methods = getAnnotationMembers(annotationType);
		// 获取别名属性映射
		Map<String, String> aliasMembers = getAliasMembers(methods);
		// 存储所有成员的默认值
		Map<String, Member> members = new LinkedHashMap<>();
		// 遍历所有成员方法，使用默认值初始化
		for (Method method : methods) {
			String name = method.getName();
			members.put(name, new Member(method, method.getDefaultValue(), method.getDefaultValue()));
		}
		// 处理别名属性，将主属性的值同步到别名属性
		aliasMembers.forEach((k, v) -> {
			Member member = members.get(k);
			Member aliasMember = members.get(v);
			if (member != null && aliasMember != null) {
				// 如果主属性不是默认值，则将主属性的值设置到别名属性
				if (!member.isDefault()) {
					aliasMember.setValue(member.getValue());
				}
			}
		});
		this.members = Collections.unmodifiableMap(members);
		this.aliasMembers = Collections.unmodifiableMap(aliasMembers);
	}

	/**
	 * 获取注解的别名属性映射
	 * <p>
	 * 该方法扫描注解的所有成员方法，查找使用了别名注解（如 {@code @AliasFor}）的方法，
	 * 并建立属性名与别名之间的映射关系。
	 * 只处理别名注解类型与当前注解类型相同或是 Annotation.class 的情况。
	 *
	 * @param methods 注解的成员方法数组
	 * @return 别名属性映射，键为属性名，值为对应的别名属性名
	 */
	@Nonnull
	private Map<String, String> getAliasMembers(Method[] methods) {
		Map<String, String> aliasMembers = new HashMap<>();
		// 遍历所有成员方法，查找别名注解
		for (Method method : methods) {
			Set<AliasAttribute> aliasAttributes = AliasFinders.findAliasAttributes(method);
			if (aliasAttributes != null) {
				for (AliasAttribute alias : aliasAttributes) {
					// 只处理别名注解类型与当前注解类型相同或是 Annotation.class 的情况
					if (alias != null && (alias.annotationType() == this.annotationType || alias.annotationType() == Annotation.class)) {
						String aliasName = alias.attribute();
						String name = method.getName();
						// 使用 putIfAbsent 确保只记录第一个别名映射
						aliasMembers.putIfAbsent(name, aliasName);
					}
				}
			}
		}
		return aliasMembers;
	}


	/**
	 * 从注解实例创建 AnnotationAttributes
	 * <p>
	 * 该静态工厂方法用于从已有的注解实例创建 AnnotationAttributes 对象。
	 * 会获取注解的所有属性值并处理别名映射关系。
	 *
	 * @param <A>        注解类型泛型参数
	 * @param annotation 注解实例，不能为 null
	 * @return AnnotationAttributes 对象，封装了注解的所有属性信息
	 */
	public static <A extends Annotation> AnnotationAttributes of(A annotation) {
		return new AnnotationAttributes(annotation);
	}

	/**
	 * 从注解类型创建 AnnotationAttributes（使用默认值）
	 * <p>
	 * 该静态工厂方法用于根据注解类型创建 AnnotationAttributes 对象。
	 * 所有属性的初始值都为其默认值，适用于需要获取注解默认配置的场景。
	 *
	 * @param <A>            注解类型泛型参数
	 * @param annotationType 注解类型，不能为 null
	 * @return AnnotationAttributes 对象，包含注解类型的默认属性值
	 */
	public static <A extends Annotation> AnnotationAttributes of(Class<A> annotationType) {
		return new AnnotationAttributes(annotationType);
	}

	/**
	 * 从注解类型创建 AnnotationAttributes（使用指定值覆盖默认值）
	 * <p>
	 * 该静态工厂方法用于根据注解类型和指定的属性值创建 AnnotationAttributes 对象。
	 * 首先基于注解类型创建包含所有默认值的 AnnotationAttributes 实例，
	 * 然后使用提供的 values 映射覆盖对应的属性值。
	 * 适用于需要自定义部分属性值而保留其他默认值的场景。
	 *
	 * @param <A>            注解类型泛型参数，必须是 Annotation 的子类
	 * @param annotationType 注解类型，不能为 null
	 * @param values         属性值映射，键为属性名，值为属性值，用于覆盖默认值；可以为 null 或空映射
	 * @return AnnotationAttributes 对象，包含注解类型的默认属性值并用提供的值进行覆盖
	 */
	public static <A extends Annotation> AnnotationAttributes of(Class<A> annotationType, Map<String, Object> values) {
		// 基于注解类型创建包含默认值的 AnnotationAttributes 实例
		AnnotationAttributes annotationAttributes = new AnnotationAttributes(annotationType);
		// 使用提供的值覆盖对应的属性
		annotationAttributes.set(values);
		return annotationAttributes;
	}

	/**
	 * 获取注解类型的有效成员方法数组
	 * <p>
	 * 该方法用于获取注解类型中所有有效的成员方法。
	 * 有效成员方法的定义：无参数且有非 void 返回值的方法。
	 * 这些方法对应注解的属性。
	 *
	 * @param <A>            注解类型泛型参数
	 * @param annotationType 注解类型，不能为 null
	 * @return 注解的成员方法数组
	 */
	public static <A extends Annotation> Method[] getAnnotationMembers(Class<A> annotationType) {
		// 获取注解类型声明的所有方法
		Method[] methods = annotationType.getDeclaredMethods();
		List<Method> list = new ArrayList<>();
		// 过滤出有效的成员方法（无参数且有非 void 返回值）
		for (Method method : methods) {
			if ((method.getParameterCount() == 0 && method.getReturnType() != void.class)) {
				list.add(method);
			}
		}
		return list.toArray(new Method[0]);
	}

	@Override
	public AnnotationAttributes copy() {
		return new AnnotationAttributes(members, annotationType, aliasMembers);
	}

	@Override
	public AnnotationAttributes clone() {
		return new AnnotationAttributes(members, annotationType, aliasMembers);
	}

	/**
	 * 将 AnnotationAttributes 转换为实际的注解实例
	 * <p>
	 * 该方法使用当前的属性值创建一个动态代理注解实例。
	 * 通过 {@link AnnotationInvocationHandler#createProxy(Class, Map, boolean)} 实现。
	 * 返回的注解实例可以用于反射操作或传递给其他需要注解的方法。
	 *
	 * @param <A> 注解类型泛型参数
	 * @return 实际的注解实例
	 */
	public <A extends Annotation> A asAnnotation() {
		return (A) AnnotationInvocationHandler.createProxy(annotationType, Collections.unmodifiableMap(asMap()), true);
	}

	/**
	 * 将 AnnotationAttributes 转换为 Map
	 * <p>
	 * 该方法将所有属性值转换为 Map 形式，键为属性名，值为属性值。
	 * 返回的 Map 保持了属性的插入顺序（LinkedHashMap）。
	 *
	 * @return 包含所有属性值的 Map
	 */
	public Map<String, Object> asMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		// 遍历所有成员，提取属性值
		members.forEach((k, v) -> map.put(k, v.getValue()));
		return map;
	}

	public Class<? extends Annotation> annotationType() {
		return annotationType;
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

	/**
	 * 设置多个属性值
	 * <p>
	 * 该方法用于批量设置多个属性值。
	 * 遍历传入的 Map，对每个属性调用 {@link #set(String, Object)} 方法。
	 *
	 * @param values 属性值映射，键为属性名，值为属性值
	 */
	public void set(Map<String, Object> values) {
		// 遍历所有属性值，逐个设置
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

	/**
	 * 根据条件批量设置属性值
	 * <p>
	 * 该方法用于根据给定的条件批量设置多个属性值。
	 * 只有当属性存在且满足给定条件时才会设置新值。
	 * 遍历传入的 Map，对每个属性调用 {@link #setIf(String, Object, BiPredicate)} 方法。
	 *
	 * @param values    属性值映射，键为属性名，值为属性值
	 * @param predicate 判断条件，用于决定是否设置值，接收 Member 对象和新值作为参数
	 */
	public void setIf(Map<String, Object> values, BiPredicate<Member, Object> predicate) {
		// 遍历所有属性值，根据条件逐个设置
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
		// 遍历所有属性值，只对非默认值的属性进行设置
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

	/**
	 * 获取指定名称的属性值（带类型转换）
	 * <p>
	 * 该方法用于获取指定类型的属性值。
	 * 首先调用 {@link #get(String)} 获取原始值，然后通过转换器转换为目标类型。
	 *
	 * @param <T>  目标类型
	 * @param name 属性名称
	 * @param type 目标类型
	 * @return 转换后的属性值
	 */
	public <T> T get(String name, Class<T> type) {
		return Converters.convert(type, get(name));
	}

	/**
	 * 获取 String 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的 String 类型值。
	 * 通过转换器将原始值转换为 String 类型。
	 *
	 * @param name 属性名称
	 * @return String 类型的属性值
	 */
	public String getString(String name) {
		return Converters.convert(String.class, get(name));
	}

	/**
	 * 获取 String[] 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的 String 数组类型值。
	 * 通过转换器将原始值转换为 String[] 类型。
	 *
	 * @param name 属性名称
	 * @return String[] 类型的属性值
	 */
	public String[] getStringArray(String name) {
		return Converters.convert(String[].class, get(name));
	}

	/**
	 * 获取 Boolean 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的 Boolean 类型值。
	 * 通过转换器将原始值转换为 Boolean 类型。
	 *
	 * @param name 属性名称
	 * @return Boolean 类型的属性值
	 */
	public Boolean getBoolean(String name) {
		return Converters.convert(Boolean.class, get(name));
	}

	/**
	 * 获取 Number 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的 Number 类型值。
	 * 通过转换器将原始值转换为 Number 类型，适用于各种数值类型。
	 *
	 * @param name 属性名称
	 * @return Number 类型的属性值
	 */
	public Number getNumber(String name) {
		return Converters.convert(Number.class, get(name));
	}

	/**
	 * 获取 Enum 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的枚举类型值。
	 * 通过转换器将原始值转换为 Enum 类型。
	 *
	 * @param name 属性名称
	 * @return Enum 类型的属性值
	 */
	public <E extends Enum<E>> E getEnum(String name, Class<E> type) {
		return Converters.convert(type, get(name));
	}

	/**
	 * 获取 Enum[] 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的枚举数组类型值。
	 * 通过转换器将原始值转换为 Enum[] 类型。
	 *
	 * @param name 属性名称
	 * @return Enum[] 类型的属性值
	 */
	public <E extends Enum<E>> E[] getEnumArray(String name, Class<E> type) {
		return Converters.convert(Array.newInstance(type, 0).getClass(), get(name));
	}

	/**
	 * 获取 Enum 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的枚举类型值。
	 * 通过转换器将原始值转换为 Enum 类型。
	 *
	 * @param name 属性名称
	 * @return Enum 类型的属性值
	 */
	public Enum getEnum(String name) {
		return Converters.convert(Enum.class, get(name));
	}

	/**
	 * 获取 Enum[] 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的枚举数组类型值。
	 * 通过转换器将原始值转换为 Enum[] 类型。
	 *
	 * @param name 属性名称
	 * @return Enum[] 类型的属性值
	 */
	public Enum[] getEnumArray(String name) {
		return Converters.convert(Enum[].class, get(name));
	}

	/**
	 * 获取 Class 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的 Class 类型值。
	 * 通过转换器将原始值转换为 Class 类型。
	 *
	 * @param name 属性名称
	 * @return Class 类型的属性值
	 */
	public Class getClass(String name) {
		return Converters.convert(Class.class, get(name));
	}

	/**
	 * 获取 Class[] 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的 Class 数组类型值。
	 * 通过转换器将原始值转换为 Class[] 类型。
	 *
	 * @param name 属性名称
	 * @return Class[] 类型的属性值
	 */
	public Class[] getClassArray(String name) {
		return Converters.convert(Class[].class, get(name));
	}

	/**
	 * 获取 Annotation 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的注解类型值。
	 * 通过转换器将原始值转换为 Annotation 类型。
	 *
	 * @param name 属性名称
	 * @return Annotation 类型的属性值
	 */
	public Annotation getAnnotation(String name) {
		return Converters.convert(Annotation.class, get(name));
	}

	/**
	 * 获取 Annotation[] 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的注解数组类型值。
	 * 通过转换器将原始值转换为 Annotation[] 类型。
	 *
	 * @param name 属性名称
	 * @return Annotation[] 类型的属性值
	 */
	public Annotation[] getAnnotationArray(String name) {
		return Converters.convert(Annotation[].class, get(name));
	}

	/**
	 * 获取 AnnotationAttributes 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的注解包装类型值。
	 * 首先获取 Annotation 类型的值，然后将其包装为 AnnotationAttributes 对象。
	 *
	 * @param name 属性名称
	 * @return AnnotationAttributes 类型的属性值
	 */
	public AnnotationAttributes getAnnotationAttributes(String name) {
		return AnnotationAttributes.of(getAnnotation(name));
	}

	/**
	 * 获取 AnnotationAttributes[] 类型的属性值
	 * <p>
	 * 该方法用于获取指定属性的注解包装数组类型值。
	 * 首先获取 Annotation[] 类型的值，然后将每个注解包装为 AnnotationAttributes 对象。
	 *
	 * @param name 属性名称
	 * @return AnnotationAttributes[] 类型的属性值
	 */
	public AnnotationAttributes[] getAnnotationAttributesArray(String name) {
		// 获取注解数组
		Annotation[] arr = getAnnotationArray(name);
		// 将每个注解转换为 AnnotationAttributes
		AnnotationAttributes[] attributes = new AnnotationAttributes[arr.length];
		for (int i = 0; i < arr.length; i++) {
			attributes[i] = AnnotationAttributes.of(arr[i]);
		}
		return attributes;
	}


	/**
	 * 获取指定类型的注解属性值
	 * <p>
	 * 该方法用于获取指定类型的具体注解属性值。
	 * 通过转换器将原始值转换为指定的注解类型。
	 *
	 * @param <V>            注解类型泛型参数
	 * @param name           属性名称
	 * @param annotationType 注解类型
	 * @return 指定类型的注解实例
	 */
	public <V extends Annotation> V getAnnotation(String name, Class<V> annotationType) {
		return Converters.convert(annotationType, get(name));
	}

	/**
	 * 获取指定类型注解的 AnnotationAttributes
	 * <p>
	 * 该方法用于获取指定类型的具体注解，并包装为 AnnotationAttributes 对象。
	 * 首先转换为指定注解类型，然后创建 AnnotationAttributes 实例。
	 *
	 * @param <V>            注解类型泛型参数
	 * @param name           属性名称
	 * @param annotationType 注解类型
	 * @return 指定类型注解的 AnnotationAttributes 对象
	 */
	public <V extends Annotation> AnnotationAttributes getAnnotationAttributes(String name, Class<V> annotationType) {
		V a = Converters.convert(annotationType, get(name));
		return AnnotationAttributes.of(a);
	}

	/**
	 * 获取指定类型的注解数组属性值
	 * <p>
	 * 该方法用于获取指定类型的具体注解数组属性值。
	 * 通过转换器将原始值转换为指定类型的注解数组。
	 *
	 * @param <V>            注解类型泛型参数
	 * @param name           属性名称
	 * @param annotationType 注解类型
	 * @return 指定类型的注解数组
	 */
	public <V extends Annotation> V[] getAnnotationArray(String name, Class<V> annotationType) {
		return Converters.convert(Array.newInstance(annotationType, 0).getClass(), get(name));
	}

	/**
	 * 获取指定类型注解数组的 AnnotationAttributes 数组
	 * <p>
	 * 该方法用于获取指定类型的注解数组，并将每个注解包装为 AnnotationAttributes 对象。
	 * 首先转换为指定类型的注解数组，然后逐个创建 AnnotationAttributes 实例。
	 *
	 * @param <V>            注解类型泛型参数
	 * @param name           属性名称
	 * @param annotationType 注解类型
	 * @return 指定类型注解的 AnnotationAttributes 数组
	 */
	public <V extends Annotation> AnnotationAttributes[] getAnnotationAttributesArray(String name, Class<V> annotationType) {
		// 转换为指定类型的注解数组
		V[] arr = Converters.convert(Array.newInstance(annotationType, 0).getClass(), get(name));
		// 将每个注解转换为 AnnotationAttributes
		AnnotationAttributes[] attributes = new AnnotationAttributes[arr.length];
		for (int i = 0; i < arr.length; i++) {
			attributes[i] = AnnotationAttributes.of(arr[i]);
		}
		return attributes;
	}


	@Getter
	@ToString
	@EqualsAndHashCode
	public static class Member implements Cloneable, Copyable<Member> {
		private Method method;
		private Object value;
		private Object defaultValue;

		public Member(Method method, Object value, Object defaultValue) {
			this.method = method;
			this.value = value;
			this.defaultValue = defaultValue;
		}

		@Override
		public Member copy() {
			return this.clone();
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

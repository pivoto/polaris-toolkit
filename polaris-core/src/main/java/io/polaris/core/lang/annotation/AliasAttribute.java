package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 表示一个别名属性的类，用于存储注解中别名的值和关联的注解类型
 *
 * @author Qt
 * @since Oct 07, 2025
 */
@EqualsAndHashCode
@ToString
public class AliasAttribute {
	private final String attribute;
	private final Class<? extends Annotation> annotationType;

	/**
	 * 构造一个AliasAttribute实例
	 *
	 * @param attribute 别名的值
	 * @param annotationType 关联的注解类型，如果为null则默认为Annotation.class
	 */
	public AliasAttribute(String attribute, Class<? extends Annotation> annotationType) {
		this.attribute = attribute == null ? "" : attribute.trim();
		this.annotationType = annotationType == null ? Annotation.class : annotationType;
	}

	/**
	 * 构造一个AliasAttribute实例，使用默认的注解类型
	 *
	 * @param attribute 别名的值
	 */
	public AliasAttribute(String attribute) {
		this(attribute, null);
	}

	/**
	 * 获取别名的值
	 *
	 * @return 别名的值，经过trim处理，如果原值为null则返回空字符串
	 */
	public String attribute() {
		return attribute;
	}

	/**
	 * 获取关联的注解类型
	 *
	 * @return 注解类型，如果构造时传入null则返回Annotation.class
	 */
	public Class<? extends Annotation> annotationType() {
		return annotationType;
	}
}

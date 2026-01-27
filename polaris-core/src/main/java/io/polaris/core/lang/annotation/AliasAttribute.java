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
	private final String value;
	private final Class<? extends Annotation> annotation;

	/**
	 * 构造一个AliasAttribute实例
	 *
	 * @param value 别名的值
	 * @param annotation 关联的注解类型，如果为null则默认为Annotation.class
	 */
	public AliasAttribute(String value, Class<? extends Annotation> annotation) {
		this.value = value == null ? "" : value.trim();
		this.annotation = annotation == null ? Annotation.class : annotation;
	}

	/**
	 * 构造一个AliasAttribute实例，使用默认的注解类型
	 *
	 * @param value 别名的值，不能为空或null
	 */
	public AliasAttribute(String value) {
		this(value, null);
	}

	/**
	 * 获取别名的值
	 *
	 * @return 别名的值，经过trim处理，如果原值为null则返回空字符串
	 */
	public String value() {
		return value;
	}

	/**
	 * 获取关联的注解类型
	 *
	 * @return 注解类型，如果构造时传入null则返回Annotation.class
	 */
	public Class<? extends Annotation> annotation() {
		return annotation;
	}
}

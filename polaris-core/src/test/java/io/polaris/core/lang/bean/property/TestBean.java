package io.polaris.core.lang.bean.property;

import io.polaris.core.lang.bean.property.annotation.IgnoredProperties;
import io.polaris.core.lang.bean.property.annotation.IgnoredProperty;

/**
 * 测试用的简单 Bean 类
 */
public class TestBean {
	private String name;
	private int age;
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

/**
 * 测试用的带有忽略属性的 Bean 类
 */
@IgnoredProperties({"ignoredField"})
class IgnoredTestBean {
	private String normalField;
	private String ignoredField;

	@IgnoredProperty
	private String ignoredByAnnotation;

	public String getNormalField() {
		return normalField;
	}

	public void setNormalField(String normalField) {
		this.normalField = normalField;
	}

	public String getIgnoredField() {
		return ignoredField;
	}

	public void setIgnoredField(String ignoredField) {
		this.ignoredField = ignoredField;
	}

	@IgnoredProperty
	public String getIgnoredByAnnotation() {
		return ignoredByAnnotation;
	}

	public void setIgnoredByAnnotation(String ignoredByAnnotation) {
		this.ignoredByAnnotation = ignoredByAnnotation;
	}
}

/**
 * 测试用的只有字段的 Bean 类
 */
class FieldOnlyBean {
	public String publicField;
	private String privateField;  // 不会被识别，因为不是 public
	public final String finalField = "final";  // 不会被识别，因为是 final
	public static String staticField;  // 不会被识别，因为是 static
}

/**
 * 测试用的继承关系的 Bean 类
 */
class ParentBean {
	private String parentField;

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}
}

class ChildBean extends ParentBean {
	private String childField;

	public String getChildField() {
		return childField;
	}

	public void setChildField(String childField) {
		this.childField = childField;
	}
}

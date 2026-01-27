package io.polaris.core.classpath.analyzer;

import java.util.Objects;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Getter
@ToString
public class MethodKey {

	private final String classInternalName;
	private final String methodName;
	private final String methodDesc;

	public MethodKey(String classInternalName, String methodName, String methodDesc) {
		this.classInternalName = classInternalName;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}

	public String dump() {
		return classInternalName + "." + methodName + methodDesc;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MethodKey)) return false;
		MethodKey methodKey = (MethodKey) o;
		return Objects.equals(getClassInternalName(), methodKey.getClassInternalName()) && Objects.equals(getMethodName(), methodKey.getMethodName()) && Objects.equals(getMethodDesc(), methodKey.getMethodDesc());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClassInternalName(), getMethodName(), getMethodDesc());
	}
}

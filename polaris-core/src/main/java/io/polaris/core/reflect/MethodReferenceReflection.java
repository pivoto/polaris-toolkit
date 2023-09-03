package io.polaris.core.reflect;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public interface MethodReferenceReflection extends Serializable {
	default SerializedLambda serialized() {
		try {
			Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
			replaceMethod.setAccessible(true);
			return (SerializedLambda) replaceMethod.invoke(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	default Class getContainingClass() {
		try {
			String className = serialized().getImplClass().replaceAll("/", ".");
			return Class.forName(className);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	default Method method() {
		SerializedLambda lambda = serialized();
		Class containingClass = getContainingClass();
		return Arrays.asList(containingClass.getDeclaredMethods())
			.stream()
			//TODO check parameter types to deal with overloads
			.filter(method -> Objects.equals(method.getName(), lambda.getImplMethodName()))
			.findFirst()
			.orElseThrow(RuntimeException::new);
	}

}

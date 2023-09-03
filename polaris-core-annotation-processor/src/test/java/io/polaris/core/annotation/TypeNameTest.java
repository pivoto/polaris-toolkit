package io.polaris.core.annotation;

import io.polaris.core.annotation.processing.AnnotationProcessorUtils;
import com.squareup.javapoet.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 24, 2023
 */
public class TypeNameTest {
	@Test
	void test01() {
		System.out.println(ArrayTypeName.of(TypeName.get(int.class)));
		System.out.println(ArrayTypeName.of(TypeName.get(Map.class)));
		System.out.println(ArrayTypeName.of(TypeVariableName.get("T", Map.class)));
		System.out.println(ArrayTypeName.of(WildcardTypeName.subtypeOf(Map.class)));
		System.out.println(ArrayTypeName.of(ParameterizedTypeName.get(Map.class, String.class, Object.class)));

		System.out.println(removeTypeVariable(ArrayTypeName.of(TypeName.get(int.class))));
		System.out.println(removeTypeVariable(ArrayTypeName.of(TypeName.get(Map.class))));
		System.out.println(removeTypeVariable(ArrayTypeName.of(TypeVariableName.get("T", Map.class))));
		System.out.println(removeTypeVariable(ArrayTypeName.of(WildcardTypeName.subtypeOf(Map.class))));
		System.out.println(removeTypeVariable(ArrayTypeName.of(ParameterizedTypeName.get(Map.class, String.class, Object.class))));

	}


	public TypeName removeTypeVariable(TypeName typeName) {
		return AnnotationProcessorUtils.rawType(typeName);
	}
}

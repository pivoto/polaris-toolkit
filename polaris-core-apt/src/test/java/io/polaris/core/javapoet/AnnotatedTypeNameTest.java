/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.polaris.core.javapoet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotatedTypeNameTest {

	private final static String NN = NeverNull.class.getCanonicalName();
	private final AnnotationSpec NEVER_NULL = AnnotationSpec.builder(NeverNull.class).build();
	private final static String TUA = TypeUseAnnotation.class.getCanonicalName();
	private final AnnotationSpec TYPE_USE_ANNOTATION =
		AnnotationSpec.builder(TypeUseAnnotation.class).build();

	@Target(ElementType.TYPE_USE)
	public @interface NeverNull {}

	@Target(ElementType.TYPE_USE)
	public @interface TypeUseAnnotation {}


	@Test
	public void nullAnnotationArray() {
		assertThrows(NullPointerException.class, () -> TypeName.BOOLEAN.annotated((AnnotationSpec[]) null));
	}

	@Test
	public void nullAnnotationList() {
		assertThrows(NullPointerException.class, () -> TypeName.DOUBLE.annotated((List<AnnotationSpec>) null));
		;
	}

	@Test
	public void annotated() {
		TypeName simpleString = TypeName.get(String.class);
		assertFalse(simpleString.isAnnotated());
		assertEquals(simpleString, TypeName.get(String.class));

		TypeName annotated = simpleString.annotated(NEVER_NULL);
		assertTrue(annotated.isAnnotated());
		assertEquals(annotated, annotated.annotated());
	}

	@Test
	public void annotatedType() {
		TypeName type = TypeName.get(String.class);
		TypeName actual = type.annotated(TYPE_USE_ANNOTATION);
		assertEquals(actual.toString(), "java.lang. @" + TUA + " String");
	}

	@Test
	public void annotatedTwice() {
		TypeName type = TypeName.get(String.class);
		TypeName actual =
			type.annotated(NEVER_NULL)
				.annotated(TYPE_USE_ANNOTATION);
		assertEquals(actual.toString()
			, "java.lang. @" + NN + " @" + TUA + " String");
	}

	@Test
	public void annotatedParameterizedType() {
		TypeName type = ParameterizedTypeName.get(List.class, String.class);
		TypeName actual = type.annotated(TYPE_USE_ANNOTATION);
		assertEquals(actual.toString(), "java.util. @" + TUA + " List<java.lang.String>");
	}

	@Test
	public void annotatedArgumentOfParameterizedType() {
		TypeName type = TypeName.get(String.class).annotated(TYPE_USE_ANNOTATION);
		TypeName actual = ParameterizedTypeName.get(ClassName.get(List.class), type);
		assertEquals(actual.toString(), "java.util.List<java.lang. @" + TUA + " String>");
	}

	@Test
	public void annotatedWildcardTypeNameWithSuper() {
		TypeName type = TypeName.get(String.class).annotated(TYPE_USE_ANNOTATION);
		TypeName actual = WildcardTypeName.supertypeOf(type);
		assertEquals(actual.toString(), "? super java.lang. @" + TUA + " String");
	}

	@Test
	public void annotatedWildcardTypeNameWithExtends() {
		TypeName type = TypeName.get(String.class).annotated(TYPE_USE_ANNOTATION);
		TypeName actual = WildcardTypeName.subtypeOf(type);
		assertEquals(actual.toString(), "? extends java.lang. @" + TUA + " String");
	}

	@Test
	public void annotatedEquivalence() {
		annotatedEquivalence(TypeName.VOID);
		annotatedEquivalence(ArrayTypeName.get(Object[].class));
		annotatedEquivalence(ClassName.get(Object.class));
		annotatedEquivalence(ParameterizedTypeName.get(List.class, Object.class));
		annotatedEquivalence(TypeVariableName.get(Object.class));
		annotatedEquivalence(WildcardTypeName.get(Object.class));
	}

	private void annotatedEquivalence(TypeName type) {
		assertFalse(type.isAnnotated());
		assertEquals(type, type);
		assertEquals(type.annotated(TYPE_USE_ANNOTATION), type.annotated(TYPE_USE_ANNOTATION));
		assertNotEquals(type, type.annotated(TYPE_USE_ANNOTATION));
		assertEquals(type.hashCode(), type.hashCode());
		assertEquals(type.annotated(TYPE_USE_ANNOTATION).hashCode(),
			type.annotated(TYPE_USE_ANNOTATION).hashCode());
		assertNotEquals(type.hashCode(), type.annotated(TYPE_USE_ANNOTATION).hashCode());
	}

	// https://github.com/square/javapoet/issues/431
	@Test
	public void annotatedNestedType() {
		TypeName type = TypeName.get(Map.Entry.class).annotated(TYPE_USE_ANNOTATION);
		assertEquals(type.toString(), "java.util.Map. @" + TUA + " Entry");
	}

	@Test
	public void annotatedEnclosingAndNestedType() {
		TypeName type = ((ClassName) TypeName.get(Map.class).annotated(TYPE_USE_ANNOTATION))
			.nestedClass("Entry").annotated(TYPE_USE_ANNOTATION);
		assertEquals(type.toString(), "java.util. @" + TUA + " Map. @" + TUA + " Entry");
	}

	// https://github.com/square/javapoet/issues/431
	@Test
	public void annotatedNestedParameterizedType() {
		TypeName type = ParameterizedTypeName.get(Map.Entry.class, Byte.class, Byte.class)
			.annotated(TYPE_USE_ANNOTATION);
		assertEquals(type.toString()
			, "java.util.Map. @" + TUA + " Entry<java.lang.Byte, java.lang.Byte>");
	}

	@Test
	public void withoutAnnotationsOnAnnotatedEnclosingAndNestedType() {
		TypeName type = ((ClassName) TypeName.get(Map.class).annotated(TYPE_USE_ANNOTATION))
			.nestedClass("Entry").annotated(TYPE_USE_ANNOTATION);
		assertTrue(type.isAnnotated());
		assertEquals(type.withoutAnnotations(), TypeName.get(Map.Entry.class));
	}

	@Test
	public void withoutAnnotationsOnAnnotatedEnclosingType() {
		TypeName type = ((ClassName) TypeName.get(Map.class).annotated(TYPE_USE_ANNOTATION))
			.nestedClass("Entry");
		assertTrue(type.isAnnotated());
		assertEquals(type.withoutAnnotations(), TypeName.get(Map.Entry.class));
	}

	@Test
	public void withoutAnnotationsOnAnnotatedNestedType() {
		TypeName type = ((ClassName) TypeName.get(Map.class))
			.nestedClass("Entry").annotated(TYPE_USE_ANNOTATION);
		assertTrue(type.isAnnotated());
		assertEquals(type.withoutAnnotations(), TypeName.get(Map.Entry.class));
	}

	// https://github.com/square/javapoet/issues/614
	@Test
	public void annotatedArrayType() {
		TypeName type = ArrayTypeName.of(ClassName.get(Object.class)).annotated(TYPE_USE_ANNOTATION);
		assertEquals(type.toString(), "java.lang.Object @" + TUA + " []");
	}

	@Test
	public void annotatedArrayElementType() {
		TypeName type = ArrayTypeName.of(ClassName.get(Object.class).annotated(TYPE_USE_ANNOTATION));
		assertEquals(type.toString(), "java.lang. @" + TUA + " Object[]");
	}

	// https://github.com/square/javapoet/issues/614
	@Test
	public void annotatedOuterMultidimensionalArrayType() {
		TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class)))
			.annotated(TYPE_USE_ANNOTATION);
		assertEquals(type.toString(), "java.lang.Object @" + TUA + " [][]");
	}

	// https://github.com/square/javapoet/issues/614
	@Test
	public void annotatedInnerMultidimensionalArrayType() {
		TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class))
			.annotated(TYPE_USE_ANNOTATION));
		assertEquals(type.toString(), "java.lang.Object[] @" + TUA + " []");
	}

	// https://github.com/square/javapoet/issues/614
	@Test
	public void annotatedArrayTypeVarargsParameter() {
		TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class)))
			.annotated(TYPE_USE_ANNOTATION);
		MethodSpec varargsMethod = MethodSpec.methodBuilder("m")
			.addParameter(
				ParameterSpec.builder(type, "p")
					.build())
			.varargs()
			.build();
		assertEquals(varargsMethod.toString(), ""
			+ "void m(java.lang.Object @" + TUA + " []... p) {\n"
			+ "}\n");
	}

	// https://github.com/square/javapoet/issues/614
	@Test
	public void annotatedArrayTypeInVarargsParameter() {
		TypeName type = ArrayTypeName.of(ArrayTypeName.of(ClassName.get(Object.class))
			.annotated(TYPE_USE_ANNOTATION));
		MethodSpec varargsMethod = MethodSpec.methodBuilder("m")
			.addParameter(
				ParameterSpec.builder(type, "p")
					.build())
			.varargs()
			.build();
		assertEquals(varargsMethod.toString(), ""
			+ "void m(java.lang.Object[] @" + TUA + " ... p) {\n"
			+ "}\n");
	}
}

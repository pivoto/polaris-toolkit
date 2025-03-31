/*
 * Copyright (C) 2014 Google, Inc.
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

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public abstract class AbstractTypesTest {
	protected abstract Elements getElements();

	protected abstract Types getTypes();

	private TypeElement getElement(Class<?> clazz) {
		return getElements().getTypeElement(clazz.getCanonicalName());
	}

	private TypeMirror getMirror(Class<?> clazz) {
		return getElement(clazz).asType();
	}

	@Test
	public void getBasicTypeMirror() {
		assertEquals(TypeName.get(getMirror(Object.class))
			, ClassName.get(Object.class));
		assertEquals(TypeName.get(getMirror(Charset.class))
			, ClassName.get(Charset.class));
		assertEquals(TypeName.get(getMirror(AbstractTypesTest.class))
			, ClassName.get(AbstractTypesTest.class));
	}

	@Test
	public void getParameterizedTypeMirror() {
		DeclaredType setType =
			getTypes().getDeclaredType(getElement(Set.class), getMirror(Object.class));
		assertEquals(TypeName.get(setType)
			, ParameterizedTypeName.get(ClassName.get(Set.class), ClassName.OBJECT));
	}

	static class Parameterized<
		Simple,
		ExtendsClass extends Number,
		ExtendsInterface extends Runnable,
		ExtendsTypeVariable extends Simple,
		Intersection extends Number & Runnable,
		IntersectionOfInterfaces extends Runnable & Serializable> {}

	@Test
	public void getTypeVariableTypeMirror() {
		List<? extends TypeParameterElement> typeVariables =
			getElement(Parameterized.class).getTypeParameters();

		// Members of converted types use ClassName and not Class<?>.
		ClassName number = ClassName.get(Number.class);
		ClassName runnable = ClassName.get(Runnable.class);
		ClassName serializable = ClassName.get(Serializable.class);

		assertEquals(TypeName.get(typeVariables.get(0).asType())
			, TypeVariableName.get("Simple"));
		assertEquals(TypeName.get(typeVariables.get(1).asType())
			, TypeVariableName.get("ExtendsClass", number));
		assertEquals(TypeName.get(typeVariables.get(2).asType())
			, TypeVariableName.get("ExtendsInterface", runnable));
		assertEquals(TypeName.get(typeVariables.get(3).asType())
			, TypeVariableName.get("ExtendsTypeVariable", TypeVariableName.get("Simple")));
		assertEquals(TypeName.get(typeVariables.get(4).asType())
			, TypeVariableName.get("Intersection", number, runnable));
		assertEquals(TypeName.get(typeVariables.get(5).asType())
			, TypeVariableName.get("IntersectionOfInterfaces", runnable, serializable));
		assertTrue(((TypeVariableName) TypeName.get(typeVariables.get(4).asType())).bounds.contains(number));
		assertTrue(((TypeVariableName) TypeName.get(typeVariables.get(4).asType())).bounds.contains(runnable));
	}

	static class Recursive<T extends Map<List<T>, Set<T[]>>> {}

	@Test
	public void getTypeVariableTypeMirrorRecursive() {
		TypeMirror typeMirror = getElement(Recursive.class).asType();
		ParameterizedTypeName typeName = (ParameterizedTypeName) TypeName.get(typeMirror);
		String className = Recursive.class.getCanonicalName();
		assertEquals(typeName.toString(), className + "<T>");

		TypeVariableName typeVariableName = (TypeVariableName) typeName.typeArguments.get(0);

		try {
			typeVariableName.bounds.set(0, null);
			fail("Expected UnsupportedOperationException");
		} catch (UnsupportedOperationException expected) {
		}

		assertEquals(typeVariableName.toString(), "T");
		assertEquals(typeVariableName.bounds.toString()
			, "[java.util.Map<java.util.List<T>, java.util.Set<T[]>>]");
	}

	@Test
	public void getPrimitiveTypeMirror() {
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.BOOLEAN))
			, TypeName.BOOLEAN);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.BYTE))
			, TypeName.BYTE);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.SHORT))
			, TypeName.SHORT);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.INT))
			, TypeName.INT);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.LONG))
			, TypeName.LONG);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.CHAR))
			, TypeName.CHAR);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.FLOAT))
			, TypeName.FLOAT);
		assertEquals(TypeName.get(getTypes().getPrimitiveType(TypeKind.DOUBLE))
			, TypeName.DOUBLE);
	}

	@Test
	public void getArrayTypeMirror() {
		assertEquals(TypeName.get(getTypes().getArrayType(getMirror(Object.class)))
			, ArrayTypeName.of(ClassName.OBJECT));
	}

	@Test
	public void getVoidTypeMirror() {
		assertEquals(TypeName.get(getTypes().getNoType(TypeKind.VOID))
			, TypeName.VOID);
	}

	@Test
	public void getNullTypeMirror() {
		try {
			TypeName.get(getTypes().getNullType());
			fail();
		} catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void parameterizedType() throws Exception {
		ParameterizedTypeName type = ParameterizedTypeName.get(Map.class, String.class, Long.class);
		assertEquals(type.toString(), "java.util.Map<java.lang.String, java.lang.Long>");
	}

	@Test
	public void arrayType() throws Exception {
		ArrayTypeName type = ArrayTypeName.of(String.class);
		assertEquals(type.toString(), "java.lang.String[]");
	}

	@Test
	public void wildcardExtendsType() throws Exception {
		WildcardTypeName type = WildcardTypeName.subtypeOf(CharSequence.class);
		assertEquals(type.toString(), "? extends java.lang.CharSequence");
	}

	@Test
	public void wildcardExtendsObject() throws Exception {
		WildcardTypeName type = WildcardTypeName.subtypeOf(Object.class);
		assertEquals(type.toString(), "?");
	}

	@Test
	public void wildcardSuperType() throws Exception {
		WildcardTypeName type = WildcardTypeName.supertypeOf(String.class);
		assertEquals(type.toString(), "? super java.lang.String");
	}

	@Test
	public void wildcardMirrorNoBounds() throws Exception {
		WildcardType wildcard = getTypes().getWildcardType(null, null);
		TypeName type = TypeName.get(wildcard);
		assertEquals(type.toString(), "?");
	}

	@Test
	public void wildcardMirrorExtendsType() throws Exception {
		Types types = getTypes();
		Elements elements = getElements();
		TypeMirror charSequence = elements.getTypeElement(CharSequence.class.getName()).asType();
		WildcardType wildcard = types.getWildcardType(charSequence, null);
		TypeName type = TypeName.get(wildcard);
		assertEquals(type.toString(), "? extends java.lang.CharSequence");
	}

	@Test
	public void wildcardMirrorSuperType() throws Exception {
		Types types = getTypes();
		Elements elements = getElements();
		TypeMirror string = elements.getTypeElement(String.class.getName()).asType();
		WildcardType wildcard = types.getWildcardType(null, string);
		TypeName type = TypeName.get(wildcard);
		assertEquals(type.toString(), "? super java.lang.String");
	}

	@Test
	public void typeVariable() throws Exception {
		TypeVariableName type = TypeVariableName.get("T", CharSequence.class);
		assertEquals(type.toString(), "T"); // (Bounds are only emitted in declaration.)
	}

	@Test
	public void box() throws Exception {
		assertEquals(TypeName.INT.box(), ClassName.get(Integer.class));
		assertEquals(TypeName.VOID.box(), ClassName.get(Void.class));
		assertEquals(ClassName.get(Integer.class).box(), ClassName.get(Integer.class));
		assertEquals(ClassName.get(Void.class).box(), ClassName.get(Void.class));
		assertEquals(TypeName.OBJECT.box(), TypeName.OBJECT);
		assertEquals(ClassName.get(String.class).box(), ClassName.get(String.class));
	}

	@Test
	public void unbox() throws Exception {
		assertEquals(TypeName.INT, TypeName.INT.unbox());
		assertEquals(TypeName.VOID, TypeName.VOID.unbox());
		assertEquals(ClassName.get(Integer.class).unbox(), TypeName.INT.unbox());
		assertEquals(ClassName.get(Void.class).unbox(), TypeName.VOID.unbox());
		try {
			TypeName.OBJECT.unbox();
			fail();
		} catch (UnsupportedOperationException expected) {
		}
		try {
			ClassName.get(String.class).unbox();
			fail();
		} catch (UnsupportedOperationException expected) {
		}
	}
}

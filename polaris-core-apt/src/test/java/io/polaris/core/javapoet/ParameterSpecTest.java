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

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class ParameterSpecTest {

	@Test
	public void equalsAndHashCode() {
		ParameterSpec a = ParameterSpec.builder(int.class, "foo").build();
		ParameterSpec b = ParameterSpec.builder(int.class, "foo").build();
		assertTrue(a.equals(b));
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.toString(), b.toString());
		a = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
		b = ParameterSpec.builder(int.class, "i").addModifiers(Modifier.STATIC).build();
		assertTrue(a.equals(b));
		assertEquals(a.hashCode(), b.hashCode());
		assertEquals(a.toString(), b.toString());
	}

	@Test
	public void receiverParameterInstanceMethod() {
		ParameterSpec.Builder builder = ParameterSpec.builder(int.class, "this");
		assertEquals(builder.build().name, "this");
	}

	@Test
	public void receiverParameterNestedClass() {
		ParameterSpec.Builder builder = ParameterSpec.builder(int.class, "Foo.this");
		assertEquals(builder.build().name, "Foo.this");
	}

	@Test
	public void keywordName() {
		try {
			ParameterSpec.builder(int.class, "super");
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(), "not a valid name: super");
		}
	}

	@Test
	public void nullAnnotationsAddition() {
		try {
			ParameterSpec.builder(int.class, "foo").addAnnotations(null);
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage()
				, "annotationSpecs == null");
		}
	}

	final class VariableElementFieldClass {
		String name;
	}

	@Test
	public void addNonFinalModifier() {
		List<Modifier> modifiers = new ArrayList<>();
		modifiers.add(Modifier.FINAL);
		modifiers.add(Modifier.PUBLIC);

		try {
			ParameterSpec.builder(int.class, "foo")
				.addModifiers(modifiers);
			fail();
		} catch (Exception e) {
			assertEquals(e.getMessage(), "unexpected parameter modifier: public");
		}
	}

	@Test
	public void modifyAnnotations() {
		ParameterSpec.Builder builder = ParameterSpec.builder(int.class, "foo")
			.addAnnotation(Override.class)
			.addAnnotation(SuppressWarnings.class);

		builder.annotations.remove(1);
		assertEquals(1, builder.build().annotations.size());
	}

	@Test
	public void modifyModifiers() {
		ParameterSpec.Builder builder = ParameterSpec.builder(int.class, "foo")
			.addModifiers(Modifier.PUBLIC, Modifier.STATIC);

		builder.modifiers.remove(1);
		assertTrue(builder.build().modifiers.contains(Modifier.PUBLIC));
	}
}

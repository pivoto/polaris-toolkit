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

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public final class AnnotationSpecTest {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface AnnotationA {
	}

	@Inherited
	@Retention(RetentionPolicy.RUNTIME)
	public @interface AnnotationB {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface AnnotationC {
		String value();
	}

	public enum Breakfast {
		WAFFLES, PANCAKES;

		public String toString() {
			return name() + " with cherries!";
		}

		;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface HasDefaultsAnnotation {

		byte a() default 5;

		short b() default 6;

		int c() default 7;

		long d() default 12345678910L;

		float e() default 9.0f;

		double f() default 10.0;

		char[] g() default {0, 0xCAFE, 'z', '€', 'ℕ', '"', '\'', '\t', '\n'};

		boolean h() default true;

		Breakfast i() default Breakfast.WAFFLES;

		AnnotationA j() default @AnnotationA();

		String k() default "maple";

		Class<? extends Annotation> l() default AnnotationB.class;

		int[] m() default {1, 2, 3};

		Breakfast[] n() default {Breakfast.WAFFLES, Breakfast.PANCAKES};

		Breakfast o();

		int p();

		AnnotationC q() default @AnnotationC("foo");

		Class<? extends Number>[] r() default {Byte.class, Short.class, Integer.class, Long.class};

	}

	@HasDefaultsAnnotation(
		o = Breakfast.PANCAKES,
		p = 1701,
		f = 11.1,
		m = {9, 8, 1},
		l = Override.class,
		j = @AnnotationA,
		q = @AnnotationC("bar"),
		r = {Float.class, Double.class})
	public class IsAnnotated {
		// empty
	}


	@Test
	public void equalsAndHashCode() {
		AnnotationSpec a = AnnotationSpec.builder(AnnotationC.class).build();
		AnnotationSpec b = AnnotationSpec.builder(AnnotationC.class).build();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		a = AnnotationSpec.builder(AnnotationC.class).addMember("value", "$S", "123").build();
		b = AnnotationSpec.builder(AnnotationC.class).addMember("value", "$S", "123").build();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}


	@Test
	public void dynamicArrayOfEnumConstants() {
		AnnotationSpec.Builder builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
		builder.addMember("n", "$T.$L", Breakfast.class, Breakfast.PANCAKES.name());
		assertEquals("@io.polaris.core.javapoet.AnnotationSpecTest.HasDefaultsAnnotation("
				+ "n = io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ ")",
			builder.build().toString());

		// builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
		builder.addMember("n", "$T.$L", Breakfast.class, Breakfast.WAFFLES.name());
		builder.addMember("n", "$T.$L", Breakfast.class, Breakfast.PANCAKES.name());
		assertEquals("@io.polaris.core.javapoet.AnnotationSpecTest.HasDefaultsAnnotation("
				+ "n = {"
				+ "io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.WAFFLES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ "})",
			builder.build().toString());

		builder = builder.build().toBuilder(); // idempotent
		assertEquals("@io.polaris.core.javapoet.AnnotationSpecTest.HasDefaultsAnnotation("
				+ "n = {"
				+ "io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.WAFFLES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ "})",
			builder.build().toString());

		builder.addMember("n", "$T.$L", Breakfast.class, Breakfast.WAFFLES.name());
		assertEquals("@io.polaris.core.javapoet.AnnotationSpecTest.HasDefaultsAnnotation("
				+ "n = {"
				+ "io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.WAFFLES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.PANCAKES"
				+ ", io.polaris.core.javapoet.AnnotationSpecTest.Breakfast.WAFFLES"
				+ "})",
			builder.build().toString());
	}

	@Test
	public void reflectAnnotation() {
		HasDefaultsAnnotation annotation = IsAnnotated.class.getAnnotation(HasDefaultsAnnotation.class);
		AnnotationSpec spec = AnnotationSpec.get(annotation);
		TypeSpec taco = TypeSpec.classBuilder("Taco")
			.addAnnotation(spec)
			.build();
		assertEquals("package com.squareup.tacos;\n"
			+ "\n"
			+ "import io.polaris.core.javapoet.AnnotationSpecTest;\n"
			+ "import java.lang.Double;\n"
			+ "import java.lang.Float;\n"
			+ "import java.lang.Override;\n"
			+ "\n"
			+ "@AnnotationSpecTest.HasDefaultsAnnotation(\n"
			+ "    f = 11.1,\n"
			+ "    l = Override.class,\n"
			+ "    m = {\n"
			+ "        9,\n"
			+ "        8,\n"
			+ "        1\n"
			+ "    },\n"
			+ "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n"
			+ "    p = 1701,\n"
			+ "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n"
			+ "    r = {\n"
			+ "        Float.class,\n"
			+ "        Double.class\n"
			+ "    }\n"
			+ ")\n"
			+ "class Taco {\n"
			+ "}\n", toString(taco));
	}

	@Test
	public void reflectAnnotationWithDefaults() {
		HasDefaultsAnnotation annotation = IsAnnotated.class.getAnnotation(HasDefaultsAnnotation.class);
		AnnotationSpec spec = AnnotationSpec.get(annotation, true);
		TypeSpec taco = TypeSpec.classBuilder("Taco")
			.addAnnotation(spec)
			.build();
		assertEquals("package com.squareup.tacos;\n"
			+ "\n"
			+ "import io.polaris.core.javapoet.AnnotationSpecTest;\n"
			+ "import java.lang.Double;\n"
			+ "import java.lang.Float;\n"
			+ "import java.lang.Override;\n"
			+ "\n"
			+ "@AnnotationSpecTest.HasDefaultsAnnotation(\n"
			+ "    a = 5,\n"
			+ "    b = 6,\n"
			+ "    c = 7,\n"
			+ "    d = 12345678910L,\n"
			+ "    e = 9.0f,\n"
			+ "    f = 11.1,\n"
			+ "    g = {\n"
			+ "        '\\u0000',\n"
			+ "        '쫾',\n"
			+ "        'z',\n"
			+ "        '€',\n"
			+ "        'ℕ',\n"
			+ "        '\"',\n"
			+ "        '\\'',\n"
			+ "        '\\t',\n"
			+ "        '\\n'\n"
			+ "    },\n"
			+ "    h = true,\n"
			+ "    i = AnnotationSpecTest.Breakfast.WAFFLES,\n"
			+ "    j = @AnnotationSpecTest.AnnotationA,\n"
			+ "    k = \"maple\",\n"
			+ "    l = Override.class,\n"
			+ "    m = {\n"
			+ "        9,\n"
			+ "        8,\n"
			+ "        1\n"
			+ "    },\n"
			+ "    n = {\n"
			+ "        AnnotationSpecTest.Breakfast.WAFFLES,\n"
			+ "        AnnotationSpecTest.Breakfast.PANCAKES\n"
			+ "    },\n"
			+ "    o = AnnotationSpecTest.Breakfast.PANCAKES,\n"
			+ "    p = 1701,\n"
			+ "    q = @AnnotationSpecTest.AnnotationC(\"bar\"),\n"
			+ "    r = {\n"
			+ "        Float.class,\n"
			+ "        Double.class\n"
			+ "    }\n"
			+ ")\n"
			+ "class Taco {\n"
			+ "}\n", toString(taco));
	}

	@Test
	public void disallowsNullMemberName() {
		AnnotationSpec.Builder builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
		try {
			AnnotationSpec.Builder $L = builder.addMember(null, "$L", "");
			fail($L.build().toString());
		} catch (NullPointerException e) {
			assertTrue(e.getMessage().contains("name == null"));
		}
	}

	@Test
	public void requiresValidMemberName() {
		AnnotationSpec.Builder builder = AnnotationSpec.builder(HasDefaultsAnnotation.class);
		try {
			AnnotationSpec.Builder $L = builder.addMember("@", "$L", "");
			fail($L.build().toString());
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("not a valid name: @"));
		}
	}

	@Test
	public void modifyMembers() {
		AnnotationSpec.Builder builder = AnnotationSpec.builder(SuppressWarnings.class)
			.addMember("value", "$S", "Foo");

		builder.members.clear();
		builder.members.put("value", Arrays.asList(CodeBlock.of("$S", "Bar")));

		assertEquals("@java.lang.SuppressWarnings(\"Bar\")", builder.build().toString());
	}

	private String toString(TypeSpec typeSpec) {
		return JavaFile.builder("com.squareup.tacos", typeSpec).build().toString();
	}
}

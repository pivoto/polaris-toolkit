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

import java.util.Map;

import javax.lang.model.element.TypeElement;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

public final class ClassNameTest {

	@Test
	public void bestGuessForString_simpleClass() {
		assertEquals(ClassName.bestGuess(String.class.getName())
			, ClassName.get("java.lang", "String"));
	}

	@Test
	public void bestGuessNonAscii() {
		ClassName className = ClassName.bestGuess(
			"com.\ud835\udc1andro\ud835\udc22d.\ud835\udc00ctiv\ud835\udc22ty");
		assertEquals("com.\ud835\udc1andro\ud835\udc22d", className.packageName());
		assertEquals("\ud835\udc00ctiv\ud835\udc22ty", className.simpleName());
	}

	static class OuterClass {
		static class InnerClass {}
	}

	@Test
	public void bestGuessForString_nestedClass() {
		assertEquals(ClassName.bestGuess(Map.Entry.class.getCanonicalName())
			, ClassName.get("java.util", "Map", "Entry"));
		assertEquals(ClassName.bestGuess(OuterClass.InnerClass.class.getCanonicalName())
			, ClassName.get("io.polaris.core.javapoet",
				"ClassNameTest", "OuterClass", "InnerClass"));
	}

	@Test
	public void bestGuessForString_defaultPackage() {
		assertEquals(ClassName.bestGuess("SomeClass")
			, ClassName.get("", "SomeClass"));
		assertEquals(ClassName.bestGuess("SomeClass.Nested")
			, ClassName.get("", "SomeClass", "Nested"));
		assertEquals(ClassName.bestGuess("SomeClass.Nested.EvenMore")
			, ClassName.get("", "SomeClass", "Nested", "EvenMore"));
	}

	@Test
	public void bestGuessForString_confusingInput() {
		assertBestGuessThrows("");
		assertBestGuessThrows(".");
		assertBestGuessThrows(".Map");
		assertBestGuessThrows("java");
		assertBestGuessThrows("java.util");
		assertBestGuessThrows("java.util.");
		assertBestGuessThrows("java..util.Map.Entry");
		assertBestGuessThrows("java.util..Map.Entry");
		assertBestGuessThrows("java.util.Map..Entry");
		assertBestGuessThrows("com.test.$");
		assertBestGuessThrows("com.test.LooksLikeAClass.pkg");
		assertBestGuessThrows("!@#$gibberish%^&*");
	}

	private void assertBestGuessThrows(String s) {
		try {
			ClassName.bestGuess(s);
			fail();
		} catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void createNestedClass() {
		ClassName foo = ClassName.get("com.example", "Foo");
		ClassName bar = foo.nestedClass("Bar");
		assertEquals(bar, ClassName.get("com.example", "Foo", "Bar"));
		ClassName baz = bar.nestedClass("Baz");
		assertEquals(baz, ClassName.get("com.example", "Foo", "Bar", "Baz"));
	}

	static class $Outer {
		static class $Inner {}
	}


	/** Returns a new instance like {@code object} that throws on {@code getKind()}. */
	private TypeElement preventGetKind(TypeElement object) {
		TypeElement spy = Mockito.spy(object);
		when(spy.getKind()).thenThrow(new AssertionError());
		when(spy.getEnclosingElement()).thenAnswer(invocation -> {
			Object enclosingElement = invocation.callRealMethod();
			return enclosingElement instanceof TypeElement
				? preventGetKind((TypeElement) enclosingElement)
				: enclosingElement;
		});
		return spy;
	}

	@Test
	public void classNameFromClass() {
		assertEquals("java.lang.Object"
			, ClassName.get(Object.class).toString());
		assertEquals("io.polaris.core.javapoet.ClassNameTest.OuterClass.InnerClass"
			, ClassName.get(OuterClass.InnerClass.class).toString());
		assertEquals("io.polaris.core.javapoet.ClassNameTest$1"
			, (ClassName.get(new Object() {}.getClass())).toString());
		assertEquals("io.polaris.core.javapoet.ClassNameTest$2$1"
			, (ClassName.get(new Object() {
				Object inner = new Object() {};
			}.inner.getClass())).toString());
		assertEquals("io.polaris.core.javapoet.ClassNameTest.$Outer"
			, (ClassName.get($Outer.class)).toString());
		assertEquals("io.polaris.core.javapoet.ClassNameTest.$Outer.$Inner"
			, (ClassName.get($Outer.$Inner.class)).toString());
	}

	@Test
	public void peerClass() {
		assertEquals(ClassName.get(Double.class).peerClass("Short")
			, ClassName.get(Short.class));
		assertEquals(ClassName.get("", "Double").peerClass("Short")
			, ClassName.get("", "Short"));
		assertEquals(ClassName.get("a.b", "Combo", "Taco").peerClass("Burrito")
			, ClassName.get("a.b", "Combo", "Burrito"));
	}

	@Test
	public void fromClassRejectionTypes() {
		try {
			ClassName.get(int.class);
			fail();
		} catch (IllegalArgumentException ignored) {
		}
		try {
			ClassName.get(void.class);
			fail();
		} catch (IllegalArgumentException ignored) {
		}
		try {
			ClassName.get(Object[].class);
			fail();
		} catch (IllegalArgumentException ignored) {
		}
	}

	@Test
	public void reflectionName() {
		assertEquals("java.lang.Object", TypeName.OBJECT.reflectionName());
		assertEquals("java.lang.Thread$State", ClassName.get(Thread.State.class).reflectionName());
		assertEquals("java.util.Map$Entry", ClassName.get(Map.Entry.class).reflectionName());
		assertEquals("Foo", ClassName.get("", "Foo").reflectionName());
		assertEquals("Foo$Bar$Baz", ClassName.get("", "Foo", "Bar", "Baz").reflectionName());
		assertEquals("a.b.c.Foo$Bar$Baz", ClassName.get("a.b.c", "Foo", "Bar", "Baz").reflectionName());
	}

	@Test
	public void canonicalName() {
		assertEquals("java.lang.Object", TypeName.OBJECT.canonicalName());
		assertEquals("java.lang.Thread.State", ClassName.get(Thread.State.class).canonicalName());
		assertEquals("java.util.Map.Entry", ClassName.get(Map.Entry.class).canonicalName());
		assertEquals("Foo", ClassName.get("", "Foo").canonicalName());
		assertEquals("Foo.Bar.Baz", ClassName.get("", "Foo", "Bar", "Baz").canonicalName());
		assertEquals("a.b.c.Foo.Bar.Baz", ClassName.get("a.b.c", "Foo", "Bar", "Baz").canonicalName());
	}
}

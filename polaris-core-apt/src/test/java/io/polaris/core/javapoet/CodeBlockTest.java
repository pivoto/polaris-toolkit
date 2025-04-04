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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public final class CodeBlockTest {
	@Test
	public void equalsAndHashCode() {
		CodeBlock a = CodeBlock.builder().build();
		CodeBlock b = CodeBlock.builder().build();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		a = CodeBlock.builder().add("$L", "taco").build();
		b = CodeBlock.builder().add("$L", "taco").build();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void of() {
		CodeBlock a = CodeBlock.of("$L taco", "delicious");
		assertEquals("delicious taco", a.toString());
	}

	@Test
	public void isEmpty() {
		assertTrue(CodeBlock.builder().isEmpty());
		assertTrue(CodeBlock.builder().add("").isEmpty());
		assertFalse(CodeBlock.builder().add(" ").isEmpty());
	}

	@Test
	public void indentCannotBeIndexed() {
		try {
			CodeBlock.builder().add("$1>", "taco").build();
			fail();
		} catch (IllegalArgumentException exp) {
			assertEquals("$$, $>, $<, $[, $], $W, and $Z may not have an index", exp.getMessage());
		}
	}

	@Test
	public void deindentCannotBeIndexed() {
		try {
			CodeBlock.builder().add("$1<", "taco").build();
			fail();
		} catch (IllegalArgumentException exp) {
			assertEquals("$$, $>, $<, $[, $], $W, and $Z may not have an index", exp.getMessage());
		}
	}

	@Test
	public void dollarSignEscapeCannotBeIndexed() {
		try {
			CodeBlock.builder().add("$1$", "taco").build();
			fail();
		} catch (IllegalArgumentException exp) {
			assertEquals("$$, $>, $<, $[, $], $W, and $Z may not have an index", exp.getMessage());
		}
	}

	@Test
	public void statementBeginningCannotBeIndexed() {
		try {
			CodeBlock.builder().add("$1[", "taco").build();
			fail();
		} catch (IllegalArgumentException exp) {
			assertEquals("$$, $>, $<, $[, $], $W, and $Z may not have an index", exp.getMessage());
		}
	}

	@Test
	public void statementEndingCannotBeIndexed() {
		try {
			CodeBlock.builder().add("$1]", "taco").build();
			fail();
		} catch (IllegalArgumentException exp) {
			assertEquals("$$, $>, $<, $[, $], $W, and $Z may not have an index", exp.getMessage());
		}
	}

	@Test
	public void nameFormatCanBeIndexed() {
		CodeBlock block = CodeBlock.builder().add("$1N", "taco").build();
		assertEquals("taco", block.toString());
	}

	@Test
	public void literalFormatCanBeIndexed() {
		CodeBlock block = CodeBlock.builder().add("$1L", "taco").build();
		assertEquals("taco", block.toString());
	}

	@Test
	public void stringFormatCanBeIndexed() {
		CodeBlock block = CodeBlock.builder().add("$1S", "taco").build();
		assertEquals("\"taco\"", block.toString());
	}

	@Test
	public void typeFormatCanBeIndexed() {
		CodeBlock block = CodeBlock.builder().add("$1T", String.class).build();
		assertEquals("java.lang.String", block.toString());
	}

	@Test
	public void simpleNamedArgument() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("text", "taco");
		CodeBlock block = CodeBlock.builder().addNamed("$text:S", map).build();
		assertEquals("\"taco\"", block.toString());
	}

	@Test
	public void repeatedNamedArgument() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("text", "tacos");
		CodeBlock block = CodeBlock.builder()
			.addNamed("\"I like \" + $text:S + \". Do you like \" + $text:S + \"?\"", map)
			.build();
		assertEquals("\"I like \" + \"tacos\" + \". Do you like \" + \"tacos\" + \"?\"",
			block.toString());
	}

	@Test
	public void namedAndNoArgFormat() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("text", "tacos");
		CodeBlock block = CodeBlock.builder()
			.addNamed("$>\n$text:L for $$3.50", map).build();
		assertEquals("\n  tacos for $3.50", block.toString());
	}

	@Test
	public void missingNamedArgument() {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			CodeBlock.builder().addNamed("$text:S", map).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "Missing named argument for $text");
		}
	}

	@Test
	public void lowerCaseNamed() {
		try {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("Text", "tacos");
			CodeBlock block = CodeBlock.builder().addNamed("$Text:S", map).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "argument 'Text' must start with a lowercase character");
		}
	}

	@Test
	public void multipleNamedArguments() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("pipe", System.class);
		map.put("text", "tacos");

		CodeBlock block = CodeBlock.builder()
			.addNamed("$pipe:T.out.println(\"Let's eat some $text:L\");", map)
			.build();

		assertEquals("java.lang.System.out.println(\"Let's eat some tacos\");",
			block.toString());
	}

	@Test
	public void namedNewline() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("clazz", Integer.class);
		CodeBlock block = CodeBlock.builder().addNamed("$clazz:T\n", map).build();
		assertEquals("java.lang.Integer\n", block.toString());
	}

	@Test
	public void danglingNamed() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("clazz", Integer.class);
		try {
			CodeBlock.builder().addNamed("$clazz:T$", map).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "dangling $ at end");
		}
	}

	@Test
	public void indexTooHigh() {
		try {
			CodeBlock.builder().add("$2T", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "index 2 for '$2T' not in range (received 1 arguments)");
		}
	}

	@Test
	public void indexIsZero() {
		try {
			CodeBlock.builder().add("$0T", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "index 0 for '$0T' not in range (received 1 arguments)");
		}
	}

	@Test
	public void indexIsNegative() {
		try {
			CodeBlock.builder().add("$-1T", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "invalid format string: '$-1T'");
		}
	}

	@Test
	public void indexWithoutFormatType() {
		try {
			CodeBlock.builder().add("$1", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "dangling format characters in '$1'");
		}
	}

	@Test
	public void indexWithoutFormatTypeNotAtStringEnd() {
		try {
			CodeBlock.builder().add("$1 taco", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "invalid format string: '$1 taco'");
		}
	}

	@Test
	public void indexButNoArguments() {
		try {
			CodeBlock.builder().add("$1T").build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "index 1 for '$1T' not in range (received 0 arguments)");
		}
	}

	@Test
	public void formatIndicatorAlone() {
		try {
			CodeBlock.builder().add("$", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "dangling format characters in '$'");
		}
	}

	@Test
	public void formatIndicatorWithoutIndexOrFormatType() {
		try {
			CodeBlock.builder().add("$ tacoString", String.class).build();
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "invalid format string: '$ tacoString'");
		}
	}

	@Test
	public void sameIndexCanBeUsedWithDifferentFormats() {
		CodeBlock block = CodeBlock.builder()
			.add("$1T.out.println($1S)", ClassName.get(System.class))
			.build();
		assertEquals("java.lang.System.out.println(\"java.lang.System\")", block.toString());
	}

	@Test
	public void tooManyStatementEnters() {
		CodeBlock codeBlock = CodeBlock.builder().add("$[$[").build();
		try {
			// We can't report this error until rendering type because code blocks might be composed.
			codeBlock.toString();
			fail();
		} catch (IllegalStateException expected) {
			assertEquals(expected.getMessage(), "statement enter $[ followed by statement enter $[");
		}
	}

	@Test
	public void statementExitWithoutStatementEnter() {
		CodeBlock codeBlock = CodeBlock.builder().add("$]").build();
		try {
			// We can't report this error until rendering type because code blocks might be composed.
			codeBlock.toString();
			fail();
		} catch (IllegalStateException expected) {
			assertEquals(expected.getMessage(), "statement exit $] has no matching statement enter $[");
		}
	}

	@Test
	public void join() {
		List<CodeBlock> codeBlocks = new ArrayList<>();
		codeBlocks.add(CodeBlock.of("$S", "hello"));
		codeBlocks.add(CodeBlock.of("$T", ClassName.get("world", "World")));
		codeBlocks.add(CodeBlock.of("need tacos"));

		CodeBlock joined = CodeBlock.join(codeBlocks, " || ");
		assertEquals("\"hello\" || world.World || need tacos", joined.toString());
	}

	@Test
	public void joining() {
		List<CodeBlock> codeBlocks = new ArrayList<>();
		codeBlocks.add(CodeBlock.of("$S", "hello"));
		codeBlocks.add(CodeBlock.of("$T", ClassName.get("world", "World")));
		codeBlocks.add(CodeBlock.of("need tacos"));

		CodeBlock joined = codeBlocks.stream().collect(CodeBlock.joining(" || "));
		assertEquals("\"hello\" || world.World || need tacos", joined.toString());
	}

	@Test
	public void joiningSingle() {
		List<CodeBlock> codeBlocks = new ArrayList<>();
		codeBlocks.add(CodeBlock.of("$S", "hello"));

		CodeBlock joined = codeBlocks.stream().collect(CodeBlock.joining(" || "));
		assertEquals("\"hello\"", joined.toString());
	}

	@Test
	public void joiningWithPrefixAndSuffix() {
		List<CodeBlock> codeBlocks = new ArrayList<>();
		codeBlocks.add(CodeBlock.of("$S", "hello"));
		codeBlocks.add(CodeBlock.of("$T", ClassName.get("world", "World")));
		codeBlocks.add(CodeBlock.of("need tacos"));

		CodeBlock joined = codeBlocks.stream().collect(CodeBlock.joining(" || ", "start {", "} end"));
		assertEquals("start {\"hello\" || world.World || need tacos} end", joined.toString());
	}

	@Test
	public void clear() {
		CodeBlock block = CodeBlock.builder()
			.addStatement("$S", "Test string")
			.clear()
			.build();

		assertTrue(block.toString().isEmpty());
	}
}

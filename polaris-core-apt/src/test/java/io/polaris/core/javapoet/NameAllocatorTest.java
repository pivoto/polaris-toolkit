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


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public final class NameAllocatorTest {

	@Test
	public void usage() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("foo", 1), "foo");
		assertEquals(nameAllocator.newName("bar", 2), "bar");
		assertEquals(nameAllocator.get(1), "foo");
		assertEquals(nameAllocator.get(2), "bar");
	}

	@Test
	public void nameCollision() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("foo"), "foo");
		assertEquals(nameAllocator.newName("foo"), "foo_");
		assertEquals(nameAllocator.newName("foo"), "foo__");
	}

	@Test
	public void nameCollisionWithTag() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("foo", 1), "foo");
		assertEquals(nameAllocator.newName("foo", 2), "foo_");
		assertEquals(nameAllocator.newName("foo", 3), "foo__");
		assertEquals(nameAllocator.get(1), "foo");
		assertEquals(nameAllocator.get(2), "foo_");
		assertEquals(nameAllocator.get(3), "foo__");
	}

	@Test
	public void characterMappingSubstitute() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("a-b", 1), "a_b");
	}

	@Test
	public void characterMappingSurrogate() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("a\uD83C\uDF7Ab", 1), "a_b");
	}

	@Test
	public void characterMappingInvalidStartButValidPart() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("1ab", 1), "_1ab");
		assertEquals(nameAllocator.newName("a-1", 2), "a_1");
	}

	@Test
	public void characterMappingInvalidStartIsInvalidPart() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("&ab", 1), "_ab");
	}

	@Test
	public void javaKeyword() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		assertEquals(nameAllocator.newName("public", 1), "public_");
		assertEquals(nameAllocator.get(1), "public_");
	}

	@Test
	public void tagReuseForbidden() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		nameAllocator.newName("foo", 1);
		try {
			nameAllocator.newName("bar", 1);
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "tag 1 cannot be used for both 'foo' and 'bar'");
		}
	}

	@Test
	public void useBeforeAllocateForbidden() throws Exception {
		NameAllocator nameAllocator = new NameAllocator();
		try {
			nameAllocator.get(1);
			fail();
		} catch (IllegalArgumentException expected) {
			assertEquals(expected.getMessage(), "unknown tag: 1");
		}
	}

	@Test
	public void cloneUsage() throws Exception {
		NameAllocator outterAllocator = new NameAllocator();
		outterAllocator.newName("foo", 1);

		NameAllocator innerAllocator1 = outterAllocator.clone();
		assertEquals(innerAllocator1.newName("bar", 2), "bar");
		assertEquals(innerAllocator1.newName("foo", 3), "foo_");

		NameAllocator innerAllocator2 = outterAllocator.clone();
		assertEquals(innerAllocator2.newName("foo", 2), "foo_");
		assertEquals(innerAllocator2.newName("bar", 3), "bar");
	}
}

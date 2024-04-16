package io.polaris.core.lang.copier;

import io.polaris.core.TestConsole;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeanToBeanCopierTest {

	@Test
	void testCopy() {
		A a = new A();
		B b = new B();

		a.id = "a";
		a.name = "a.name";
		a.nickname = "a.nickname";
		b.name = "b.name";

		CopyOptions copyOptions = CopyOptions.create();
		CopyOptions copyOptions1 = copyOptions.ignoreNull(true).override(false);
		B t = Copiers.create(a, b, B.class,
			copyOptions1.ignoreCase(true)
		).copy();
		TestConsole.println(a);
		TestConsole.println(b);
		TestConsole.println(t);

		Assertions.assertEquals(a.id, b.id);
		Assertions.assertNotEquals(a.name, b.name);
		Assertions.assertEquals(a.nickname, b.nickName);
	}

	@ToString
	@Data
	public static class A {
		private String id;
		private String name;
		private String nickname;
		private int age;
	}

	@ToString
	@Data
	public static class B {
		private String id;
		private String name;
		private String nickName;
		private int age;
	}
}

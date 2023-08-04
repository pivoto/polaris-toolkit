package io.polaris.core.object.copier;

import io.polaris.core.lang.copier.BeanToBeanCopier;
import io.polaris.core.lang.copier.CopyOptions;
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

		B t = new BeanToBeanCopier<>(a, b, B.class,
			CopyOptions.create().ignoreNull().override(false).ignoreCase()
		).copy();
		System.out.println(a);
		System.out.println(b);
		System.out.println(t);

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

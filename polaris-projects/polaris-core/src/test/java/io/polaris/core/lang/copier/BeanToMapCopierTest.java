package io.polaris.core.lang.copier;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.TestConsole;
import io.polaris.core.lang.TypeRef;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

class BeanToMapCopierTest {

	@Test
	void testCopy() {
		B b = new B();
		b.id = "b";
		b.name = "b.name";
		Map<String, Object> m = new HashMap<>();
		CopyOptions copyOptions = CopyOptions.create();
		CopyOptions copyOptions1 = copyOptions
			.ignoreNull(true).override(false);
		Copiers.create(b, m, new TypeRef<Map<String, Object>>() {
			}.getType(),
			copyOptions1.ignoreCase(true)
		).copy();
		TestConsole.println(b);
		TestConsole.println(m);
	}

	@Test
	void testCopy2() {
		Map a = new HashMap();
		a.put("id", "a");
		a.put("name", "a.name");
		a.put("nickname", "a.nickname");
//		a.put("age", "18");
		B b = new B();
		b.name = "b.name";
		CopyOptions copyOptions = CopyOptions.create();
		CopyOptions copyOptions1 = copyOptions.ignoreNull(true).override(false);
		Copiers.create(a, b, B.class,
			copyOptions1.ignoreCase(true)
		).copy();
		TestConsole.println(a);
		TestConsole.println(b);
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

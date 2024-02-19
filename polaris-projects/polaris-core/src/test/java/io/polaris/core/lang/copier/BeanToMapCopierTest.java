package io.polaris.core.lang.copier;

import io.polaris.core.TestConsole;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.copier.BeanToMapCopier;
import io.polaris.core.lang.copier.CopyOptions;
import io.polaris.core.lang.copier.MapToBeanCopier;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class BeanToMapCopierTest {

	@Test
	void testCopy() {
		B b = new B();
		b.id = "b";
		b.name = "b.name";
		Map<String, Object> m = new HashMap<>();
		new BeanToMapCopier<>(b, m, new TypeRef<Map<String, Object>>() {
		}.getType(),
			CopyOptions.create().ignoreNull().override(false).ignoreCase()
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
		new MapToBeanCopier<>(a, b, B.class,
			CopyOptions.create().ignoreNull().override(false).ignoreCase()
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

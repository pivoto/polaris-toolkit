package io.polaris.core.object.copier;

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
		Map m = new HashMap();
		new BeanToMapCopier<>(b, m, new TypeRef<Map<String, Integer>>() {
		}.getType(),
			CopyOptions.create().ignoreNull().override(false).ignoreCase()
		).copy();
		System.out.println(b);
		System.out.println(m);
	}

	@Test
	void testCopy2() {
		Map a = new HashMap();
		a.put("id", "a");
		a.put("name", "a.name");
		a.put("nickname", "a.nickname");
		B b = new B();
		b.name = "b.name";
		new MapToBeanCopier<>(a, b, B.class,
			CopyOptions.create().ignoreNull().override(false).ignoreCase()
		).copy();
		System.out.println(a);
		System.out.println(b);
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

package io.polaris.core.object;

import io.polaris.core.object.copier.CopyOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class CopierTest {

	@Test
	void test01() {
		Map<Object, Object> source = new HashMap<>();
		source.put("key1", "val1");
		source.put("key2", "val2");
		source.put("key3", Integer.valueOf(123));
		source.put("key4", new Object());
		source.put("key5", new Object[]{"", ""});

		Map<Object, Object> target = new HashMap<>();
		Copiers.copy(source, target, CopyOptions.create());

		System.out.println("source:" + source);
		System.out.println("target:" + target);
	}

	@Test
	void test03() {
		CopyObj source = new CopyObj();
		source.setKey1("val1");
		source.setKey2("val2");
		source.setKey3(123L);
		source.setKey4(true);
		source.setKey5(new String[]{"x","y"});

		Map<Object, Object> target = new HashMap<>();
		Copiers.copy(source, target, CopyOptions.create());

		System.out.println("source:" + source);
		System.out.println("target:" + target);
	}

	@Test
	void test04() {
		CopyObj source = new CopyObj();
		source.setKey1("val1");
		source.setKey2("val2");
		source.setKey3(123L);
		source.setKey4(true);
		source.setKey5(new String[]{"x","y"});

		CopyObj target = new CopyObj();
		Copiers.copy(source, target, CopyOptions.create());

		System.out.println("source:" + source);
		System.out.println("target:" + target);
	}

	@Test
	void test02() {
		Map<Object, Object> source = new HashMap<>();
		source.put("key1", "val1");
		source.put("key2", "val2");
		source.put("key3", Integer.valueOf(123));
		source.put("key4", new Object());
		source.put("key5", new Object[]{"", ""});

		Object target = new CopyObj();
		Copiers.copy(source, target, CopyOptions.create());

		System.out.println("source:" + source);
		System.out.println("target:" + target);
	}


	@Data
	@ToString
	@EqualsAndHashCode
	public static class CopyObj {
		private String key1;
		private String key2;
		private Long key3;
		private Boolean key4;
		private String[] key5;
	}
}

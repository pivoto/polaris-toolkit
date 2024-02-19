package io.polaris.core.lang.copier;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.TestConsole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
@TestClassOrder(ClassOrderer.ClassName.class)
class CopierTest {

	@Test
	void test01_MapToMap() {
		Map<Object, Object> source = new HashMap<>();
		source.put("key1", "val1");
		source.put("key2", "val2");
		source.put("key3", Integer.valueOf(123));
		source.put("key4", new Object());
		source.put("key5", new Object[]{"", ""});

		Map<Object, Object> target = new HashMap<>();
		Copiers.copy(source, target, CopyOptions.create());

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);
		Assertions.assertEquals(source, target);
	}

	@Test
	void test02_MapToBean() {
		Map<Object, Object> source = new HashMap<>();
		source.put("key1", "val1");
		source.put("key2", "val2");
		source.put("key3", Integer.valueOf(123));
		source.put("key4", new Object());
		source.put("key5", new Object[]{"", ""});

		CopyObj target = new CopyObj();
		Copiers.copy(source, target, CopyOptions.create());

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);

		Assertions.assertEquals("val1", target.getKey1());
		Assertions.assertEquals("val2", target.getKey2());
		Assertions.assertEquals(123L, target.getKey3());
		Assertions.assertEquals(false, target.getKey4());
	}

	@Test
	void test03_BeanToMap() {
		CopyObj source = new CopyObj();
		source.setKey1("val1");
		source.setKey2("val2");
		source.setKey3(123L);
		source.setKey4(true);
		source.setKey5(new String[]{"x", "y"});

		Map<Object, Object> target = new HashMap<>();
		Copiers.copy(source, target, CopyOptions.create());

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);
		Assertions.assertEquals("val1", target.get("key1"));
		Assertions.assertEquals("val2", target.get("key2"));
		Assertions.assertEquals(123L, target.get("key3"));
		Assertions.assertEquals(true, target.get("key4"));
	}

	@Test
	void test04_BeanToBean() {
		CopyObj source = new CopyObj();
		source.setKey1("val1");
		source.setKey2("val2");
		source.setKey3(123L);
		source.setKey4(true);
		source.setKey5(new String[]{"x", "y"});

		CopyObj target = new CopyObj();
		Copiers.copy(source, target, CopyOptions.create());

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);
		Assertions.assertEquals(source, target);
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

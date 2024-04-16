package io.polaris.core.lang.copier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.polaris.core.TestConsole;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
		source.put("objId1", "objId1");
		source.put("obj_id1", "obj_id1");
		source.put("vStr1", "vStr1");
		source.put("VStr1", "VStr1");
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
		source.put("objId1", "objId1");
		source.put("obj_id1", "obj_id1");
		source.put("vStr1", "vStr1");
		source.put("VStr1", "VStr1");
		source.put("key1", "val1");
		source.put("key2", "val2");
		source.put("key3", Integer.valueOf(123));
		source.put("key4", new Object());
		source.put("key5", new Object[]{"", ""});

		CopyObj target = new CopyObj();
		CopyOptions copyOptions = CopyOptions.create().override(false);
		CopyOptions copyOptions1 = copyOptions.ignoreCapitalize(true);
		CopyOptions copyOptions2 = copyOptions1.enableUnderlineToCamelCase(true);
		Copiers.copy(source, target, copyOptions2.enableCamelToUnderlineCase(true));

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);

		Assertions.assertEquals("val1", target.getKey1());
		Assertions.assertEquals("val2", target.getKey2());
		Assertions.assertEquals(123L, target.getKey3());
		Assertions.assertEquals(false, target.getKey4());
		Assertions.assertEquals("vStr1", target.getvStr1());
		Assertions.assertEquals("VStr1", target.getVStr1());
		Assertions.assertEquals("objId1", target.getObjId1());
		Assertions.assertEquals("obj_id1", target.getObj_id1());

	}

	@Test
	void test03_BeanToMap() {
		CopyObj source = new CopyObj();
		source.setObjId1("objId1");
		source.setObj_id1("obj_id1");
		source.setvStr1("vStr1");
		source.setVStr1("VStr1");
		source.setKey1("val1");
		source.setKey2("val2");
		source.setKey3(123L);
		source.setKey4(true);
		source.setKey5(new String[]{"x", "y"});

		Map<Object, Object> target = new HashMap<>();
		CopyOptions copyOptions = CopyOptions.create().override(false);
		CopyOptions copyOptions1 = copyOptions.ignoreCapitalize(true);
		CopyOptions copyOptions2 = copyOptions1.enableUnderlineToCamelCase(true);
		Copiers.copy(source, target, copyOptions2.enableCamelToUnderlineCase(true));

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);
		Assertions.assertEquals("val1", target.get("key1"));
		Assertions.assertEquals("val2", target.get("key2"));
		Assertions.assertEquals(123L, target.get("key3"));
		Assertions.assertEquals(true, target.get("key4"));
		Assertions.assertEquals("vStr1", target.get("vStr1"));
		Assertions.assertEquals("VStr1", target.get("VStr1"));
		Assertions.assertEquals("objId1", target.get("objId1"));
		Assertions.assertEquals("obj_id1", target.get("obj_id1"));
	}

	@Test
	void test04_BeanToBean() {
		CopyObj source = new CopyObj();
		source.setObjId1("objId1");
		source.setObj_id1("obj_id1");
		source.setvStr1("vStr1");
		source.setVStr1("VStr1");
		source.setKey1("val1");
		source.setKey2("val2");
		source.setKey3(123L);
		source.setKey4(true);
		source.setKey5(new String[]{"x", "y"});

		CopyObj target = new CopyObj();
		CopyOptions copyOptions = CopyOptions.create().override(false);
		CopyOptions copyOptions1 = copyOptions.ignoreCapitalize(true);
		CopyOptions copyOptions2 = copyOptions1.enableUnderlineToCamelCase(true);
		Copiers.copy(source, target, copyOptions2.enableCamelToUnderlineCase(true));

		TestConsole.println("source:" + source);
		TestConsole.println("target:" + target);
		Assertions.assertEquals(source, target);
	}


	@Data
	public static class CopyObj {
		private String key1;
		private String key2;
		private Long key3;
		private Boolean key4;
		private String[] key5;
		// lombok的大小写bug
		@Getter(AccessLevel.NONE)
		@Setter(AccessLevel.NONE)
		private String vStr1;
		@Getter(AccessLevel.NONE)
		@Setter(AccessLevel.NONE)
		private String VStr1;
		private String obj_id1;
		private String objId1;

		public String getvStr1() {
			return vStr1;
		}

		public void setvStr1(String vStr1) {
			this.vStr1 = vStr1;
		}

		public String getVStr1() {
			return VStr1;
		}

		public void setVStr1(String VStr1) {
			this.VStr1 = VStr1;
		}

		@Override
		public String toString() {
			return "CopyObj{" +
				"key1='" + key1 + '\'' +
				", key2='" + key2 + '\'' +
				", key3=" + key3 +
				", key4=" + key4 +
				", key5=" + Arrays.toString(key5) +
				", vStr1='" + vStr1 + '\'' +
				", VStr1='" + VStr1 + '\'' +
				", obj_id1='" + obj_id1 + '\'' +
				", objId1='" + objId1 + '\'' +
				'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			CopyObj copyObj = (CopyObj) o;
			return Objects.equals(key1, copyObj.key1) && Objects.equals(key2, copyObj.key2) && Objects.equals(key3, copyObj.key3) && Objects.equals(key4, copyObj.key4) && Arrays.equals(key5, copyObj.key5) && Objects.equals(vStr1, copyObj.vStr1) && Objects.equals(VStr1, copyObj.VStr1) && Objects.equals(obj_id1, copyObj.obj_id1) && Objects.equals(objId1, copyObj.objId1);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(key1, key2, key3, key4, vStr1, VStr1, obj_id1, objId1);
			result = 31 * result + Arrays.hashCode(key5);
			return result;
		}
	}
}

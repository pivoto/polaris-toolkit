package io.polaris.core.asm.reflect;

import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.io.Consoles;
import io.polaris.core.string.StringCases;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccessTest extends BaseAsmTest {

	@Test
	void testClassAccess(){
		AccessBean01 o = AccessBean01.newRandom();
		ClassAccess<AccessBean01> access = ClassAccess.get(AccessBean01.class);
		access.invokeMethod(o,"set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		Object[] args1 = new Object[]{access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get: {}", args1);
		Object[] args = new Object[]{o.toString()};
		Consoles.log("toString: {}", args);
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
	}

	@Test
	void testMethodAccess() {
		AccessBean01 o = AccessBean01.newRandom();
		MethodAccess methodAccess = MethodAccess.get(AccessBean01.class);
		methodAccess.invoke(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		Object[] args1 = new Object[]{methodAccess.invoke(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get: {}", args1);
		Object[] args = new Object[]{o.toString()};
		Consoles.log("toString: {}", args);
		Assertions.assertEquals("strVal0", methodAccess.invoke(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
	}

	@Test
	void testReflectiveAccess() {
		ClassAccess<AccessBean01> access = ClassAccess.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		Object[] args6 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args6);
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		Object[] args5 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args5);
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		Object[] args4 = new Object[]{AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0))};
		Consoles.log("get {}: {}", args4);
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		Object[] args3 = new Object[]{"publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0"))};
		Consoles.log("get {}: {}", args3);
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			Object[] args = new Object[]{AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0)};
			Consoles.log("get {}: {}", args);
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		Object[] args2 = new Object[]{AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0)};
		Consoles.log("get {}: {}", args2);
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		Object[] args1 = new Object[]{"publicStaticIntVal0", access.getField(o, "publicStaticIntVal0")};
		Consoles.log("get {}: {}", args1);
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		Object[] args = new Object[]{o.toString()};
		Consoles.log("toString: {}", args);
	}

	@Test
	void testReflectiveAccessV1() {
		ClassAccessV1<AccessBean01> access = ClassAccessV1.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		Object[] args6 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args6);
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		Object[] args5 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args5);
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		Object[] args4 = new Object[]{AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0))};
		Consoles.log("get {}: {}", args4);
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		Object[] args3 = new Object[]{"publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0"))};
		Consoles.log("get {}: {}", args3);
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			Object[] args = new Object[]{AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0)};
			Consoles.log("get {}: {}", args);
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		Object[] args2 = new Object[]{AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0)};
		Consoles.log("get {}: {}", args2);
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		Object[] args1 = new Object[]{"publicStaticIntVal0", access.getField(o, "publicStaticIntVal0")};
		Consoles.log("get {}: {}", args1);
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		Object[] args = new Object[]{o.toString()};
		Consoles.log("toString: {}", args);

	}

	@Test
	void testReflectiveAccessV2() {
		ClassAccessV2<AccessBean01> access = ClassAccessV2.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		Object[] args6 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args6);
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		Object[] args5 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args5);
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		Object[] args4 = new Object[]{AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0))};
		Consoles.log("get {}: {}", args4);
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		Object[] args3 = new Object[]{"publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0"))};
		Consoles.log("get {}: {}", args3);
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			Object[] args = new Object[]{AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0)};
			Consoles.log("get {}: {}", args);
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		Object[] args2 = new Object[]{AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0)};
		Consoles.log("get {}: {}", args2);
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		Object[] args1 = new Object[]{"publicStaticIntVal0", access.getField(o, "publicStaticIntVal0")};
		Consoles.log("get {}: {}", args1);
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		Object[] args = new Object[]{o.toString()};
		Consoles.log("toString: {}", args);
	}

	@Test
	void testReflectiveAccessV3() {
		ClassAccessV3<AccessBean01> access = ClassAccessV3.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		Object[] args6 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args6);
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		Object[] args5 = new Object[]{AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0))};
		Consoles.log("get {}: {}", args5);
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		Object[] args4 = new Object[]{AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0))};
		Consoles.log("get {}: {}", args4);
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		Object[] args3 = new Object[]{"publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0"))};
		Consoles.log("get {}: {}", args3);
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			Object[] args = new Object[]{AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0)};
			Consoles.log("get {}: {}", args);
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		Object[] args2 = new Object[]{AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0)};
		Consoles.log("get {}: {}", args2);
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		Object[] args1 = new Object[]{"publicStaticIntVal0", access.getField(o, "publicStaticIntVal0")};
		Consoles.log("get {}: {}", args1);
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		Object[] args = new Object[]{o.toString()};
		Consoles.log("toString: {}", args);
	}

	@Test
	void test01() {
		ConstructorAccess<StaticInner> access = ConstructorAccess.get(StaticInner.class);
		StaticInner o = access.newInstance();

		FieldAccess fieldAccess = FieldAccess.get(StaticInner.class);
		fieldAccess.set(o, "id", "test");
		Assertions.assertEquals("test", fieldAccess.get(o, "id"));

		MethodAccess methodAccess = MethodAccess.get(StaticInner.class);
		methodAccess.invoke(o, "setId", "test2");
		Assertions.assertEquals("test2", fieldAccess.get(o, "id"));
	}

	@Test
	void test02() {
		ConstructorAccess<MemberInner> access = ConstructorAccess.get(MemberInner.class);
		MemberInner o = access.newInstance(this);
		Assertions.assertInstanceOf(MemberInner.class, o);
	}

	@Data
	static class StaticInner {
		String id;
	}

	@Data
	class MemberInner {
	}
}

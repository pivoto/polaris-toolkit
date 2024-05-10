package io.polaris.core.asm.reflect;

import io.polaris.core.TestConsole;
import io.polaris.core.consts.SystemKeys;
import io.polaris.core.string.StringCases;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccessTest {
	static {
		System.setProperty(SystemKeys.JAVA_CLASS_BYTES_TMPDIR, "/data/classes");
	}

	@Test
	void testClassAccess(){
		AccessBean01 o = AccessBean01.newRandom();
		ClassAccess<AccessBean01> access = ClassAccess.get(AccessBean01.class);
		access.invokeMethod(o,"set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		TestConsole.printx("get: {}", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		TestConsole.printx("toString: {}", o.toString());
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
	}

	@Test
	void testMethodAccess() {
		AccessBean01 o = AccessBean01.newRandom();
		MethodAccess methodAccess = MethodAccess.get(AccessBean01.class);
		methodAccess.invoke(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		TestConsole.printx("get: {}", methodAccess.invoke(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		TestConsole.printx("toString: {}", o.toString());
		Assertions.assertEquals("strVal0", methodAccess.invoke(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
	}

	@Test
	void testReflectiveAccess() {
		ClassAccess<AccessBean01> access = ClassAccess.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		TestConsole.printx("get {}: {}", "publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			TestConsole.printx("get {}: {}", AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0));
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0));
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		TestConsole.printx("get {}: {}", "publicStaticIntVal0", access.getField(o, "publicStaticIntVal0"));
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		TestConsole.printx("toString: {}", o.toString());
	}

	@Test
	void testReflectiveAccessV1() {
		ClassAccessV1<AccessBean01> access = ClassAccessV1.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		TestConsole.printx("get {}: {}", "publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			TestConsole.printx("get {}: {}", AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0));
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0));
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		TestConsole.printx("get {}: {}", "publicStaticIntVal0", access.getField(o, "publicStaticIntVal0"));
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		TestConsole.printx("toString: {}", o.toString());

	}

	@Test
	void testReflectiveAccessV2() {
		ClassAccessV2<AccessBean01> access = ClassAccessV2.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		TestConsole.printx("get {}: {}", "publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			TestConsole.printx("get {}: {}", AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0));
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0));
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		TestConsole.printx("get {}: {}", "publicStaticIntVal0", access.getField(o, "publicStaticIntVal0"));
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		TestConsole.printx("toString: {}", o.toString());
	}

	@Test
	void testReflectiveAccessV3() {
		ClassAccessV3<AccessBean01> access = ClassAccessV3.get(AccessBean01.class);

		//AccessBean01 o = AccessBean01.newRandom();
		AccessBean01 o = access.newInstance("test");

		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("test", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.strVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));
		Assertions.assertEquals("strVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0), "publicStrVal0");
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicStrVal0, access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));
		Assertions.assertEquals("publicStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.publicStrVal0)));

		access.invokeMethod(o, "set" + StringCases.capitalize("publicStaticStrVal0"), "publicStaticStrVal0");
		TestConsole.printx("get {}: {}", "publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));
		Assertions.assertEquals("publicStaticStrVal0", access.invokeMethod(o, "get" + StringCases.capitalize("publicStaticStrVal0")));

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			access.setField(o, AccessBean01.Fields.intVal0, 123);
			TestConsole.printx("get {}: {}", AccessBean01.Fields.intVal0, access.getField(o, AccessBean01.Fields.intVal0));
			Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.intVal0));
		});

		access.setField(o, AccessBean01.Fields.publicIntVal0, 123);
		TestConsole.printx("get {}: {}", AccessBean01.Fields.publicIntVal0, access.getField(o, AccessBean01.Fields.publicIntVal0));
		Assertions.assertEquals(123, access.getField(o, AccessBean01.Fields.publicIntVal0));

		access.setField(o, "publicStaticIntVal0", 123);
		TestConsole.printx("get {}: {}", "publicStaticIntVal0", access.getField(o, "publicStaticIntVal0"));
		Assertions.assertEquals(123, access.getField(o, "publicStaticIntVal0"));

		TestConsole.printx("toString: {}", o.toString());
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

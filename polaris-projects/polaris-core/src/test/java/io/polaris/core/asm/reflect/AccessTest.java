package io.polaris.core.asm.reflect;

import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccessTest {


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

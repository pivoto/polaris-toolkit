package io.polaris.core.asm.reflect;

import lombok.Data;
import org.junit.jupiter.api.Test;

@Data
class AccessTest {


	@Test
	void test01() {
		ConstructorAccess<StaticInner> access = ConstructorAccess.get(StaticInner.class);
		StaticInner o = access.newInstance();

		FieldAccess fieldAccess = FieldAccess.get(StaticInner.class);
		fieldAccess.set(o, "id", "test");
		System.out.println(fieldAccess.get(o, "id"));
		System.out.println(o);

		MethodAccess methodAccess = MethodAccess.get(StaticInner.class);
		methodAccess.invoke(o, "setId", "test2");
		System.out.println(o);
	}

	@Test
	void test02() {
		ConstructorAccess<MemberInner> access = ConstructorAccess.get(MemberInner.class);
		MemberInner o = access.newInstance(this);
		System.out.println(o);
	}

	@Data
	static class StaticInner {

		String id;


	}

	@Data
	class MemberInner {

	}
}

package io.polaris.core.reflect;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClassHandleTest {

	@Test
	void test01() throws Throwable {
		ClassHandle<MethodsTestCtx.Target> classHandle = ClassHandles.get(MethodsTestCtx.Target.class);

		MethodsTestCtx.Target target = classHandle.newInstance();
		MethodsTestCtx.Target target1 = classHandle.newPrivateInstance(new Class<?>[]{int.class}, new Object[]{1});
		Consoles.log(target);
		Consoles.log(target1);
		Consoles.log(target.equals(target1));

		Assertions.assertEquals(target, target1, "target != target1");

		Consoles.log("invoke publicMethod()I", classHandle.invoke(target, "publicMethod", int.class));
		Consoles.log(target);

		Consoles.log("invoke publicMethod(I)I", classHandle.invoke(target, "publicMethod", int.class, new Class<?>[]{int.class}, new Object[]{2}));
		Consoles.log(target);

		Consoles.log("invoke privateMethod()I", classHandle.invokePrivate(target, "privateMethod", int.class));
		Consoles.log(target);

		Consoles.log("invoke privateMethod(I)I", classHandle.invokePrivate(target, "privateMethod", int.class, new Class<?>[]{int.class}, new Object[]{2}));
		Consoles.log(target);


		Consoles.log("invoke publicStaticMethod()I", classHandle.invokeStatic("publicStaticMethod", int.class));
		Consoles.log(target);

		Consoles.log("invoke publicMethod(I)I", classHandle.invokeStatic("publicStaticMethod", int.class, new Class<?>[]{int.class}, new Object[]{2}));
		Consoles.log(target);

		Consoles.log("invoke privateMethod()I", classHandle.invokePrivateStatic("privateStaticMethod", int.class));
		Consoles.log(target);

		Consoles.log("invoke privateMethod(I)I", classHandle.invokePrivateStatic("privateStaticMethod", int.class, new Class<?>[]{int.class}, new Object[]{2}));
		Consoles.log(target);

		classHandle.setField(target, "publicField", int.class, 3);
		Consoles.log("get publicField:I", classHandle.getField(target, "publicField", int.class));
		Consoles.log(target);

		classHandle.setPrivateField(target, "privateField", int.class, 3);
		Consoles.log("get privateField:I", classHandle.getPrivateField(target, "privateField", int.class));
		Consoles.log(target);

		classHandle.setStaticField("publicStaticField", int.class, 3);
		Consoles.log("get publicStaticField:I", classHandle.getStaticField("publicStaticField", int.class));
		Consoles.log(target);

		classHandle.setPrivateStaticField("privateStaticField", int.class, 3);
		Consoles.log("get privateStaticField:I", classHandle.getPrivateStaticField("privateStaticField", int.class));
		Consoles.log(target);

	}
}

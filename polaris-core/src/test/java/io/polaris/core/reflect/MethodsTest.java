package io.polaris.core.reflect;

import io.polaris.core.io.Consoles;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Returnee;
import org.junit.jupiter.api.Test;

public class MethodsTest {

	@Test
	void test01() throws Throwable {
		Object[] objects = new Object[]{
			Methods.newInstance(MethodsTestCtx.Target.class),
			Methods.newPrivateInstance(MethodsTestCtx.Target.class, new Class[]{int.class}, new Object[]{2}),
		};
		Consoles.println(Strings.repeat('=', 80));
		for (Object obj : objects) {
			Returnee.of(() -> {
				Methods.setPrivateStaticField(obj.getClass(), "privateStaticField", int.class, 2);
			});
			Object[] args11 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.getPrivateStaticField(obj.getClass(), "privateStaticField", int.class))};
			Consoles.log("{}.privateStaticField:int => {}", args11);
			Returnee.of(() -> Methods.setPrivateField(obj, "privateField", int.class, 2));
			Object[] args10 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.getPrivateField(obj, "privateField", int.class))};
			Consoles.log("{}.privateField:int => {}", args10);

			Returnee.of(() -> Methods.setStaticField(obj.getClass(), "publicStaticField", int.class, 2));
			Object[] args9 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.getStaticField(obj.getClass(), "publicStaticField", int.class))};
			Consoles.log("{}.publicStaticField:int => {}", args9);
			Returnee.of(() -> Methods.setField(obj, "publicField", int.class, 2));
			Object[] args8 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.getField(obj, "publicField", int.class))};
			Consoles.log("{}.publicField:int => {}", args8);

			Object[] args7 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invokeStatic(obj.getClass(), "publicStaticMethod", int.class))};
			Consoles.log("{}.publicStaticMethod()int => {}", args7);
			Object[] args6 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invokeStatic(obj.getClass(), "publicStaticMethod", int.class, new Class[]{int.class}, new Object[]{2}))};
			Consoles.log("{}.publicStaticMethod(int)int => {}", args6);
			Object[] args5 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invokePrivateStatic(obj.getClass(), "privateStaticMethod", int.class))};
			Consoles.log("{}.privateStaticMethod()int => {}", args5);
			Object[] args4 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invokePrivateStatic(obj.getClass(), "privateStaticMethod", int.class, new Class[]{int.class}, new Object[]{2}))};
			Consoles.log("{}.privateStaticMethod(int)int => {}", args4);

			Object[] args3 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invoke(obj, "publicMethod", int.class))};
			Consoles.log("{}.publicMethod()int => {}", args3);
			Object[] args2 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invoke(obj, "publicMethod", int.class, new Class[]{int.class}, new Object[]{2}))};
			Consoles.log("{}.publicMethod(int)int => {}", args2);
			Object[] args1 = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invokePrivate(obj, "privateMethod", int.class))};
			Consoles.log("{}.privateMethod()int => {}", args1);
			Object[] args = new Object[]{obj.getClass().getSimpleName(), Returnee.of(() -> Methods.invokePrivate(obj, "privateMethod", int.class, new Class[]{int.class}, new Object[]{2}))};
			Consoles.log("{}.privateMethod(int)int => {}", args);

			Consoles.println(Strings.repeat('=', 80));
		}

	}

}

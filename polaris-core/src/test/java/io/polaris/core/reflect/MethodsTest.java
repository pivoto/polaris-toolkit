package io.polaris.core.reflect;

import io.polaris.core.io.Consoles;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

public class MethodsTest {

	@Test
	void test01() throws Throwable {
		Object[] objects = new Object[]{
//			new PublicStaticTarget(),
			Methods.newInstance(PublicStaticTarget.class),
//			new StaticTarget(),
			Methods.newInstance(StaticTarget.class),
//			new Target(),
			Methods.newInstance(Target.class),
		};
		String msg1 = Strings.repeat('=', 80);
		Consoles.println(msg1);
		for (Object obj : objects) {
			Methods.setPrivateStaticField(obj.getClass(), "privateStaticField", int.class, 2);
			Object[] args11 = new Object[]{obj.getClass().getSimpleName(), Methods.getPrivateStaticField(obj.getClass(), "privateStaticField", int.class)};
			Consoles.log("{}.privateStaticField:int => {}", args11);
			Methods.setPrivateField(obj, "privateField", int.class, 2);
			Object[] args10 = new Object[]{obj.getClass().getSimpleName(), Methods.getPrivateField(obj, "privateField", int.class)};
			Consoles.log("{}.privateField:int => {}", args10);

			Methods.setStaticField(obj.getClass(), "publicStaticField", int.class, 2);
			Object[] args9 = new Object[]{obj.getClass().getSimpleName(), Methods.getStaticField(obj.getClass(), "publicStaticField", int.class)};
			Consoles.log("{}.publicStaticField:int => {}", args9);
			Methods.setField(obj, "publicField", int.class, 2);
			Object[] args8 = new Object[]{obj.getClass().getSimpleName(), Methods.getField(obj, "publicField", int.class)};
			Consoles.log("{}.publicField:int => {}", args8);

			Object[] args7 = new Object[]{obj.getClass().getSimpleName(), Methods.invokeStatic(obj.getClass(), "publicStaticMethod", int.class)};
			Consoles.log("{}.publicStaticMethod()int => {}", args7);
			Object[] args6 = new Object[]{obj.getClass().getSimpleName(), Methods.invokeStatic(obj.getClass(), "publicStaticMethod", int.class, new Class[]{int.class}, new Object[]{2})};
			Consoles.log("{}.publicStaticMethod(int)int => {}", args6);
			Object[] args5 = new Object[]{obj.getClass().getSimpleName(), Methods.invokePrivateStatic(obj.getClass(), "privateStaticMethod", int.class)};
			Consoles.log("{}.privateStaticMethod()int => {}", args5);
			Object[] args4 = new Object[]{obj.getClass().getSimpleName(), Methods.invokePrivateStatic(obj.getClass(), "privateStaticMethod", int.class, new Class[]{int.class}, new Object[]{2})};
			Consoles.log("{}.privateStaticMethod(int)int => {}", args4);

			Object[] args3 = new Object[]{obj.getClass().getSimpleName(), Methods.invoke(obj, "publicMethod", int.class)};
			Consoles.log("{}.publicMethod()int => {}", args3);
			Object[] args2 = new Object[]{obj.getClass().getSimpleName(), Methods.invoke(obj, "publicMethod", int.class, new Class[]{int.class}, new Object[]{2})};
			Consoles.log("{}.publicMethod(int)int => {}", args2);
			Object[] args1 = new Object[]{obj.getClass().getSimpleName(), Methods.invokePrivate(obj, "privateMethod", int.class)};
			Consoles.log("{}.privateMethod()int => {}", args1);
			Object[] args = new Object[]{obj.getClass().getSimpleName(), Methods.invokePrivate(obj, "privateMethod", int.class, new Class[]{int.class}, new Object[]{2})};
			Consoles.log("{}.privateMethod(int)int => {}", args);

			String msg = Strings.repeat('=', 80);
			Consoles.println(msg);
		}

	}

	public static class PublicStaticTarget {
		public static int publicStaticField = 1;
		public int publicField = 1;
		private static int privateStaticField = 1;
		private int privateField = 1;

		public static int publicStaticMethod() {
			return 1;
		}

		public static int publicStaticMethod(int arg) {
			return arg;
		}

		private static int privateStaticMethod() {
			return 1;
		}

		private static int privateStaticMethod(int arg) {
			return arg;
		}

		public int publicMethod() {
			return 1;
		}

		public int publicMethod(int arg) {
			return arg;
		}

		private int privateMethod() {
			return 1;
		}

		private int privateMethod(int arg) {
			return arg;
		}


	}

	public static class StaticTarget {
		public static int publicStaticField = 1;
		public int publicField = 1;
		private static int privateStaticField = 1;
		private int privateField = 1;

		public static int publicStaticMethod() {
			return 1;
		}

		public static int publicStaticMethod(int arg) {
			return arg;
		}

		private static int privateStaticMethod() {
			return 1;
		}

		private static int privateStaticMethod(int arg) {
			return arg;
		}

		public int publicMethod() {
			return 1;
		}

		public int publicMethod(int arg) {
			return arg;
		}

		private int privateMethod() {
			return 1;
		}

		private int privateMethod(int arg) {
			return arg;
		}

	}

	public static class Target {
		public static int publicStaticField = 1;
		public int publicField = 1;
		private static int privateStaticField = 1;
		private int privateField = 1;

		public static int publicStaticMethod() {
			return 1;
		}

		public static int publicStaticMethod(int arg) {
			return arg;
		}

		private static int privateStaticMethod() {
			return 1;
		}

		private static int privateStaticMethod(int arg) {
			return arg;
		}

		public int publicMethod() {
			return 1;
		}

		public int publicMethod(int arg) {
			return arg;
		}

		private int privateMethod() {
			return 1;
		}

		private int privateMethod(int arg) {
			return arg;
		}

	}
}

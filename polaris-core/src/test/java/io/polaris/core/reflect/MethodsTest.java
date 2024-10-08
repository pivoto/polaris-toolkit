package io.polaris.core.reflect;

import io.polaris.core.TestConsole;
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
		TestConsole.println(Strings.repeat('=', 80));
		for (Object obj : objects) {
			Methods.setPrivateStaticField(obj.getClass(), "privateStaticField", int.class, 2);
			TestConsole.printx("{}.privateStaticField:int => {}", obj.getClass().getSimpleName(),
				Methods.getPrivateStaticField(obj.getClass(), "privateStaticField", int.class));
			Methods.setPrivateField(obj, "privateField", int.class, 2);
			TestConsole.printx("{}.privateField:int => {}", obj.getClass().getSimpleName(),
				Methods.getPrivateField(obj, "privateField", int.class));

			Methods.setStaticField(obj.getClass(), "publicStaticField", int.class, 2);
			TestConsole.printx("{}.publicStaticField:int => {}", obj.getClass().getSimpleName(),
				Methods.getStaticField(obj.getClass(), "publicStaticField", int.class));
			Methods.setField(obj, "publicField", int.class, 2);
			TestConsole.printx("{}.publicField:int => {}", obj.getClass().getSimpleName(),
				Methods.getField(obj, "publicField", int.class));

			TestConsole.printx("{}.publicStaticMethod()int => {}", obj.getClass().getSimpleName(),
				Methods.invokeStatic(obj.getClass(), "publicStaticMethod", int.class));
			TestConsole.printx("{}.publicStaticMethod(int)int => {}", obj.getClass().getSimpleName(),
				Methods.invokeStatic(obj.getClass(), "publicStaticMethod", int.class, new Class[]{int.class}, new Object[]{2}));
			TestConsole.printx("{}.privateStaticMethod()int => {}", obj.getClass().getSimpleName(),
				Methods.invokePrivateStatic(obj.getClass(), "privateStaticMethod", int.class));
			TestConsole.printx("{}.privateStaticMethod(int)int => {}", obj.getClass().getSimpleName(),
				Methods.invokePrivateStatic(obj.getClass(), "privateStaticMethod", int.class, new Class[]{int.class}, new Object[]{2}));

			TestConsole.printx("{}.publicMethod()int => {}", obj.getClass().getSimpleName(),
				Methods.invoke(obj, "publicMethod", int.class));
			TestConsole.printx("{}.publicMethod(int)int => {}", obj.getClass().getSimpleName(),
				Methods.invoke(obj, "publicMethod", int.class, new Class[]{int.class}, new Object[]{2}));
			TestConsole.printx("{}.privateMethod()int => {}", obj.getClass().getSimpleName(),
				Methods.invokePrivate(obj, "privateMethod", int.class));
			TestConsole.printx("{}.privateMethod(int)int => {}", obj.getClass().getSimpleName(),
				Methods.invokePrivate(obj, "privateMethod", int.class, new Class[]{int.class}, new Object[]{2}));

			TestConsole.println(Strings.repeat('=', 80));
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

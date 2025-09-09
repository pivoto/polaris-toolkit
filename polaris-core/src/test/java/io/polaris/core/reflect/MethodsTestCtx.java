package io.polaris.core.reflect;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since Sep 09, 2025
 */
public class MethodsTestCtx {

	@ToString
	@EqualsAndHashCode
	public static class Target {
		@ToString.Include
		public static int publicStaticField = 1;
		@ToString.Include
		private static int privateStaticField = 1;
		public int publicField = 1;
		private int privateField = 1;

		public Target() {
		}

		private Target(int v) {
			publicStaticField = v;
			privateStaticField = v;
			publicField = v;
			privateField = v;
		}



		public static int publicStaticMethod() {
			return publicStaticField;
		}

		public static int publicStaticMethod(int arg) {
			return publicStaticField = arg;
		}

		private static int privateStaticMethod() {
			return privateStaticField;
		}

		private static int privateStaticMethod(int arg) {
			return privateStaticField = arg;
		}

		public int publicMethod() {
			return publicField;
		}

		public int publicMethod(int arg) {
			return publicField = arg;
		}

		private int privateMethod() {
			return privateField;
		}

		private int privateMethod(int arg) {
			return privateField = arg;
		}

	}
}

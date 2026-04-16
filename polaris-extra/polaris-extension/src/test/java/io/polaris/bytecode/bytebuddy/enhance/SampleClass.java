package io.polaris.bytecode.bytebuddy.enhance;

/**
 * @author Qt
 * @since Apr 15, 2026
 */
public class SampleClass {


	public static void testStaticMethod() {
		System.out.println("test static method");
	}


	public String testMethod(String arg0, int arg1, String arg2) {
		System.out.println("test method, args: " + arg0 + ", " + arg1 + ", " + arg2);
		return "test method result";
	}

}

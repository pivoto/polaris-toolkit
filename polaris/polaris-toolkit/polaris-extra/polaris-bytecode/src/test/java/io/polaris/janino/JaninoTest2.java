package io.polaris.janino;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.ICompiler;
import org.codehaus.commons.compiler.Sandbox;
import org.codehaus.commons.compiler.util.ResourceFinderClassLoader;
import org.codehaus.commons.compiler.util.resource.MapResourceCreator;
import org.codehaus.commons.compiler.util.resource.MapResourceFinder;
import org.codehaus.commons.compiler.util.resource.StringResource;
import org.codehaus.janino.CompilerFactory;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.ScriptEvaluator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

/**
 * @author Qt
 * @since 1.8
 */
public class JaninoTest2 {

	@Test
	void testExpression() throws CompileException, InvocationTargetException {
		// Now here's where the story begins...
		ExpressionEvaluator ee = new ExpressionEvaluator();

		// The expression will have two "int" parameters: "a" and "b".
		ee.setParameters(new String[]{"a", "b"}, new Class[]{int.class, int.class});

		// And the expression (i.e. "result") type is also "int".
		ee.setExpressionType(int.class);

		// And now we "cook" (scan, parse, compile and load) the fabulous expression.
		ee.cook("a + b");

		// Eventually we evaluate the expression - and that goes super-fast.
		int result = (Integer) ee.evaluate(new Object[]{19, 23});
		System.out.println(result);
	}

	@Test
	void testScript() throws CompileException, InvocationTargetException {
		ScriptEvaluator se = new ScriptEvaluator();

		se.cook(
			""
				+ "static  void method1(Object t) {\n"
				+ "    System.out.println(t);\n"
				+ "}\n"
				+ "\n"
				+ "method1(1);\n"
				+ "method2();\n"
				+ "method1(\"test...\");\n"
				+ "\n"
				+ "static void method2() {\n"
				+ "    System.out.println(2);\n"
				+ "}\n"
		);

		se.evaluate();
	}

	@Test
	void testCompiler() throws Exception {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//		ICompilerFactory factory = CompilerFactoryFactory.getDefaultCompilerFactory(contextClassLoader);
		CompilerFactory factory = new CompilerFactory();
		ICompiler compiler = factory.newCompiler();

		Map<String, byte[]> classes = new HashMap<String, byte[]>();
		compiler.setClassFileCreator(new MapResourceCreator(classes));

		compiler.compile(new StringResource[]{
			new StringResource("JaninoCompilerTest.java", "" +
				"package io.polaris.janino;" +
				"public class JaninoCompilerTest{" +
				"" +
				"}")
		});

		ClassLoader cl = new ResourceFinderClassLoader(
			new MapResourceFinder(classes),    // resourceFinder
			ClassLoader.getSystemClassLoader() // parent
		);

		Class<?> c = cl.loadClass("io.polaris.janino.JaninoCompilerTest");
		System.out.println(c);

	}

	@Test
	void testSandbox() throws CompileException {
		// Create a JANINO script evaluator. The example, however, will work as fine with
		// ExpressionEvaluators, ClassBodyEvaluators and SimpleCompilers.
		ScriptEvaluator se = new ScriptEvaluator();
		se.setDebuggingInformation(true, true, false);

		// Now create a "Permissions" object which allows to read the system variable
		// "foo", and forbids everything else.
		Permissions permissions = new Permissions();
		permissions.add(new PropertyPermission("foo", "read"));

		// Compile a simple script which reads two system variables - "foo" and "bar".
		PrivilegedAction<?> pa = se.createFastEvaluator((
			"System.getProperty(\"foo\");\n" +
				"System.getProperty(\"bar\");\n" +
				"return null;\n"
		), PrivilegedAction.class, new String[0]);

		// Finally execute the script in the sandbox. Getting system property "foo" will
		// succeed, and getting "bar" will throw a
		//    java.security.AccessControlException: access denied (java.util.PropertyPermission bar read)
		// in line 2 of the script. Et voila!
		Sandbox sandbox = new Sandbox(permissions);
		sandbox.confine(pa);
	}
}

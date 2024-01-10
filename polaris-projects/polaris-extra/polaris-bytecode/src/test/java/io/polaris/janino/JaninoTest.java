package io.polaris.janino;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.codehaus.commons.compiler.samples.ClassBodyDemo;
import org.codehaus.commons.compiler.samples.ExpressionDemo;
import org.codehaus.commons.compiler.samples.ScriptDemo;
import org.codehaus.commons.compiler.samples.ShippingCost;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.SimpleCompiler;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Qt
 * @since 1.8
 */
public class JaninoTest {

	@Test
	void test01() throws CompileException, InvocationTargetException {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		ee.cook("3 + 4");
		System.out.println(ee.evaluate());
	}

	@Test
	void test02() throws Exception {
		// Convert command line argument to call argument "total".
		Object[] arguments = {new Double(11)};

		// Create "ExpressionEvaluator" object.
		IExpressionEvaluator ee = (
			CompilerFactoryFactory
				.getDefaultCompilerFactory(ShippingCost.class.getClassLoader())
				.newExpressionEvaluator()
		);
		ee.setDefaultImports();
		ee.setExpressionType(double.class);
		ee.setParameters(new String[]{"total"}, new Class[]{double.class});
		ee.cook("total >= 100.0 ? 0.0 : 7.95");

		// Evaluate expression with actual parameter values.
		Object res = ee.evaluate(arguments);

		// Print expression result.
		System.out.println("Result = " + String.valueOf(res));
	}

	@Test
	void test03() throws Exception {
		ShippingCost.main(new String[]{"123"});
	}

	@Test
	void test04() throws Exception {
		//ExpressionDemo.main(new String[]{"-help"});
		ExpressionDemo.main(new String[]{"3 + 2 * 8"});
	}

	@Test
	void test05() throws Exception {
		ScriptDemo.main(new String[]{"int i = 1; System.out.println(i);"});
	}

	@Test
	void test06() throws Exception {
		ClassBodyDemo.main(new String[]{"public static void\n" +
			"main(String[] args) { System.out.println(\"test...\");}"});
	}

	@Test
	void test07() {
		ILogger log = ILoggers.of(getClass());
		log.trace("test....");
		log.debug("test....");
		log.info("test....");
		log.warn("test....");
		log.error("test....");
	}

	@Test
	void test0() throws Exception {
		SimpleCompiler.main(new String[]{"" +
			"public\n" +
			"class Foo {\n" +
			" \n" +
			"    public static void\n" +
			"    main(String[] args) {\n" +
			"        new Bar().meth();\n" +
			"    }\n" +
			"}\n" +
			" \n" +
			"public\n" +
			"class Bar {\n" +
			" \n" +
			"    public void\n" +
			"    meth() {\n" +
			"        System.out.println(\"HELLO!\");\n" +
			"    }\n" +
			"}" +
			""});
	}
}

package io.polaris.example.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.script.VelocityScriptEngine;
import org.apache.velocity.script.VelocityScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Qt
 * @version Jun 03, 2019
 */
public class Example_0 {


	public void test0() {
		Velocity.init();

		VelocityContext context = new VelocityContext();

		context.put("name", new String("Velocity"));

		Template template = null;

		try {
			template = Velocity.getTemplate("mytemplate.vm");
		} catch (ResourceNotFoundException rnfe) {
			// couldn't find the template
		} catch (ParseErrorException pee) {
			// syntax error: problem parsing the template
		} catch (MethodInvocationException mie) {
			// something invoked in the template
			// threw an exception
		} catch (Exception e) {
		}

		StringWriter sw = new StringWriter();

		template.merge(context, sw);
	}

	public void test1() {
		/*
		 *  Configure the engine
		 */

		Velocity.setProperty(
			Velocity.RUNTIME_LOG_NAME, "mylog");

		/*
		 *  now initialize the engine
		 */

		Velocity.init();

		// ...

		Template t = Velocity.getTemplate("foo.vm");
	}

	public void test2() {
		/*
		 *  create a new instance of the engine
		 */

		VelocityEngine ve = new VelocityEngine();

		/*
		 *  configure the engine.  In this case, we are using
		 *  a specific logger name
		 */

		ve.setProperty(
			VelocityEngine.RUNTIME_LOG_NAME, "mylog");

		/*
		 *  initialize the engine
		 */

		ve.init();

		//...

		Template t = ve.getTemplate("foo.vm");
	}
	public void test3() throws ScriptException {
		// get script manager, create a new Velocity script engine factory and get an engine from it
		ScriptEngineManager manager = new ScriptEngineManager();
		manager.registerEngineName("velocity", new VelocityScriptEngineFactory());
		ScriptEngine engine = manager.getEngineByName("velocity");

		System.setProperty(VelocityScriptEngine.VELOCITY_PROPERTIES_KEY, "path/to/velocity.properties");
		String script = "Hello $world";
		Writer writer = new StringWriter();
		engine.getContext().setWriter(writer);
		Object result = engine.eval(script);
		System.out.println(writer);
	}
}

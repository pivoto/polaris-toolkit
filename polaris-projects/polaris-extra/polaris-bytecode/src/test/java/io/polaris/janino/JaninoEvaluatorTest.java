package io.polaris.janino;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.script.Evaluator;
import io.polaris.core.script.ScriptEvaluators;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class JaninoEvaluatorTest {

	public static void main(String[] args) throws Exception {
		Evaluator evaluator = ScriptEvaluators.getEvaluator("Janino");
		Object input = new HashMap<String, Object>();
		Object output = new HashMap<String, Object>();
		Map<String, Object> bindings = new HashMap<String, Object>();
//		Object rs = evaluator.eval("System.out.println(\"hello world\"); return 1+1", input, output, bindings);
		Object rs = evaluator.eval(" 1+1", input, output, bindings);
		System.out.println(rs);
	}
}

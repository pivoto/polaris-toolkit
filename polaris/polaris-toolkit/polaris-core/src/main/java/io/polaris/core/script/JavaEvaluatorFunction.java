package io.polaris.core.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class JavaEvaluatorFunction {
	protected static final Logger log = LoggerFactory.getLogger(JavaEvaluatorFunction.class);

	public JavaEvaluatorFunction() {
	}

	public void eval(Object input, Object output, Map<String, Object> bindings) {
		doEval(input, output, bindings);
	}

	protected void doEval(Object input, Object output, Map<String, Object> bindings) {
	}

}

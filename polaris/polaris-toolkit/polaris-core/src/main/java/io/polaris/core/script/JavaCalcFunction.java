package io.polaris.core.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class JavaCalcFunction {
	protected static final Logger log = LoggerFactory.getLogger(JavaCalcFunction.class);

	public JavaCalcFunction() {
	}

	public void eval(Object input, Object output, Map<String, Object> bindings) {
		calc(input, output, bindings);
	}

	protected void calc(Object input, Object output, Map<String, Object> bindings) {
	}

}

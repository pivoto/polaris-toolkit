package io.polaris.core.script;

import io.polaris.core.service.ServiceOrder;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceOrder(100)
public class JavaScriptScriptEvaluator extends AbstractStandardScriptEvaluator {

	public static final String ENGINE_NAME = "javascript";

	@Override
	protected String getEngineName() {
		return ENGINE_NAME;
	}
}

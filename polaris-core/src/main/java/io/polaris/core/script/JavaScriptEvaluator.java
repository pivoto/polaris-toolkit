package io.polaris.core.script;

import io.polaris.core.service.SpiName;
import io.polaris.core.service.SpiOrder;

/**
 * @author Qt
 */
@SpiOrder(100)
@SpiName(JavaScriptEvaluator.ENGINE_NAME)
public class JavaScriptEvaluator extends AbstractStandardEvaluator {

	public static final String ENGINE_NAME = "javascript";

	@Override
	protected String getEngineName() {
		return ENGINE_NAME;
	}
}

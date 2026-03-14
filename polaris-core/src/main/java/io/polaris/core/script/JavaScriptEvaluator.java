package io.polaris.core.script;

import io.polaris.core.spi.SpiName;
import io.polaris.core.spi.SpiOrder;

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

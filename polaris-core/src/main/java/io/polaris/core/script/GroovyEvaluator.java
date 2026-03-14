package io.polaris.core.script;

import io.polaris.core.service.SpiName;
import io.polaris.core.service.SpiOrder;

/**
 * @author Qt
 */
@SpiOrder(200)
@SpiName(GroovyEvaluator.ENGINE_NAME)
public class GroovyEvaluator extends AbstractStandardEvaluator {
	public static final String ENGINE_NAME = "groovy";

	@Override
	protected String getEngineName() {
		return ENGINE_NAME;
	}

}

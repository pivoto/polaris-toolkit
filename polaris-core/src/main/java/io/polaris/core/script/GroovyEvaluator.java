package io.polaris.core.script;

import io.polaris.core.service.ServiceName;
import io.polaris.core.service.ServiceOrder;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceOrder(200)
@ServiceName(GroovyEvaluator.ENGINE_NAME)
public class GroovyEvaluator extends AbstractStandardEvaluator {
	public static final String ENGINE_NAME = "groovy";

	@Override
	protected String getEngineName() {
		return ENGINE_NAME;
	}

}

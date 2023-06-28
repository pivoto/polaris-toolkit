package io.polaris.core.script;

import io.polaris.core.service.ServiceOrder;

/**
 * @author Qt
 * @since 1.8
 */
@ServiceOrder(200)
public class GroovyCalcEngine extends StandardCalcEngine {
	public static final String ENGINE_NAME = "groovy";

	@Override
	protected String getEngineName() {
		return ENGINE_NAME;
	}

}

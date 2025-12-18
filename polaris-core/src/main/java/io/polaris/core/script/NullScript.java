package io.polaris.core.script;

import java.util.Map;

/**
 * @author Qt
 */
public class NullScript implements Script {
	public static final NullScript INSTANCE = new NullScript();

	@Override
	public Object run(Map<String, Object> binding) {
		return null;
	}
}

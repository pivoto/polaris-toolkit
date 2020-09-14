package io.polaris.core.script;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class NullScript implements IScript {
	public static final NullScript INSTANCE = new NullScript();

	@Override
	public Object run(Map<String, Object> binding) {
		return null;
	}
}

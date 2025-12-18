package io.polaris.core.script;

import java.util.Map;

/**
 * @author Qt
 */
public class ErrorScript implements Script {
	private final Throwable throwable;

	public ErrorScript(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public Object run(Map<String, Object> binding) {
		throw new ScriptEvalException("脚本编译错误", throwable);
	}
}

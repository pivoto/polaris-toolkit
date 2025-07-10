package io.polaris.core.script;

import java.util.Map;

import io.polaris.core.service.ServiceLoadable;

/**
 * @author Qt
 * @since 1.8
 */
public interface Evaluator extends ServiceLoadable {
	String INPUT = "input";
	String OUTPUT = "output";
	String BINDINGS = "bindings";
	String RESULT = "result";

	/**
	 * 执行脚本
	 *
	 * @param scriptContent 脚本内容
	 * @param input         输入参数
	 * @param output        输出参数
	 * @param mergeBindings 其他绑定变量
	 * @return
	 */
	Object eval(String scriptContent, Object input, Object output, Map<String, Object> mergeBindings) throws ScriptEvalException;

	default Object eval(String scriptContent, Object input, Object output) throws ScriptEvalException {
		return eval(scriptContent, input, output, null);
	}

	default Object eval(String scriptContent, Object input) throws ScriptEvalException {
		return eval(scriptContent, input, null, null);
	}

	default Object eval(String scriptContent) throws ScriptEvalException {
		return eval(scriptContent, null, null, null);
	}


}

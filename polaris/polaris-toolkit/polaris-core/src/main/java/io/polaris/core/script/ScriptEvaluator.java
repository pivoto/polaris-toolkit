package io.polaris.core.script;

import io.polaris.core.script.ScriptEvalException;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public interface ScriptEvaluator {
	String INPUT = "input";
	String OUTPUT = "output";
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

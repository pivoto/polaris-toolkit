package io.polaris.core.script;

import io.polaris.core.err.CalcException;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public interface CalcEngine {
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
	Object eval(String scriptContent, Object input, Object output, Map<String, Object> mergeBindings) throws CalcException;

	default Object eval(String scriptContent, Object input, Object output) throws CalcException {
		return eval(scriptContent, input, output, null);
	}

	default Object eval(String scriptContent, Object input) throws CalcException {
		return eval(scriptContent, input, null, null);
	}

	default Object eval(String scriptContent) throws CalcException {
		return eval(scriptContent, null, null, null);
	}


}

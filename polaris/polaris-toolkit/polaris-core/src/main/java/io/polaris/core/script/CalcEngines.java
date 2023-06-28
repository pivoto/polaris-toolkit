package io.polaris.core.script;

import io.polaris.core.err.CalcException;

import java.io.IOException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class CalcEngines {

	private static final Map<String, CalcEngine> engineMap = new ConcurrentHashMap<>();
	private static CalcEngine defaultEngine;

	static {
		ServiceLoader<CalcEngine> loader = ServiceLoader.load(CalcEngine.class);
		for (CalcEngine calcEngine : loader) {
			if (defaultEngine == null) {
				defaultEngine = calcEngine;
				String simpleName = calcEngine.getClass().getSimpleName();
				String engineName = simpleName.replaceFirst(CalcEngine.class.getSimpleName() + "$", "");
				register(engineName, calcEngine);
				register(engineName.toUpperCase(), calcEngine);
				register(engineName.toLowerCase(), calcEngine);
			}
		}
		if (defaultEngine == null) {
			defaultEngine = new JavaScriptCalcEngine();
		}
	}

	public static CalcEngine getCalcEngine(String engineName) {
		return engineMap.get(engineName);
	}

	public static void register(String engineName, CalcEngine calcEngine) {
		engineMap.put(engineName, calcEngine);
	}


	public static Object eval(String content, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws CalcException {
		return defaultEngine.eval(content, input, output, mergeBindings);
	}

	public static Object evalFile(String file, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws IOException, CalcException {
		return defaultEngine.eval(ScriptContent.getContent(file), input, output, mergeBindings);
	}

	public static Object eval(String engineName, String content, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws CalcException {
		CalcEngine calcEngine = engineMap.get(engineName);
		return calcEngine.eval(content, input, output, mergeBindings);
	}

	public static Object evalFile(String engineName, String file, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws IOException, CalcException {
		CalcEngine calcEngine = engineMap.get(engineName);
		return calcEngine.eval(ScriptContent.getContent(file), input, output, mergeBindings);
	}

}

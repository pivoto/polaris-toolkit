package io.polaris.core.script;

import io.polaris.core.io.IO;
import io.polaris.core.script.JavaScriptScriptEvaluator;
import io.polaris.core.script.ScriptEvalException;
import io.polaris.core.script.ScriptEvaluator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class ScriptEvaluators {

	private static final String FILE_PREFIX = "file:";
	private static final String CLASSPATH_PREFIX = "classpath:";
	private static final Map<String, ScriptEvaluator> engineMap = new ConcurrentHashMap<>();
	private static ScriptEvaluator defaultEngine;

	static {
		ServiceLoader<ScriptEvaluator> loader = ServiceLoader.load(ScriptEvaluator.class);
		for (ScriptEvaluator scriptEvaluator : loader) {
			if (defaultEngine == null) {
				defaultEngine = scriptEvaluator;
				String simpleName = scriptEvaluator.getClass().getSimpleName();
				String engineName = simpleName.replaceFirst(ScriptEvaluator.class.getSimpleName() + "$", "");
				register(engineName, scriptEvaluator);
				register(engineName.toUpperCase(), scriptEvaluator);
				register(engineName.toLowerCase(), scriptEvaluator);
			}
		}
		if (defaultEngine == null) {
			defaultEngine = new JavaScriptScriptEvaluator();
		}
	}

	public static ScriptEvaluator getCalcEngine(String engineName) {
		return engineMap.get(engineName);
	}

	public static void register(String engineName, ScriptEvaluator scriptEvaluator) {
		engineMap.put(engineName, scriptEvaluator);
	}


	public static Object eval(String content, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws ScriptEvalException {
		return defaultEngine.eval(content, input, output, mergeBindings);
	}

	public static Object evalFile(String file, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws IOException, ScriptEvalException {
		return defaultEngine.eval(getContent(file), input, output, mergeBindings);
	}

	public static Object eval(String engineName, String content, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws ScriptEvalException {
		ScriptEvaluator scriptEvaluator = engineMap.get(engineName);
		return scriptEvaluator.eval(content, input, output, mergeBindings);
	}

	public static Object evalFile(String engineName, String file, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws IOException, ScriptEvalException {
		ScriptEvaluator scriptEvaluator = engineMap.get(engineName);
		return scriptEvaluator.eval(getContent(file), input, output, mergeBindings);
	}

	private static String getContent(String path) throws IOException {
		try (InputStream in = getInputStream(path)) {
			return IO.toString(in, Charset.defaultCharset());
		}
	}

	private static InputStream getInputStream(String path) throws FileNotFoundException {
		InputStream in = null;
		boolean isClasspath = path.startsWith(CLASSPATH_PREFIX);
		String resource;
		if (isClasspath) {
			resource = path.substring(CLASSPATH_PREFIX.length());
		} else {
			if (path.startsWith(FILE_PREFIX)) {
				resource = path.substring(FILE_PREFIX.length());
			} else {
				resource = path;
			}
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (isClasspath) {
			in = classLoader.getResourceAsStream(resource);
		}
		if (in == null) {
			try {
				in = new FileInputStream(resource);
			} catch (FileNotFoundException e) {
				in = classLoader.getResourceAsStream(resource);
				if (in == null) {
					in = ClassLoader.getSystemResourceAsStream(resource);
				}
				if (in == null) {
					throw new FileNotFoundException(resource);
				}
			}
		}
		return in;
	}
}

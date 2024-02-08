package io.polaris.core.script;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.io.IO;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.service.Service;
import io.polaris.core.service.ServiceLoader;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class ScriptEvaluators {
	private static final ILogger log = ILoggers.of(ScriptEvaluators.class);
	private static final String FILE_PREFIX = "file:";
	private static final String CLASSPATH_PREFIX = "classpath:";
	private static final Map<String, Evaluator> engineMap = new ConcurrentHashMap<>();
	private static Evaluator defaultEngine;

	static {
		ServiceLoader<Evaluator> services = ServiceLoader.of(Evaluator.class);
		for (Service<Evaluator> service : services) {
			try {
				String engineName = service.getServiceName();
				Evaluator evaluator = service.getSingleton();
				if (Strings.isBlank(engineName)) {
					String simpleName = evaluator.getClass().getSimpleName();
					engineName = simpleName.replaceFirst(Evaluator.class.getSimpleName() + "$", "");
				}
				if (!hasEvaluator(engineName)) {
					register(engineName, evaluator);
					register(engineName.toUpperCase(), evaluator);
					register(engineName.toLowerCase(), evaluator);
					if (defaultEngine == null) {
						defaultEngine = evaluator;
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		if (defaultEngine == null) {
			defaultEngine = new JavaScriptEvaluator();
		}
	}

	public static boolean hasEvaluator(String engineName) {
		return engineMap.containsKey(engineName);
	}

	public static Evaluator getEvaluator(String engineName) {
		return engineMap.get(engineName);
	}

	public static void register(String engineName, Evaluator evaluator) {
		engineMap.put(engineName, evaluator);
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
		Evaluator evaluator = engineMap.get(engineName);
		return evaluator.eval(content, input, output, mergeBindings);
	}

	public static Object evalFile(String engineName, String file, Map<String, Object> input, Map<String, Object> output, Map<String, Object> mergeBindings)
		throws IOException, ScriptEvalException {
		Evaluator evaluator = engineMap.get(engineName);
		return evaluator.eval(getContent(file), input, output, mergeBindings);
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

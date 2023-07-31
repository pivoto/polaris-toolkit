package io.polaris.core.script;

import io.polaris.core.cache.ICache;
import io.polaris.core.cache.MapCache;
import io.polaris.core.compiler.MemoryCompiler;
import io.polaris.core.crypto.digest.Digests;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.reflect.SerializableQuaternionConsumer;
import io.polaris.core.service.ServiceDefault;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
@ServiceDefault(Integer.MAX_VALUE)
public class JavaEvaluator implements Evaluator {
	private static final AtomicLong CLASS_NO = new AtomicLong(0);
	private static final Pattern importPattern = Pattern.compile("\\s*\\bimport\\s+(static\\s+)?[\\w\\.\\*]+;\\s*+");

	private ICache<String, JavaEvaluatorFunction> cache = new MapCache<>(0x1000, true);

	static String nextClassName() {
		return JavaEvaluatorFunction.class.getName() + "Impl" + CLASS_NO.incrementAndGet();
	}

	static String asClassContent(String className, String classBody, String inputType, String outputType) {
		StringBuilder sb = new StringBuilder();
		String simpleName = className;
		{
			int i = className.lastIndexOf(".");
			if (i > 0) {
				sb.append("package ").append(className, 0, i).append(";\n");
				simpleName = className.substring(i + 1);
			}
		}
		sb.append("import java.math.*;").append("\n");
		sb.append("import java.util.*;").append("\n");
		sb.append("import java.util.function.*;").append("\n");
		sb.append("import java.sql.*;").append("\n");

		StringBuffer body = new StringBuffer();
		{
			Matcher matcher = importPattern.matcher(classBody);
			while (matcher.find()) {
				sb.append(matcher.group()).append("\n");
				matcher.appendReplacement(body, "");
			}
			matcher.appendTail(body);
			classBody = body.toString();
			body.setLength(0);
		}

		sb.append("public class ").append(simpleName).append(" extends ").append(JavaEvaluatorFunction.class.getName());
		sb.append("{");
		sb.append("\n");

		sb.append("public ").append(simpleName).append("(){");
		sb.append("super();");
		sb.append("\n");
		sb.append("}");
		sb.append("\n");

		// region override
		sb.append("public void ")
			.append(Reflects.getLambdaMethodName(
				(SerializableQuaternionConsumer<JavaEvaluatorFunction, Object, Object, Map<String, Object>>) JavaEvaluatorFunction::doEval))
			.append("(Object _input, Object _output, Map<String, Object> bindings){\n");
		sb.append(inputType).append(" input = (").append(inputType).append(")_input;").append("\n");
		sb.append(outputType).append(" output = (").append(outputType).append(")_output;").append("\n");
		sb.append(classBody).append("\n");
		sb.append("}");
		sb.append("\n");
		// endregion calc

		sb.append("}");
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public Object eval(String scriptContent, Object input, Object output, Map<String, Object> mergeBindings) throws ScriptEvalException {
		try {
			String inputType = input == null ? Map.class.getName() : input.getClass().getName();
			String outputType = output == null ? Map.class.getName() : output.getClass().getName();
			String sha1 = Base64.getEncoder().encodeToString(
				Digests.sha1(scriptContent + "\n" + inputType + "\n" + outputType)
			);
			JavaEvaluatorFunction bean = cache.getIfPresent(sha1);
			if (bean == null) {
				synchronized (this) {
					bean = cache.getIfPresent(sha1);
					if (bean == null) {
						String className = nextClassName();
						String content = asClassContent(className, scriptContent, inputType, outputType);
						Class<?> clazz = MemoryCompiler.getInstance().compile(className, content);
						bean = (JavaEvaluatorFunction) clazz.newInstance();
						cache.put(sha1, bean);
					}
				}
			}

			if (input == null) {
				input = new LinkedHashMap<>();
			}
			if (output == null) {
				output = new LinkedHashMap<>();
			}
			Map<String, Object> bindings = new HashMap<>();
			if (mergeBindings != null) {
				bindings.putAll(mergeBindings);
			} else {
				if (input instanceof Map) {
					bindings.putAll((Map) input);
				}
			}
			bindings.put(INPUT, input);
			bindings.put(OUTPUT, output);
			bean.eval(input, output, bindings);
			return output;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ScriptEvalException(e.getMessage(), e);
		}
	}
}

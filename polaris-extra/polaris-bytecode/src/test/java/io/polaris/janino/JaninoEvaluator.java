package io.polaris.janino;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.cache.ICache;
import io.polaris.core.cache.MapCache;
import io.polaris.core.crypto.digest.Digests;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.script.Evaluator;
import io.polaris.core.script.JavaEvaluator;
import io.polaris.core.script.ScriptEvalException;
import io.polaris.core.service.ServiceDefault;
import io.polaris.core.service.ServiceName;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class JaninoEvaluator implements Evaluator {
	private static final ILogger log = ILoggers.of(JaninoEvaluator.class);
	private static final AtomicLong CLASS_NO = new AtomicLong(0);
	private static final Pattern importPattern = Pattern.compile("\\s*\\bimport\\s+((static\\s+)?[\\w\\.\\*]+);\\s*+");
	private ICache<String, IExpressionEvaluator> cache = new MapCache<>(0x1000, true);


	@Override
	public Object eval(String scriptContent, Object input, Object output, Map<String, Object> mergeBindings) throws ScriptEvalException {
		try {

			Class<?> inputType = input == null ? Map.class : input.getClass();
			Class<?> outputType = output == null ? Map.class : output.getClass();
			String sha1 = Base64.getEncoder().encodeToString(
				Digests.sha1(scriptContent + "\n" + inputType.getName() + "\n" + outputType.getName())
			);
			IExpressionEvaluator bean = cache.getIfPresent(sha1);
			if (bean == null) {
				synchronized (this) {
					bean = cache.getIfPresent(sha1);
					if (bean == null) {
						IExpressionEvaluator ee = (
							CompilerFactoryFactory
								.getDefaultCompilerFactory(JaninoEvaluator.class.getClassLoader())
								.newExpressionEvaluator()
						);
						Set<String> imports = new HashSet<>();
						imports.add("java.math.*");
						imports.add("java.util.*");
						imports.add("java.util.function.*");
						imports.add("java.sql.*");
						String scriptBody = null;
						{
							StringBuffer body = new StringBuffer();
							Matcher matcher = importPattern.matcher(scriptContent);
							while (matcher.find()) {
								imports.add(matcher.group(1));
								matcher.appendReplacement(body, "");
							}
							matcher.appendTail(body);
							scriptBody = body.toString();
							body.setLength(0);
						}
						ee.setDefaultImports(imports.toArray(new String[0]));
						ee.setExpressionType(Object.class);
						ee.setParameters(new String[]{ INPUT, OUTPUT, BINDINGS}, new Class[]{inputType, outputType, Map.class});
						ee.cook(scriptBody);
						bean = ee;
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

			Object rs = bean.evaluate(new Object[]{input, output, bindings});
			if (output instanceof Map) {
				((Map) output).putIfAbsent(RESULT, rs);
			}
			return rs;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ScriptEvalException(e.getMessage(), e);
		}
	}
}

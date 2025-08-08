package io.polaris.core.script;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import io.polaris.core.cache.Cache;
import io.polaris.core.cache.MapCache;
import io.polaris.core.crypto.digest.Digests;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

/**
 * @author Qt
 * @since 1.8
 */
public abstract class AbstractStandardEvaluator implements Evaluator {
	private static final ILogger log = ILoggers.of(AbstractStandardEvaluator.class);

	public static final String OUT = "out";
	public static final String ERR = "err";

	protected final ScriptEngineManager manager = new ScriptEngineManager();
	protected final ScriptEngine scriptEngine;
	private boolean compilable;
	private Cache<String, CompiledScript> cache;

	public AbstractStandardEvaluator() {
		String engineName = getEngineName();
		scriptEngine = manager.getEngineByName(engineName);
		if (scriptEngine != null) {
			initGlobalScope(scriptEngine);
			if ((scriptEngine instanceof Compilable)) {
				compilable = true;
				Cache<String, CompiledScript> cache = createCache();
				if (cache == null) {
					cache = new MapCache<>(0x1000, true);
				}
				this.cache = cache;
			}
		} else {
			log.error("脚本引擎不支持:{}", engineName);
		}
	}

	protected abstract String getEngineName();

	protected Cache<String, CompiledScript> createCache() {
		return null;
	}

	protected void initGlobalScope(ScriptEngine scriptEngine) {
		Bindings globalBindings = scriptEngine.createBindings();
		globalBindings.put(OUT, System.out);
		globalBindings.put(ERR, System.err);
		scriptEngine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);
	}


	protected ScriptEngine getScriptEngine() {
		return scriptEngine;
	}


	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public Object eval(String scriptContent, Object input, Object output, Map<String, Object> mergeBindings) throws ScriptEvalException {
		try {
			if (scriptEngine == null) {
				throw new ScriptEvalException("脚本引擎不支持:" + getEngineName());
			}
			Bindings bindings = scriptEngine.createBindings();
			if (mergeBindings != null) {
				bindings.putAll(mergeBindings);
				bindings.put(BINDINGS, mergeBindings);
			} else {
				if (input instanceof Map) {
					bindings.putAll((Map) input);
				}
			}
			bindings.put(INPUT, input);
			if (output == null) {
				bindings.put(OUTPUT, new LinkedHashMap<>());
			} else {
				bindings.put(OUTPUT, output);
			}

			Object rs;
			if (compilable) {
				String sourceId = Base64.getEncoder().encodeToString(Digests.sha1(scriptContent));
				CompiledScript compiledScript = cache.getIfPresent(sourceId);
				if (compiledScript == null) {
					synchronized (this) {
						compiledScript = cache.getIfPresent(sourceId);
						if (compiledScript == null) {
							compiledScript = ((Compilable) scriptEngine).compile(scriptContent);
							cache.put(sourceId, compiledScript);
						}
					}
				}
				rs = compiledScript.eval(bindings);
			} else {
				rs = scriptEngine.eval(scriptContent, bindings);
			}
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

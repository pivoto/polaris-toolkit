package io.polaris.core.script;

import io.polaris.core.cache.ICache;
import io.polaris.core.cache.MapCache;
import io.polaris.core.crypto.Digests;
import io.polaris.core.err.CalcException;
import lombok.extern.slf4j.Slf4j;

import javax.script.*;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
public abstract class StandardCalcEngine implements CalcEngine {

	public static final String OUT = "out";
	public static final String ERR = "err";

	protected final ScriptEngineManager manager = new ScriptEngineManager();
	protected final ScriptEngine scriptEngine;
	private boolean compilable;
	private ICache<String, CompiledScript> cache;

	public StandardCalcEngine() {
		String engineName = getEngineName();
		scriptEngine = manager.getEngineByName(engineName);
		if (scriptEngine != null) {
			initGlobalScope(scriptEngine);
			if ((scriptEngine instanceof Compilable)) {
				compilable = true;
				ICache<String, CompiledScript> iCache = createCache();
				if (iCache == null) {
					iCache = new MapCache<>(0x1000, true);
				}
				cache = iCache;
			}
		} else {
			log.error("脚本引擎不支持: {}", engineName);
		}
	}

	protected abstract String getEngineName();

	protected ICache<String, CompiledScript> createCache() {
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
	public Object eval(String scriptContent, Object input, Object output, Map<String, Object> mergeBindings) throws CalcException {
		try {
			Bindings bindings = scriptEngine.createBindings();
			if (mergeBindings != null) {
				bindings.putAll(mergeBindings);
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
				;
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
				((Map) output).put(RESULT, rs);
			}
			return rs;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CalcException(e.getMessage(), e);
		}
	}

}

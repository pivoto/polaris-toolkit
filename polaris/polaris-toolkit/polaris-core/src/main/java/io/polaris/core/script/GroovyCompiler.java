package io.polaris.core.script;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import io.polaris.core.crypto.digest.Digests;
import io.polaris.core.string.Strings;
import lombok.Getter;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @since 1.8
 */
public class GroovyCompiler {
	private String defaultClassNamePrefix = "DefGvy_";
	private String defaultClassNameSuffix = ".groovy";
	private MyGroovyClassLoader groovyClassLoader;
	private Map<String, ClassEntry> classCache = new ConcurrentHashMap<>();

	private static class Holder {
		private static final Map<ClassLoader, GroovyCompiler> COMPILERS = new ConcurrentHashMap<>();

		public static GroovyCompiler get(ClassLoader classLoader) {
			return COMPILERS.computeIfAbsent(classLoader, loader -> new GroovyCompiler(classLoader));
		}
	}

	public GroovyCompiler(ClassLoader classLoader) {
		CompilerConfiguration config = CompilerConfiguration.DEFAULT;
		this.groovyClassLoader = new MyGroovyClassLoader(classLoader, config);
	}

	public static GroovyCompiler getInstance() {
		return GroovyCompiler.Holder.get(Thread.currentThread().getContextClassLoader());
	}

	public static GroovyCompiler getInstance(ClassLoader classLoader) {
		return GroovyCompiler.Holder.get(classLoader);
	}

	private static String sha1(String content) {
		try {
			return Base64.getEncoder().encodeToString(Digests.sha1(content != null ? content.trim() : ""));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private String buildDefaultScriptName(String str) {
		return defaultClassNamePrefix + str + defaultClassNameSuffix;
	}

	public void clearScript(String scriptName) {
		if (Strings.isNotBlank(scriptName)) {
			ClassEntry entry = classCache.get(scriptName);
			if (entry != null) {
				classCache.remove(scriptName);
				groovyClassLoader.clearCache(entry.getValue());
			}
		}
	}

	public void clearScript(String scriptName, String scriptSource) {
		if (Strings.isBlank(scriptName)) {
			String hash = sha1(scriptSource);
			scriptName = buildDefaultScriptName(hash);
		}
		clearScript(scriptName);
	}

	public IScript compileScript(String scriptName, String scriptSource) {
		try {
			Class klass = compile(scriptName, scriptSource);
			return createScript(klass);
		} catch (Throwable e) {
			return new ErrorScript(e);
		}
	}

	public IScript createScript(Class klass) {
		if (klass == null) {
			return new NullScript();
		}
		if (IScript.class.isAssignableFrom(klass)) {
			try {
				return (IScript) klass.newInstance();
			} catch (ReflectiveOperationException e) {
				throw new ScriptEvalException("初始化脚本类失败", e);
			}
		}
		try {
			return (variables) -> {
				Script script = InvokerHelper.createScript(klass, null);
				script.setBinding(new Binding(variables));
				return script.run();
			};
		} catch (Exception e) {
			throw new ScriptEvalException("初始化脚本类失败", e);
		}
	}

	public GroovyClassLoader getClassLoader() {
		return groovyClassLoader;
	}

	public Class compile(String content) {
		return compile(null, content);
	}

	public Class compile(String name, String content) {
		String hash = sha1(content);
		if (Strings.isBlank(name)) {
			name = buildDefaultScriptName(hash);
		}
		ClassEntry entry = classCache.get(name);
		if (entry != null) {
			if (entry.getHash().equals(hash)) {
				return entry.getValue();
			}
			groovyClassLoader.clearCache(entry.getValue());
		}
		GroovyCodeSource codeSource = new GroovyCodeSource(content, name, "/groovy/script");
		codeSource.setCachable(false);
		Class klass = groovyClassLoader.parseClass(codeSource);
		classCache.put(name, new ClassEntry(hash, klass));
		return klass;
	}


	public Script createGroovyScript(Class scriptClass, Binding binding) {
		return InvokerHelper.createScript(scriptClass, binding);
	}

	public Script compileGroovyScript(String name, String content, Binding binding) {
		Class clazz = compile(name, content);
		Script script = createGroovyScript(clazz, binding);
		return script;
	}

	public Script compileGroovyScript(String name, String content, Map<String, Object> variables) {
		return compileGroovyScript(name, content, new Binding(variables));
	}

	public Script compileGroovyScript(String content, Map<String, Object> variables) {
		return compileGroovyScript(null, content, new Binding(variables));
	}

	public Script compileGroovyScript(String name, String content, String... args) {
		return compileGroovyScript(name, content, new Binding(args));
	}

	public Script compileGroovyScript(String content, String... args) {
		return compileGroovyScript(null, content, new Binding(args));
	}

	public Object runScript(String content, String name, Map<String, Object> variables) {
		return compileGroovyScript(name, content, variables).run();
	}

	public Object runScript(String content, Map<String, Object> variables) {
		return compileGroovyScript(content, variables).run();
	}

	public Object runScript(String content, String name, String... args) {
		return compileGroovyScript(name, content, args).run();
	}

	public Object runScript(String content, String... args) {
		return compileGroovyScript(content, args).run();
	}

	public Object runScript(String content, String name, Binding binding) {
		return compileGroovyScript(name, content, binding).run();
	}

	@Getter
	private static class ClassEntry {
		private String hash;
		private Class value;

		public ClassEntry(String hash, Class value) {
			this.hash = hash;
			this.value = value;
		}
	}

	private static class MyGroovyClassLoader extends GroovyClassLoader {
		public MyGroovyClassLoader(ClassLoader classLoader, CompilerConfiguration config) {
			super(classLoader, config);
		}

		public MyGroovyClassLoader(CompilerConfiguration config) {
			super(Thread.currentThread().getContextClassLoader(), config);
		}

		public void clearCache(Class cls) {
			Class removed = classCache.remove(cls.getName());
			if (removed != null) {
				InvokerHelper.removeClass(removed);
			}
		}
	}

}

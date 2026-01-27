package io.polaris.core.classpath.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import io.polaris.core.classpath.analyzer.spi.AnalyzerPackageProvider;
import io.polaris.core.classpath.analyzer.spi.MethodCallAnalyzerProvider;
import io.polaris.core.classpath.analyzer.model.ContextDto;
import io.polaris.core.classpath.analyzer.model.EntryDto;
import io.polaris.core.classpath.analyzer.model.FlatEntryDto;
import io.polaris.core.classpath.analyzer.model.MethodDto;
import io.polaris.core.collection.Lists;
import io.polaris.core.function.TriFunction;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.service.Service;
import io.polaris.core.service.ServiceLoader;
import io.polaris.core.service.SpiLoaders;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;


/**
 * @author Qt
 * @since Aug 11, 2025
 */
@NotThreadSafe
public class EntryContext {
	private static final Logger log = Loggers.of(EntryContext.class);

	private final Map<String, EntryInfo> entryMap = new HashMap<>();
	private final Map<String, ClassInfo> classMap = new HashMap<>();
	private final Map<MethodKey, MethodInfo> methodMap = new HashMap<>();
	private final List<TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>> methodCallAnalyzers = new ArrayList<>();

	private Function<String, Boolean> packageFilter = buildDefaultPackageFilter();

	public EntryContext() {
		addDefaultMethodCallAnalyzers();
	}


	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("entryMap: \n");
		for (EntryInfo entry : entryMap.values()) {
			sb.append("\t").append(entry.dump(1)).append("\n");
		}
		sb.append("classMap: \n");
		for (ClassInfo classInfo : classMap.values()) {
			sb.append("\t").append(classInfo.dump(1)).append("\n");
		}
		sb.append("methodMap: \n");
		for (MethodInfo methodInfo : methodMap.values()) {
			sb.append("\t").append(methodInfo.dump(1)).append("\n");
		}
		return sb.toString();
	}

	public ContextDto dumpToDto() {
		return dumpToDto(false);
	}

	public ContextDto dumpToDto(boolean sha1Only) {
		ContextDto dto = new ContextDto();
		dto.setEntryList(new ArrayList<>());
		if (sha1Only) {
			dto.setMethodSha1(new HashMap<>());
			for (EntryInfo entry : entryMap.values()) {
				dto.getEntryList().add(EntryDto.from(entry));
			}
			for (MethodInfo methodInfo : methodMap.values()) {
				dto.getMethodSha1().put(methodInfo.getKey().dump(), methodInfo.getSha1());
			}
		} else {
			dto.setMethodMap(new HashMap<>());
			Set<MethodInfo> entryMethods = new HashSet<>();
			for (EntryInfo entry : entryMap.values()) {
				dto.getEntryList().add(EntryDto.from(entry));
				if (entry.getTraces() != null) {
					entryMethods.addAll(entry.getTraces());
				}
			}
			for (MethodInfo methodInfo : methodMap.values()) {
				if (entryMethods.contains(methodInfo)) {
					dto.getMethodMap().put(methodInfo.getKey().dump(), MethodDto.from(methodInfo, true));
				} else {
					dto.getMethodMap().put(methodInfo.getKey().dump(), MethodDto.from(methodInfo));
				}
			}
		}
		return dto;
	}

	public Map<String, FlatEntryDto> dumpToFlatDto() {
		Map<String, FlatEntryDto> map = new HashMap<>();
		for (EntryInfo entry : entryMap.values()) {
			map.put(entry.getKey(), FlatEntryDto.from(entry));
		}
		return map;
	}

	public EntryInfo doAnalyze(String entryKey, Class<?> entryClass, String methodName, String methodDesc) throws IOException {
		EntryInfo entry = entryMap.get(entryKey);
		if (entry != null) {
			return entry;
		}
		entry = new EntryInfo(entryKey, entryClass, methodName, methodDesc);
		entryMap.put(entryKey, entry);


		List<MethodInfo> methodInfos = new ArrayList<>();

		// 循环分析所有父类字码码
		Class<?> targetClass = entryClass;
		while (targetClass != null && targetClass != Object.class) {
			String classInternalName = Type.getInternalName(targetClass);
			methodInfos.addAll(doAnalyze(entryClass, classInternalName, entry.getMethodName(), entry.getMethodDesc()));
			targetClass = targetClass.getSuperclass();
		}


		ClassInfo classInfo = classMap.get(Type.getInternalName(entryClass));

		entry.addTraces(methodInfos);
		entry.setClassInfo(classInfo);

		return entry;
	}

	public List<MethodInfo> doAnalyze(Class<?> entryClass, String classInternalName, String methodName, String methodDesc) throws IOException {
		classInternalName = classInternalName.replace('.', '/');
		MethodKey methodKey = new MethodKey(classInternalName, methodName, methodDesc);
		MethodInfo methodInfo = methodMap.get(methodKey);
		if (methodInfo != null) {
			return Lists.asList(methodInfo);
		}
		ClassReader classReader = EntryAnalyzer.getReader(classInternalName, entryClass);
		EntryClassVisitor classVisitor = new EntryClassVisitor(this, entryClass, classInternalName, methodName, methodDesc);
		int parsingOptions = 0;
		parsingOptions |= ClassReader.EXPAND_FRAMES;
		parsingOptions |= ClassReader.SKIP_DEBUG;
		classReader.accept(classVisitor, parsingOptions);
		return classVisitor.getMethodInfoList();
	}

	Set<String> doAnalyzeMethodCall(EntryMethodVisitor methodVisitor, MethodCodeAnalyzer analyzer, MethodCall methodCall) {
		// 特定类的信息抓取: rpc:xxx, mq:xxx, job:xxx, sql:xxx
		Set<String> rs = new LinkedHashSet<>();
		for (TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>> callAnalyzer : methodCallAnalyzers) {
			Set<String> entryKeys = callAnalyzer.apply(methodVisitor, analyzer, methodCall);
			if (entryKeys != null && !entryKeys.isEmpty()) {
				rs.addAll(entryKeys);
			}
		}
		return rs;
	}

	public void addDefaultMethodCallAnalyzers() {
		addMethodCallAnalyzers(buildDefaultMethodCallAnalyzers());
	}

	@Nonnull
	public Set<TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>> buildDefaultMethodCallAnalyzers() {
		Set<TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>> rs = new LinkedHashSet<>();
		for (Service<MethodCallAnalyzerProvider> service : SpiLoaders.load(MethodCallAnalyzerProvider.class)) {
			try {
				MethodCallAnalyzerProvider provider = service.getSingleton();
				Collection<TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>> functions = provider.buildMethodCallAnalyzers();
				if (functions != null) {
					rs.addAll(functions);
				}
				TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>> function = provider.buildMethodCallAnalyzer();
				if (function != null) {
					rs.add(function);
				}
			} catch (Exception e) {
				log.error(e, "加载方法调用链静态代码分析器失败");
			}
		}
		return rs;
	}

	public void addMethodCallAnalyzer(TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>> methodCallAnalyzer) {
		this.methodCallAnalyzers.add(methodCallAnalyzer);
	}

	public void addMethodCallAnalyzers(Collection<TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>> methodCallAnalyzers) {
		this.methodCallAnalyzers.addAll(methodCallAnalyzers);
	}

	public void addMethodCallAnalyzers(TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>[] methodCallAnalyzers) {
		this.methodCallAnalyzers.addAll(Arrays.asList(methodCallAnalyzers));
	}

	public void clearMethodCallAnalyzers() {
		this.methodCallAnalyzers.clear();
	}

	public Function<String, Boolean> getPackageFilter() {
		return packageFilter;
	}

	public void setPackageFilter(Function<String, Boolean> packageFilter) {
		if (packageFilter == null) {
			this.packageFilter = buildDefaultPackageFilter();
			return;
		}
		this.packageFilter = packageFilter;
	}

	public boolean filterPackage(String classname) {
		return packageFilter != null && packageFilter.apply(classname);
	}

	public Function<String, Boolean> buildDefaultPackageFilter() {
		Set<String> excludeBasePackages = new HashSet<>();
		Set<String> includeBasePackages = new HashSet<>();
		try {
			ServiceLoader<AnalyzerPackageProvider> services = SpiLoaders.load(AnalyzerPackageProvider.class);
			AnalyzerPackageProvider provider = services.getSingleton();
			if (provider != null) {
				Set<String> arr1 = provider.excludePackages();
				if (arr1 != null) {
					for (String s : arr1) {
						excludeBasePackages.add(s.trim().replace('.', '/').replaceFirst("/?$", "/"));
					}
				}
				Set<String> arr2 = provider.includePackages();
				if (arr2 != null) {
					for (String s : arr2) {
						includeBasePackages.add(s.trim().replace('.', '/').replaceFirst("/?$", "/"));
					}
				}
			}
		} catch (Throwable ignored) {
		}

		return classname -> {
			if (classname == null) {
				return false;
			}
			classname = classname.replace('.', '/');
			for (String basePackage : excludeBasePackages) {
				if (classname.startsWith(basePackage)) {
					return false;
				}
			}
			for (String basePackage : includeBasePackages) {
				if (classname.startsWith(basePackage)) {
					return true;
				}
			}
			return false;
		};
	}

	public Map<String, EntryInfo> entryMap() {
		return entryMap;
	}

	public Map<String, ClassInfo> classMap() {
		return classMap;
	}

	public Map<MethodKey, MethodInfo> methodMap() {
		return methodMap;
	}

}

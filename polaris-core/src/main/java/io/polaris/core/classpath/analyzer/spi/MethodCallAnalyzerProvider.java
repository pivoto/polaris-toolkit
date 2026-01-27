package io.polaris.core.classpath.analyzer.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import io.polaris.core.classpath.analyzer.EntryMethodVisitor;
import io.polaris.core.classpath.analyzer.MethodCall;
import io.polaris.core.classpath.analyzer.MethodCodeAnalyzer;
import io.polaris.core.function.TriFunction;
import io.polaris.core.service.ServiceLoadable;
import io.polaris.core.service.Spi;

/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Spi("加载方法调用链静态代码分析器")
public interface MethodCallAnalyzerProvider extends ServiceLoadable {


	default TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>> buildMethodCallAnalyzer() {
		return null;
	}


	default Collection<TriFunction<EntryMethodVisitor, MethodCodeAnalyzer, MethodCall, Set<String>>> buildMethodCallAnalyzers() {
		return Collections.emptyList();
	}

}

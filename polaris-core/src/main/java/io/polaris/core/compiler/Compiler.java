package io.polaris.core.compiler;

/**
 * @author Qt
 * @since 1.8
 */
public interface Compiler {

	Class<?> compile(String className, String sourceCode) throws ClassNotFoundException;
}

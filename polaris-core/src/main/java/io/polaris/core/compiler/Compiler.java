package io.polaris.core.compiler;

/**
 * @author Qt
 */
public interface Compiler {

	Class<?> compile(String className, String sourceCode) throws ClassNotFoundException;
}

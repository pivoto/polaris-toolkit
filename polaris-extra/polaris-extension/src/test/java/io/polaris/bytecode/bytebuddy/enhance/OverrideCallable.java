package io.polaris.bytecode.bytebuddy.enhance;

/**
 * @author Qt
 * @since Apr 15, 2026
 */
public interface OverrideCallable {

	Object call(Object[] args) throws Throwable;

}

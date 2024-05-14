package io.polaris.core.asm.internal;

import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 10, 2024
 */
public interface ProcessArrayCallback {

	void processElement(Type type);

}

package io.polaris.core.asm.internal;

import org.objectweb.asm.Label;

/**
 * @author Qt
 * @since May 10, 2024
 */
public interface ProcessSwitchCallback {

	void processCase(int key, Label end) throws Exception;

	void processDefault() throws Exception;

}

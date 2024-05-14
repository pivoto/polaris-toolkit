package io.polaris.core.asm;

import io.polaris.core.consts.SystemKeys;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class BaseAsmTest {
	static {
		System.setProperty(SystemKeys.JAVA_CLASS_BYTES_TMPDIR, "/data/classes");
	}
}

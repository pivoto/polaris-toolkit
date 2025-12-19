package io.polaris.core.asm;

import io.polaris.core.consts.StdKeys;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class BaseAsmTest {
	static {
		System.setProperty(StdKeys.BYTECODE_GENERATOR_TMPDIR, "/data/classes");
	}
}

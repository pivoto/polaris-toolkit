package io.polaris.core.asm;

import java.io.File;

import io.polaris.core.consts.StdKeys;

/**
 * @author Qt
 * @since May 10, 2024
 */
public class BaseAsmTest {
	static {
		File dir = new File(BaseAsmTest.class.getProtectionDomain().getCodeSource().getLocation().getFile());
		if (dir.exists()) {
			System.setProperty(StdKeys.BYTECODE_GENERATOR_TMPDIR, dir.getAbsolutePath());
		}
	}
}

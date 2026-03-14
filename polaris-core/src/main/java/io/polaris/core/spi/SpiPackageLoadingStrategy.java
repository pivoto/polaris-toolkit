package io.polaris.core.spi;

import io.polaris.core.consts.StdConsts;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public interface SpiPackageLoadingStrategy {

	default int priority() {
		return Integer.MAX_VALUE;
	}

	default String[] basePackages() {
		return StdConsts.EMPTY_STRING_ARRAY;
	}

}

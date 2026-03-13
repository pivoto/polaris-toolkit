package io.polaris.core.service.support;

import io.polaris.core.service.SpiPackageLoadingStrategy;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public class DefaultSpiPackageLoadingStrategy implements SpiPackageLoadingStrategy {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public String[] basePackages() {
		return new String[]{"io.polaris"};
	}
}

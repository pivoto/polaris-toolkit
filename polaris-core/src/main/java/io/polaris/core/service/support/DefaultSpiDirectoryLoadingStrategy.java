package io.polaris.core.service.support;

import io.polaris.core.service.SpiDirectoryLoadingStrategy;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
public class DefaultSpiDirectoryLoadingStrategy implements SpiDirectoryLoadingStrategy {

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public String[] directories() {
		return new String[]{"META-INF/services/"};
	}

}

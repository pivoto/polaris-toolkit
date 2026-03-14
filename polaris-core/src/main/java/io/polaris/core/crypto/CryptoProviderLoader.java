package io.polaris.core.crypto;

import java.security.Provider;
import java.security.Security;

import io.polaris.core.spi.Service;
import io.polaris.core.spi.SpiLoadable;
import io.polaris.core.spi.SpiLoader;
import org.slf4j.LoggerFactory;

/**
 * @author Qt
 */
public interface CryptoProviderLoader extends SpiLoadable {

	Provider provider();

	default int position() {
		return Integer.MAX_VALUE;
	}

	public static void loadProviders() {
		try {
			SpiLoader<CryptoProviderLoader> serviceLoader = SpiLoader.of(CryptoProviderLoader.class);
			for (Service<CryptoProviderLoader> service : serviceLoader) {
				try {
					CryptoProviderLoader providerLoader = service.getSingleton();
					Security.insertProviderAt(providerLoader.provider(), providerLoader.position());
				} catch (Throwable e) {
					LoggerFactory.getLogger(CryptoProviderLoader.class).error("", e);
				}
			}
		} catch (Throwable e) {
			LoggerFactory.getLogger(CryptoProviderLoader.class).error("", e);
		}
	}
}

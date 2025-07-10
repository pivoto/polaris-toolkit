package io.polaris.core.crypto;

import io.polaris.core.service.Service;
import io.polaris.core.service.ServiceLoadable;
import io.polaris.core.service.ServiceLoader;
import org.slf4j.LoggerFactory;

import java.security.Provider;
import java.security.Security;

/**
 * @author Qt
 * @since 1.8
 */
public interface ICryptoProviderLoader extends ServiceLoadable {

	Provider provider();

	default int position() {
		return Integer.MAX_VALUE;
	}

	public static void loadProviders() {
		try {
			ServiceLoader<ICryptoProviderLoader> serviceLoader = ServiceLoader.of(ICryptoProviderLoader.class);
			for (Service<ICryptoProviderLoader> service : serviceLoader) {
				try {
					ICryptoProviderLoader providerLoader = service.getSingleton();
					Security.insertProviderAt(providerLoader.provider(), providerLoader.position());
				} catch (Throwable e) {
					LoggerFactory.getLogger(ICryptoProviderLoader.class).error("", e);
				}
			}
		} catch (Throwable e) {
			LoggerFactory.getLogger(ICryptoProviderLoader.class).error("", e);
		}
	}
}

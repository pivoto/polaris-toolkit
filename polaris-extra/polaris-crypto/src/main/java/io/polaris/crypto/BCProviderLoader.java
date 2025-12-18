package io.polaris.crypto;

import io.polaris.core.crypto.CryptoProviderLoader;

import java.security.Provider;

/**
 * @author Qt
 */
public class BCProviderLoader implements CryptoProviderLoader {

	@Override
	public Provider provider() {
		return new org.bouncycastle.jce.provider.BouncyCastleProvider();
	}

	@Override
	public int position() {
		return 1;
	}
}

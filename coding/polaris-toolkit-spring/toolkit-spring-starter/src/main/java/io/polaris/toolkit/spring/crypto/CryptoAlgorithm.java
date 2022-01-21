package io.polaris.toolkit.spring.crypto;

import io.polaris.toolkit.spring.crypto.encrypt.Encryptor;
import io.polaris.toolkit.spring.crypto.encrypt.RsaEncryptor;
import io.polaris.toolkit.spring.crypto.encrypt.RsaPrivateEncryptor;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
public enum CryptoAlgorithm {

	RSA(RsaEncryptor.INSTANCE),
	RSA_PUB(RsaEncryptor.INSTANCE),
	RSA_PRI(RsaPrivateEncryptor.INSTANCE),

	;

	private Encryptor encryptor;

	CryptoAlgorithm(Encryptor encryptor) {
		this.encryptor = encryptor;
	}

	public Encryptor getEncryptor() {
		return encryptor;
	}

}

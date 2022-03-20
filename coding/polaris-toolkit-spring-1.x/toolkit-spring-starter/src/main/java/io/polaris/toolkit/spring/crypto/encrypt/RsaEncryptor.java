package io.polaris.toolkit.spring.crypto.encrypt;

import io.polaris.toolkit.spring.core.crypto.RSA;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
public class RsaEncryptor implements Encryptor {

	public static final RsaEncryptor INSTANCE = new RsaEncryptor();

	@Override
	public String decrypt(String decryptKey, String text) throws Exception {
		return RSA.decryptByPublicKey(decryptKey, text);
	}

	@Override
	public String encrypt(String encryptKey, String text) throws Exception {
		return RSA.encryptByPrivateKey(encryptKey, text);
	}
}

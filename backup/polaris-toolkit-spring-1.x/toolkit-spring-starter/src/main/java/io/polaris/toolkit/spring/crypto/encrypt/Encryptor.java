package io.polaris.toolkit.spring.crypto.encrypt;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
public interface Encryptor {

	/**
	 * 解密
	 *
	 * @param decryptKey 解密密钥
	 * @param text       密文内容
	 * @return
	 * @throws Exception
	 */
	String decrypt(String decryptKey, String text) throws Exception;

	/**
	 * 加密
	 *
	 * @param encryptKey 加密密钥
	 * @param text       明细内容
	 * @return
	 * @throws Exception
	 */
	String encrypt(String encryptKey, String text) throws Exception;

}

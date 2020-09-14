package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.Algorithm;

/**
 * @author Qt
 * @since 1.8
 */
public enum AsymmetricAlgorithm implements Algorithm {
	/** 国密SM2非对称算法 */
	SM2("SM2"),
	/** RSA算法 */
	RSA("RSA"),
	/** RSA算法，此算法用了默认补位方式为RSA/ECB/PKCS1Padding */
	RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
	/** RSA算法，此算法用了默认补位方式为RSA/ECB/NoPadding */
	RSA_ECB("RSA/ECB/NoPadding"),
	/** RSA算法，此算法用了RSA/None/NoPadding */
	RSA_None("RSA/None/NoPadding"),
	DSA("DSA"),
	;

	private final String code;

	/**
	 * 构造
	 *
	 * @param code 算法字符表示，区分大小写
	 */
	AsymmetricAlgorithm(String code) {
		this.code = code;
	}

	public String code() {
		return this.code;
	}
}

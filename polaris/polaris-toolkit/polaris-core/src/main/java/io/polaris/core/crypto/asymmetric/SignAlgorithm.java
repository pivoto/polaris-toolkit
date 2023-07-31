package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.Algorithm;

/**
 * @author Qt
 * @since 1.8
 */
public enum SignAlgorithm implements Algorithm {
	// The RSA signature algorithm
	NONEwithRSA("NONEwithRSA"),

	// The MD2/MD5 with RSA Encryption signature algorithm
	MD2withRSA("MD2withRSA"),
	MD5withRSA("MD5withRSA"),

	// The signature algorithm with SHA-* and the RSA
	SHA1withRSA("SHA1withRSA"),
	SHA256withRSA("SHA256withRSA"),
	SHA384withRSA("SHA384withRSA"),
	SHA512withRSA("SHA512withRSA"),

	// The Digital Signature Algorithm
	NONEwithDSA("NONEwithDSA"),
	// The DSA with SHA-1 signature algorithm
	SHA1withDSA("SHA1withDSA"),

	// The ECDSA signature algorithms
	NONEwithECDSA("NONEwithECDSA"),
	SHA1withECDSA("SHA1withECDSA"),
	SHA256withECDSA("SHA256withECDSA"),
	SHA384withECDSA("SHA384withECDSA"),
	SHA512withECDSA("SHA512withECDSA"),

	// 需要BC库加入支持
	SHA256withRSA_PSS("SHA256WithRSA/PSS"),
	SHA384withRSA_PSS("SHA384WithRSA/PSS"),
	SHA512withRSA_PSS("SHA512WithRSA/PSS"),

	;

	private final String code;

	/**
	 * 构造
	 *
	 * @param code 算法字符表示，区分大小写
	 */
	SignAlgorithm(String code) {
		this.code = code;
	}

	public String code() {
		return this.code;
	}
}

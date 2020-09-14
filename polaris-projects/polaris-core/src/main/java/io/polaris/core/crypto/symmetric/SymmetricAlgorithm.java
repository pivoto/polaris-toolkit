package io.polaris.core.crypto.symmetric;

import io.polaris.core.crypto.Algorithm;

/**
 * @author Qt
 * @since 1.8
 */
public enum SymmetricAlgorithm implements Algorithm {
	/** 国密SM4对称加密算法S */
	SM4("SM4"),
	/** 默认的AES加密方式：AES/ECB/PKCS5Padding */
	AES("AES"),
	ARCFOUR("ARCFOUR"),
	Blowfish("Blowfish"),
	/** 默认的DES加密方式：DES/ECB/PKCS5Padding */
	DES("DES"),
	/** 3DES算法，默认实现为：DESede/ECB/PKCS5Padding */
	DESede("DESede"),
	RC2("RC2"),

	;

	private final String code;

	SymmetricAlgorithm(String code) {
		this.code = code;
	}

	public String code() {
		return this.code;
	}
}

package io.polaris.core.crypto.digest;

import io.polaris.core.crypto.Algorithm;

/**
 * @author Qt
 * @since 1.8
 */
public enum HmacAlgorithm implements Algorithm {

	/** The HmacMD5 Message Authentication Code (MAC) algorithm specified in RFC 2104 and RFC 1321. */
	HmacMD5("HmacMD5"),
	/** The HmacSHA1 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2. */
	HmacSHA1("HmacSHA1"),
	/** The HmacSHA224 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2. */
	HmacSHA224("HmacSHA224"),
	/** The HmacSHA256 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2. */
	HmacSHA256("HmacSHA256"),
	/** The HmacSHA384 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2. */
	HmacSHA384("HmacSHA384"),
	/** The HmacSHA512 Message Authentication Code (MAC) algorithm specified in RFC 2104 and FIPS PUB 180-2. */
	HmacSHA512("HmacSHA512"),

	/** HmacSM3算法实现，需要BouncyCastle库支持 */
	HmacSM3("HmacSM3"),
	/** SM4 CMAC模式实现，需要BouncyCastle库支持 */
	SM4CMAC("SM4CMAC"),


	;

	private final String code;

	HmacAlgorithm(String code) {
		this.code = code;
	}

	public String code() {
		return this.code;
	}
}

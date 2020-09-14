package io.polaris.crypto.digest;

import io.polaris.core.crypto.digest.DigestAlgorithm;
import io.polaris.core.crypto.digest.Digest;

import java.security.NoSuchAlgorithmException;

/**
 * 国密SM3杂凑（摘要）算法
 *
 * <p>
 * 国密算法包括：
 * <ol>
 *     <li>非对称加密和签名：SM2</li>
 *     <li>摘要签名算法：SM3</li>
 *     <li>对称加密：SM4</li>
 * </ol>
 *
 * @author Qt
 * @since 1.8
 */
public class SM3 extends Digest {
	public SM3() throws NoSuchAlgorithmException {
		super(DigestAlgorithm.SM3.code());
	}

}

package io.polaris.crypto.symmetric;

import io.polaris.core.crypto.symmetric.Symmetric;
import io.polaris.core.crypto.symmetric.SymmetricAlgorithm;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * 国密对称加密算法SM4实现
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
public class SM4 extends Symmetric {
	public SM4(Key key) {
		super(SymmetricAlgorithm.SM4, key);
	}

	public SM4() throws NoSuchAlgorithmException {
		super(SymmetricAlgorithm.SM4);
	}
}

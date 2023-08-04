package io.polaris.core.hash;


import io.polaris.core.lang.primitive.Bytes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Ketama算法，用于在一致性Hash中快速定位服务器位置
 *
 * @author Qt
 * @since 1.8,  Aug 01, 2023
 */
public class KetamaHash  {

	public static long hash64(String key) {
		byte[] bKey = md5(key);
		return ((long) (bKey[3] & 0xFF) << 24)
			| ((long) (bKey[2] & 0xFF) << 16)
			| ((long) (bKey[1] & 0xFF) << 8)
			| (bKey[0] & 0xFF);
	}

	public static int hash32(String key) {
		return (int) (hash64(key) & 0xffffffffL);
	}

	public static long hash(String key) {
		return hash64(key);
	}

	/**
	 * 计算MD5值，使用UTF-8编码
	 *
	 * @param key 被计算的键
	 * @return MD5值
	 */
	private static byte[] md5(String key) {
		final MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("MD5 algorithm not support!", e);
		}
		return md5.digest(Bytes.utf8Bytes(key));
	}
}


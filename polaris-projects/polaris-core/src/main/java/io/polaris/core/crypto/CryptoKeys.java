package io.polaris.core.crypto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.CharConsts;
import io.polaris.core.crypto.asymmetric.AsymmetricAlgorithm;
import io.polaris.core.err.CryptoRuntimeException;
import io.polaris.core.crypto.symmetric.SymmetricAlgorithm;
import io.polaris.core.io.IO;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("All")
public class CryptoKeys {

	public static final int DEFAULT_KEY_SIZE = 1024;
	public static final String SM2_DEFAULT_CURVE = "sm2p256v1";
	public static final String KEY_TYPE_JKS = "JKS";
	public static final String KEY_TYPE_PKCS12 = "pkcs12";
	public static final String CERT_TYPE_X509 = "X.509";

	static {
		ICryptoProviderLoader.loadProviders();
	}


	// region 获取密钥生成器或工厂

	/**
	 * @param algorithm 对称加密算法
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyGenerator getKeyGenerator(String algorithm) {
		try {
			return KeyGenerator.getInstance(getMainAlgorithm(algorithm));
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyGenerator getKeyGenerator(String algorithm, String provider) {
		try {
			return KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyGenerator getKeyGenerator(String algorithm, Provider provider) {
		try {
			return KeyGenerator.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * @param algorithm 非对称加密算法
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPairGenerator getKeyPairGenerator(String algorithm) {
		try {
			return KeyPairGenerator.getInstance(getMainAlgorithm(algorithm));
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyPairGenerator getKeyPairGenerator(String algorithm, String provider) {
		try {
			return KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyPairGenerator getKeyPairGenerator(String algorithm, Provider provider) {
		try {
			return KeyPairGenerator.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * @param algorithm 对称加密算法
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static SecretKeyFactory getSecretKeyFactory(String algorithm) {
		try {
			return SecretKeyFactory.getInstance(getMainAlgorithm(algorithm));
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKeyFactory getSecretKeyFactory(String algorithm, String provider) {
		try {
			return SecretKeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKeyFactory getSecretKeyFactory(String algorithm, Provider provider) {
		try {
			return SecretKeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * @param algorithm 非对称加密算法
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyFactory getKeyFactory(String algorithm) {
		try {
			return KeyFactory.getInstance(getMainAlgorithm(algorithm));
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyFactory getKeyFactory(String algorithm, String provider) {
		try {
			return KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static KeyFactory getKeyFactory(String algorithm, Provider provider) {
		try {
			return KeyFactory.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}


	public static Signature getSignature(String algorithm) {
		try {
			return Signature.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Signature getSignature(String algorithm, String provider) {
		try {
			return Signature.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Signature getSignature(String algorithm, Provider provider) {
		try {
			return Signature.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Cipher getCipher(String algorithm) {
		try {
			return Cipher.getInstance(algorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Cipher getCipher(String algorithm, String provider) {
		try {
			return Cipher.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Cipher getCipher(String algorithm, Provider provider) {
		try {
			return Cipher.getInstance(getMainAlgorithm(algorithm), provider);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 获取主体算法名，例如RSA/ECB/PKCS1Padding的主体算法是RSA
	 *
	 * @param algorithm XXXwithXXX算法
	 * @return 主体算法名
	 */
	public static String getMainAlgorithm(@Nonnull String algorithm) {
		final int slashIndex = algorithm.indexOf(CharConsts.SLASH);
		if (slashIndex > 0) {
			return algorithm.substring(0, slashIndex);
		}
		return algorithm;
	}

	/**
	 * 获取用于密钥生成的算法<br>
	 * 获取XXXwithXXX算法的后半部分算法，如果为ECDSA或SM2，返回算法为EC
	 *
	 * @param algorithm XXXwithXXX算法
	 * @return 算法
	 */
	public static String getAlgorithmAfterWith(String algorithm) {
		if (Strings.startsWithIgnoreCase(algorithm, "ECIESWith")) {
			return "EC";
		}
		int indexOfWith = Strings.lastIndexOfIgnoreCase(algorithm, "with");
		if (indexOfWith > 0) {
			algorithm = algorithm.substring(indexOfWith + "with".length());
		}
		if ("ECDSA".equalsIgnoreCase(algorithm)
			|| "SM2".equalsIgnoreCase(algorithm)
			|| "ECIES".equalsIgnoreCase(algorithm)
		) {
			algorithm = "EC";
		}
		return algorithm;
	}

	// endregion

	// region 生成对称加密或摘要算法密钥

	/**
	 * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成
	 *
	 * @param algorithm 算法，支持PBE算法
	 * @return {@link SecretKey}
	 */
	public static SecretKey generateKey(String algorithm) {
		return generateKey(algorithm, -1);
	}

	public static SecretKey generateKey(String provider, String algorithm) {
		return generateKey(provider, algorithm, -1);
	}

	public static SecretKey generateKey(Provider provider, String algorithm) {
		return generateKey(provider, algorithm, -1);
	}

	/**
	 * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成<br>
	 * 当指定keySize&lt;0时，AES默认长度为128，其它算法不指定。
	 *
	 * @param algorithm 算法，支持PBE算法
	 * @param keySize   密钥长度，&lt;0表示不设定密钥长度，即使用默认长度
	 * @return {@link SecretKey}
	 */
	public static SecretKey generateKey(String algorithm, int keySize) {
		return generateKey(algorithm, keySize, (SecureRandom) null);
	}

	public static SecretKey generateKey(String provider, String algorithm, int keySize) {
		return generateKey(provider, algorithm, keySize, (SecureRandom) null);
	}

	public static SecretKey generateKey(Provider provider, String algorithm, int keySize) {
		return generateKey(provider, algorithm, keySize, (SecureRandom) null);
	}

	/**
	 * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成<br>
	 * 当指定keySize&lt;0时，AES默认长度为128，其它算法不指定。
	 *
	 * @param algorithm 算法，支持PBE算法
	 * @param keySize   密钥长度，&lt;0表示不设定密钥长度，即使用默认长度
	 * @param random    随机数生成器，null表示默认
	 * @return {@link SecretKey}
	 */
	public static SecretKey generateKey(String algorithm, int keySize, SecureRandom random) {
		algorithm = getMainAlgorithm(algorithm);
		final KeyGenerator keyGenerator = getKeyGenerator(algorithm);
		if (keySize <= 0 && SymmetricAlgorithm.AES.code().equals(algorithm)) {
			// 对于AES的密钥，除非指定，否则强制使用128位
			keySize = 128;
		}
		if (keySize > 0) {
			if (null == random) {
				keyGenerator.init(keySize);
			} else {
				keyGenerator.init(keySize, random);
			}
		}
		return keyGenerator.generateKey();
	}

	public static SecretKey generateKey(String provider, String algorithm, int keySize, SecureRandom random) {
		algorithm = getMainAlgorithm(algorithm);
		final KeyGenerator keyGenerator = getKeyGenerator(algorithm, provider);
		if (keySize <= 0 && SymmetricAlgorithm.AES.code().equals(algorithm)) {
			// 对于AES的密钥，除非指定，否则强制使用128位
			keySize = 128;
		}
		if (keySize > 0) {
			if (null == random) {
				keyGenerator.init(keySize);
			} else {
				keyGenerator.init(keySize, random);
			}
		}
		return keyGenerator.generateKey();
	}

	public static SecretKey generateKey(Provider provider, String algorithm, int keySize, SecureRandom random) {
		algorithm = getMainAlgorithm(algorithm);
		final KeyGenerator keyGenerator = getKeyGenerator(algorithm, provider);
		if (keySize <= 0 && SymmetricAlgorithm.AES.code().equals(algorithm)) {
			// 对于AES的密钥，除非指定，否则强制使用128位
			keySize = 128;
		}
		if (keySize > 0) {
			if (null == random) {
				keyGenerator.init(keySize);
			} else {
				keyGenerator.init(keySize, random);
			}
		}
		return keyGenerator.generateKey();
	}

	/**
	 * 生成 {@link SecretKey}，仅用于对称加密和摘要算法密钥生成
	 *
	 * @param algorithm 算法
	 * @param key       密钥，如果为{@code null} 自动生成随机密钥
	 * @return {@link SecretKey}
	 */
	public static SecretKey generateKey(String algorithm, byte[] key) {
		SecretKey secretKey;
		if (algorithm.startsWith("PBE")) {
			// PBE密钥
			secretKey = generatePBEKey(algorithm, (null == key) ? null : new String(key, StandardCharsets.UTF_8).toCharArray());
		} else if (algorithm.startsWith("DES")) {
			// DES密钥
			secretKey = generateDESKey(algorithm, key);
		} else {
			// 其它算法密钥
			secretKey = (null == key) ? generateKey(algorithm) : new SecretKeySpec(key, algorithm);
		}
		return secretKey;
	}

	public static SecretKey generateKey(String provider, String algorithm, byte[] key) {
		SecretKey secretKey;
		if (algorithm.startsWith("PBE")) {
			// PBE密钥
			secretKey = generatePBEKey(provider, algorithm, (null == key) ? null : new String(key, StandardCharsets.UTF_8).toCharArray());
		} else if (algorithm.startsWith("DES")) {
			// DES密钥
			secretKey = generateDESKey(provider, algorithm, key);
		} else {
			// 其它算法密钥
			secretKey = (null == key) ? generateKey(provider, algorithm) : new SecretKeySpec(key, algorithm);
		}
		return secretKey;
	}

	public static SecretKey generateKey(Provider provider, String algorithm, byte[] key) {
		SecretKey secretKey;
		if (algorithm.startsWith("PBE")) {
			// PBE密钥
			secretKey = generatePBEKey(provider, algorithm, (null == key) ? null : new String(key, StandardCharsets.UTF_8).toCharArray());
		} else if (algorithm.startsWith("DES")) {
			// DES密钥
			secretKey = generateDESKey(provider, algorithm, key);
		} else {
			// 其它算法密钥
			secretKey = (null == key) ? generateKey(provider, algorithm) : new SecretKeySpec(key, algorithm);
		}
		return secretKey;
	}

	public static SecretKey generateKeyBySeed(String algorithm, byte[] seed) {
		if (seed == null) {
			return generateKey(algorithm);
		}
		KeyGenerator keyGen = getKeyGenerator(algorithm);
		keyGen.init(new SecureRandom(seed));
		return keyGen.generateKey();
	}

	public static SecretKey generateKeyBySeed(String provider, String algorithm, byte[] seed) {
		if (seed == null) {
			return generateKey(algorithm);
		}
		KeyGenerator keyGen = getKeyGenerator(algorithm, provider);
		keyGen.init(new SecureRandom(seed));
		return keyGen.generateKey();
	}

	public static SecretKey generateKeyBySeed(Provider provider, String algorithm, byte[] seed) {
		if (seed == null) {
			return generateKey(algorithm);
		}
		KeyGenerator keyGen = getKeyGenerator(algorithm, provider);
		keyGen.init(new SecureRandom(seed));
		return keyGen.generateKey();
	}

	/**
	 * 生成PBE {@link SecretKey}
	 *
	 * @param algorithm PBE算法，包括：PBEWithMD5AndDES、PBEWithSHA1AndDESede、PBEWithSHA1AndRC2_40等
	 * @param key       密钥
	 * @return {@link SecretKey}
	 */
	public static SecretKey generatePBEKey(String algorithm, char[] key) {
		if (Strings.isNotBlank(algorithm) || !algorithm.startsWith("PBE")) {
			throw new IllegalArgumentException("Not PBE algorithm!");
		}
		if (key == null) {
			key = Randoms.randomString(32).toCharArray();
		}
		PBEKeySpec keySpec = new PBEKeySpec(key);
		return generateKey(algorithm, keySpec);
	}

	public static SecretKey generatePBEKey(String provider, String algorithm, char[] key) {
		if (Strings.isNotBlank(algorithm) || !algorithm.startsWith("PBE")) {
			throw new IllegalArgumentException("Not PBE algorithm!");
		}
		if (key == null) {
			key = Randoms.randomString(32).toCharArray();
		}
		PBEKeySpec keySpec = new PBEKeySpec(key);
		return generateKey(provider, algorithm, keySpec);
	}

	public static SecretKey generatePBEKey(Provider provider, String algorithm, char[] key) {
		if (Strings.isNotBlank(algorithm) || !algorithm.startsWith("PBE")) {
			throw new IllegalArgumentException("Not PBE algorithm!");
		}
		if (key == null) {
			key = Randoms.randomString(32).toCharArray();
		}
		PBEKeySpec keySpec = new PBEKeySpec(key);
		return generateKey(provider, algorithm, keySpec);
	}

	/**
	 * 生成 {@link SecretKey}
	 *
	 * @param algorithm DES算法，包括DES、DESede等
	 * @param key       密钥
	 * @return {@link SecretKey}
	 */
	public static SecretKey generateDESKey(String algorithm, byte[] key) {
		if (Strings.isBlank(algorithm) || !algorithm.startsWith("DES")) {
			throw new IllegalArgumentException("Not DES algorithm!");
		}
		try {
			SecretKey secretKey;
			if (null == key) {
				secretKey = generateKey(algorithm);
			} else {
				KeySpec keySpec;
				// 兼容 DESede
				if (algorithm.startsWith("DESede")) {
					keySpec = new DESedeKeySpec(key);
				} else {
					keySpec = new DESKeySpec(key);
				}
				secretKey = generateKey(algorithm, keySpec);
			}
			return secretKey;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKey generateDESKey(String provider, String algorithm, byte[] key) {
		if (Strings.isBlank(algorithm) || !algorithm.startsWith("DES")) {
			throw new IllegalArgumentException("Not DES algorithm!");
		}
		try {
			SecretKey secretKey;
			if (null == key) {
				secretKey = generateKey(provider, algorithm);
			} else {
				KeySpec keySpec;
				// 兼容 DESede
				if (algorithm.startsWith("DESede")) {
					keySpec = new DESedeKeySpec(key);
				} else {
					keySpec = new DESKeySpec(key);
				}
				secretKey = generateKey(provider, algorithm, keySpec);
			}
			return secretKey;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKey generateDESKey(Provider provider, String algorithm, byte[] key) {
		if (Strings.isBlank(algorithm) || !algorithm.startsWith("DES")) {
			throw new IllegalArgumentException("Not DES algorithm!");
		}
		try {
			SecretKey secretKey;
			if (null == key) {
				secretKey = generateKey(provider, algorithm);
			} else {
				KeySpec keySpec;
				// 兼容 DESede
				if (algorithm.startsWith("DESede")) {
					keySpec = new DESedeKeySpec(key);
				} else {
					keySpec = new DESKeySpec(key);
				}
				secretKey = generateKey(provider, algorithm, keySpec);
			}
			return secretKey;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 生成 {@link SecretKey}，仅用于对称加密和摘要算法
	 *
	 * @param algorithm 算法
	 * @param keySpec   {@link KeySpec}
	 * @return {@link SecretKey}
	 */
	public static SecretKey generateKey(String algorithm, KeySpec keySpec) {
		final SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm);
		try {
			return keyFactory.generateSecret(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKey generateKey(String provider, String algorithm, KeySpec keySpec) {
		final SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm, provider);
		try {
			return keyFactory.generateSecret(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static SecretKey generateKey(Provider provider, String algorithm, KeySpec keySpec) {
		final SecretKeyFactory keyFactory = getSecretKeyFactory(algorithm, provider);
		try {
			return keyFactory.generateSecret(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	// endregion

	// region 生成非对称加密密钥


	/**
	 * 生成RSA私钥，仅用于非对称加密<br>
	 * 采用PKCS#8规范，此规范定义了私钥信息语法和加密私钥语法<br>
	 *
	 * @param key 密钥，必须为DER编码存储
	 * @return RSA私钥 {@link PrivateKey}
	 */
	public static PrivateKey generateRSAPrivateKey(@Nonnull byte[] key) {
		return generatePrivateKey(AsymmetricAlgorithm.RSA.code(), key);
	}

	public static PrivateKey generateRSAPrivateKey(String provider, @Nonnull byte[] key) {
		return generatePrivateKey(provider, AsymmetricAlgorithm.RSA.code(), key);
	}

	public static PrivateKey generateRSAPrivateKey(Provider provider, @Nonnull byte[] key) {
		return generatePrivateKey(provider, AsymmetricAlgorithm.RSA.code(), key);
	}

	/**
	 * 生成私钥，仅用于非对称加密<br>
	 * 采用PKCS#8规范，此规范定义了私钥信息语法和加密私钥语法<br>
	 *
	 * @param algorithm 算法，如RSA、EC、SM2等
	 * @param key       密钥，PKCS#8格式
	 * @return 私钥 {@link PrivateKey}
	 */
	public static PrivateKey generatePrivateKey(@Nonnull String algorithm, @Nonnull byte[] key) {
		return generatePrivateKey(algorithm, new PKCS8EncodedKeySpec(key));
	}

	public static PrivateKey generatePrivateKey(String provider, @Nonnull String algorithm, @Nonnull byte[] key) {
		return generatePrivateKey(provider, algorithm, new PKCS8EncodedKeySpec(key));
	}

	public static PrivateKey generatePrivateKey(Provider provider, @Nonnull String algorithm, @Nonnull byte[] key) {
		return generatePrivateKey(provider, algorithm, new PKCS8EncodedKeySpec(key));
	}

	/**
	 * 生成私钥，仅用于非对称加密<br>
	 *
	 * @param algorithm 算法，如RSA、EC、SM2等
	 * @param keySpec   {@link KeySpec}
	 * @return 私钥 {@link PrivateKey}
	 */
	public static PrivateKey generatePrivateKey(@Nonnull String algorithm, @Nonnull KeySpec keySpec) {
		algorithm = getAlgorithmAfterWith(algorithm);
		try {
			return getKeyFactory(algorithm).generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PrivateKey generatePrivateKey(String provider, @Nonnull String algorithm, @Nonnull KeySpec keySpec) {
		algorithm = getAlgorithmAfterWith(algorithm);
		try {
			return getKeyFactory(algorithm, provider).generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PrivateKey generatePrivateKey(Provider provider, @Nonnull String algorithm, @Nonnull KeySpec keySpec) {
		algorithm = getAlgorithmAfterWith(algorithm);
		try {
			return getKeyFactory(algorithm, provider).generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 生成RSA公钥，仅用于非对称加密<br>
	 * 采用X509证书规范<br>
	 *
	 * @param key 密钥，必须为DER编码存储
	 * @return 公钥 {@link PublicKey}
	 */
	public static PublicKey generateRSAPublicKey(@Nonnull byte[] key) {
		return generatePublicKey(AsymmetricAlgorithm.RSA.code(), key);
	}

	public static PublicKey generateRSAPublicKey(String provider, @Nonnull byte[] key) {
		return generatePublicKey(provider, AsymmetricAlgorithm.RSA.code(), key);
	}

	public static PublicKey generateRSAPublicKey(Provider provider, @Nonnull byte[] key) {
		return generatePublicKey(provider, AsymmetricAlgorithm.RSA.code(), key);
	}

	/**
	 * 生成公钥，仅用于非对称加密<br>
	 * 采用X509证书规范<br>
	 *
	 * @param algorithm 算法
	 * @param key       密钥，必须为DER编码存储
	 * @return 公钥 {@link PublicKey}
	 */
	public static PublicKey generatePublicKey(String algorithm, byte[] key) {
		return generatePublicKey(algorithm, new X509EncodedKeySpec(key));
	}

	public static PublicKey generatePublicKey(String provider, String algorithm, byte[] key) {
		return generatePublicKey(provider, algorithm, new X509EncodedKeySpec(key));
	}

	public static PublicKey generatePublicKey(Provider provider, String algorithm, byte[] key) {
		return generatePublicKey(provider, algorithm, new X509EncodedKeySpec(key));
	}

	/**
	 * 生成公钥，仅用于非对称加密<br>
	 *
	 * @param algorithm 算法
	 * @param keySpec   {@link KeySpec}
	 * @return 公钥 {@link PublicKey}
	 */
	public static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
		algorithm = getAlgorithmAfterWith(algorithm);
		try {
			return getKeyFactory(algorithm).generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey generatePublicKey(String provider, String algorithm, KeySpec keySpec) {
		algorithm = getAlgorithmAfterWith(algorithm);
		try {
			return getKeyFactory(algorithm, provider).generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey generatePublicKey(Provider provider, String algorithm, KeySpec keySpec) {
		algorithm = getAlgorithmAfterWith(algorithm);
		try {
			return getKeyFactory(algorithm, provider).generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 生成用于非对称加密的公钥和私钥，仅用于非对称加密
	 *
	 * @param algorithm 非对称加密算法
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm) {
		// ECIES算法对KEY的长度有要求，此处默认256
		int keySize = "ECIES".equalsIgnoreCase(algorithm) ? 256 : DEFAULT_KEY_SIZE;
		return generateKeyPair(algorithm, keySize);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm) {
		// ECIES算法对KEY的长度有要求，此处默认256
		int keySize = "ECIES".equalsIgnoreCase(algorithm) ? 256 : DEFAULT_KEY_SIZE;
		return generateKeyPair(provider, algorithm, keySize);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm) {
		// ECIES算法对KEY的长度有要求，此处默认256
		int keySize = "ECIES".equalsIgnoreCase(algorithm) ? 256 : DEFAULT_KEY_SIZE;
		return generateKeyPair(provider, algorithm, keySize);
	}

	/**
	 * 生成用于非对称加密的公钥和私钥
	 *
	 * @param algorithm 非对称加密算法
	 * @param keySize   密钥模（modulus ）长度
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm, int keySize) {
		return generateKeyPair(algorithm, keySize, null);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm, int keySize) {
		return generateKeyPair(provider, algorithm, keySize, null);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm, int keySize) {
		return generateKeyPair(provider, algorithm, keySize, null);
	}

	/**
	 * 生成用于非对称加密的公钥和私钥
	 *
	 * @param algorithm 非对称加密算法
	 * @param keySize   密钥模（modulus ）长度
	 * @param seed      种子
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {
		// SM2算法需要单独定义其曲线生成
		if ("SM2".equalsIgnoreCase(algorithm)) {
			final ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec(SM2_DEFAULT_CURVE);
			return generateKeyPair(algorithm, keySize, seed, sm2p256v1);
		}
		return generateKeyPair(algorithm, keySize, seed, (AlgorithmParameterSpec[]) null);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm, int keySize, byte[] seed) {
		// SM2算法需要单独定义其曲线生成
		if ("SM2".equalsIgnoreCase(algorithm)) {
			final ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec(SM2_DEFAULT_CURVE);
			return generateKeyPair(algorithm, keySize, seed, sm2p256v1);
		}
		return generateKeyPair(provider, algorithm, keySize, seed, (AlgorithmParameterSpec[]) null);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm, int keySize, byte[] seed) {
		// SM2算法需要单独定义其曲线生成
		if ("SM2".equalsIgnoreCase(algorithm)) {
			final ECGenParameterSpec sm2p256v1 = new ECGenParameterSpec(SM2_DEFAULT_CURVE);
			return generateKeyPair(algorithm, keySize, seed, sm2p256v1);
		}
		return generateKeyPair(provider, algorithm, keySize, seed, (AlgorithmParameterSpec[]) null);
	}

	/**
	 * 生成用于非对称加密的公钥和私钥
	 *
	 * @param algorithm 非对称加密算法
	 * @param params    {@link AlgorithmParameterSpec}
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm, AlgorithmParameterSpec params) {
		return generateKeyPair(algorithm, (byte[]) null, params);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm, AlgorithmParameterSpec params) {
		return generateKeyPair(provider, algorithm, (byte[]) null, params);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm, AlgorithmParameterSpec params) {
		return generateKeyPair(provider, algorithm, (byte[]) null, params);
	}

	/**
	 * 生成用于非对称加密的公钥和私钥
	 *
	 * @param algorithm 非对称加密算法
	 * @param param     {@link AlgorithmParameterSpec}
	 * @param seed      种子
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm, byte[] seed, AlgorithmParameterSpec param) {
		return generateKeyPair(algorithm, DEFAULT_KEY_SIZE, seed, param);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm, byte[] seed, AlgorithmParameterSpec param) {
		return generateKeyPair(provider, algorithm, DEFAULT_KEY_SIZE, seed, param);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm, byte[] seed, AlgorithmParameterSpec param) {
		return generateKeyPair(provider, algorithm, DEFAULT_KEY_SIZE, seed, param);
	}

	/**
	 * 生成用于非对称加密的公钥和私钥
	 *
	 * @param algorithm 非对称加密算法
	 * @param keySize   密钥模（modulus ）长度（单位bit）
	 * @param seed      种子
	 * @param params    {@link AlgorithmParameterSpec}
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec... params) {
		return generateKeyPair(algorithm, keySize, Randoms.createSecureRandom(seed), params);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec... params) {
		return generateKeyPair(provider, algorithm, keySize, Randoms.createSecureRandom(seed), params);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm, int keySize, byte[] seed, AlgorithmParameterSpec... params) {
		return generateKeyPair(provider, algorithm, keySize, Randoms.createSecureRandom(seed), params);
	}

	/**
	 * 生成用于非对称加密的公钥和私钥<br>
	 * 密钥对生成算法见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator
	 *
	 * <p>
	 * 对于非对称加密算法，密钥长度有严格限制，具体如下：
	 *
	 * <p>
	 * <b>RSA：</b>
	 * <pre>
	 * RS256、PS256：2048 bits
	 * RS384、PS384：3072 bits
	 * RS512、RS512：4096 bits
	 * </pre>
	 *
	 * <p>
	 * <b>EC（Elliptic Curve）：</b>
	 * <pre>
	 * EC256：256 bits
	 * EC384：384 bits
	 * EC512：512 bits
	 * </pre>
	 *
	 * @param algorithm 非对称加密算法
	 * @param keySize   密钥模（modulus ）长度（单位bit）
	 * @param random    {@link SecureRandom} 对象，创建时可选传入seed
	 * @param params    {@link AlgorithmParameterSpec}
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair(String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
		algorithm = getAlgorithmAfterWith(algorithm);
		final KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm);

		return getKeyPair(keyPairGen, algorithm, keySize, random, params);
	}

	public static KeyPair generateKeyPair(String provider, String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
		algorithm = getAlgorithmAfterWith(algorithm);
		final KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm, provider);

		return getKeyPair(keyPairGen, algorithm, keySize, random, params);
	}

	public static KeyPair generateKeyPair(Provider provider, String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec... params) {
		algorithm = getAlgorithmAfterWith(algorithm);
		final KeyPairGenerator keyPairGen = getKeyPairGenerator(algorithm, provider);

		return getKeyPair(keyPairGen, algorithm, keySize, random, params);
	}

	private static KeyPair getKeyPair(KeyPairGenerator keyPairGen, String algorithm, int keySize, SecureRandom random, AlgorithmParameterSpec[] params) {
		try {
			// 密钥模（modulus ）长度初始化定义
			if (keySize > 0) {
				// key长度适配修正
				if ("EC".equalsIgnoreCase(algorithm) && keySize > 256) {
					// 对于EC（EllipticCurve）算法，密钥长度有限制，在此使用默认256
					keySize = 256;
				}
				if (null != random) {
					keyPairGen.initialize(keySize, random);
				} else {
					keyPairGen.initialize(keySize);
				}
			}

			// 自定义初始化参数
			if (Iterables.isNotEmpty(params)) {
				for (AlgorithmParameterSpec param : params) {
					if (null == param) {
						continue;
					}
					if (null != random) {
						keyPairGen.initialize(param, random);
					} else {
						keyPairGen.initialize(param);
					}
				}
			}
			return keyPairGen.generateKeyPair();
		} catch (InvalidAlgorithmParameterException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	// endregion


	// region 非对称公私密钥转换

	public static PrivateKey toPrivateKey(String algorithm, byte[] key) {
		try {
			return getKeyFactory(algorithm).generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PrivateKey toPrivateKey(String provider, String algorithm, byte[] key) {
		try {
			return getKeyFactory(algorithm, provider).generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PrivateKey toPrivateKey(Provider provider, String algorithm, byte[] key) {
		try {
			return getKeyFactory(algorithm, provider).generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey toPublicKey(String algorithm, byte[] key) {
		try {
			return getKeyFactory(algorithm).generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey toPublicKey(String provider, String algorithm, byte[] key) {
		try {
			return getKeyFactory(algorithm, provider).generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey toPublicKey(Provider provider, String algorithm, byte[] key) {
		try {
			return getKeyFactory(algorithm, provider).generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPrivateKey toRSAPrivateKey(RSAPublicKey rsaPublicKey) {
		try {
			return (RSAPrivateKey) getKeyFactory(AsymmetricAlgorithm.RSA.code())
				.generatePrivate(new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent()));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPrivateKey toRSAPrivateKey(String provider, RSAPublicKey rsaPublicKey) {
		try {
			return (RSAPrivateKey) getKeyFactory(AsymmetricAlgorithm.RSA.code(), provider)
				.generatePrivate(new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent()));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPrivateKey toRSAPrivateKey(Provider provider, RSAPublicKey rsaPublicKey) {
		try {
			return (RSAPrivateKey) getKeyFactory(AsymmetricAlgorithm.RSA.code(), provider)
				.generatePrivate(new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent()));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPublicKey toRSAPublicKey(RSAPrivateKey rsaPrivateKey) {
		try {
			return (RSAPublicKey) getKeyFactory(AsymmetricAlgorithm.RSA.code())
				.generatePrivate(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent()));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPublicKey toRSAPublicKey(String provider, RSAPrivateKey rsaPrivateKey) {
		try {
			return (RSAPublicKey) getKeyFactory(AsymmetricAlgorithm.RSA.code(), provider)
				.generatePrivate(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent()));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPublicKey toRSAPublicKey(Provider provider, RSAPrivateKey rsaPrivateKey) {
		try {
			return (RSAPublicKey) getKeyFactory(AsymmetricAlgorithm.RSA.code(), provider)
				.generatePrivate(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent()));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPrivateKey toRSAPrivateKey(byte[] modulus, byte[] publicExponent) {
		try {
			return (RSAPrivateKey) getKeyFactory(AsymmetricAlgorithm.RSA.code())
				.generatePrivate(new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(publicExponent)));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static RSAPublicKey toRSAPublicKey(byte[] modulus, byte[] publicExponent) {
		try {
			return (RSAPublicKey) getKeyFactory(AsymmetricAlgorithm.RSA.code())
				.generatePublic(new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent)));
		} catch (InvalidKeySpecException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	// endregion


	// region 读写文件密钥


	/**
	 * 生成私钥，仅用于非对称加密
	 *
	 * @param keyStore {@link KeyStore}
	 * @param alias    别名
	 * @param password 密码
	 * @return 私钥 {@link PrivateKey}
	 */
	public static PrivateKey readPrivateKey(KeyStore keyStore, String alias, char[] password) {
		try {
			return (PrivateKey) keyStore.getKey(alias, password);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey readPublicKeyByX509(String x509File) throws IOException {
		try (InputStream in = IO.getInputStream(x509File)) {
			CertificateFactory factory = CertificateFactory.getInstance(CERT_TYPE_X509);
			Certificate cer = factory.generateCertificate(in);
			return cer.getPublicKey();
		} catch (CertificateException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static PublicKey readPublicKeyByX509(InputStream in) {
		final Certificate certificate = readX509Certificate(in);
		if (null != certificate) {
			return certificate.getPublicKey();
		}
		return null;
	}

	public static PublicKey readPublicKeyFile(String algorithm, String file) throws IOException {
		try (InputStream in = IO.getInputStream(file)) {
			byte[] publicKeyBytes = IO.toBytes(in, 64);
			return toPublicKey(algorithm, publicKeyBytes);
		}
	}

	/**
	 * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param keyFile  密钥库文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readJKSKeyStore(File keyFile, char[] password) throws IOException {
		return readKeyStore(KEY_TYPE_JKS, keyFile, password);
	}

	/**
	 * 读取密钥库(Java Key Store，JKS) KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param in       证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readJKSKeyStore(InputStream in, char[] password) throws IOException {
		return readKeyStore(KEY_TYPE_JKS, in, password);
	}

	/**
	 * 读取PKCS12 KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param keyFile  证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readPKCS12KeyStore(File keyFile, char[] password) throws IOException {
		return readKeyStore(KEY_TYPE_PKCS12, keyFile, password);
	}

	/**
	 * 读取PKCS12 KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param in       证书文件
	 * @param password 密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readPKCS12KeyStore(InputStream in, char[] password) throws IOException {
		return readKeyStore(KEY_TYPE_PKCS12, in, password);
	}

	/**
	 * 读取KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param type     类型
	 * @param keyFile  证书文件
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readKeyStore(String type, File keyFile, char[] password) throws IOException {
		InputStream in = null;
		try {
			in = IO.getInputStream(keyFile);
			return readKeyStore(type, in, password);
		} finally {
			IO.close(in);
		}
	}

	/**
	 * 读取KeyStore文件<br>
	 * KeyStore文件用于数字证书的密钥对保存
	 *
	 * @param type     类型
	 * @param in       证书文件
	 * @param password 密码，null表示无密码
	 * @return {@link KeyStore}
	 */
	public static KeyStore readKeyStore(String type, InputStream in, char[] password) throws IOException {
		try {
			final KeyStore keyStore = getKeyStore(type);
			keyStore.load(in, password);
			return keyStore;
		} catch (NoSuchAlgorithmException | CertificateException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 获取{@link KeyStore}对象
	 *
	 * @param type 类型
	 * @return {@link KeyStore}
	 */
	public static KeyStore getKeyStore(final String type) {
		try {
			return KeyStore.getInstance(type);
		} catch (KeyStoreException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 从KeyStore中获取私钥公钥
	 *
	 * @param type     类型
	 * @param in       证书文件
	 * @param password 密码
	 * @param alias    别名
	 * @return {@link KeyPair}
	 */
	public static KeyPair getKeyPair(String type, InputStream in, char[] password, String alias) throws IOException {
		final KeyStore keyStore = readKeyStore(type, in, password);
		return getKeyPair(keyStore, password, alias);
	}

	/**
	 * 从KeyStore中获取私钥公钥
	 *
	 * @param keyStore {@link KeyStore}
	 * @param password 密码
	 * @param alias    别名
	 * @return {@link KeyPair}
	 */
	public static KeyPair getKeyPair(KeyStore keyStore, char[] password, String alias) {
		try {
			PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
			return new KeyPair(publicKey, privateKey);
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	/**
	 * 读取X.509 Certification文件
	 *
	 * @param in       证书文件
	 * @param password 密码
	 * @param alias    别名
	 * @return {@link KeyStore}
	 */
	public static Certificate readX509Certificate(InputStream in, char[] password, String alias) throws IOException {
		return readCertificate(CERT_TYPE_X509, in, password, alias);
	}

	/**
	 * 读取X.509 Certification文件
	 */
	public static Certificate readX509Certificate(InputStream in) {
		return readCertificate(CERT_TYPE_X509, in);
	}

	public static Certificate readCertificate(String type, InputStream in, char[] password, String alias) throws IOException {
		final KeyStore keyStore = readKeyStore(type, in, password);
		try {
			return keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Certificate readCertificate(String type, InputStream in) {
		try {
			return getCertificateFactory(type).generateCertificate(in);
		} catch (CertificateException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Certificate getCertificate(KeyStore keyStore, String alias) {
		try {
			return keyStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static CertificateFactory getCertificateFactory(String type) {
		try {
			return CertificateFactory.getInstance(type);
		} catch (CertificateException e) {
			throw new CryptoRuntimeException(e);
		}
	}


	// endregion
}

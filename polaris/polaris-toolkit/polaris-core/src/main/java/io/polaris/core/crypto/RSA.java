package io.polaris.core.crypto;


import io.polaris.core.consts.StdConsts;
import io.polaris.core.lang.IO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RSA {
	private static final String DEFAULT_PUBLIC_KEY_STRING
			= "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAINE31e8jyk0fkrqLCjyLRR6MYp2yZ8z4OXtVaK6VUcOyPDB+FYQs599/BS604NoTWKavPvlcB30n7lu3Of7uGcCAwEAAQ==";
	private static final String DEFAULT_PRIVATE_KEY_STRING
			= "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAg0TfV7yPKTR+SuosKPItFHoxinbJnzPg5e1VorpVRw7I8MH4VhCzn338FLrTg2hNYpq8++VwHfSfuW7c5/u4ZwIDAQABAkBctrhIb5VttPW+U8b85I+SdvumoF+nYizmOMrTNGgm1utZQMgfnfg4d9tZ7jxa5EQGSTb0ZF4TvnfZbMLtV/qBAiEA+MFCuwcft+c+hM/0hlVWM5RmVMvBpexhqSbpp/cg9lUCIQCHF6BTsalwwXNMsgGN/gR6u0iCZOfHwnNO7f4xKxvXywIhALwrPTZLKkQsCc/fmyIu1XlJDauhQkgoKqwRAnkLaQTNAiEAgIww2bMjAtJx/rcb15uxc4Am/T07eNBFnQPGb88PjYUCIAZTE6hMTE5lywStZmUe8+A+G7zSJzXd4yMN/WPBmsjL";


	public static String decryptByPrivateKey(String key, String cipherText) throws GeneralSecurityException, UnsupportedEncodingException {
		return decrypt(toPrivateKey(key), cipherText);
	}

	public static String decryptByPublicKey(String key, String cipherText) throws GeneralSecurityException, UnsupportedEncodingException {
		return decrypt(toPublicKey(key), cipherText);
	}

	public static String decrypt(PublicKey publicKey, String cipherText)
			throws GeneralSecurityException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		try {
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
		} catch (InvalidKeyException e) {
			// 因为 IBM JDK 不支持私钥加密, 公钥解密, 所以要反转公私钥
			// 也就是说对于解密, 可以通过公钥的参数伪造一个私钥对象欺骗 IBM JDK
			RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
			RSAPrivateKeySpec spec = new RSAPrivateKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
			Key fakePrivateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
			cipher = Cipher.getInstance("RSA"); //It is a stateful object. so we need to get new one.
			cipher.init(Cipher.DECRYPT_MODE, fakePrivateKey);
		}

		if (cipherText == null || cipherText.length() == 0) {
			return cipherText;
		}

		byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
		byte[] plainBytes = cipher.doFinal(cipherBytes);

		return new String(plainBytes, StdConsts.UTF_8);
	}

	public static String decrypt(PrivateKey privateKey, String cipherText)
			throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
		byte[] plainBytes = cipher.doFinal(cipherBytes);
		return new String(plainBytes, StdConsts.UTF_8);
	}

	public static String encryptByPublicKey(String key, String plainText) throws GeneralSecurityException, UnsupportedEncodingException {
		return encrypt(toPublicKey(key), plainText);
	}

	public static String encryptByPrivateKey(String key, String plainText) throws GeneralSecurityException, UnsupportedEncodingException {
		return encrypt(toPrivateKey(key), plainText);
	}

	public static String encrypt(PrivateKey privateKey, String plainText)
			throws GeneralSecurityException, UnsupportedEncodingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		try {
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		} catch (InvalidKeyException e) {
			//For IBM JDK, 原因请看解密方法中的说明
			RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
			Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
		}

		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StdConsts.UTF_8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String encrypt(PublicKey publicKey, String plainText)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StdConsts.UTF_8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}


	public static PublicKey toPublicKey(String publicKeyText) {
		byte[] publicKeyBytes = Base64.getDecoder().decode(
				(publicKeyText == null || (publicKeyText = publicKeyText.trim()).length() == 0)
						? DEFAULT_PUBLIC_KEY_STRING : publicKeyText);
		return toPublicKey(publicKeyBytes);
	}

	public static PublicKey toPublicKey(byte[] publicKeyBytes) {
		try {
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(
					publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
			return keyFactory.generatePublic(x509KeySpec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		}
	}

	public static PrivateKey toPrivateKey(String privateKeyText) {
		byte[] publicKeyBytes = Base64.getDecoder().decode(
				(privateKeyText == null || (privateKeyText = privateKeyText.trim()).length() == 0)
						? DEFAULT_PRIVATE_KEY_STRING : privateKeyText);
		return toPrivateKey(publicKeyBytes);
	}


	public static PrivateKey toPrivateKey(byte[] privateKeyBytes) {
		try {
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory factory = KeyFactory.getInstance("RSA", "SunRsaSign");
			return factory.generatePrivate(spec);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get private key", e);
		}
	}

	public static KeyPair genKeyPair(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
		gen.initialize(keySize, new SecureRandom());
		KeyPair pair = gen.generateKeyPair();
		return pair;
	}

	public static byte[][] genKeyPairBytes(int keySize)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[][] keyPairBytes = new byte[2][];

		KeyPair pair = genKeyPair(keySize);

		keyPairBytes[0] = pair.getPrivate().getEncoded();
		keyPairBytes[1] = pair.getPublic().getEncoded();

		return keyPairBytes;
	}

	public static String[] genKeyPairStr(int keySize)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[][] keyPairBytes = genKeyPairBytes(keySize);
		String[] keyPairs = new String[2];

		keyPairs[0] = Base64.getEncoder().encodeToString(keyPairBytes[0]);
		keyPairs[1] = Base64.getEncoder().encodeToString(keyPairBytes[1]);

		return keyPairs;
	}

	public static PublicKey getPublicKeyByX509(String x509File) {
		if (x509File == null || x509File.length() == 0) {
			return toPublicKey((String) null);
		}

		try (InputStream in = IO.getInputStream(x509File)) {
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			Certificate cer = factory.generateCertificate(in);
			return cer.getPublicKey();
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		}
	}

	public static PublicKey getPublicKeyByFile(String file) {
		if (file == null || file.length() == 0) {
			return toPublicKey((String) null);
		}
		try (InputStream in = IO.getInputStream(file)) {
			byte[] publicKeyBytes = IO.toBytes(in, 64);
			return toPublicKey(publicKeyBytes);
		} catch (IOException e) {
			throw new IllegalArgumentException("Failed to get public key", e);
		}
	}

}

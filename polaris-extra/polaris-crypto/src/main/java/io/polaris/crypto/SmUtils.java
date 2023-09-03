package io.polaris.crypto;

import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.asymmetric.AsymmetricAlgorithm;
import io.polaris.crypto.asymmetric.SM2;
import io.polaris.crypto.digest.SM3;
import io.polaris.crypto.symmetric.SM4;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;

/**
 * @author Qt
 * @since 1.8
 */
public class SmUtils {
	/**
	 * SM2默认曲线
	 */
	public static final String SM2_CURVE_NAME = "sm2p256v1";
	/**
	 * SM2推荐曲线参数（来自https://github.com/ZZMarquis/gmhelper）
	 */
	public static final ECDomainParameters SM2_DOMAIN_PARAMS = BCKeys.toDomainParams(GMNamedCurves.getByName(SM2_CURVE_NAME));
	/**
	 * SM2国密算法公钥参数的Oid标识
	 */
	public static final ASN1ObjectIdentifier ID_SM2_PUBLIC_KEY_PARAM = new ASN1ObjectIdentifier("1.2.156.10197.1.301");
	private final static int RS_LEN = 32;

	public static SM2 sm2() throws GeneralSecurityException {
		KeyPair keyPair = CryptoKeys.generateKeyPair(AsymmetricAlgorithm.SM2.code());
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		return new SM2(privateKey, publicKey);
	}

	public static SM2 sm2(byte[] privateKey, byte[] publicKey) throws GeneralSecurityException {
		return new SM2(CryptoKeys.generatePrivateKey(AsymmetricAlgorithm.SM2.code(), privateKey),
			CryptoKeys.generatePublicKey(AsymmetricAlgorithm.SM2.code(), publicKey));
	}

	public static SM2 sm2(PrivateKey privateKey, PublicKey publicKey) throws GeneralSecurityException {
		return new SM2(privateKey, publicKey);
	}

	public static SM3 sm3() throws NoSuchAlgorithmException {
		return new SM3();
	}

	public static SM4 sm4() throws NoSuchAlgorithmException {
		return new SM4();
	}

	public static SM4 sm4(Key key) {
		return new SM4(key);
	}

	/**
	 * BigInteger转固定长度bytes
	 *
	 * @param rOrS {@link BigInteger}
	 * @return 固定长度bytes
	 */
	private static byte[] bigIntToFixedLengthBytes(BigInteger rOrS) {
		// for sm2p256v1, n is 00fffffffeffffffffffffffffffffffff7203df6b21c6052b53bbf40939d54123,
		// r and s are the result of mod n, so they should be less than n and have length<=32
		byte[] rs = rOrS.toByteArray();
		if (rs.length == RS_LEN) {
			return rs;
		} else if (rs.length == RS_LEN + 1 && rs[0] == 0) {
			return Arrays.copyOfRange(rs, 1, RS_LEN + 1);
		} else if (rs.length < RS_LEN) {
			byte[] result = new byte[RS_LEN];
			Arrays.fill(result, (byte) 0);
			System.arraycopy(rs, 0, result, RS_LEN - rs.length, rs.length);
			return result;
		} else {
			throw new IllegalArgumentException("Error rs: " + Hex.toHexString(rs));
		}
	}

	/**
	 * BC的SM3withSM2签名得到的结果的rs是asn1格式的，这个方法转化成直接拼接r||s
	 *
	 * @param rsDer rs in asn1 format
	 * @return sign result in plain byte array
	 * @since 4.5.0
	 */
	public static byte[] rsAsn1ToPlain(byte[] rsDer) throws IOException {
		final BigInteger[] decode;
		decode = StandardDSAEncoding.INSTANCE.decode(SM2_DOMAIN_PARAMS.getN(), rsDer);
		final byte[] r = bigIntToFixedLengthBytes(decode[0]);
		final byte[] s = bigIntToFixedLengthBytes(decode[1]);
		return Arrays.concatenate(r, s);
	}


	/**
	 * BC的SM3withSM2验签需要的rs是asn1格式的，这个方法将直接拼接r||s的字节数组转化成asn1格式
	 *
	 * @param sign in plain byte array
	 * @return rs result in asn1 format
	 * @since 4.5.0
	 */
	public static byte[] rsPlainToAsn1(byte[] sign) throws IOException {
		if (sign.length != RS_LEN * 2) {
			throw new IllegalArgumentException("err rs. ");
		}
		BigInteger r = new BigInteger(1, Arrays.copyOfRange(sign, 0, RS_LEN));
		BigInteger s = new BigInteger(1, Arrays.copyOfRange(sign, RS_LEN, RS_LEN * 2));
		return StandardDSAEncoding.INSTANCE.encode(SM2_DOMAIN_PARAMS.getN(), r, s);
	}

	/**
	 * bc加解密使用旧标c1||c2||c3，此方法在加密后调用，将结果转化为c1||c3||c2
	 *
	 * @param c1c2c3             加密后的bytes，顺序为C1C2C3
	 * @param ecDomainParameters {@link ECDomainParameters}
	 * @return 加密后的bytes，顺序为C1C3C2
	 */
	public static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3, ECDomainParameters ecDomainParameters) {
		// sm2p256v1的这个固定65。可看GMNamedCurves、ECCurve代码。
		final int c1Len = (ecDomainParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
		final int c3Len = 32;
		byte[] result = new byte[c1c2c3.length];
		System.arraycopy(c1c2c3, 0, result, 0, c1Len); // c1
		System.arraycopy(c1c2c3, c1c2c3.length - c3Len, result, c1Len, c3Len); // c3
		System.arraycopy(c1c2c3, c1Len, result, c1Len + c3Len, c1c2c3.length - c1Len - c3Len); // c2
		return result;
	}

	/**
	 * bc加解密使用旧标c1||c3||c2，此方法在解密前调用，将密文转化为c1||c2||c3再去解密
	 *
	 * @param c1c3c2             加密后的bytes，顺序为C1C3C2
	 * @param ecDomainParameters {@link ECDomainParameters}
	 * @return c1c2c3 加密后的bytes，顺序为C1C2C3
	 */
	public static byte[] changeC1C3C2ToC1C2C3(byte[] c1c3c2, ECDomainParameters ecDomainParameters) {
		// sm2p256v1的这个固定65。可看GMNamedCurves、ECCurve代码。
		final int c1Len = (ecDomainParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1;
		final int c3Len = 32;
		byte[] result = new byte[c1c3c2.length];
		System.arraycopy(c1c3c2, 0, result, 0, c1Len); // c1: 0->65
		System.arraycopy(c1c3c2, c1Len + c3Len, result, c1Len, c1c3c2.length - c1Len - c3Len); // c2
		System.arraycopy(c1c3c2, c1Len, result, c1c3c2.length - c3Len, c3Len); // c3
		return result;
	}

}

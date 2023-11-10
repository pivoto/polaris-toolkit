package io.polaris.crypto;

import io.polaris.core.crypto.CryptoKeys;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

/**
 * Bouncy Castle相关工具类封装
 * @author Qt
 * @since 1.8
 */
public class BCKeys {

	/**
	 * 只获取私钥里的d，32字节
	 *
	 * @param privateKey {@link PublicKey}，必须为org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
	 * @return 压缩得到的X
	 */
	public static byte[] encodeECPrivateKey(PrivateKey privateKey) {
		return ((BCECPrivateKey) privateKey).getD().toByteArray();
	}

	/**
	 * 编码压缩EC公钥（基于BouncyCastle），即Q值<br>
	 *
	 * @param publicKey {@link PublicKey}，必须为org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
	 * @return 压缩得到的Q
	 */
	public static byte[] encodeECPublicKey(PublicKey publicKey) {
		return encodeECPublicKey(publicKey, true);
	}

	/**
	 * 编码压缩EC公钥（基于BouncyCastle），即Q值<br>
	 *
	 * @param publicKey    {@link PublicKey}，必须为org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
	 * @param isCompressed 是否压缩
	 * @return 得到的Q
	 */
	public static byte[] encodeECPublicKey(PublicKey publicKey, boolean isCompressed) {
		return ((BCECPublicKey) publicKey).getQ().getEncoded(isCompressed);
	}

	/**
	 * 解码恢复EC压缩公钥,支持Base64和Hex编码,（基于BouncyCastle）<br>
	 * 见：https://www.cnblogs.com/xinzhao/p/8963724.html
	 *
	 * @param encode    压缩公钥
	 * @param curveName EC曲线名
	 * @return 公钥
	 */
	public static PublicKey decodeECPoint(String encode, String curveName) throws GeneralSecurityException {
		return decodeECPoint(Base64.getDecoder().decode(encode), curveName);
	}

	/**
	 * 解码恢复EC压缩公钥,支持Base64和Hex编码,（基于BouncyCastle）
	 *
	 * @param encodeByte 压缩公钥
	 * @param curveName  EC曲线名
	 * @return 公钥
	 */
	public static PublicKey decodeECPoint(byte[] encodeByte, String curveName) throws GeneralSecurityException {
		final X9ECParameters x9ECParameters = ECUtil.getNamedCurveByName(curveName);
		final ECCurve curve = x9ECParameters.getCurve();
		final ECPoint point = EC5Util.convertPoint(curve.decodePoint(encodeByte));

		// 根据曲线恢复公钥格式
		final ECNamedCurveSpec ecSpec = new ECNamedCurveSpec(curveName, curve, x9ECParameters.getG(), x9ECParameters.getN());
		return CryptoKeys.generatePublicKey("EC", new ECPublicKeySpec(point, ecSpec));
	}

	/**
	 * 构建ECDomainParameters对象
	 *
	 * @param parameterSpec ECParameterSpec
	 * @return {@link ECDomainParameters}
	 */
	public static ECDomainParameters toDomainParams(ECParameterSpec parameterSpec) {
		return new ECDomainParameters(
			parameterSpec.getCurve(),
			parameterSpec.getG(),
			parameterSpec.getN(),
			parameterSpec.getH());
	}

	/**
	 * 构建ECDomainParameters对象
	 *
	 * @param curveName Curve名称
	 * @return {@link ECDomainParameters}
	 */
	public static ECDomainParameters toDomainParams(String curveName) {
		return toDomainParams(ECUtil.getNamedCurveByName(curveName));
	}

	/**
	 * 构建ECDomainParameters对象
	 *
	 * @param x9ECParameters {@link X9ECParameters}
	 * @return {@link ECDomainParameters}
	 */
	public static ECDomainParameters toDomainParams(X9ECParameters x9ECParameters) {
		return new ECDomainParameters(
			x9ECParameters.getCurve(),
			x9ECParameters.getG(),
			x9ECParameters.getN(),
			x9ECParameters.getH()
		);
	}

	/**
	 * 密钥转换为AsymmetricKeyParameter
	 *
	 * @param key PrivateKey或者PublicKey
	 * @return ECPrivateKeyParameters或者ECPublicKeyParameters
	 */
	public static AsymmetricKeyParameter toParams(Key key) throws InvalidKeyException {
		return ECKeys.toParams(key);
	}

	/**
	 * 转换为 ECPrivateKeyParameters
	 *
	 * @param d 私钥d值
	 * @return ECPrivateKeyParameters
	 */
	public static ECPrivateKeyParameters toSm2Params(String d) {
		return ECKeys.toSm2PrivateParams(d);
	}

	/**
	 * 转换为 ECPrivateKeyParameters
	 *
	 * @param dHex             私钥d值16进制字符串
	 * @param domainParameters ECDomainParameters
	 * @return ECPrivateKeyParameters
	 */
	public static ECPrivateKeyParameters toParams(String dHex, ECDomainParameters domainParameters) {
		return ECKeys.toPrivateParams(dHex, domainParameters);
	}

	/**
	 * 转换为 ECPrivateKeyParameters
	 *
	 * @param d 私钥d值
	 * @return ECPrivateKeyParameters
	 */
	public static ECPrivateKeyParameters toSm2Params(byte[] d) {
		return ECKeys.toSm2PrivateParams(d);
	}

	/**
	 * 转换为 ECPrivateKeyParameters
	 *
	 * @param d                私钥d值
	 * @param domainParameters ECDomainParameters
	 * @return ECPrivateKeyParameters
	 */
	public static ECPrivateKeyParameters toParams(byte[] d, ECDomainParameters domainParameters) {
		return ECKeys.toPrivateParams(d, domainParameters);
	}

	/**
	 * 转换为 ECPrivateKeyParameters
	 *
	 * @param d 私钥d值
	 * @return ECPrivateKeyParameters
	 */
	public static ECPrivateKeyParameters toSm2Params(BigInteger d) {
		return ECKeys.toSm2PrivateParams(d);
	}

	/**
	 * 转换为 ECPrivateKeyParameters
	 *
	 * @param d                私钥d值
	 * @param domainParameters ECDomainParameters
	 * @return ECPrivateKeyParameters
	 */
	public static ECPrivateKeyParameters toParams(BigInteger d, ECDomainParameters domainParameters) {
		return ECKeys.toPrivateParams(d, domainParameters);
	}

	/**
	 * 转换为ECPublicKeyParameters
	 *
	 * @param x                公钥X
	 * @param y                公钥Y
	 * @param domainParameters ECDomainParameters
	 * @return ECPublicKeyParameters
	 */
	public static ECPublicKeyParameters toParams(BigInteger x, BigInteger y, ECDomainParameters domainParameters) {
		return ECKeys.toPublicParams(x, y, domainParameters);
	}

	/**
	 * 转换为SM2的ECPublicKeyParameters
	 *
	 * @param xHex 公钥X
	 * @param yHex 公钥Y
	 * @return ECPublicKeyParameters
	 */
	public static ECPublicKeyParameters toSm2Params(String xHex, String yHex) {
		return ECKeys.toSm2PublicParams(xHex, yHex);
	}

	/**
	 * 转换为ECPublicKeyParameters
	 *
	 * @param xHex             公钥X
	 * @param yHex             公钥Y
	 * @param domainParameters ECDomainParameters
	 * @return ECPublicKeyParameters
	 */
	public static ECPublicKeyParameters toParams(String xHex, String yHex, ECDomainParameters domainParameters) {
		return ECKeys.toPublicParams(xHex, yHex, domainParameters);
	}

	/**
	 * 转换为SM2的ECPublicKeyParameters
	 *
	 * @param xBytes 公钥X
	 * @param yBytes 公钥Y
	 * @return ECPublicKeyParameters
	 */
	public static ECPublicKeyParameters toSm2Params(byte[] xBytes, byte[] yBytes) {
		return ECKeys.toSm2PublicParams(xBytes, yBytes);
	}

	/**
	 * 转换为ECPublicKeyParameters
	 *
	 * @param xBytes           公钥X
	 * @param yBytes           公钥Y
	 * @param domainParameters ECDomainParameters
	 * @return ECPublicKeyParameters
	 */
	public static ECPublicKeyParameters toParams(byte[] xBytes, byte[] yBytes, ECDomainParameters domainParameters) {
		return ECKeys.toPublicParams(xBytes, yBytes, domainParameters);
	}

	/**
	 * 公钥转换为 {@link ECPublicKeyParameters}
	 *
	 * @param publicKey 公钥，传入null返回null
	 * @return {@link ECPublicKeyParameters}或null
	 */
	public static ECPublicKeyParameters toParams(PublicKey publicKey) throws InvalidKeyException {
		return ECKeys.toPublicParams(publicKey);
	}

	/**
	 * 私钥转换为 {@link ECPrivateKeyParameters}
	 *
	 * @param privateKey 私钥，传入null返回null
	 * @return {@link ECPrivateKeyParameters}或null
	 */
	public static ECPrivateKeyParameters toParams(PrivateKey privateKey) throws InvalidKeyException {
		return ECKeys.toPrivateParams(privateKey);
	}

	/**
	 * 读取PEM格式的私钥
	 *
	 * @param pemStream pem流
	 * @return {@link PrivateKey}
	 * @see PemKeys#readPemPrivateKey(InputStream)
	 */
	public static PrivateKey readPemPrivateKey(InputStream pemStream) throws GeneralSecurityException, IOException {
		return PemKeys.readPemPrivateKey(pemStream);
	}

	/**
	 * 读取PEM格式的公钥
	 *
	 * @param pemStream pem流
	 * @return {@link PublicKey}
	 * @see PemKeys#readPemPublicKey(InputStream)
	 */
	public static PublicKey readPemPublicKey(InputStream pemStream) throws GeneralSecurityException, IOException {
		return PemKeys.readPemPublicKey(pemStream);
	}

	/**
	 * Java中的PKCS#8格式私钥转换为OpenSSL支持的PKCS#1格式
	 *
	 * @param privateKey PKCS#8格式私钥
	 * @return PKCS#1格式私钥
	 */
	public static byte[] toPkcs1(PrivateKey privateKey) throws IOException {
		final PrivateKeyInfo pkInfo = PrivateKeyInfo.getInstance(privateKey.getEncoded());
		return pkInfo.parsePrivateKey().toASN1Primitive().getEncoded();
	}

	/**
	 * Java中的X.509格式公钥转换为OpenSSL支持的PKCS#1格式
	 *
	 * @param publicKey X.509格式公钥
	 * @return PKCS#1格式公钥
	 */
	public static byte[] toPkcs1(PublicKey publicKey) throws IOException {
		final SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo
			.getInstance(publicKey.getEncoded());
		return spkInfo.parsePublicKey().getEncoded();
	}
}

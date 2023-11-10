package io.polaris.crypto.asymmetric;

import io.polaris.crypto.ECKeys;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 国密SM2非对称算法实现，基于BC库<br>
 * SM2算法只支持公钥加密，私钥解密<br>
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
public class SM2 {

	private final Lock lock = new ReentrantLock();
	private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
	private Digest digest = new SM3Digest();
	private SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
	private SM2Engine engine;
	private SM2Signer signer;

	private ECPrivateKeyParameters privateKeyParams;
	private ECPublicKeyParameters publicKeyParams;

	public SM2(ECPrivateKeyParameters privateKeyParams, ECPublicKeyParameters publicKeyParams) {
		this.privateKeyParams = privateKeyParams;
		this.publicKeyParams = publicKeyParams;
	}

	public SM2(PrivateKey privateKey, PublicKey publicKey) throws InvalidKeyException {
		this((ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(privateKey), (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter(publicKey));
	}

	public SM2(byte[] privateKey, byte[] publicKey) throws GeneralSecurityException {
		this(ECKeys.decodePrivateKeyParams(privateKey), ECKeys.decodePublicKeyParams(publicKey));
	}


	/**
	 * 加密，SM2非对称加密的结果由C1,C2,C3三部分组成，其中：
	 *
	 * <pre>
	 * C1 生成随机数的计算出的椭圆曲线点
	 * C2 密文数据
	 * C3 SM3的摘要值
	 * </pre>
	 *
	 * @param data 被加密的bytes
	 * @return 加密后的bytes
	 */
	public byte[] encrypt(byte[] data) throws CryptoException {
		lock.lock();
		try {
			final SM2Engine engine = getEngine();
			engine.init(true, new ParametersWithRandom(this.publicKeyParams));
			return engine.processBlock(data, 0, data.length);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 使用私钥解密
	 *
	 * @param data SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
	 * @return 加密后的bytes
	 */
	public byte[] decrypt(byte[] data) throws CryptoException {
		lock.lock();
		try {
			final SM2Engine engine = getEngine();
			engine.init(false, this.privateKeyParams);
			return engine.processBlock(data, 0, data.length);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 用私钥对信息生成数字签名，签名格式为ASN1<br>
	 * * 在硬件签名中，返回结果为R+S，可以通过调用{@link io.polaris.crypto.SmUtils#rsAsn1ToPlain(byte[])}方法转换之。
	 *
	 * @param data 加密数据
	 * @return 签名
	 */
	public byte[] sign(byte[] data) throws CryptoException {
		return sign(data, null);
	}

	/**
	 * 用私钥对信息生成数字签名，签名格式为ASN1<br>
	 * 在硬件签名中，返回结果为R+S，可以通过调用{@link io.polaris.crypto.SmUtils#rsAsn1ToPlain(byte[])}方法转换之。
	 *
	 * @param data 被签名的数据数据
	 * @param id   可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
	 * @return 签名
	 */
	public byte[] sign(byte[] data, byte[] id) throws CryptoException {
		lock.lock();
		try {
			final SM2Signer signer = getSigner();
			CipherParameters param = new ParametersWithRandom(this.privateKeyParams);
			if (id != null) {
				param = new ParametersWithID(param, id);
			}
			signer.init(true, param);
			signer.update(data, 0, data.length);
			return signer.generateSignature();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 用公钥检验数字签名的合法性
	 *
	 * @param data 签名后的数据
	 * @param sign 签名
	 * @return 是否验证通过
	 */
	public boolean verify(byte[] data, byte[] sign) {
		return verify(data, sign, null);
	}

	/**
	 * 用公钥检验数字签名的合法性
	 *
	 * @param data 数据签名后的数据
	 * @param sign 签名
	 * @param id   可以为null
	 * @return 是否验证通过
	 */
	public boolean verify(byte[] data, byte[] sign, byte[] id) {
		lock.lock();
		try {
			final SM2Signer signer = getSigner();
			CipherParameters param = this.publicKeyParams;
			if (id != null) {
				param = new ParametersWithID(param, id);
			}
			signer.init(false, param);
			signer.update(data, 0, data.length);
			return signer.verifySignature(sign);
		} finally {
			lock.unlock();
		}
	}

	private SM2Engine getEngine() {
		if (null == this.engine) {
			this.engine = new SM2Engine(this.digest, this.mode);
		}
		this.digest.reset();
		return this.engine;
	}

	private SM2Signer getSigner() {
		if (null == this.signer) {
			this.signer = new SM2Signer(this.encoding, this.digest);
		}
		this.digest.reset();
		return this.signer;
	}

	/**
	 * 设置SM2模式，旧版是C1C2C3，新版本是C1C3C2
	 */
	public SM2 setMode(SM2Engine.Mode mode) {
		this.mode = mode;
		this.engine = null;
		return this;
	}

	/**
	 * 设置Hash算法
	 */
	public SM2 setDigest(Digest digest) {
		this.digest = digest;
		this.engine = null;
		this.signer = null;
		return this;
	}

	/**
	 * 设置DSA signatures的编码
	 */
	public SM2 setEncoding(DSAEncoding encoding) {
		this.encoding = encoding;
		this.signer = null;
		return this;
	}

	/**
	 * 设置DSA signatures的编码为PlainDSAEncoding
	 */
	public SM2 usePlainEncoding() {
		return setEncoding(PlainDSAEncoding.INSTANCE);
	}

	/**
	 * 获得私钥D值（编码后的私钥）
	 *
	 * @return D值
	 */
	public byte[] getD() {
		return BigIntegers.asUnsignedByteArray(32, getDBigInteger());
	}

	/**
	 * 获得私钥D值
	 *
	 * @return D值
	 */
	public BigInteger getDBigInteger() {
		return this.privateKeyParams.getD();
	}

	/**
	 * 获得公钥Q值（编码后的公钥）
	 *
	 * @param isCompressed 是否压缩
	 * @return Q值
	 */
	public byte[] getQ(boolean isCompressed) {
		return this.publicKeyParams.getQ().getEncoded(isCompressed);
	}
}

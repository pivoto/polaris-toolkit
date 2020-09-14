package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.symmetric.AES;
import io.polaris.core.crypto.symmetric.DES;

import java.security.KeyPair;

class DSATest {

	public static void main(String[] args) throws Exception {
		KeyPair keyPair = DSA.getKeyPair();
		byte[] data = "123456".getBytes();
		{
			byte[] sign = DSA.sign(keyPair.getPrivate(), data);
			System.out.println(DSA.verify(keyPair.getPublic(), data, sign));
			System.out.println(DSA.verify(keyPair.getPublic(), "12345".getBytes(), sign));
		}

		{
			byte[] encrypt = AES.encrypt(data, "test".getBytes());
			byte[] decrypt = AES.decrypt(encrypt, "test".getBytes());
			System.out.println(new String(decrypt));
		}

		{
			byte[] encrypt = DES.encrypt(data, "test".getBytes());
			byte[] decrypt = DES.decrypt(encrypt, "test".getBytes());
			System.out.println(new String(decrypt));
		}

	}
}

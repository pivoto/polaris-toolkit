package io.polaris.toolkit.spring.crypto;

import io.polaris.toolkit.spring.core.io.IOUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @version Nov 01, 2021
 * @since 1.8
 */
@Slf4j
@EnableConfigurationProperties(CryptoConfigurationProperties.class)
public class CryptoPropertyResolver {

	@Autowired
	private CryptoConfigurationProperties properties;
	private ConcurrentHashMap<String, DecryptedValue> cache = new ConcurrentHashMap<>();

	public CryptoPropertyResolver(CryptoConfigurationProperties properties) {
		this.properties = properties;
	}


	@PostConstruct
	public void init() {
		{
			String encryptKey = properties.getEncryptKey();
			if (encryptKey == null || encryptKey.trim().length() == 0) {
				String encryptKeyLocation = properties.getEncryptKeyLocation();
				if (encryptKeyLocation != null && encryptKeyLocation.trim().length() != 0) {
					try (InputStream in = IOUtils.getInputStream(encryptKeyLocation)) {
						properties.setEncryptKey(IOUtils.toString(in));
					} catch (IOException e) {
						throw new BeanInitializationException("encrypt key file is wrong", e);
					}
				}
			}
		}
		{
			String decryptKey = properties.getDecryptKey();
			if (decryptKey == null || decryptKey.trim().length() == 0) {
				String decryptKeyLocation = properties.getDecryptKeyLocation();
				if (decryptKeyLocation != null && decryptKeyLocation.trim().length() != 0) {
					try (InputStream in = IOUtils.getInputStream(decryptKeyLocation)) {
						properties.setEncryptKey(IOUtils.toString(in));
					} catch (IOException e) {
						throw new BeanInitializationException("decrypt key file is wrong", e);
					}
				}
			}
		}

		if (properties.getProperty().getPrefix() == null) {
			properties.getProperty().setPrefix("");
		}
		if (properties.getProperty().getSuffix() == null) {
			properties.getProperty().setSuffix("");
		}
		if (properties.getProperty().getKeyPrefix() == null) {
			properties.getProperty().setKeyPrefix("");
		}
		if (properties.getProperty().getKeySuffix() == null) {
			properties.getProperty().setKeySuffix("");
		}
	}

	public String decrypt(CryptoPropertySource source, String name, String value) {
		if (properties == null || value == null) {
			return value;
		}
		// 存在上次解密缓存时直接返回
		DecryptedValue decryptedValue = cache.get(name);
		if (decryptedValue != null && decryptedValue.getEncrypted().equals(value)) {
			return decryptedValue.getDecrypted();
		}
		String prefix = properties.getProperty().getPrefix();
		String suffix = properties.getProperty().getSuffix();
		List<String> includes = properties.getProperty().getIncludes();
		List<String> excludes = properties.getProperty().getExcludes();
		boolean matchValueOnly = CryptoMatchType.VALUE_ONLY == properties.getProperty().getMatchType();
		boolean notMatchValue = !value.startsWith(prefix) || !value.endsWith(suffix);
		if (notMatchValue && matchValueOnly) {
			// not match
			return value;
		}

		boolean matchName = includes == null || includes.isEmpty() || includes.stream().anyMatch(s -> name.matches(s));
		if (matchName) {
			if (excludes != null && !excludes.isEmpty()) {
				matchName = !excludes.stream().anyMatch(s -> name.matches(s));
			}
		}
		if (!matchName) {
			return value;
		}

		String keyPrefix = properties.getProperty().getKeyPrefix();
		String keySuffix = properties.getProperty().getKeySuffix();
		String decryptKey = properties.getDecryptKey();
		String keyName = keyPrefix + name + keySuffix;
		Object keyValue = source.getProperty(keyName);

		if (keyValue == null || keyValue.equals("")) {
			// not match name
			if (notMatchValue) {
				return value;
			}
		} else {
			decryptKey = keyValue.toString();
		}

		String subValue = value.substring(prefix.length(), value.length() - suffix.length());
		if (!properties.isShowOrigin()) {
			log.warn("****** decrypt property: `{}`", name);
		}
		try {
			CryptoAlgorithm algorithm = properties.getAlgorithm();
			String origin = algorithm.getEncryptor().decrypt(decryptKey, subValue);
			if (properties.isShowOrigin()) {
				log.warn("****** decrypt property: `{}` = `{}` ", name, origin);
			}
			// 添加Cache
			this.cache.put(name, new DecryptedValue(value, origin));
			return origin;
		} catch (Exception e) {
			if (properties.isThrowOnFail()) {
				throw new CryptoOperationException("decrypt property `" + name + "` error!", e);
			} else {
				log.error("decrypt property `" + name + "` error!", e);
				return value;
			}
		}

	}

	public String[] cachedKeys() {
		return cache.keySet().toArray(new String[0]);
	}

	void merge(CryptoPropertyResolver resolver) {
		resolver.cache.forEach((k, v) -> this.cache.putIfAbsent(k, v));
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	static class DecryptedValue {
		private String encrypted;
		private String decrypted;

		public DecryptedValue(String encrypted, String decrypted) {
			this.encrypted = encrypted;
			this.decrypted = decrypted;
		}
	}
}

package io.polaris.toolkit.spring.crypto;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties(prefix = ToolkitConstants.TOOLKIT_CRYPTO, ignoreUnknownFields = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CryptoConfigurationProperties {

	/**
	 * 是否启用
	 */
	private boolean enabled = false;
	/**
	 * 是否显示原文
	 */
	private boolean showOrigin = false;
	/**
	 * 解密失败后是否抛出异常
	 */
	private boolean throwOnFail = true;
	/**
	 * 密钥算法
	 */
	private CryptoAlgorithm algorithm = CryptoAlgorithm.RSA;
	/**
	 * 解密密钥(非对称加密方式下通常为公钥)内容(Base64)
	 */
	private String decryptKey;
	/**
	 * 解密密钥(非对称加密方式下通常为公钥)文件路径
	 */
	private String decryptKeyLocation;
	/**
	 * 加密密钥(非对称加密方式下通常为私钥)内容(Base64)
	 */
	private String encryptKey;
	/**
	 * 加密密钥(非对称加密方式下通常为私钥)文件路径
	 */
	private String encryptKeyLocation;

	@NestedConfigurationProperty
	private EnvConfigurationProperties property = new EnvConfigurationProperties();

	@Getter
	@Setter
	@ToString
	@EqualsAndHashCode
	public static class EnvConfigurationProperties {
		/**
		 * 加密属性匹配方式(VALUE_ONLY-仅匹配属性值的前后缀, VALUE_OR_KEY-匹配属性名或属性值的前后缀)
		 */
		private CryptoMatchType matchType = CryptoMatchType.VALUE_OR_KEY;
		/** 加密属性值前缀 */
		private String prefix = "ENC@";
		/** 加密属性值后缀 */
		private String suffix = "";
		/** 加密属性的关联密钥属性名前缀 */
		private String keyPrefix = "";
		/** 加密属性的关联密钥属性名后缀 */
		private String keySuffix = "@key";
		/** 加密属性名匹配范式 */
		private List<String> includes = null;
		/** 加密属性名排除范式 */
		private List<String> excludes = new ArrayList<>(Arrays.asList("^toolkit\\.crypto.*"));
	}
}

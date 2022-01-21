package io.polaris.toolkit.spring.constants;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author Qt
 * @version Nov 01, 2021
 * @since 1.8
 */
public interface ToolkitConstants {

	int ORDER_LOG_ASPECT = Ordered.HIGHEST_PRECEDENCE;
	int ORDER_DATASOURCE_ASPECT = -128;
	int ORDER_TRANSACTION_ASPECT = Ordered.LOWEST_PRECEDENCE - 128;
	int ORDER_TRANSACTION_JTA_ANNOTATION_ASPECT = ORDER_TRANSACTION_ASPECT + 1;
	int ORDER_TRANSACTION_SPRING_ANNOTATION_ASPECT = ORDER_TRANSACTION_ASPECT + 2;
	int ORDER_TRANSACTION_SERVICE_BEAN_ASPECT = ORDER_TRANSACTION_ASPECT + 3;
	int ORDER_TRANSACTION_REPOSITORY_BEAN_ASPECT = ORDER_TRANSACTION_ASPECT + 4;
	int ORDER_DRUID_MONITOR_ASPECT = Ordered.LOWEST_PRECEDENCE;

	String STANDARD_DELIMITER = ",";

	String CRYPTO_PROPERTY_SOURCE_NAME = "cryptoProperties";

	String DYNAMIC_DATASOURCE_DEFAULT_KEY = "default";
	String DYNAMIC_DATASOURCE_BEAN_NAME = "dynamicDataSource";
	String DYNAMIC_DATASOURCE_ASPECT_BEAN_NAME = "dynamicDataSourceAspect";

	// region properties-key

	String TOOLKIT_CRYPTO = "toolkit.crypto";
	String TOOLKIT_CRYPTO_ENABLED = "toolkit.crypto.enabled";
	String TOOLKIT_CRYPTO_ALGORITHM = "toolkit.crypto.algorithm";
	String TOOLKIT_CRYPTO_DECRYPT_KEY = "toolkit.crypto.decryptKey";
	String TOOLKIT_CRYPTO_DECRYPT_KEY2 = "toolkit.crypto.decrypt-key";
	String TOOLKIT_CRYPTO_DECRYPT_KEY_LOCATION = "toolkit.crypto.decryptKeyLocation";
	String TOOLKIT_CRYPTO_DECRYPT_KEY_LOCATION2 = "toolkit.crypto.decrypt-key-location";
	String TOOLKIT_CRYPTO_ENCRYPT_KEY = "toolkit.crypto.encryptKey";
	String TOOLKIT_CRYPTO_ENCRYPT_KEY2 = "toolkit.crypto.encrypt-key";
	String TOOLKIT_CRYPTO_ENCRYPT_KEY_LOCATION = "toolkit.crypto.encryptKeyLocation";
	String TOOLKIT_CRYPTO_ENCRYPT_KEY_LOCATION2 = "toolkit.crypto.encrypt-key-location";
	String TOOLKIT_CRYPTO_PROPERTY_PREFIX = "toolkit.crypto.property.prefix";
	String TOOLKIT_CRYPTO_PROPERTY_SUFFIX = "toolkit.crypto.property.suffix";
	String TOOLKIT_CRYPTO_PROPERTY_INCLUDES = "toolkit.crypto.property.includes";
	String TOOLKIT_CRYPTO_PROPERTY_EXCLUDES = "toolkit.crypto.property.excludes";

	String TOOLKIT_DYNAMIC_DATASOURCE = "toolkit.dynamic.datasource";
	String TOOLKIT_DYNAMIC_DATASOURCE_ENABLED = "toolkit.dynamic.datasource.enabled";

	String TOOLKIT_DYNAMIC_TRANSACTION = "toolkit.dynamic.transaction";
	String TOOLKIT_DYNAMIC_TRANSACTION_ENABLED = "toolkit.dynamic.transaction.enabled";
	String TOOLKIT_DYNAMIC_TRANSACTION_ENABLED_ASPECTJ = "toolkit.dynamic.transaction.enableAspectj";

	// endregion properties-key

}

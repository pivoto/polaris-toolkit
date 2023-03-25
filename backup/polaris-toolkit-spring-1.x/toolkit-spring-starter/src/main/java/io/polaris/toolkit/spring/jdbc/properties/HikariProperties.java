package io.polaris.toolkit.spring.jdbc.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Properties;

/**
 * @author Qt
 * @version Jan 03, 2022
 * @since 1.8
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HikariProperties {
	private String catalog;
	private Long connectionTimeout;
	private Long validationTimeout;
	private Long idleTimeout;
	private Long leakDetectionThreshold;
	private Long maxLifetime;
	private Integer maximumPoolSize;
	private Integer minimumIdle;
	private String username;
	private String password;


	private Long initializationFailTimeout;
	private String connectionInitSql;
	private String connectionTestQuery;
	private String dataSourceClassName;
	private String dataSourceJNDI;
	private String driverClassName;
	private String exceptionOverrideClassName;
	private String jdbcUrl;
	private String poolName;
	private String schema;
	private String transactionIsolation;
	private Boolean autoCommit;
	private Boolean readOnly;
	private Boolean isolateInternalQueries;
	private Boolean registerMbeans;
	private Boolean allowPoolSuspension;
	private Properties dataSourceProperties;
	private Properties healthCheckProperties;

	public HikariProperties() {
		dataSourceProperties = new Properties();
		healthCheckProperties = new Properties();
	}

}

package io.polaris.toolkit.spring.jdbc.properties;

import io.polaris.toolkit.spring.util.BeanUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Qt
 * @version Jan 03, 2022
 * @since 1.8
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DruidProperties {
	private String name;
	private String url;
	private String username;
	private String password;
	private Boolean testWhileIdle;
	private Boolean testOnBorrow;
	private String validationQuery;
	private Boolean useGlobalDataSourceStat;
	private Boolean asyncInit;
	private String filters;
	private Long timeBetweenLogStatsMillis;
	private Integer maxSqlSize;
	private Boolean clearFiltersEnable;
	private Boolean resetStatEnable;
	private Integer notFullTimeoutRetryCount;
	private Long timeBetweenEvictionRunsMillis;
	private Integer maxWaitThreadCount;
	private Integer maxWait;
	private Boolean failFast;
	private Long phyTimeoutMillis;
	private Long phyMaxUseCount;
	private Long minEvictableIdleTimeMillis;
	private Long maxEvictableIdleTimeMillis;
	private Boolean keepAlive;
	private Long keepAliveBetweenTimeMillis;
	private Boolean poolPreparedStatements;
	private Boolean initVariants;
	private Boolean initGlobalVariants;
	private Boolean useUnfairLock;
	private String driverClassName;
	private Integer initialSize;
	private Integer minIdle;
	private Integer maxActive;
	private Boolean killWhenSocketReadTimeout;
	private String connectProperties;
	private Integer maxPoolPreparedStatementPerConnectionSize;
	private String initConnectionSqls;

	public DruidProperties() {
	}

}

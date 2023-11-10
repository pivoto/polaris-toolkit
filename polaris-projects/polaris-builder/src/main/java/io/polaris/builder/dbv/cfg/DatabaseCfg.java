package io.polaris.builder.dbv.cfg;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.Data;

import java.util.*;


/**
 * @author Qt
 */
@Data
@XStreamAlias("database")
public class DatabaseCfg {
	@XStreamAlias("database-type")
	private String databaseType;
	@XStreamAlias("jdbc-driver")
	private String jdbcDriver;
	@XStreamAlias("jdbc-url")
	private String jdbcUrl;
	@XStreamAlias("jdbc-username")
	private String jdbcUsername;
	@XStreamAlias("jdbc-password")
	private String jdbcPassword;
	@XStreamAlias("jdbc-info-properties-path")
	private String jdbcInfoPropertiesPath;

	@XStreamAlias("sqls")
	private List<SqlCfg> sqlmaps = new ArrayList<>();

	@XStreamOmitField
	private Properties jdbcInfoProperties = new Properties();

	@XStreamOmitField
	private Map<String, SqlCfg> sqlmap = new LinkedHashMap<String, SqlCfg>();

	public DatabaseCfg() {
	}

	public Properties getJdbcInfoProperties() {
		if (jdbcInfoProperties == null) {
			jdbcInfoProperties = new Properties();
		}
		jdbcInfoProperties.setProperty("remarks", "true");
		jdbcInfoProperties.setProperty("remarksReporting", "true");
		jdbcInfoProperties.setProperty("useInformationSchema", "true");
		jdbcInfoProperties.setProperty("user", jdbcUsername);
		jdbcInfoProperties.setProperty("password", jdbcPassword);
		return jdbcInfoProperties;
	}
}

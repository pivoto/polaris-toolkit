package io.polaris.toolkit.spring.jdbc.properties;

import io.polaris.toolkit.spring.jdbc.DynamicDataSourceBuilder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @version Jan 03, 2022
 * @since 1.8
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TargetDataSourceProperties {

	// region DataSourceProperties properties

	/** Name of the datasource. Default to "testdb" when using an embedded database. */
	private String name = "testdb";
	/** Whether to generate a random datasource name. */
	private boolean generateUniqueName;
	/**
	 * Fully qualified name of the connection pool implementation to use. By default, it
	 * is auto-detected from the classpath.
	 */
	private Class<? extends DataSource> type;
	/** Fully qualified name of the JDBC driver. Auto-detected based on the URL by default. */
	private String driverClassName;
	/** JDBC URL of the database. */
	private String url;
	/** Login username of the database. */
	private String username;
	/** Login password of the database. */
	private String password;
	/** JNDI location of the datasource. Class, url, username and password are ignored when set. */
	private String jndiName;
	/** Platform to use in the DDL or DML scripts (such as schema-${platform}.sql or data-${platform}.sql). */
	private String platform = "all";
	/** Schema (DDL) script resource references. */
	private List<String> schema;
	/** Username of the database to execute DDL scripts (if different). */
	private String schemaUsername;
	/** Password of the database to execute DDL scripts (if different). */
	private String schemaPassword;
	/** Data (DML) script resource references. */
	private List<String> data;
	/** Username of the database to execute DML scripts (if different). */
	private String dataUsername;
	/** Password of the database to execute DML scripts (if different). */
	private String dataPassword;
	/** Whether to stop if an error occurs while initializing the database. */
	private boolean continueOnError = false;
	/** Statement separator in SQL initialization scripts. */
	private String separator = ";";
	/** SQL scripts encoding. */
	private Charset sqlScriptEncoding;
	private EmbeddedDatabaseConnection embeddedDatabaseConnection = EmbeddedDatabaseConnection.NONE;
	private DataSourceProperties.Xa xa = new DataSourceProperties.Xa();
	private String uniqueName;

	// endregion DataSourceProperties properties


	/**
	 * 多数据源拦截类匹配模式，匹配类使用本数据源(AspectJ匹配语法，支持 and/or/not 逻辑操作符)
	 * <ul>Examples include:
	 * <li>
	 * <code class="code">
	 * org.springframework.beans.*
	 * </code>
	 * This will match any class or interface in the given package.
	 * </li>
	 * <li>
	 * <code class="code">
	 * org.springframework.beans.ITestBean+
	 * </code>
	 * This will match the {@code ITestBean} interface and any class
	 * that implements it.
	 * </li>
	 * </ul>
	 */
	private String classPattern = "";

	/** 特定于Hikari数据源配置, 属性同HikariConfig */
	private HikariProperties hikari;
	/** 特定于Druid数据源配置, 属性同DruidDataSource */
	private DruidProperties druid;
	private Map<String, String> ext = new HashMap<>();

	@Setter(AccessLevel.NONE)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ClassLoader classLoader;
	@Setter(AccessLevel.NONE)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private DataSourceProperties properties;

	public DataSource buildDataSource(ClassLoader classLoader) {
		afterPropertiesSet(classLoader);
		return buildDataSource();
	}

	public void afterPropertiesSet(ClassLoader classLoader) {
		if (this.classLoader == null) {
			this.classLoader = classLoader;
		}
		if (properties == null) {
			properties = asDataSourceProperties(classLoader);
		}
		this.embeddedDatabaseConnection = EmbeddedDatabaseConnection.get(this.classLoader);
	}

	public DataSource buildDataSource() {
		return DynamicDataSourceBuilder.create(classLoader)
				.properties(this)
				.build();
	}

	public DataSourceProperties asDataSourceProperties(ClassLoader classLoader) {
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.setBeanClassLoader(classLoader);
		dataSourceProperties.setName(this.getName());
		dataSourceProperties.setGenerateUniqueName(this.isGenerateUniqueName());
		dataSourceProperties.setType(this.getType());
		dataSourceProperties.setDriverClassName(this.getDriverClassName());
		dataSourceProperties.setUrl(this.getUrl());
		dataSourceProperties.setUsername(this.getUsername());
		dataSourceProperties.setPassword(this.getPassword());
		dataSourceProperties.setJndiName(this.getJndiName());
		dataSourceProperties.setPlatform(this.getPlatform());
		dataSourceProperties.setSchema(this.getSchema());
		dataSourceProperties.setSchemaUsername(this.getSchemaUsername());
		dataSourceProperties.setSchemaPassword(this.getSchemaPassword());
		dataSourceProperties.setData(this.getData());
		dataSourceProperties.setDataUsername(this.getDataUsername());
		dataSourceProperties.setDataPassword(this.getDataPassword());
		dataSourceProperties.setContinueOnError(this.isContinueOnError());
		dataSourceProperties.setSeparator(this.getSeparator());
		dataSourceProperties.setSqlScriptEncoding(this.getSqlScriptEncoding());
		dataSourceProperties.setXa(this.getXa());
		try {
			dataSourceProperties.afterPropertiesSet();
		} catch (Exception ignore) {
		}
		dataSourceProperties.setUsername(dataSourceProperties.determineUsername());
		dataSourceProperties.setPassword(dataSourceProperties.determinePassword());
		dataSourceProperties.setUrl(dataSourceProperties.determineUrl());
		dataSourceProperties.setDriverClassName(dataSourceProperties.determineDriverClassName());
		return dataSourceProperties;
	}

	public DataSourceProperties asDataSourceProperties() {
		return asDataSourceProperties(classLoader);
	}
}

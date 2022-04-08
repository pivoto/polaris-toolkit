package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import io.polaris.toolkit.spring.util.BeanUtils;
import io.polaris.toolkit.spring.util.Binders;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Qt
 * @version Jan 03, 2022
 * @since 1.8
 */
public class DynamicDataSourceBuilder {
	private static final String[] DATA_SOURCE_TYPE_NAMES = new String[]{"com.zaxxer.hikari.HikariDataSource",
			"org.apache.tomcat.jdbc.pool.DataSource", "org.apache.commons.dbcp2.BasicDataSource"};
	private Class<? extends DataSource> type;
	private String poolName;
	private ClassLoader classLoader;
	private Map<String, String> properties = new HashMap<>();
	private TargetDataSourceProperties targetProperties;
	private DataSourceProperties baseProperties;

	private Consumer<DataSource> postProcessor = null;

	public static DynamicDataSourceBuilder create() {
		return new DynamicDataSourceBuilder(null);
	}

	public static DynamicDataSourceBuilder create(ClassLoader classLoader) {
		return new DynamicDataSourceBuilder(classLoader);
	}

	private DynamicDataSourceBuilder(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private EmbeddedDatabase buildEmbeddedDatabase() {
		EmbeddedDatabaseType embeddedDatabaseType = EmbeddedDatabaseConnection.get(classLoader).getType();
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder().setType(embeddedDatabaseType);
		if (baseProperties != null) {
			builder.setName(baseProperties.determineDatabaseName());
			if (baseProperties.getSqlScriptEncoding() != null) {
				builder.setScriptEncoding(baseProperties.getSqlScriptEncoding().name());
			}
			if (!CollectionUtils.isEmpty(baseProperties.getSchema())) {
				for (String script : baseProperties.getSchema()) {
					builder.addScript(script);
				}
			}
			if (!CollectionUtils.isEmpty(baseProperties.getData())) {
				for (String script : baseProperties.getData()) {
					builder.addScript(script);
				}
			}
		} else {
			builder.setName("testdb");
		}
		EmbeddedDatabase embeddedDatabase = builder.build();
		// bind ext properties
		if (targetProperties != null && !CollectionUtils.isEmpty(targetProperties.getExt())) {
			Binders.bind(targetProperties.getExt(), embeddedDatabase, "", null);
		}
		// bind standard properties
		if (baseProperties != null) {
			BeanUtils.copyPropertiesQuietly(baseProperties, embeddedDatabase, null, (name, value) -> value != null);
		}
		return embeddedDatabase;
	}

	public DataSource build() {
		Class<? extends DataSource> type = determineType();
		if (type == EmbeddedDatabase.class) {
			return buildEmbeddedDatabase();
		}
		DataSource result = BeanUtils.instantiateClass(type);
		maybeGetDriverClassName();
		maybeGetExtProperties(type);
		bind(result);
		return result;
	}

	public Class<? extends DataSource> determineType() {
		Class<? extends DataSource> type = this.type;
		if (type == null) {
			Class<? extends DataSource> foundType = findType(this.classLoader);
			EmbeddedDatabaseType embeddedDatabaseType = EmbeddedDatabaseConnection.get(classLoader).getType();
			if (embeddedDatabaseType != null
					&& !StringUtils.hasText(this.properties.get("url"))
					&& foundType == null) {
				return EmbeddedDatabase.class;
			}
			type = foundType;
		}
		if (type != null) {
			return type;
		}
		throw new IllegalStateException("未知数据源类型");
	}

	private void maybeGetExtProperties(Class<? extends DataSource> type) {
		if ("com.zaxxer.hikari.HikariDataSource".equals(type.getName())) {
			if (targetProperties != null) {
				this.ext(targetProperties.getHikari());
			}
			if (StringUtils.hasText(poolName)) {
				postProcessor = ds -> {
					ReflectionUtils.invokeMethod(
							ReflectionUtils.findMethod(type, "setPoolName", new Class[]{String.class}),
							ds, poolName);
				};
			}
		} else if ("com.alibaba.druid.pool.DruidDataSource".equals(type.getName())) {
			if (targetProperties != null) {
				this.ext(targetProperties.getDruid());
			}
		} else if ("org.apache.commons.dbcp2.BasicDataSource".equals(type.getName())) {
			final String url = this.properties.get("url");
			final DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(url);
			final String validationQuery = databaseDriver.getValidationQuery();
			if (validationQuery != null) {
				postProcessor = ds -> {
					ReflectionUtils.invokeMethod(
							ReflectionUtils.findMethod(type, "setTestOnBorrow", new Class[]{boolean.class}),
							ds, true);
					ReflectionUtils.invokeMethod(
							ReflectionUtils.findMethod(type, "setValidationQuery", new Class[]{String.class}),
							ds, validationQuery);
				};
			}
		} else if ("org.apache.tomcat.jdbc.pool.DataSource".equals(type.getName())) {
			// nothing
		} else {
			// nothing
		}
	}

	private void maybeGetDriverClassName() {
		if (!this.properties.containsKey("driverClassName") && this.properties.containsKey("url")) {
			String url = this.properties.get("url");
			String driverClass = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
			this.properties.put("driverClassName", driverClass);
		}
	}

	private void bind(DataSource result) {
		if (this.properties != null) {
			Map<String, List<String>> aliasMap = new HashMap<>();
			aliasMap.put("driver-class-name", Arrays.asList("driver-class"));
			aliasMap.put("url", Arrays.asList("jdbc-url"));
			aliasMap.put("username", Arrays.asList("user"));
			Binders.bind(this.properties, result, "", aliasMap);
		}
		if (baseProperties != null) {
			// bind standard properties
			BeanUtils.copyPropertiesQuietly(baseProperties, result, null, (name, value) -> value != null);
		}
		if (postProcessor != null) {
			postProcessor.accept(result);
		}
	}

	public DynamicDataSourceBuilder properties(TargetDataSourceProperties properties) {
		if (properties != null) {
			this.targetProperties = properties;
			DataSourceProperties dataSourceProperties = properties.getProperties();
			if (dataSourceProperties == null) {
				dataSourceProperties = properties.asDataSourceProperties();
			}
			this.ext(properties.getExt());
			this.properties(dataSourceProperties);
		}
		return this;
	}

	private DynamicDataSourceBuilder ext(Object... ext) {
		if (ext != null) {
			Map<String, String> properties = new HashMap<>();
			for (Object prop : ext) {
				if (prop != null) {
					Binders.toProperties(prop, properties, "");
				}
			}
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				if (!this.properties.containsKey(entry.getKey())) {
					this.properties.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return this;
	}

	public DynamicDataSourceBuilder properties(DataSourceProperties properties) {
		if (properties != null) {
			this.baseProperties = properties;
			this.type(properties.getType())
					.poolName(properties.getName())
					.driverClassName(properties.determineDriverClassName())
					.url(properties.determineUrl())
					.username(properties.determineUsername())
					.password(properties.determinePassword());
		}
		return this;
	}

	public DynamicDataSourceBuilder properties(Map<String, String> properties) {
		if (properties != null) {
			this.properties.putAll(properties);
		}
		return this;
	}

	public DynamicDataSourceBuilder type(Class<? extends DataSource> type) {
		this.type = type;
		return this;
	}

	public DynamicDataSourceBuilder poolName(String name) {
		this.poolName = name;
		return this;
	}


	public DynamicDataSourceBuilder url(String url) {
		this.properties.put("url", url);
		return this;
	}


	public DynamicDataSourceBuilder driverClassName(String driverClassName) {
		this.properties.put("driverClassName", driverClassName);
		return this;
	}

	public DynamicDataSourceBuilder username(String username) {
		this.properties.put("username", username);
		return this;
	}

	public DynamicDataSourceBuilder password(String password) {
		this.properties.put("password", password);
		return this;
	}

	public static Class<? extends DataSource> findType(ClassLoader classLoader) {
		for (String name : DATA_SOURCE_TYPE_NAMES) {
			try {
				return (Class<? extends DataSource>) ClassUtils.forName(name, classLoader);
			} catch (Exception ex) {
				// Swallow and continue
			}
		}
		return null;
	}


}

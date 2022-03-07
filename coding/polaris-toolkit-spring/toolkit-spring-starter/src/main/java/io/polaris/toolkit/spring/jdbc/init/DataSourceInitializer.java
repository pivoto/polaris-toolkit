package io.polaris.toolkit.spring.jdbc.init;

import io.polaris.toolkit.core.util.Tuple;
import io.polaris.toolkit.spring.jdbc.DynamicDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.config.SortedResourcesFactoryBean;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @version Mar 04, 2022
 * @since 1.8
 */
@Slf4j
class DataSourceInitializer {
	@Getter
	private final DynamicDataSource dynamicDataSource;
	private final ResourceLoader resourceLoader;
	private final Map<String, Tuple<DataSource, DataSourceProperties>> dataSourceProperties;

	public DataSourceInitializer(DynamicDataSource dynamicDataSource, ApplicationContext applicationContext) {
		this.dynamicDataSource = dynamicDataSource;
		this.resourceLoader = (applicationContext != null) ? applicationContext : new DefaultResourceLoader(null);
		Map<String, Tuple<DataSource, DataSourceProperties>> dataSourceProperties = dynamicDataSource.getDataSourceProperties();
		this.dataSourceProperties = dataSourceProperties;
	}

	boolean createSchema() {
		boolean created = false;
		for (Map.Entry<String, Tuple<DataSource, DataSourceProperties>> entry : dataSourceProperties.entrySet()) {
			String dsName = entry.getKey();
			Tuple<DataSource, DataSourceProperties> tuple = entry.getValue();
			DataSource ds = tuple.getFirst();
			DataSourceProperties properties = tuple.getSecond();
			List<Resource> scripts = getScripts(properties, properties.getSchema(), "schema-" + dsName);
			if (!scripts.isEmpty()) {
				if (!isEnabled(ds, properties)) {
					log.debug("DynamicDataSource[{}] initialization disabled (not running DDL scripts)", dsName);
					continue;
				}
				String username = properties.getSchemaUsername();
				String password = properties.getSchemaPassword();
				log.info("执行数据源[{}]的初始化DDL脚本", dsName);
				runScripts(ds, properties, scripts, username, password);
				created = true;
			}
		}
		return created;
	}

	void initSchema() {
		for (Map.Entry<String, Tuple<DataSource, DataSourceProperties>> entry : dataSourceProperties.entrySet()) {
			String dsName = entry.getKey();
			Tuple<DataSource, DataSourceProperties> tuple = entry.getValue();
			DataSource ds = tuple.getFirst();
			DataSourceProperties properties = tuple.getSecond();
			List<Resource> scripts = getScripts(properties, properties.getData(), "data-" + dsName);
			if (!scripts.isEmpty()) {
				if (!isEnabled(ds, properties)) {
					log.debug("DynamicDataSource[{}] initialization disabled (not running data scripts)", dsName);
					continue;
				}
				String username = properties.getDataUsername();
				String password = properties.getDataPassword();
				log.info("执行数据源[{}]的初始化Data脚本", dsName);
				runScripts(ds, properties, scripts, username, password);
			}
		}
	}

	private boolean isEnabled(DataSource dataSource, DataSourceProperties properties) {
		DataSourceInitializationMode mode = properties.getInitializationMode();
		if (mode == DataSourceInitializationMode.NEVER) {
			return false;
		}
		if (mode == DataSourceInitializationMode.EMBEDDED && !isEmbedded(dataSource)) {
			return false;
		}
		return true;
	}

	private boolean isEmbedded(DataSource dataSource) {
		try {
			return EmbeddedDatabaseConnection.isEmbedded(dataSource);
		} catch (Exception ex) {
			log.debug("Could not determine if datasource is embedded", ex);
			return false;
		}
	}

	private List<Resource> getScripts(DataSourceProperties properties, List<String> resources, String fallback) {
		if (resources != null) {
			return getResources(resources);
		}
		String platform = properties.getPlatform();
		List<String> fallbackResources = new ArrayList<>();
		fallbackResources.add("classpath*:" + fallback + "-" + platform + ".sql");
		fallbackResources.add("classpath*:" + fallback + ".sql");
		return getResources(fallbackResources);
	}

	private List<Resource> getResources(List<String> locations) {
		List<Resource> resources = new ArrayList<>();
		for (String location : locations) {
			for (Resource resource : doGetResources(location)) {
				if (resource.exists()) {
					resources.add(resource);
				}
			}
		}
		return resources;
	}

	private Resource[] doGetResources(String location) {
		try {
			SortedResourcesFactoryBean factory = new SortedResourcesFactoryBean(this.resourceLoader,
					Collections.singletonList(location));
			factory.afterPropertiesSet();
			return factory.getObject();
		} catch (Exception ex) {
			throw new IllegalStateException("Unable to load resources from " + location, ex);
		}
	}

	private void runScripts(DataSource dataSource, DataSourceProperties properties, List<Resource> resources, String username, String password) {
		if (resources.isEmpty()) {
			return;
		}
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setContinueOnError(properties.isContinueOnError());
		populator.setSeparator(properties.getSeparator());
		if (properties.getSqlScriptEncoding() != null) {
			populator.setSqlScriptEncoding(properties.getSqlScriptEncoding().name());
		}
		for (Resource resource : resources) {
			populator.addScript(resource);
		}
		if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
			dataSource = DataSourceBuilder.create(properties.getClassLoader())
					.driverClassName(properties.determineDriverClassName()).url(properties.determineUrl())
					.username(username).password(password).build();
		}
		DatabasePopulatorUtils.execute(populator, dataSource);
	}
}

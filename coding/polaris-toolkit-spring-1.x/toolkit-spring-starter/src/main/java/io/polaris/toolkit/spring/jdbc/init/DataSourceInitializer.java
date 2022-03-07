package io.polaris.toolkit.spring.jdbc.init;

import io.polaris.toolkit.core.util.Tuple;
import io.polaris.toolkit.spring.jdbc.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceInitializedEvent;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.config.SortedResourcesFactoryBean;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
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
public class DataSourceInitializer implements ApplicationListener<DataSourceInitializedEvent>, ApplicationContextAware {
	private final DynamicDataSource dynamicDataSource;
	private ApplicationContext applicationContext;
	private final Map<String, Tuple<DataSource, DataSourceProperties>> dataSourceProperties;
	private boolean initialized;

	public DataSourceInitializer(DynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
		Map<String, Tuple<DataSource, DataSourceProperties>> dataSourceProperties = dynamicDataSource.getDataSourceProperties();
		this.dataSourceProperties = dataSourceProperties;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void init() {
		runSchemaScripts();
	}

	private void runSchemaScripts() {
		for (Map.Entry<String, Tuple<DataSource, DataSourceProperties>> entry : dataSourceProperties.entrySet()) {
			String dsName = entry.getKey();
			Tuple<DataSource, DataSourceProperties> tuple = entry.getValue();
			DataSource ds = tuple.getFirst();
			DataSourceProperties properties = tuple.getSecond();

			if (!properties.isInitialize()) {
				log.debug("DynamicDataSource[{}] initialization disabled (not running DDL scripts)", dsName);
				continue;
			}

			List<Resource> scripts = getScripts(properties, properties.getSchema(), "schema-" + dsName);
			if (!scripts.isEmpty()) {
				String username = properties.getSchemaUsername();
				String password = properties.getSchemaPassword();
				runScripts(ds, properties, scripts, username, password);
			}
		}
		try {
			this.applicationContext.publishEvent(new DataSourceInitializedEvent(this.dynamicDataSource));
			// The listener might not be registered yet, so don't rely on it.
			if (!this.initialized) {
				runDataScripts();
				this.initialized = true;
			}
		} catch (IllegalStateException ex) {
			log.warn("Could not send event to complete DataSource initialization (" + ex.getMessage() + ")");
		}
	}

	@Override
	public void onApplicationEvent(DataSourceInitializedEvent event) {
		// NOTE the event can happen more than once and
		// the event datasource is not used here
		if (!this.initialized) {
			runDataScripts();
			this.initialized = true;
		}
	}

	private void runDataScripts() {
		for (Map.Entry<String, Tuple<DataSource, DataSourceProperties>> entry : dataSourceProperties.entrySet()) {
			String dsName = entry.getKey();
			Tuple<DataSource, DataSourceProperties> tuple = entry.getValue();
			DataSource ds = tuple.getFirst();
			DataSourceProperties properties = tuple.getSecond();

			if (!properties.isInitialize()) {
				log.debug("DynamicDataSource[{}] initialization disabled (not running data scripts)", dsName);
				continue;
			}

			List<Resource> scripts = getScripts(properties, properties.getData(), "data-" + dsName);
			String username = properties.getDataUsername();
			String password = properties.getDataPassword();
			log.info("执行数据源[{}]的初始化Data脚本", dsName);
			runScripts(ds, properties, scripts, username, password);
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
		List<Resource> resources = new ArrayList<Resource>();
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
			SortedResourcesFactoryBean factory = new SortedResourcesFactoryBean(
					this.applicationContext, Collections.singletonList(location));
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
					.driverClassName(properties.determineDriverClassName())
					.url(properties.determineUrl()).username(username)
					.password(password).build();
		}
		DatabasePopulatorUtils.execute(populator, dataSource);
	}

}

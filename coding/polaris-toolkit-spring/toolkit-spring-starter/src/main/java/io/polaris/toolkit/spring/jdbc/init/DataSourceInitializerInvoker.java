package io.polaris.toolkit.spring.jdbc.init;

import io.polaris.toolkit.spring.jdbc.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceSchemaCreatedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

/**
 * @author Qt
 * @version Mar 04, 2022
 * @since 1.8
 */
@Slf4j
public class DataSourceInitializerInvoker implements ApplicationListener<DataSourceSchemaCreatedEvent>, InitializingBean, ApplicationContextAware {
	private final DynamicDataSource dataSource;
	private ApplicationContext applicationContext;
	private boolean initialized;
	private DataSourceInitializer dataSourceInitializer;

	public DataSourceInitializerInvoker(DynamicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() {
		DataSourceInitializer initializer = getDataSourceInitializer();
		if (initializer != null) {
			boolean schemaCreated = this.dataSourceInitializer.createSchema();
			if (schemaCreated) {
				initialize(initializer);
			}
		}
	}


	private void initialize(DataSourceInitializer initializer) {
		try {
			this.applicationContext.publishEvent(new DataSourceSchemaCreatedEvent(initializer.getDynamicDataSource()));
			// The listener might not be registered yet, so don't rely on it.
			if (!this.initialized) {
				this.dataSourceInitializer.initSchema();
				this.initialized = true;
			}
		} catch (IllegalStateException ex) {
			log.warn("Could not send event to complete DataSource initialization ({})",
					ex.getMessage());
		}
	}

	@Override
	public void onApplicationEvent(DataSourceSchemaCreatedEvent event) {
		// NOTE the event can happen more than once and
		// the event datasource is not used here
		DataSourceInitializer initializer = getDataSourceInitializer();
		if (!this.initialized && initializer != null) {
			initializer.initSchema();
			this.initialized = true;
		}
	}

	private DataSourceInitializer getDataSourceInitializer() {
		if (this.dataSourceInitializer == null) {
			if (dataSource != null) {
				this.dataSourceInitializer = new DataSourceInitializer(dataSource, this.applicationContext);
			}
		}
		return this.dataSourceInitializer;
	}
}

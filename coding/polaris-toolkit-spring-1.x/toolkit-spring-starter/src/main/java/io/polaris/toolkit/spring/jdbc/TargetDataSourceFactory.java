package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

/**
 * @author Qt
 * @version Jan 04, 2022
 * @since 1.8
 */
public class TargetDataSourceFactory implements FactoryBean<DataSource> {

	private final String beanName;
	private final DynamicDataSourceBuilder builder;
	private DataSource dataSource;

	public TargetDataSourceFactory(String beanName, TargetDataSourceProperties properties) {
		this.beanName = beanName;
		this.builder = DynamicDataSourceBuilder.create(properties.getClassLoader()).properties(properties);
	}

	@Override
	public DataSource getObject() throws Exception {
		synchronized (builder) {
			if (dataSource != null) {
				return dataSource;
			}
			DataSource dataSource = builder.build();
			this.dataSource = dataSource;
			DynamicDataSourceKeys.add(beanName);
			return dataSource;
		}
	}

	@Override
	public Class<?> getObjectType() {
		return builder.determineType();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}

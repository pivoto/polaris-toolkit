package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author Qt
 * @version Jan 04, 2022
 * @since 1.8
 */
public class DynamicDataSourceFactory implements FactoryBean<DataSource> {

	private final DynamicDataSourceProperties properties;
	private final Map<String, DataSource> multiDataSources;
	private final Map<String, TargetDataSourceProperties> multiProperties;
	private final String defaultKey;
	private DataSource dataSource;

	public DynamicDataSourceFactory(DynamicDataSourceProperties properties, String defaultKey
			, Map<String, TargetDataSourceProperties> multiProperties
			, Map<String, DataSource> multiDataSources) {
		this.properties = properties;
		this.multiDataSources = multiDataSources;
		this.defaultKey = defaultKey;
		this.multiProperties = multiProperties;
	}

	@Override
	public DataSource getObject() throws Exception {
		synchronized (properties) {
			if (dataSource != null) {
				return dataSource;
			}
			dataSource = getDynamicDataSource();
			if (properties.isEnableTransactionDelegate()) {
				dataSource = new TransactionAwareDataSourceProxy(dataSource);
			}
			return dataSource;
		}
	}

	private DynamicDataSource getDynamicDataSource() {
		DynamicDataSource dynamicDataSource = new DynamicDataSource(multiDataSources, defaultKey,multiProperties);
		// factory bean 不执行`initializeBean`方法
		dynamicDataSource.afterPropertiesSet();
		return dynamicDataSource;
	}

	@Override
	public Class<?> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}

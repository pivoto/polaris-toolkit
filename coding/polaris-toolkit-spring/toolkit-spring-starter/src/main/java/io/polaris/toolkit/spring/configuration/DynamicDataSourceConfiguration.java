package io.polaris.toolkit.spring.configuration;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.jdbc.DynamicDataSource;
import io.polaris.toolkit.spring.jdbc.DynamicDataSourceProperties;
import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */
@Configuration
@EnableConfigurationProperties(DynamicDataSourceProperties.class)
@Slf4j
@Deprecated
public class DynamicDataSourceConfiguration implements BeanClassLoaderAware {

	private DynamicDataSourceProperties properties;
	private ClassLoader classLoader;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	public DynamicDataSourceConfiguration(DynamicDataSourceProperties properties) {
		this.properties = properties;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	DataSource asTransactionDelegate(DataSource ds) {
		if (!this.properties.isEnableTransactionDelegate()) {
			return ds;
		}
		return new TransactionAwareDataSourceProxy(ds);
	}

	@Bean
	public DataSource dynamicDataSource() {
		DataSource dynamicDataSource = null;
		if (!this.properties.isMultiple()) {
			TargetDataSourceProperties primary = this.properties.getPrimary();
			if (primary == null) {
				String primaryName = this.properties.getPrimaryName();
				if (StringUtils.hasText(primaryName)) {
					primary = this.properties.getTargetDataSourceProperties(primaryName);
				}
			}
			if (primary == null) {
				throw new BeanInitializationException("创建数据源实例失败！未启用多数据源配置且未配置主数据源信息");
			}
			dynamicDataSource = primary.buildDataSource(classLoader);
		} else {
			Map<String, DataSource> targetDataSources = new HashMap<>();
			String defaultTargetDataSource = null;

			TargetDataSourceProperties primary = this.properties.getPrimary();
			Map<String, TargetDataSourceProperties> targets = this.properties.getTargets();

			Set<String> keys = new HashSet<>();
			if (StringUtils.hasText(this.properties.getPrimaryName())) {
				defaultTargetDataSource = this.properties.getPrimaryName();
				keys.add(defaultTargetDataSource);
				if (!targets.containsKey(defaultTargetDataSource)) {
					if (primary != null) {
						targets.put(defaultTargetDataSource, primary);
					} else {
						throw new BeanInitializationException("创建数据源实例失败！主数据源配置缺失");
					}
				}
			} else if (primary != null) {
				if (targets.containsKey(ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY)) {
					defaultTargetDataSource = ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY
							+ "@" + UUID.randomUUID();
				} else {
					defaultTargetDataSource = ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY;
				}
				keys.add(defaultTargetDataSource);
				targets.put(defaultTargetDataSource, primary);
			}

			if (this.properties.isEnableAllTargets()) {
				for (Map.Entry<String, TargetDataSourceProperties> entry : targets.entrySet()) {
					String name = entry.getKey();
					DataSource dataSource = entry.getValue().buildDataSource(classLoader);
					targetDataSources.put(name, dataSource);
				}
			} else {
				String names = this.properties.getTargetNames();
				if (!StringUtils.hasText(names)) {
					throw new BeanInitializationException("无法创建多数据源实例！未指定数据源名称列表");
				}
				String[] nameArray = StringUtils.delimitedListToStringArray(names, ToolkitConstants.STANDARD_DELIMITER);
				for (String name : nameArray) {
					keys.add(name);
				}
				for (String name : keys) {
					TargetDataSourceProperties dataSourceProperties = targets.get(name);
					if (dataSourceProperties == null) {
						throw new BeanInitializationException("未配置目标数据源：" + name);
					}
					DataSource dataSource = dataSourceProperties.buildDataSource(classLoader);
					targetDataSources.put(name, dataSource);
				}
			}
			dynamicDataSource = new DynamicDataSource(targetDataSources, defaultTargetDataSource, targets);
		}
		return asTransactionDelegate(dynamicDataSource);
	}

}

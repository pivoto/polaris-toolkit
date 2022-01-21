package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
@ConfigurationProperties(prefix = ToolkitConstants.TOOLKIT_DYNAMIC_DATASOURCE, ignoreUnknownFields = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DynamicDataSourceProperties implements BeanClassLoaderAware, InitializingBean {

	/** 是否启用 */
	private boolean enabled = false;
	/** 是否多数据源 */
	private boolean multiple = true;
	/** 注册的Bean名称 */
	private String beanName = ToolkitConstants.DYNAMIC_DATASOURCE_BEAN_NAME;
	/** 是否标识为 @Primary */
	private boolean registerPrimary = true;
	/** 是否将所有数据源都注册为Bean */
	private boolean registerAllTargets = true;
	/** 是否启用AspectJ */
	private boolean enableAspectj = true;
	/** 是否启用已配置的所有目标数据源,启用则忽略名称列表，否则须指定名称列表 */
	private boolean enableAllTargets = false;
	/** 启用的多数据源名称列表，逗号分隔 */
	private String targetNames;
	/** 主数据源名称 */
	private String primaryName;
	/** 主数据源配置 */
	private TargetDataSourceProperties primary;
	/** 多数据源下的各数据源配置, 必须先指定多数据源名称列表 */
	private Map<String, TargetDataSourceProperties> targets = new HashMap<>();
	/** 是否使用TransactionAwareDataSourceProxy包装 */
	private boolean enableTransactionDelegate = false;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private ClassLoader classLoader;

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (primary != null) {
			primary.afterPropertiesSet(classLoader);
		}
		if (targets != null) {
			for (TargetDataSourceProperties targetDataSourceProperties : targets.values()) {
				targetDataSourceProperties.afterPropertiesSet(classLoader);
			}
		}
	}

	public TargetDataSourceProperties getTargetDataSourceProperties(String key) {
		TargetDataSourceProperties properties = targets.get(key);
		return properties == null ? null : properties;
	}


	public DataSourceProperties asDataSourceProperties(TargetDataSourceProperties sourceProperties) {
		return sourceProperties.asDataSourceProperties(classLoader);
	}

}

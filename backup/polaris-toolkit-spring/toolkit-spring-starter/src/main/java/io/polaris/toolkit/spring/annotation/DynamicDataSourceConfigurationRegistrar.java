package io.polaris.toolkit.spring.annotation;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.jdbc.DynamicDataSourceAspect;
import io.polaris.toolkit.spring.jdbc.DynamicDataSourceBuilder;
import io.polaris.toolkit.spring.jdbc.DynamicDataSourceFactory;
import io.polaris.toolkit.spring.jdbc.init.DataSourceInitializerInvoker;
import io.polaris.toolkit.spring.jdbc.init.DynamicDataSourceInitializerPostProcessor;
import io.polaris.toolkit.spring.jdbc.DynamicDataSourceMethodInterceptor;
import io.polaris.toolkit.spring.jdbc.DynamicDataSourceProperties;
import io.polaris.toolkit.spring.jdbc.TargetDataSource;
import io.polaris.toolkit.spring.jdbc.TargetDataSourceFactory;
import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import io.polaris.toolkit.spring.support.AnnotationStaticMethodMatcherPointcut;
import io.polaris.toolkit.spring.support.TypePatternClassPointcut;
import io.polaris.toolkit.spring.util.Binders;
import io.polaris.toolkit.spring.util.Contexts;
import org.aopalliance.aop.Advice;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
class DynamicDataSourceConfigurationRegistrar extends AbstractImportBeanDefinitionRegistrar
		implements BeanClassLoaderAware {
	private ClassLoader classLoader;

	@Override
	public void doRegisterBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// 已经注册过一次，防止同时使用注解与配置检测
		if (registry.containsBeanDefinition(DynamicDataSourceInitializerPostProcessor.class.getName())) {
			return;
		}
		for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
			if (DynamicDataSourceFactory.class.getName().equals(beanDefinition.getBeanClassName())
				|| TargetDataSourceFactory.class.getName().equals(beanDefinition.getBeanClassName())) {
				return;
			}
		}
		Environment environment = Contexts.getEnvironment();
		DynamicDataSourceProperties properties = Binders.bind(environment,
				DynamicDataSourceProperties.class, ToolkitConstants.TOOLKIT_DYNAMIC_DATASOURCE);
		properties.setBeanClassLoader(classLoader);
		try {
			properties.afterPropertiesSet();
		} catch (Exception ignore) {
		}

		if (!properties.isMultiple()) {
			registerSingle(registry, properties);
		} else {
			registerMultiple(registry, properties);

			boolean enableAspectJAutoProxy = isEnableAspectJAutoProxy(importingClassMetadata, registry);
			registerAnnotationAspect(enableAspectJAutoProxy, registry, properties);
		}
		// dynamic dataSource initializer
		registry.registerBeanDefinition(DynamicDataSourceInitializerPostProcessor.class.getName(),
				BeanDefinitionBuilder.genericBeanDefinition(DynamicDataSourceInitializerPostProcessor.class)
						.setRole(BeanDefinition.ROLE_SUPPORT)
						.getBeanDefinition());
	}

	private void registerSingle(BeanDefinitionRegistry registry, DynamicDataSourceProperties properties) {
		TargetDataSourceProperties primary = properties.getPrimary();
		if (primary == null) {
			String primaryName = properties.getPrimaryName();
			if (StringUtils.hasText(primaryName)) {
				primary = properties.getTargetDataSourceProperties(primaryName);
			}
		}
		if (primary == null) {
			throw new BeanInitializationException("创建数据源实例失败！未启用多数据源配置且未配置主数据源信息");
		}
		String beanName = properties.getBeanName();
		if (StringUtils.hasText(beanName)) {
			beanName = uniqueBeanName(registry, ToolkitConstants.DYNAMIC_DATASOURCE_BEAN_NAME);
		}
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(TargetDataSourceFactory.class)
				.addConstructorArgValue(beanName)
				.addConstructorArgValue(primary)
				.setPrimary(properties.isRegisterPrimary())
				.setRole(BeanDefinition.ROLE_SUPPORT);
		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
		// initializer
		registry.registerBeanDefinition(DataSourceInitializerInvoker.class.getName(),
				BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializerInvoker.class)
						.addConstructorArgReference(beanName)
						.getBeanDefinition());
	}

	private void registerMultiple(BeanDefinitionRegistry registry, DynamicDataSourceProperties properties) {
		String defaultTargetDataSource = null;
		Map<String, TargetDataSourceProperties> targetProperties = new HashMap<>();

		TargetDataSourceProperties primary = properties.getPrimary();
		Map<String, TargetDataSourceProperties> targets = properties.getTargets();
		String beanName = properties.getBeanName();
		if (!StringUtils.hasText(beanName)) {
			beanName = uniqueBeanName(registry, ToolkitConstants.DYNAMIC_DATASOURCE_BEAN_NAME);
		}
		if (StringUtils.hasText(properties.getPrimaryName())) {
			defaultTargetDataSource = properties.getPrimaryName();
			if (!targets.containsKey(defaultTargetDataSource)) {
				if (primary != null) {
					targetProperties.put(defaultTargetDataSource, primary);
				} else {
					throw new BeanInitializationException("创建数据源实例失败！主数据源" + defaultTargetDataSource + "未配置");
				}
			}
		} else if (primary != null) {
			String primaryName = ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY;
			int i = 1;
			while (targets.containsKey(primaryName)) {
				primaryName = ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY + "." + (i++);
			}
			defaultTargetDataSource = primaryName;
			targetProperties.put(defaultTargetDataSource, primary);
		}

		if (properties.isEnableAllTargets()) {
			for (Map.Entry<String, TargetDataSourceProperties> entry : targets.entrySet()) {
				targetProperties.put(entry.getKey(), entry.getValue());
			}
		} else {
			String names = properties.getTargetNames();
			if (!StringUtils.hasText(names)) {
				throw new BeanInitializationException("无法创建多数据源实例！未指定数据源名称列表");
			}
			String[] nameArray = StringUtils.delimitedListToStringArray(names, ToolkitConstants.STANDARD_DELIMITER);
			for (String name : nameArray) {
				TargetDataSourceProperties dataSourceProperties = targets.get(name);
				if (dataSourceProperties == null) {
					throw new BeanInitializationException("未配置目标数据源：" + name);
				}
				targetProperties.put(name, dataSourceProperties);
			}
		}

		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(DynamicDataSourceFactory.class)
				.setPrimary(properties.isRegisterPrimary())
				.setRole(BeanDefinition.ROLE_SUPPORT)
				.addConstructorArgValue(properties)
				.addConstructorArgValue(defaultTargetDataSource)
				.addConstructorArgValue(targetProperties);
		if (properties.isRegisterAllTargets()) {
			ManagedMap<Object, Object> managedMap = new ManagedMap<>();
			managedMap.setKeyTypeName(String.class.getName());
			managedMap.setValueTypeName(DataSource.class.getName());
			for (Map.Entry<String, TargetDataSourceProperties> entry : targetProperties.entrySet()) {
				String key = entry.getKey();
				String dsBeanName = beanName + "." + key;
				TargetDataSourceProperties value = entry.getValue();
				BeanDefinition beanDefinition = BeanDefinitionBuilder
						.genericBeanDefinition(TargetDataSourceFactory.class)
						.addConstructorArgValue(dsBeanName)
						.addConstructorArgValue(value)
						.setRole(BeanDefinition.ROLE_SUPPORT)
						.getBeanDefinition();
				registry.registerBeanDefinition(dsBeanName, beanDefinition);
				managedMap.put(key, new RuntimeBeanReference(dsBeanName));
				if (StringUtils.hasText(value.getClassPattern())) {
					registerClassPatternAspectBean(registry, key, value.getClassPattern());
				}
			}
			builder.addConstructorArgValue(managedMap);
		} else {
			Map<String, DataSource> targetDataSources = new HashMap<>();
			for (Map.Entry<String, TargetDataSourceProperties> entry : targetProperties.entrySet()) {
				TargetDataSourceProperties value = entry.getValue();
				DataSource dataSource = DynamicDataSourceBuilder.create(value.getClassLoader())
						.properties(value).build();
				targetDataSources.put(entry.getKey(), dataSource);
				if (StringUtils.hasText(value.getClassPattern())) {
					registerClassPatternAspectBean(registry, entry.getKey(), value.getClassPattern());
				}
			}
			builder.addConstructorArgValue(targetDataSources);
		}

		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
		// initializer
		registry.registerBeanDefinition(DataSourceInitializerInvoker.class.getName(),
				BeanDefinitionBuilder.genericBeanDefinition(DataSourceInitializerInvoker.class)
						.addConstructorArgReference(beanName)
						.getBeanDefinition());
	}

	private boolean isEnableAspectJAutoProxy(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		boolean enableAspectJAutoProxy = registry.containsBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
		if (!enableAspectJAutoProxy) {
			AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
					EnableDynamicTransaction.class.getName(), false));
			if (annotationAttributes != null) {
				enableAspectJAutoProxy = annotationAttributes.getBoolean("enableAspectJAutoProxy");
			}
		}
		if (!enableAspectJAutoProxy) {
			AnnotationAttributes aspectj = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
					EnableAspectJAutoProxy.class.getName(), false));
			enableAspectJAutoProxy = aspectj != null;
		}
		return enableAspectJAutoProxy;
	}

	private void registerAnnotationAspect(boolean enableAspectJAutoProxy, BeanDefinitionRegistry registry, DynamicDataSourceProperties properties) {
		if (enableAspectJAutoProxy && properties.isEnableAspectj()) {
			AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(DynamicDataSourceAspect.class);
			beanDefinition.setRole(BeanDefinition.ROLE_SUPPORT);
			registry.registerBeanDefinition(ToolkitConstants.DYNAMIC_DATASOURCE_ASPECT_BEAN_NAME, beanDefinition);
		} else {
			StaticMethodMatcherPointcut pointcut = new AnnotationStaticMethodMatcherPointcut(TargetDataSource.class);
			Advice advice = new DynamicDataSourceMethodInterceptor();
			//DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut,advice);
			AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DefaultPointcutAdvisor.class)
					.addConstructorArgValue(pointcut)
					.addConstructorArgValue(advice)
					.setRole(BeanDefinition.ROLE_SUPPORT)
					.getBeanDefinition();
			registry.registerBeanDefinition(ToolkitConstants.DYNAMIC_DATASOURCE_ASPECT_BEAN_NAME, beanDefinition);
		}
	}

	private void registerClassPatternAspectBean(BeanDefinitionRegistry registry, String key, String classPattern) {
		TypePatternClassPointcut pointcut = new TypePatternClassPointcut(classPattern);
		Advice advice = new DynamicDataSourceMethodInterceptor(key);
		registry.registerBeanDefinition(
				ToolkitConstants.DYNAMIC_DATASOURCE_ASPECT_BEAN_NAME + "." + key,
				BeanDefinitionBuilder.genericBeanDefinition(DefaultPointcutAdvisor.class)
						.addConstructorArgValue(pointcut)
						.addConstructorArgValue(advice)
						.setRole(BeanDefinition.ROLE_SUPPORT)
						.getBeanDefinition());
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
}
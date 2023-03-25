package io.polaris.toolkit.spring.annotation;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import io.polaris.toolkit.spring.transaction.DynamicTransactionProperties;
import io.polaris.toolkit.spring.configuration.DynamicAspectTransactionConfiguration;
import io.polaris.toolkit.spring.configuration.DynamicProxyTransactionConfiguration;
import io.polaris.toolkit.spring.transaction.TransactionAspectHelper;
import io.polaris.toolkit.spring.util.Binders;
import io.polaris.toolkit.spring.util.Contexts;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
public class DynamicTransactionConfigurationRegistrar extends AbstractImportSelector implements ImportSelector {

	@Override
	public String[] doSelectImports(AnnotationMetadata importingClassMetadata) {
		boolean enableAspectJAutoProxy = false;
		BeanDefinitionRegistry registry = Contexts.getBeanDefinitionRegistry();
		if (registry != null) {
			enableAspectJAutoProxy = TransactionAspectHelper.registerAspectJIfNecessary(importingClassMetadata, registry);
		}
		if (!enableAspectJAutoProxy) {
			AnnotationAttributes aspectj = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(
					EnableAspectJAutoProxy.class.getName(), false));
			enableAspectJAutoProxy = aspectj != null;
		}

		if (Contexts.getEnvironment() != null) {
			DynamicTransactionProperties properties = Binders.bind(Contexts.getEnvironment(), DynamicTransactionProperties.class, ToolkitConstants.TOOLKIT_DYNAMIC_TRANSACTION);
			if (enableAspectJAutoProxy && properties.isEnableAspectj()) {
				List<String> list = new ArrayList<>();
				if (properties.isEnableTransactionalAspect()) {
					list.add(DynamicAspectTransactionConfiguration.TransactionalAspectConfig.class.getName());
				}
				if (properties.isEnableServiceAspect()) {
					list.add(DynamicAspectTransactionConfiguration.SpringServiceAspectConfig.class.getName());
				}
				if (properties.isEnableRepositoryAspect()) {
					list.add(DynamicAspectTransactionConfiguration.SpringRepositoryAspectConfig.class.getName());
				}
				if (StringUtils.hasText(properties.getClassPattern())) {
					list.add(DynamicAspectTransactionConfiguration.ProxyTransactionConfig.class.getName());
				}
				return list.toArray(new String[0]);
			}
		}
		return new String[]{DynamicProxyTransactionConfiguration.class.getName()};
	}

}

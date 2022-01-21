package io.polaris.toolkit.spring.annotation;

import io.polaris.toolkit.spring.annotation.AbstractImportBeanDefinitionRegistrar;
import io.polaris.toolkit.spring.transaction.TransactionAspectHelper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
public class DynamicTransactionConfigurationAspectjRegistrar extends AbstractImportBeanDefinitionRegistrar
		implements ImportBeanDefinitionRegistrar {

	@Override
	public void doRegisterBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		TransactionAspectHelper.registerAspectJIfNecessary(importingClassMetadata, registry);
	}
}

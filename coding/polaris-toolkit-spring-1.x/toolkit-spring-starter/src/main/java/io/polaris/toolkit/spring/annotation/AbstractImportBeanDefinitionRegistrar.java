package io.polaris.toolkit.spring.annotation;

import io.polaris.toolkit.spring.crypto.CryptoPropertiesBeanHelper;
import io.polaris.toolkit.spring.util.Contexts;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Qt
 * @version Jan 02, 2022
 * @since 1.8
 */
public abstract class AbstractImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public final void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
			BeanDefinitionRegistry registry) {
		CryptoPropertiesBeanHelper.buildEarlyInRegistrarIfNecessary(importingClassMetadata);
		doRegisterBeanDefinitions(importingClassMetadata, registry);
	}

	public void doRegisterBeanDefinitions(AnnotationMetadata importingClassMetadata,
			BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
		doRegisterBeanDefinitions(importingClassMetadata, registry);
	}

	public abstract void doRegisterBeanDefinitions(AnnotationMetadata importingClassMetadata,
			BeanDefinitionRegistry registry);


	protected String uniqueBeanName(BeanDefinitionRegistry registry, String beanName) {
		String uniqueBeanName = beanName;
		int i = 0;
		while (registry.containsBeanDefinition(uniqueBeanName)) {
			uniqueBeanName = beanName + "." + (++i);
		}
		return uniqueBeanName;
	}

}

package io.polaris.toolkit.spring.annotation;

import io.polaris.toolkit.spring.crypto.CryptoPropertiesBeanHelper;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Qt
 * @version Jan 02, 2022
 * @since 1.8
 */
public abstract class AbstractImportSelector implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		CryptoPropertiesBeanHelper.buildEarlyInRegistrarIfNecessary(importingClassMetadata);
		return doSelectImports(importingClassMetadata);
	}

	protected abstract String[] doSelectImports(AnnotationMetadata importingClassMetadata);
}

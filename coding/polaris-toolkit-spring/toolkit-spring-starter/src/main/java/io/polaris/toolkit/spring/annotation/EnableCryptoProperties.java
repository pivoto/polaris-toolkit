package io.polaris.toolkit.spring.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(CryptoPropertiesConfigurationRegistrar.class)
public @interface EnableCryptoProperties {
}

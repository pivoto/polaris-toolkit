package io.polaris.core.spi.condition.case01;

import io.polaris.core.consts.JavaVersion;
import io.polaris.core.spi.condition.support.SpiConditionalOnClass;
import io.polaris.core.spi.condition.support.SpiConditionalOnJavaVersion;
import io.polaris.core.spi.condition.support.SpiConditionalOnProperty;
import io.polaris.core.spi.condition.support.SpiConditionalOnResource;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@SpiConditional01(attribute = "SpiConditionalClass")
@SpiConditionalOnProperty(name = "SpiConditionalClass.enabled", havingValue = "true")
@SpiConditionalOnJavaVersion(value = JavaVersion.JAVA_9)
@SpiConditionalOnResource({"log4j2.xml"})
@SpiConditionalOnClass(name = {"com.fasterxml.jackson.databind.ObjectMapper"})
public class SpiConditionalClass {
}

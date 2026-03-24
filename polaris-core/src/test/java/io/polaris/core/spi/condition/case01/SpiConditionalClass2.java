package io.polaris.core.spi.condition.case01;

import io.polaris.core.spi.condition.support.SpiConditionalOnProperty;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@SpiConditional01(attribute = "SpiConditionalClass2")
//@SpiConditionalOnProperty(name = "SpiConditionalClass.enabled", havingValue = "true")
public class SpiConditionalClass2 extends SpiConditionalClass{
}

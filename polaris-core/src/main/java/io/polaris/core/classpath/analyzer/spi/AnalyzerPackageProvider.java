package io.polaris.core.classpath.analyzer.spi;

import java.util.Set;

import io.polaris.core.service.Spi;
import io.polaris.core.service.SpiLoadable;

/**
 * @author Qt
 * @since Aug 21, 2025
 */
@Spi("静态代码扫描的包路径规则")
public interface AnalyzerPackageProvider extends SpiLoadable {

	Set<String> includePackages();

	Set<String> excludePackages();

}

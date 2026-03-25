package io.polaris.mybatis.auth;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;

/**
 * @author Qt
 * @since Mar 25, 2026
 */
@Spi("数据权限上下文")
public interface DataAuthEnvProvider extends SpiLoadable {

	DataAuthEnv getDataAuthEnv();

}

package io.polaris.mybatis.auth;

import java.util.Set;

import javax.annotation.Nonnull;

import io.polaris.core.service.ServiceLoadable;
import io.polaris.core.service.Spi;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
@Spi("数据权限配置")
public interface DataAuthConfigurator extends ServiceLoadable {


	/**
	 * 是否对目标表启用数据权限控制
	 *
	 * @param tableName 表名
	 * @param scopeName 数据权限名
	 * @return true表示启用数据权限，false表示不启用数据权限
	 */
	boolean isScopeEnabled(String tableName, String scopeName);

	/**
	 * 获取全局数据权限列表
	 *
	 * @return 全局数据权限
	 */
	@Nonnull
	Set<String> getGlobalScopes();

	/**
	 * 是否启用mybatis拦截器
	 *
	 * @return true表示启用，false表示不启用
	 */
	boolean isMybatisInterceptorEnabled();
}

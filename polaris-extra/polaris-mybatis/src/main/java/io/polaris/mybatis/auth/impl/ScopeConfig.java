package io.polaris.mybatis.auth.impl;

import lombok.Data;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
@Data
public class ScopeConfig {
	/** 是否启用 */
	private boolean enabled = true;
	/** 对应数据权限控制的条件语句的关联列名配置 */
	private WhereConfig where = new WhereConfig();
	/** 应数据权限控制的允许查询展示的列名配置 */
	private SelectConfig select = new SelectConfig();
}

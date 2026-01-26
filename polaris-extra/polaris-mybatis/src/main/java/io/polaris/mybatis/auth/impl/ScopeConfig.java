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
	/** 对应数据权限控制条件的关联列名 */
	private String column;
}

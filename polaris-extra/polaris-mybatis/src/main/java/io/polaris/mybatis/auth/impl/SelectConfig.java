package io.polaris.mybatis.auth.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.Data;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@Data
public class SelectConfig {
	/** 允许查询的列 */
	private Set<String> includeColumns = new ConcurrentSkipListSet<>();
	/** 禁止查询的列 */
	private Set<String> excludeColumns = new ConcurrentSkipListSet<>();
}

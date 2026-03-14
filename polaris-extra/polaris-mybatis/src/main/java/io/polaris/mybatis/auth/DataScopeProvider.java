package io.polaris.mybatis.auth;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
@Spi("数据权限实现")
public interface DataScopeProvider extends SpiLoadable {


	/**
	 * @return 数据权限名称
	 */
	String name();

	/**
	 * 获取数据权限条件语句, 会追加至查询语句where子句中，为空表示无数据权限
	 *
	 * @param tableName  表名
	 * @param tableAlias 表别名
	 * @return 数据权限条件语句
	 */
	String where(@Nonnull String tableName, @Nullable String tableAlias);


	/**
	 * 过滤查询字段
	 *
	 * @param tableName  表名
	 * @param colunmName 字段名
	 * @return true表示允许查询，false表示禁止查询
	 */
	boolean select(@Nonnull String tableName, String colunmName);


}

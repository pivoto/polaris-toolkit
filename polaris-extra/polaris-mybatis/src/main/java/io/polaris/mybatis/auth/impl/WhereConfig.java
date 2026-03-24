package io.polaris.mybatis.auth.impl;

import lombok.Data;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@Data
public class WhereConfig {
	/** 对应数据权限控制条件的关联列名 */
	private String column;
	/** 对应数据权限控制条件的自定义表达式，注意表别名的占位符使用，以防止在多表关联等场景下字段混淆，如：`coalesce($T.col_name, '1') = '1'` */
	private String expression;
	/** 对应数据权限控制条件的自定义表达式中的表别名占位符，，带`.`号分隔符，默认为`$T.` */
	private String expressionTableAliasPlaceholder = "$T.";
}

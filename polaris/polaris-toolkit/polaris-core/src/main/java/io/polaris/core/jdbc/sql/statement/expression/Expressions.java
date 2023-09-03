package io.polaris.core.jdbc.sql.statement.expression;

/**
 * @author Qt
 * @since 1.8,  Sep 01, 2023
 */
public class Expressions {
	public static PatternExpression pattern(String pattern) {
		return PatternExpression.of(pattern);
	}


	/**
	 * 创建SQL函数表达式
	 *
	 * @param name   函数名
	 * @param params 参数类型(true-字段引用,false-值引用)
	 * @return
	 */
	public static Expression function(String name, boolean... params) {
		StringBuilder pattern = new StringBuilder();
		pattern.append(name).append("(");
		if (params.length > 0) {
			int fieldIdx = 0;
			int argIdx = 0;
			for (int i = 0; i < params.length; i++) {
				if (i > 0) {
					pattern.append(", ");
				}
				if (params[i]) {
					pattern.append("${ref").append(fieldIdx).append("}");
					fieldIdx++;
				} else {
					pattern.append("#{").append(argIdx).append("}");
					argIdx++;
				}
			}
		}
		pattern.append(")");
		return PatternExpression.of(pattern.toString());
	}

}

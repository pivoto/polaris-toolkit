package io.polaris.core.jdbc.sql.statement.expression;

import java.util.LinkedHashMap;
import java.util.Map;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.function.Consumer;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import io.polaris.core.map.FluentMap;
import io.polaris.core.map.Maps;

/**
 * @author Qt
 * @since Sep 01, 2023
 */
public class Expressions {

	public static final TableField[] EMPTY_FIELDS = new TableField[0];

	/**
	 * 创建SQL模式表达式
	 * <ul>
	 * <li>
	 * 字段引用使用：`${ref}`、`${ref1}`、`${ref2}`、...
	 * </li>
	 * <li>
	 * 参数值引用使用：`${0}`、`${1}`、`${2}`、...
	 * </li>
	 * <li>
	 * 示例： funcName(${ref0}, ${ref1}, ${0}, ${1})
	 * </li>
	 * </ul>
	 *
	 * @param pattern
	 * @return
	 */
	public static PatternExpression pattern(String pattern) {
		return PatternExpression.of(pattern);
	}


	/**
	 * 创建SQL函数表达式
	 *
	 * @param funcName 函数名
	 * @param colRefs  参数类型(true-字段引用,false-值引用)
	 * @return
	 */
	public static Expression function(String funcName, boolean... colRefs) {
		StringBuilder pattern = new StringBuilder();
		pattern.append(funcName).append("(");
		if (colRefs.length > 0) {
			int fieldIdx = 0;
			int argIdx = 0;
			for (int i = 0; i < colRefs.length; i++) {
				if (i > 0) {
					pattern.append(", ");
				}
				if (colRefs[i]) {
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


	public static TableField[] emptyFields() {
		return EMPTY_FIELDS;
	}

	public static Object[] emptyBindings() {
		return StdConsts.EMPTY_ARRAY;
	}

	public static TableField[] fields(TableField... fields) {
		return fields;
	}

	public static Object[] bindings(Object... bindings) {
		return bindings;
	}

	public static Map<String, Object> bindings(Consumer<FluentMap<String, Object>> consumer) {
		FluentMap<String, Object> bindings = Maps.newFluentMap(new LinkedHashMap<>());
		consumer.accept(bindings);
		return bindings.get();
	}
}

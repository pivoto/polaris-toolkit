package io.polaris.core.jdbc.sql.statement.expression;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.core.io.Consoles;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.SqlNodes;
import io.polaris.core.map.Maps;
import io.polaris.core.regex.Patterns;
import org.junit.jupiter.api.Test;

class PatternExpressionTest {

	@Test
	void test01() {
		Pattern pattern = Patterns.getPattern("^ref(\\d*)$");
		String[] args = {"ref123", "ref", "ref11x", "ref2"};
		for (String s : args) {
			Matcher m = pattern.matcher(s);
			if (m.matches()) {
				Consoles.println("{}: {}, {} \n", s, m.group(), m.group(1));
			}
		}
	}

	@Test
	void test02() {
		System.out.println(PatternExpression.of("coalesce(#{ref},#{ref1},#{ref2})").equals(PatternExpression.of("coalesce(#{ref},#{ref1},#{ref2})")));
		PatternExpression expr = PatternExpression.of("coalesce(#{ref},#{ref1},#{ref2},#{v1},#{v1},#{v2},#{v1})");
		System.out.println(expr);
		System.out.println(expr.buildMapFunction().apply(
				SqlNodes.text("a"),
				new SqlNode[]{SqlNodes.text("b"), SqlNodes.text("c")},
				Maps.newFluentMap(new HashMap<String,Object>()).put("v1",1).put("v2",2).get())
			.asPreparedSql()
		);
		System.out.println(expr.buildArrayFunction().apply(
				SqlNodes.text("a"),
				new SqlNode[]{SqlNodes.text("b"), SqlNodes.text("c")},
				new Object[]{1,2,3})
			.asPreparedSql()
		);
	}

}

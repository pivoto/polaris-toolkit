package io.polaris.core.jdbc.sql.statement;

import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.jdbc.sql.statement.segment.TableField;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.jupiter.api.Test;

class MergeStatementTest {

	@Test
	void test01() throws JSQLParserException {
		MergeStatement<?> s = new MergeStatement<>(DemoEntity.class, "t");
//		s.using(DemoEntity.class, "s");

		s.using(new SelectStatement<>(DemoEntity.class, "o").selectAll()
				.where()
//				.rawColumn("rownum").apply("rownum <= 1").end()
				.rawColumn("rownum").apply("${ref} <= 1").end()
				.andRaw("rownum < 2")
				.end()
			, "s");

		s.on().column("id").eq(TableField.of("s", "id")).end();
		s.updateWhenMatched()
			.updateWith("name", "name")
			.updateWith("score", "score")
			.insertWhenNotMatched()
			.insertWith("id", "id")
			.insertWith("name", "name")
			.insertWith("score", "score")
		;

		PreparedSql sql = s.toSqlNode().asPreparedSql();
		System.out.println(sql);
		CCJSqlParserUtil.parse(sql.getText());
		CCJSqlParserUtil.parse("replace into t (a,b,c) values(1,2,3) ON DUPLICATE KEY UPDATE a=1");
	}
}

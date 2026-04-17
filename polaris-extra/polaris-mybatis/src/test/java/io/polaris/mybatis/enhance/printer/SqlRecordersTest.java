package io.polaris.mybatis.enhance.printer;

import java.util.List;

import io.polaris.core.jdbc.annotation.Table;
import io.polaris.mybatis.mapper.AnySqlSelectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlRecordersTest {

	@Test
	void test() {
		AnySqlSelectMapper mapper = SqlRecorders.get(AnySqlSelectMapper.class);
		List list = mapper.selectObjectListByAnySql(SqlRecordersTest$TestEntitySql.select().selectAll().toSqlNode());
		System.out.println(SqlRecorders.pollALlSqlText());
	}


	@Table("test")
	@Data
	public static class TestEntity {
		private String id;
		private String name;
		private Integer age;
	}
}

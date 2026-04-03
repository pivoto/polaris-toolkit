package io.polaris.demo.mybatis.mapper;

import io.polaris.core.io.Consoles;
import io.polaris.core.map.Maps;
import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.mybatis.enhance.printer.SqlRecorders;
import org.junit.jupiter.api.Test;

class DemoOrgAutoMapperTest {


	@Test
	void test01() {
		DemoOrgAutoMapper mapper = SqlRecorders.get(DemoOrgAutoMapper.class);
		mapper.queryAuto(DemoOrgEntity.builder().id(1L).build());
		mapper.queryAuto(DemoOrgEntity.builder().id(2L).build());
		mapper.queryAuto(Maps.asMap("id", 1L, "name", "org"));
		mapper.queryAuto(Maps.asMap("id", 1L, "name", "org%"));
		Consoles.println("sql: \n{}", SqlRecorders.popALlSql());
	}
}

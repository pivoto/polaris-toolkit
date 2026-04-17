package io.polaris.mybatis.enhance.example;

import io.polaris.core.map.Maps;
import io.polaris.core.random.Randoms;
import io.polaris.mybatis.enhance.printer.SqlRecorders;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

class ExampleAutoMapperTest {

	private final ExampleAutoMapper mapper = SqlRecorders.get(ExampleAutoMapper.class);

	@Test
	@Order(1)
	void testAny() {
		mapper.selectAny(Maps.asMap("id", new String[]{"1", "2"}));
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.updateAny(Maps.asMap("id", new String[]{"1", "2"}));
		System.out.println(SqlRecorders.pollALlSqlText());
	}

	@Test
	@Order(2)
	void testAnySql() {
		mapper.selectAnySql(ExampleEntitySql.select().selectAll().toSqlNode());
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.updateAnySql(ExampleEntitySql.update().intCol().value(12).end().where().id().eq(1)
			.enumDbCol().eq(ExampleDbEnum.ONE)
			.end().toSqlNode());
		System.out.println(SqlRecorders.pollALlSqlText());

	}

	@Test
	@Order(3)
	void testBySql() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);

		mapper.insertBySql(ExampleEntitySql.insert().withEntity(entity));
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.updateBySql(ExampleEntitySql.update().withEntity(entity));
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.deleteBySql(ExampleEntitySql.delete()
			.where().byEntity(entity)
			.end()
		);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.existsBySql(ExampleEntitySql.select().selectAll()
			.where().byEntity(entity)
			.end()
		);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.countBySql(ExampleEntitySql.select().selectAll()
			.where().byEntity(entity)
			.end()
		);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.selectEntityListBySql(ExampleEntitySql.select().selectAll()
			.where().byEntity(entity)
			.end()
		);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.selectEntityBySql(ExampleEntitySql.select().selectAll()
			.where().byEntity(entity)
			.end()
		);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.mergeBySql(entity);
		System.out.println(SqlRecorders.pollALlSqlText());


	}

	@Test
	@Order(4)
	void testInsert() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);

		mapper.insertEntity(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
	}

	@Test
	@Order(5)
	void testDelete() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);
		mapper.deleteEntityById(entity, true);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.deleteEntityById(entity, false);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.directDeleteEntityById(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.logicDeleteEntityById(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
	}

	@Test
	@Order(6)
	void testDeleteByAny() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);
		mapper.deleteEntityByAny(entity, true);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.deleteEntityByAny(entity, false);
		System.out.println(SqlRecorders.pollALlSqlText());

		mapper.directDeleteEntityByAny(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.logicDeleteEntityByAny(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
	}

	@Test
	@Order(7)
	void testUpdate() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);
		mapper.updateEntityById(entity, true);
		System.out.println(SqlRecorders.pollALlSqlText());
	}

	@Test
	@Order(8)
	void testUpdateByAny() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);
		mapper.updateEntityByAny(entity, entity);
		System.out.println(SqlRecorders.pollALlSqlText());
	}

	@Test
	@Order(9)
	void testSelectOne() {
		ExampleEntity entity = Randoms.randomEntity(ExampleEntity.class);
		mapper.selectEntityById(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.selectEntity(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.existsById(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
		mapper.exists(entity);
		System.out.println(SqlRecorders.pollALlSqlText());
	}

}

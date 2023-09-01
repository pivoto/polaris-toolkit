package io.polaris;

import io.polaris.demo.mybatis.entity.DemoUserEntity;
import io.polaris.demo.mybatis.service.DemoUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = DemoApp.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class DemoAppTest {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private DemoUserService demoUserService;


	@Test
	void test01() {
		demoUserService.doTransaction(mapper -> {
			log.warn("新增一条数据");
			DemoUserEntity entity = new DemoUserEntity();
			entity.setId(2L);
			entity.setName("user2");
			mapper.insertEntity(entity);
		});
	}

	@Test
	void test02() {
		demoUserService.doTransaction(mapper -> {
			log.warn("新增一条数据");
			DemoUserEntity entity = new DemoUserEntity();
			entity.setId(3L);
			entity.setName("user3");
			mapper.insertEntity(entity, true);
		});

	}

	@Test
	void test03() {
		demoUserService.doTransaction(mapper -> {
			log.warn("查询数据");
			DemoUserEntity param = new DemoUserEntity();
			param.setId(2L);
			System.out.println(mapper.selectEntityList(param));
			System.out.println(mapper.selectMapList(param));
			System.out.println(mapper.selectEntity(param));
		});
	}

	@Test
	void test04() {
		demoUserService.doTransaction(mapper -> {
			DemoUserEntity param = new DemoUserEntity();
			param.setId(2L);
			param.setVersion(0L);
			param.setName("update2");
			mapper.updateEntityById(param);
		});
	}

	@Test
	void test05() {
		demoUserService.doTransaction(mapper -> {
			DemoUserEntity param = new DemoUserEntity();
			param.setId(2L);
			System.out.println(mapper.selectEntityList(param));
		});
	}
}

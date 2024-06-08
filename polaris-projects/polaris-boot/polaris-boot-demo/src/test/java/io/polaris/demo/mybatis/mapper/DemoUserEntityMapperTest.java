package io.polaris.demo.mybatis.mapper;

import java.util.List;

import io.polaris.DemoApp;
import io.polaris.core.jdbc.sql.query.OrderBy;
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
class DemoUserEntityMapperTest {
	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private DemoUserService demoUserService;

	@Test
	void test01() {
		demoUserService.doTransaction(mapper -> {
			List<DemoUserEntity> rs = mapper.selectEntityList(DemoUserEntity.builder().build());
			log.info("rs: {}", rs);
		});
		demoUserService.doTransaction(mapper -> {
			List<DemoUserEntity> rs = mapper.selectEntityList(DemoUserEntity.builder().build(), null, true);
			log.info("rs: {}", rs);
		});
	}
}

package io.polaris.demo.mybatis;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.polaris.DemoApp;
import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.demo.mybatis.entity.DemoUserEntity;
import io.polaris.demo.mybatis.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * @author Qt
 * @since  Jan 30, 2024
 */
@SpringBootTest(classes = DemoApp.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
public class DemoMapperTest {
	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private DemoService demoService;


	@Test
	@Order(0)
	void test_insert() {
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(11L);
			param.setVersion(11L);
			param.setName("org11");
			log.warn("执行结果：{}", mapper.insertEntity(param));
		});
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(11L);
			param.setVersion(11L);
			param.setName("org11");
			param.setIntro("test...");
			log.warn("执行结果：{}", mapper.updateEntity(param));
		});
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(11L);
			param.setVersion(11L);
			DemoOrgEntity rs = mapper.selectOrgById(param);
			log.warn("执行结果：{}", rs);
		});
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(11L);
			param.setVersion(12L);
			DemoOrgEntity rs = mapper.selectOrgById(param);
			log.warn("执行结果：{}", rs);
		});
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(11L);
			param.setVersion(11L);
			param.setName("org11");
			param.setIntro("test...");
			log.warn("执行结果：{}", mapper.deleteEntity(param));
		});
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(11L);
			param.setVersion(12L);
			param.setName("org11");
			param.setIntro("test...");
			log.warn("执行结果：{}", mapper.deleteEntity(param));
		});
	}

	@Test
	@Order(10)
	void test_selectOrgById() {
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setId(1L);
			param.setVersion(0L);
			DemoOrgEntity rs = mapper.selectOrgById(param);
			log.warn("执行结果：{}", rs);
		});
	}

	@Test
	@Order(20)
	void test_selectOrgList() {
		demoService.doTransaction(mapper -> {
			DemoOrgEntity param = new DemoOrgEntity();
			param.setName("%org%");
			List<DemoOrgEntity> rs = mapper.selectOrgList(param);
			log.warn("执行结果：{}", rs);
		});
	}

	@Test
	@Order(30)
	void test_selectOrgListByAny() {
		demoService.doTransaction(mapper -> {
			{
				List<DemoOrgEntity> rs = mapper.selectOrgListByAny(null, null);
				log.warn("执行结果：{}", rs);
			}
			{
				List<DemoOrgEntity> rs = mapper.selectOrgListByAny(1L, null);
				log.warn("执行结果：{}", rs);
			}
			{
				List<DemoOrgEntity> rs = mapper.selectOrgListByAny(1L, "org");
				log.warn("执行结果：{}", rs);
			}
		});
	}



	@Test
	@Order(40)
	void test_selectOrgListByAny2() {
		demoService.doTransaction(mapper -> {
			{
				Map<String,Object> map = new LinkedHashMap<>();
				map.put("id",1);
				map.put("id1",1L);
				map.put("id2",1L);
				map.put("name","org");
				log.warn("执行结果：{}", mapper.selectOrgListByAny2(map));
				log.warn("执行结果：{}", mapper.selectOrgListByAny2(map));
			}
		});
	}


}

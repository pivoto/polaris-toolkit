package io.polaris.demo.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.demo.mybatis.entity.DemoUserEntity;
import io.polaris.demo.mybatis.entity.DemoUserEntitySql;
import io.polaris.demo.mybatis.service.DemoService;
import io.polaris.demo.mybatis.service.DemoUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
@RestController
@RequestMapping("demo")
public class DemoController {
	private final DemoService demoService;
	private final DemoUserService demoUserService;

	public DemoController(DemoService demoService, DemoUserService demoUserService) {
		this.demoService = demoService;
		this.demoUserService = demoUserService;
	}


	@PostMapping("test")
	public Object test() {
		return demoService.doTransaction(mapper -> {
			Map<String, Object> map = new HashMap<>();
			map.put("selectOrgById", mapper.selectOrgById(DemoOrgEntity.builder().id(1L).build()));
			map.put("selectOrgList", mapper.selectOrgList(DemoOrgEntity.builder().name("org%").build()));
			map.put("selectOrgListByAny", mapper.selectOrgListByAny(1L, "org"));
			map.put("getOrgListByIds", mapper.getOrgListByIds(new Long[] {1L, 2L}));
			map.put("getOrgListByIds2", mapper.getOrgListByIds2(new Long[] {1L, 2L}));
			map.put("getOrgListByIds3", mapper.getOrgListByIds3(new Long[] {1L, 2L}));
			Map<String, Object> param = new HashMap<>();
			param.put("id", 1L);
			param.put("name", "org%");
			map.put("selectOrgListByAny2", mapper.selectOrgListByAny2(param));
			return map;
		});
	}


	@PostMapping("listUser")
	public Object list() {
		List<DemoUserEntity> list = demoUserService.doTransaction(mapper -> {
			return mapper.selectEntityListBySql(DemoUserEntitySql.select().selectAll());
		});
		return list;
	}

}

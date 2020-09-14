package io.polaris.demo.mybatis.controller;

import io.polaris.demo.mybatis.entity.DemoUserEntity;
import io.polaris.demo.mybatis.entity.DemoUserEntitySql;
import io.polaris.demo.mybatis.service.DemoUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
@RestController
@RequestMapping("demo")
public class DemoController {
	private final DemoUserService demoUserService;

	public DemoController(DemoUserService demoUserService) {
		this.demoUserService = demoUserService;
	}


	@PostMapping("list")
	public Object list(){
		List<DemoUserEntity> list = demoUserService.doTransaction(mapper -> {
			return mapper.selectEntityListBySql(DemoUserEntitySql.select().selectAll());
		});
		return list;
	}

}

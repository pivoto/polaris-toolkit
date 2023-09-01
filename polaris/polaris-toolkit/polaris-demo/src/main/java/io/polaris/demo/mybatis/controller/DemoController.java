package io.polaris.demo.mybatis.controller;

import io.polaris.demo.mybatis.service.DemoUserService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
@RestController
public class DemoController {
	private final DemoUserService demoUserService;

	public DemoController(DemoUserService demoUserService) {
		this.demoUserService = demoUserService;
	}



}

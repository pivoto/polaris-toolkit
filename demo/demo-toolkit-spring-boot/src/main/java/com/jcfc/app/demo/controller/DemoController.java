package com.jcfc.app.demo.controller;

import com.jcfc.app.demo.component.DemoTestComponent;
import com.jcfc.app.demo.service.Demo2Service;
import com.jcfc.app.demo.service.DemoService;
import io.polaris.toolkit.spring.util.Contexts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@RestController
@Slf4j
@RequestMapping("/demo")
public class DemoController {

	@RequestMapping("**")
	public Object demo() {
		log.info("demo()");
		Contexts.getApplicationContext().getBean(DemoService.class).doSth();
		Contexts.getApplicationContext().getBean(DemoTestComponent.class).doSth();
		Contexts.getApplicationContext().getBean(Demo2Service.class).doSth();
		return "demo...";
	}

	@GetMapping("test")
	public Object test() {
		Map<String, DataSource> beans = Contexts.getApplicationContext().getBeansOfType(DataSource.class);
		log.info("ds: {}", beans);
		Contexts.getApplicationContext().getBean(DemoService.class).doTest();
		return beans.keySet();
	}

}
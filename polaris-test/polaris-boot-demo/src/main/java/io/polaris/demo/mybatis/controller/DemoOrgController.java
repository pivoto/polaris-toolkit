package io.polaris.demo.mybatis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.map.Maps;
import io.polaris.demo.mybatis.entity.DemoOrgEntity;
import io.polaris.demo.mybatis.entity.DemoUserEntity;
import io.polaris.demo.mybatis.service.DemoOrgService;
import io.polaris.demo.mybatis.service.DemoUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
@RestController
@RequestMapping("demo/org")
@RequiredArgsConstructor
@Slf4j
public class DemoOrgController {
	private final DemoOrgService demoOrgService;

	@PostMapping("list")
	public Object list() {
		Map<String, Object> params = Maps.asMap(new HashMap<>(), "id", new Long[]{1L, 2L});
		List<DemoOrgEntity> list = demoOrgService.doTransaction(mapper -> {
			return mapper.queryAuto(params);
		});
		log.info("params: {}", params);
		return list;
	}

}

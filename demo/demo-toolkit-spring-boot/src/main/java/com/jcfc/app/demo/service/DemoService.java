package com.jcfc.app.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcfc.app.demo.entity.User;
import com.jcfc.app.demo.mapper.UserMapper;
import io.polaris.toolkit.spring.jdbc.TargetDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
@Service
@Slf4j
public class DemoService {
	@Autowired
	private UserMapper userMapper;

	@Transactional
	@TargetDataSource("")
	public void doSth() {
	}

	public void doTest(){
		Page<User> page = userMapper.selectPage(Page.of(1, 10), null);
		log.info("records: {}", page.getRecords());
		log.info("total: {}", page.getTotal());
	}

}

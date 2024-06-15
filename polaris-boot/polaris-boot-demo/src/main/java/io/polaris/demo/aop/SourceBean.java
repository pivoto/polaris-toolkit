package io.polaris.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @since May 13, 2024
 */
@Component
@Slf4j
public class SourceBean {

	public void func1() {
		log.info("func1....");
		func2();
		call2();
	}

	public void func2() {
		log.info("func2....");
		func_call_3();
	}

	public void call1() {
		log.info("call1....");
		func2();
		call2();
	}

	public void call2() {
		log.info("call2....");
		func_call_3();
	}
	public void func_call_3() {
		log.info("func_call_3....");
	}


}

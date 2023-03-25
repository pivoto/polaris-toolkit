package com.jcfc.app.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@Aspect
@Component
@Slf4j
public class WebControllerAspect {

	@Around(" @within(org.springframework.stereotype.Controller) " +
			"|| @within(org.springframework.web.bind.annotation.RestController)" +
			"|| bean(*Controller)" +
			"|| @annotation(org.springframework.web.bind.annotation.RequestMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.GetMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)" +
			"|| @annotation(org.springframework.web.bind.annotation.PatchMapping)"
	)
	public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("before {}", joinPoint.getSignature());
		try {
			return joinPoint.proceed();
		} catch (Throwable throwable) {
			throw throwable;
		} finally {
			log.info("after {}", joinPoint.getSignature());
		}
	}

}


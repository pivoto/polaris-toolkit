package io.polaris.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Qt
 * @since May 13, 2024
 */
@Aspect
@Slf4j
@Component
public class Aspect02 {

	@Around(
		"execution(* io.polaris.demo.aop.SourceBean.*call*(..))"
	)
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			log.info("aspect02 before....");
			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			log.info("aspect02 after....");
		}
	}

}

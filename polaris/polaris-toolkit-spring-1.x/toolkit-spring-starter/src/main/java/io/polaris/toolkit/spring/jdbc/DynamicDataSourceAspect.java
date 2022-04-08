package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */
@Aspect
@Order(ToolkitConstants.ORDER_DATASOURCE_ASPECT)
@Slf4j
public class DynamicDataSourceAspect {


	@Around("@within(ds) || @annotation(ds)")
	public Object bindDynamicKey(ProceedingJoinPoint point, TargetDataSource ds) throws Throwable {
		if (ds != null) {
			return DynamicDataSourceKeys.doInterceptor(ds.value(), point::proceed);
		} else {
			return point.proceed();
		}
	}

}

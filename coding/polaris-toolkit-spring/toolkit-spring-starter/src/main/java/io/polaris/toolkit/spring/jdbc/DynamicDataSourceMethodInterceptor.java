package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @version Jan 05, 2022
 * @since 1.8
 */
public class DynamicDataSourceMethodInterceptor implements MethodInterceptor, Ordered {
	private String dataSourceKey;

	public DynamicDataSourceMethodInterceptor() {
	}

	public DynamicDataSourceMethodInterceptor(String dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}

	@Override
	public int getOrder() {
		return ToolkitConstants.ORDER_DATASOURCE_ASPECT;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
//		if(invocation instanceof ProxyMethodInvocation){
		if (dataSourceKey != null) {
			return DynamicDataSourceKeys.doInterceptor(dataSourceKey, invocation::proceed);
		} else {
			Method method = invocation.getMethod();
			TargetDataSource targetDataSource = AnnotationUtils.findAnnotation(method, TargetDataSource.class);
			if (targetDataSource == null) {
				targetDataSource = AnnotationUtils.findAnnotation(invocation.getThis().getClass(), TargetDataSource.class);
			}
			if (targetDataSource == null) {
				return invocation.proceed();
			}
			return DynamicDataSourceKeys.doInterceptor(targetDataSource.value(), invocation::proceed);
		}
	}

}

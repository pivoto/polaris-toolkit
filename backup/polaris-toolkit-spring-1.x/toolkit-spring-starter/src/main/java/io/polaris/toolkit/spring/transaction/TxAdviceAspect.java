package io.polaris.toolkit.spring.transaction;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * @author Qt
 * @version Dec 29, 2021
 * @since 1.8
 */
@Aspect
@Order(ToolkitConstants.ORDER_TRANSACTION_SPRING_ANNOTATION_ASPECT)
public class TxAdviceAspect {
	private TransactionInterceptor transactionInterceptor;
	private TransactionInterceptor transactionInterceptorAnno;

	public TxAdviceAspect(TransactionInterceptor transactionInterceptor, TransactionInterceptor transactionInterceptorAnno) {
		this.transactionInterceptor = transactionInterceptor;
		this.transactionInterceptorAnno = transactionInterceptorAnno;
	}

	@Around(value = "@within(javax.transaction.Transactional)"
			+ " || @within(org.springframework.transaction.annotation.Transactional)"
			+ " || @annotation(javax.transaction.Transactional)"
			+ " || @annotation(org.springframework.transaction.annotation.Transactional)")
	public Object annotationService(ProceedingJoinPoint joinPoint) throws Throwable {
		return transactionInterceptorAnno.invoke(new MethodInvocationAdapter(joinPoint));
	}

	@Around(value = //"execution(public * " + BASE_PACKAGE + "..service..*+.*(..)) || "+
			"@within(org.springframework.stereotype.Service) ")
	public Object wildcardService(ProceedingJoinPoint joinPoint) throws Throwable {
		return transactionInterceptor.invoke(new MethodInvocationAdapter(joinPoint));
	}

}

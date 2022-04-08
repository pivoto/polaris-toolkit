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
public class SpringTransactionalAspect extends AbstractTransactionAspect {

	public SpringTransactionalAspect(TransactionInterceptor transactionInterceptor) {
		super(transactionInterceptor);
	}


	@Around(value = "@within(org.springframework.transaction.annotation.Transactional)"
			+ " || @annotation(org.springframework.transaction.annotation.Transactional)")
	public Object annotationService(ProceedingJoinPoint joinPoint) throws Throwable {
		return TransactionAspectHelper.proceed(
				() -> transactionInterceptor.invoke(new MethodInvocationAdapter(joinPoint)),
				() -> joinPoint.proceed());
	}

}

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
@Order(ToolkitConstants.ORDER_TRANSACTION_JTA_ANNOTATION_ASPECT)
public class JtaTransactionalAspect extends AbstractTransactionAspect {

	public JtaTransactionalAspect(TransactionInterceptor transactionInterceptor) {
		super(transactionInterceptor);
	}


	@Around(value = "@within(javax.transaction.Transactional)"
			+ " || @annotation(javax.transaction.Transactional)")
	public Object annotationService(ProceedingJoinPoint joinPoint) throws Throwable {
		return TransactionAspectHelper.proceed(
				() -> transactionInterceptor.invoke(new MethodInvocationAdapter(joinPoint)),
				() -> joinPoint.proceed());
	}

}

package io.polaris.toolkit.spring.transaction;

import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * @author Qt
 * @version Dec 31, 2021
 * @since 1.8
 */
public abstract class AbstractTransactionAspect {
	protected TransactionInterceptor transactionInterceptor;

	public AbstractTransactionAspect(TransactionInterceptor transactionInterceptor) {
		this.transactionInterceptor = transactionInterceptor;
	}

}

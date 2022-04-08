package io.polaris.toolkit.spring.transaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.TransactionDefinition;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */
public interface TransactionAttributeName {

	Propagation DEFAULT_PROPAGATION = Propagation.REQUIRED;
	Isolation DEFAULT_ISOLATION = Isolation.DEFAULT;
	int DEFAULT_TIMEOUT = TransactionDefinition.TIMEOUT_DEFAULT;
	boolean DEFAULT_READONLY = false;

	@RequiredArgsConstructor
	@Getter
	enum Propagation {
		REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),
		SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),
		MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),
		REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),
		NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),
		NEVER(TransactionDefinition.PROPAGATION_NEVER),
		NESTED(TransactionDefinition.PROPAGATION_NESTED),
		;
		private final int number;

	}

	@RequiredArgsConstructor
	@Getter
	enum Isolation {
		DEFAULT(TransactionDefinition.ISOLATION_DEFAULT),
		READ_UNCOMMITTED(TransactionDefinition.ISOLATION_READ_UNCOMMITTED),
		READ_COMMITTED(TransactionDefinition.ISOLATION_READ_COMMITTED),
		REPEATABLE_READ(TransactionDefinition.ISOLATION_REPEATABLE_READ),
		SERIALIZABLE(TransactionDefinition.ISOLATION_SERIALIZABLE),
		;
		private final int number;

	}


}

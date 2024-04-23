package io.polaris.core.concurrent.pool;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public interface TransactionConsumer<Data, Resource> {
	int DEFAULT_COMMIT_COUNT = 1000;

	default int commitCount() {
		return DEFAULT_COMMIT_COUNT;
	}

	Resource openResource() throws Throwable;

	void processData(Resource resource, Data data) throws Throwable;

	void commitResource(Resource resource) throws Throwable;

	void rollbackResource(Resource resource) throws Throwable;

	void closeResource(Resource resource);
}


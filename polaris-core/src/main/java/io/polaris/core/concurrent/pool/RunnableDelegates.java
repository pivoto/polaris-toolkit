package io.polaris.core.concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.polaris.core.collection.Iterables;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public class RunnableDelegates {

	private static final ILogger log = Loggers.of(RunnableDelegates.class);

	public static <E> Runnable createDelegate(RunnableState<E> pState, Consumer<E> pConsumer,
		AtomicReference<Consumer<ErrorRecords<E>>> rejectConsumerRef) {
		return () -> {
			RunnableState<E> state = pState;
			Consumer<E> consumer = pConsumer;
			RunnableStatistics statistics = state.runnableStatistics();

			state.incrementActiveCount();
			try {
				if (statistics != null) {
					while (state.hasNext()) {
						E data = state.next();
						if (data != null) {
							statistics.getTotal().incrementAndGet();
							try {
								consumer.accept(data);
								statistics.getSuccess().incrementAndGet();
							} catch (Throwable e) {
								statistics.getError().incrementAndGet();
								try {
									Consumer<ErrorRecords<E>> rejectConsumer = rejectConsumerRef.get();
									if (rejectConsumer != null) {
										ErrorRecords<E> errorRecords = new ErrorRecords<>(Iterables.asList(new ErrorRecord<>(data, e)), e);
										rejectConsumer.accept(errorRecords);
									}
								} catch (Throwable ex) {
									e.addSuppressed(ex);
								}
								log.error("处理失败", e);
							}
							state.notifyFinished();
						}
					}
				} else {
					while (state.hasNext()) {
						E data = state.next();
						if (data != null) {
							try {
								consumer.accept(data);
								state.notifyFinished();
							} catch (Throwable e) {
								try {
									Consumer<ErrorRecords<E>> rejectConsumer = rejectConsumerRef.get();
									if (rejectConsumer != null) {
										ErrorRecords<E> errorRecords = new ErrorRecords<>(Iterables.asList(new ErrorRecord<>(data, e)), e);
										rejectConsumer.accept(errorRecords);
									}
								} catch (Throwable ex) {
									e.addSuppressed(ex);
								}
								log.error("处理失败", e);
							}
						}
					}
				}
			} finally {
				state.decrementActiveCount();
				state.notifyFinished();
				log.trace("处理完成");
			}
		};
	}


	public static <E, Resource> Runnable createDelegate(RunnableState<E> pState, TransactionConsumer<E, Resource> pConsumer,
		AtomicReference<Consumer<ErrorRecords<E>>> rejectConsumerRef) {
		return () -> {
			RunnableState<E> state = pState;
			TransactionConsumer<E, Resource> consumer = pConsumer;
			RunnableStatistics statistics = state.runnableStatistics();
			state.incrementActiveCount();

			Resource resource = null;
			try {
				int commitCount = consumer.commitCount();
				if (commitCount <= 0) {
					commitCount = TransactionConsumer.DEFAULT_COMMIT_COUNT;
				}

				int total = 0;
				int success = 0;
				int error = 0;
				int commit = 0;

				List<ErrorRecord<E>> batchData = new ArrayList<>(commitCount);

				resource = consumer.openResource();
				while (state.hasNext()) {
					E data = state.next();
					if (data != null) {
						try {
							total++;
							if (statistics != null) {
								statistics.getTotal().incrementAndGet();
							}
							commit++;
							// add to batch
							batchData.add(new ErrorRecord<>(data, null));

							/*try {
								consumer.processData(resource, data);
								errorRecords.getRecords().add(new ErrorRecord<>(data, null));
							} catch (Throwable e) {
								errorRecords.getRecords().add(new ErrorRecord<>(data, e));
								if (processError == null) {
									processError = new DataProcessingException(e.getMessage());
								}
								processError.addSuppressed(e);
							}*/

							if (commit >= commitCount) {
								for (ErrorRecord<E> record : batchData) {
									try {
										consumer.processData(resource, record.getData());
									} catch (Throwable e) {
										record.setError(e);
										throw e;
									}
								}
								consumer.commitResource(resource);
								success += commit;
								if (statistics != null) {
									statistics.getSuccess().addAndGet(commit);
								}
								log.trace("处理/提交成功. total: {}, success: {}, error: {}", total, success, error);
								commit = 0;
								batchData = new ArrayList<>(commitCount);
							}
						} catch (Throwable e) {
							error += commit;
							if (statistics != null) {
								statistics.getError().addAndGet(commit);
							}
							commit = 0;
							log.trace("处理/提交失败. total: {}, success: {}, error: {}", total, success, error);
							try {
								consumer.rollbackResource(resource);
							} catch (Throwable ex) {
								log.error("回滚失败", ex);
								e.addSuppressed(ex);
							}
							try {
								// do reject execution
								Consumer<ErrorRecords<E>> rejectConsumer = rejectConsumerRef.get();
								if (rejectConsumer != null) {
									ErrorRecords<E> errorRecords = new ErrorRecords<>(batchData);
									errorRecords.setError(e);
									rejectConsumer.accept(errorRecords);
								}
							} catch (Throwable ex) {
								e.addSuppressed(ex);
							}
							batchData = new ArrayList<>(commitCount);
							log.error("处理/提交失败", e);
						}
						state.notifyFinished();
					}
				}

				if (commit > 0) {
					try {
						for (ErrorRecord<E> record : batchData) {
							try {
								consumer.processData(resource, record.getData());
							} catch (Throwable e) {
								record.setError(e);
								throw e;
							}
						}
						consumer.commitResource(resource);
						success += commit;
						if (statistics != null) {
							statistics.getSuccess().addAndGet(commit);
						}
						log.trace("处理/提交成功. total: {}, success: {}, error: {}", total, success, error);
					} catch (Throwable e) {
						error += commit;
						if (statistics != null) {
							statistics.getError().addAndGet(commit);
						}
						log.trace("处理/提交失败. total: {}, success: {}, error: {}", total, success, error);
						try {
							consumer.rollbackResource(resource);
						} catch (Throwable ex) {
							log.error("回滚失败", ex);
							e.addSuppressed(ex);
						}
						try {
							// do reject execution
							Consumer<ErrorRecords<E>> rejectConsumer = rejectConsumerRef.get();
							if (rejectConsumer != null) {
								ErrorRecords<E> errorRecords = new ErrorRecords<>(batchData);
								errorRecords.setError(e);
								rejectConsumer.accept(errorRecords);
							}
						} catch (Throwable ex) {
							e.addSuppressed(ex);
						}
						log.error("处理/提交失败", e);
					}
				}
			} catch (Throwable e) {
				log.error("", e);
			} finally {
				log.trace("处理完成");
				state.decrementActiveCount();
				state.notifyFinished();
				consumer.closeResource(resource);
			}
		};
	}
}

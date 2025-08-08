package io.polaris.core.concurrent.pool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public class ConsumerDelegates {
	private static final ILogger log = Loggers.of(ConsumerDelegates.class);

	public static <E> Consumer<E> createDelegate(RunnableStatisticsHolder pStatistics, Consumer<E> pConsumer) {
		return data -> {
			Consumer<E> consumer = pConsumer;
			RunnableStatistics statistics = pStatistics.runnableStatistics();
			if (statistics != null) {
				statistics.getTotal().incrementAndGet();
				try {
					consumer.accept(data);
					statistics.getSuccess().incrementAndGet();
				} catch (Exception e) {
					statistics.getError().incrementAndGet();
				}
			} else {
				consumer.accept(data);
			}
		};
	}

	public static <E, Resource> ResourceableConsumer<E> createDelegate(RunnableStatisticsHolder pStatistics, TransactionConsumer<E, Resource> pConsumer) {

		return new ResourceableConsumer<E>() {
			private TransactionConsumer<E, Resource> consumer = pConsumer;
			private RunnableStatistics statistics;
			private int commitCount;
			private volatile Resource resource;
			private AtomicInteger total = new AtomicInteger(0);
			private AtomicInteger success = new AtomicInteger(0);
			private AtomicInteger error = new AtomicInteger(0);
			private AtomicInteger commit = new AtomicInteger(0);
			private AtomicReference<Throwable> processError = new AtomicReference<>();

			@Override
			public void open() {
				try {
					int commitCount = consumer.commitCount();
					if (commitCount <= 0) {
						commitCount = TransactionConsumer.DEFAULT_COMMIT_COUNT;
					}
					this.commitCount = commitCount;
					this.statistics = pStatistics.runnableStatistics();;
					this.resource = consumer.openResource();
				} catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			}

			@Override
			public void close() {
				if (resource != null) {
					if (commit.get() > 0) {
						try {
							if (processError.get() != null) {
								throw processError.get();
							}
							consumer.commitResource(resource);
							success.addAndGet(commit.get());
							if (statistics != null) {
								statistics.getSuccess().addAndGet(commit.get());
							}
							log.trace("处理/提交成功. total: {}, success: {}, error: {}", total.get(), success.get(), error.get());
						} catch (Throwable e) {
							error.addAndGet(commit.get());
							if (statistics != null) {
								statistics.getError().addAndGet(commit.get());
							}
							try {
								log.trace("处理/提交失败. total: {}, success: {}, error: {}", total.get(), success.get(), error.get());
								log.error("处理/提交失败, 准备回滚.", e);
								consumer.rollbackResource(resource);
							} catch (Throwable ex) {
								log.error("回滚失败", ex);
							}
						}
					}
					log.trace("处理完成");
					consumer.closeResource(resource);
				}
			}

			@Override
			public void accept(E data) {
				try {
					total.incrementAndGet();
					if (statistics != null) {
						statistics.getTotal().incrementAndGet();
					}
					commit.incrementAndGet();
					try {
						consumer.processData(resource, data);
					} catch (Throwable e) {
						if (processError.get() != null) {
							processError.get().addSuppressed(e);
						} else {
							processError.set(e);
						}
					}
					if (commit.get() >= commitCount) {
						if (processError.get() != null) {
							throw processError.get();
						}
						consumer.commitResource(resource);
						success.addAndGet(commit.get());

						if (statistics != null) {
							statistics.getSuccess().addAndGet(commit.get());
						}
						log.trace("处理/提交成功. total: {}, success: {}, error: {}", total.get(), success.get(), error.get());
						commit.set(0);
					}
				} catch (Throwable e) {
					error.addAndGet(commit.get());
					if (statistics != null) {
						statistics.getError().addAndGet(commit.get());
					}
					commit.set(0);
					try {
						log.trace("处理/提交失败. total: {}, success: {}, error: {}", total.get(), success.get(), error.get());
						log.error("处理/提交失败, 准备回滚.", e);
						consumer.rollbackResource(resource);
					} catch (Throwable ex) {
						log.error("回滚失败", ex);
					}
				}
			}
		};
	}


}

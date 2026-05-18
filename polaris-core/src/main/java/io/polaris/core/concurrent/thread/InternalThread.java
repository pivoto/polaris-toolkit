package io.polaris.core.concurrent.thread;

/**
 * @author Qt
 */
public class InternalThread extends Thread implements InternalThreadSpec {

	private InternalThreadLocalMap threadLocalMap;

	public InternalThread() {
	}

	public InternalThread(Runnable target) {
		super(InternalRunnable.of(target));
	}

	public InternalThread(ThreadGroup group, Runnable target) {
		super(group, InternalRunnable.of(target));
	}

	public InternalThread(String name) {
		super(name);
	}

	public InternalThread(ThreadGroup group, String name) {
		super(group, name);
	}

	public InternalThread(Runnable target, String name) {
		super(InternalRunnable.of(target), name);
	}

	public InternalThread(ThreadGroup group, Runnable target, String name) {
		super(group, InternalRunnable.of(target), name);
	}

	public InternalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, InternalRunnable.of(target), name, stackSize);
	}

	@Override
	public final InternalThreadLocalMap threadLocalMap() {
		return threadLocalMap;
	}

	@Override
	public final void setThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
		this.threadLocalMap = threadLocalMap;
	}

}

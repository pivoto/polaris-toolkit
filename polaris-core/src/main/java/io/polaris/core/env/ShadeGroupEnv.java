package io.polaris.core.env;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since Oct 01, 2025
 */
class ShadeGroupEnv extends GroupEnv {

	private final Predicate<Env> filter;

	ShadeGroupEnv(String name, Env runtime, CopyOnWriteArrayList<Env> envList, Predicate<Env> filter) {
		super(name, runtime, envList);
		this.filter = filter == null ? e -> true : filter;
	}

	@Override
	public void addEnvFirst(Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addEnvLast(Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeEnv(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int removeAllEnv(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replaceEnv(String name, Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addEnvBefore(String name, Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addEnvAfter(String name, Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearEnv() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean filter(Env env) {
		return filter.test(env);
	}

}

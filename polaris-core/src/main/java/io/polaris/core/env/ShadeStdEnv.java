package io.polaris.core.env;

import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since Oct 01, 2025
 */
class ShadeStdEnv extends StdEnv {

	ShadeStdEnv(String name, GroupEnv runtime, DelegateEnv defaults, Predicate<Env> filter) {
		super(name, runtime.shade(filter), defaults);
	}

	@Override
	public void setAppEnv(Env appEnv) {
		throw new UnsupportedOperationException();
	}

	@Override
	public StdEnv withCustomizer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keys() {
		return super.keys();
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
	public void setDefaults(Env defaults) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaults(String key, String value) {
		throw new UnsupportedOperationException();
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
	public boolean addEnvBefore(String name, Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addEnvAfter(String name, Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replaceEnv(String name, Env env) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeEnv(String name) {
		throw new UnsupportedOperationException();
	}

}

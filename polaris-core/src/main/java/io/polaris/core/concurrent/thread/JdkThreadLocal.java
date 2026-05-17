package io.polaris.core.concurrent.thread;

import java.util.Objects;
import java.util.function.Supplier;

import io.polaris.core.concurrent.ThreadLocalSpec;

/**
 * @author Qt
 * @since May 17, 2026
 */
public class JdkThreadLocal<T> extends ThreadLocal<T> implements ThreadLocalSpec<T> {
	private final String name;

	public JdkThreadLocal(String name) {
		this.name = name;
	}

	public JdkThreadLocal() {
		this("");
	}

	public static <T> ThreadLocalSpec<T> ofInitial(String name, Supplier<? extends T> supplier) {
		return new JdkThreadLocal.SuppliedThreadLocal<>(name, supplier);
	}

	public static <T> ThreadLocalSpec<T> ofInitial(Supplier<? extends T> supplier) {
		return new JdkThreadLocal.SuppliedThreadLocal<>(supplier);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}


	static final class SuppliedThreadLocal<T> extends JdkThreadLocal<T> {

		private final Supplier<? extends T> supplier;

		SuppliedThreadLocal(String name, Supplier<? extends T> supplier) {
			super(name);
			this.supplier = Objects.requireNonNull(supplier);
		}

		SuppliedThreadLocal(Supplier<? extends T> supplier) {
			this.supplier = Objects.requireNonNull(supplier);
		}

		@Override
		protected T initialValue() {
			return supplier.get();
		}
	}
}

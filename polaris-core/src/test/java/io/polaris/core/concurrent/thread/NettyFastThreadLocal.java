package io.polaris.core.concurrent.thread;

import java.util.Objects;
import java.util.function.Supplier;

import io.netty.util.concurrent.FastThreadLocal;
import io.polaris.core.concurrent.ThreadLocalSpec;

/**
 * @author Qt
 * @since May 17, 2026
 */
public class NettyFastThreadLocal <T> extends FastThreadLocal<T> implements ThreadLocalSpec<T> {
	private final String name;

	public NettyFastThreadLocal(String name) {
		this.name = name;
	}

	public NettyFastThreadLocal() {
		this.name = "";
	}

	public static <T> ThreadLocalSpec<T> ofInitial(String name, Supplier<? extends T> supplier) {
		return new SuppliedThreadLocal<>(name, supplier);
	}

	public static <T> ThreadLocalSpec<T> ofInitial(Supplier<? extends T> supplier) {
		return new SuppliedThreadLocal<>(supplier);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}


	static final class SuppliedThreadLocal<T> extends NettyFastThreadLocal<T> {

		private final Supplier<? extends T> supplier;

		public SuppliedThreadLocal(String name, Supplier<? extends T> supplier) {
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

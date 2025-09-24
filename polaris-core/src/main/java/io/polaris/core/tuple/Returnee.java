package io.polaris.core.tuple;

import java.io.Serializable;

import io.polaris.core.function.ThrowableExecution;
import io.polaris.core.function.ThrowableSupplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Sep 09, 2025
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Returnee<R, T extends Throwable> extends Tuple2<R, T> implements Serializable, Tuple {
	private static final long serialVersionUID = 1L;

	public Returnee(final R value, final T throwable) {
		super(value, throwable);
	}

	public Returnee() {
	}

	public static <R, T extends Throwable> Returnee<R, T> of(R value, T throwable) {
		return new Returnee<>(value, throwable);
	}

	public static <R, T extends Throwable> Returnee<R, T> of(Returnee<? extends R, ? extends T> value, T throwable) {
		if (value == null) {
			return new Returnee<>((R) null, throwable);
		}
		T t = value.getThrowable();
		if (t != null) {
			if (throwable != null) {
				throwable.addSuppressed(t);
				return new Returnee<>(value.getValue(), throwable);
			}
			return new Returnee<>(value.getValue(), t);
		}
		return new Returnee<>(value.getValue(), throwable);
	}


	public static <T> Returnee<Void, Throwable> of(ThrowableExecution executable) {
		try {
			executable.execute();
			return Returnee.of((Void) null, null);
		} catch (Throwable e) {
			return Returnee.of((Void) null, e);
		}
	}

	public static <T> Returnee<T, Throwable> of(ThrowableSupplier<T> supplier) {
		try {
			return Returnee.of(supplier.get(), null);
		} catch (Throwable e) {
			return Returnee.of((T) null, e);
		}
	}


	public R getValue() {
		return getFirst();
	}

	public T getThrowable() {
		return getSecond();
	}

	public void setValue(R value) {
		setFirst(value);
	}

	public void setThrowable(T throwable) {
		setSecond(throwable);
	}

	public void throwOnThrowable() throws T {
		T throwable = getThrowable();
		if (throwable != null) {
			throw throwable;
		}
	}

	public R get() throws T {
		throwOnThrowable();
		return getValue();
	}

	@Override
	public String toString() {
		T throwable = getThrowable();
		if (throwable != null) {
			return "throws " + throwable.toString();
		}
		return "return " + getValue();
	}


}

package io.polaris.core.ulid;

/**
 * A class that generates ULIDs.
 * <p>
 * Both types of ULID can be easily created by this generator, i.e. monotonic
 * and non-monotonic.
 *
 * @author Qt
 * @see <a href="https://github.com/ulid/spec">ULID Specification</a>
 * @see <a href="https://github.com/f4b6a3/ulid-creator">ULID Creator</a>
 * @since 1.8
 */
public final class UlidCreator {

	private UlidCreator() {
	}

	/**
	 * Returns a ULID.
	 *
	 * @return a ULID
	 */
	public static Ulid getUlid() {
		return UlidFactoryHolder.INSTANCE.create();
	}

	/**
	 * Returns a ULID with a given time.
	 *
	 * @param time a number of milliseconds since 1970-01-01 (Unix epoch).
	 * @return a ULID
	 */
	public static Ulid getUlid(final long time) {
		return UlidFactoryHolder.INSTANCE.create(time);
	}

	/**
	 * Returns a Monotonic ULID.
	 *
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid() {
		return MonotonicFactoryHolder.INSTANCE.create();
	}

	/**
	 * Returns a Monotonic ULID with a given time.
	 *
	 * @param time a number of milliseconds since 1970-01-01 (Unix epoch).
	 * @return a ULID
	 */
	public static Ulid getMonotonicUlid(final long time) {
		return MonotonicFactoryHolder.INSTANCE.create(time);
	}

	private static class UlidFactoryHolder {
		static final UlidFactory INSTANCE = UlidFactory.newInstance();
	}

	private static class MonotonicFactoryHolder {
		static final UlidFactory INSTANCE = UlidFactory.newMonotonicInstance();
	}
}

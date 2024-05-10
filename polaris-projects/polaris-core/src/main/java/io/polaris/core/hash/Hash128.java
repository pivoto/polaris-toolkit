package io.polaris.core.hash;

/**
 * @author Qt
 * @since  Aug 01, 2023
 */
@FunctionalInterface
public interface Hash128<T> extends Hash<T> {
	/**
	 * 计算Hash值
	 *
	 * @param t 对象
	 * @return hash
	 */
	Number128 hash128(T t);

	@Override
	default Number hash(T t) {
		return hash128(t);
	}
}

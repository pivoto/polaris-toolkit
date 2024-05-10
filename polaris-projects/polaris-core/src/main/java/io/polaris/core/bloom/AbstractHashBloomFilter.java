package io.polaris.core.bloom;

import io.polaris.core.hash.Hashing;

import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 01, 2023
 */
public abstract class AbstractHashBloomFilter implements BloomFilter {

	private static final Function<String, Integer> DEFAULT_HASH_FUNC = Hashing::javaHash;
	private final Function<String, Integer> hashFunction;
	/** 当前过滤器预先开辟的最大空间,通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加） */
	protected final int hashCapacity;
	/** 预计所要包含的记录数 */
	protected final int expectSize;

	/**
	 * @param hashCapacity 当前过滤器预先开辟的最大空间, 通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加）
	 */
	public AbstractHashBloomFilter(int hashCapacity) {
		this(hashCapacity, 0);
	}

	/**
	 * @param hashCapacity 当前过滤器预先开辟的最大空间, 通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加）
	 * @param expectSize   预计所要包含的记录数
	 */
	protected AbstractHashBloomFilter(int hashCapacity, int expectSize) {
		this(null, hashCapacity, expectSize);
	}

	/**
	 * @param hashFunction 哈希函数集
	 * @param hashCapacity 当前过滤器预先开辟的最大空间, 通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加）
	 * @param expectSize   预计所要包含的记录数
	 */
	public AbstractHashBloomFilter(Function<String, Integer> hashFunction, int hashCapacity, int expectSize) {
		this.hashFunction = hashFunction == null ? DEFAULT_HASH_FUNC : hashFunction;
		this.hashCapacity = hashCapacity;
		this.expectSize = expectSize;
	}

	protected abstract boolean getBit(int position);

	protected abstract void setBit(int position);

	@Override
	public boolean contains(String data) {
		int hash = hash(data);
		return contains(hash);
	}

	@Override
	public boolean add(String data) {
		int hash = hash(data);
		if (contains(hash)) {
			return false;
		}
		setBit(hash);
		return true;
	}

	protected boolean contains(int hash) {
		if (!getBit(hash)) {
			return false;
		}
		return true;
	}

	/**
	 * 预估的过滤器错误率 False Negative Rate (FNR).
	 */
	protected double estimateFnr() {
		if (expectSize <= 0) {
			return 0;
		}
		// (1 - e^(-k * n / m)) ^ k
		return Math.pow((1 - Math.exp(-(double) expectSize / hashCapacity)), 1);
	}

	/**
	 * 计算Hash值
	 *
	 * @param data 被计算Hash的字符串
	 * @return
	 */
	protected int hash(String data) {
		int hash = hashFunction.apply(data);
		return Math.abs(hash % hashCapacity);
	}

}

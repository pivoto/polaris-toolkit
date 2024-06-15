package io.polaris.core.bloom;

import io.polaris.core.hash.Hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 01, 2023
 */
public abstract class AbstractMultiHashBloomFilter implements BloomFilter {

	private static final Function<String, Integer>[] DEFAULT_HASH_FUNCS;

	static {
		List<Function<String, Integer>> list = new ArrayList<>();
		list.add(Hashing::javaHash);
		list.add(Hashing::murmur32);
		list.add(Hashing::rsHash);
		list.add(Hashing::jsHash);

		list.add(Hashing::pjwHash);
		list.add(Hashing::elfHash);
		list.add(Hashing::bkdrHash);
		list.add(Hashing::sdbmHash);

		list.add(Hashing::djbHash);
		list.add(Hashing::dekHash);
		list.add(Hashing::apHash);
		list.add(Hashing::fnvHash);

		list.add(Hashing::bernstein);
		list.add(Hashing::cityHash32);
		list.add(Hashing::metroHash32);
		list.add(Hashing::ketamaHash);

		DEFAULT_HASH_FUNCS = new Function[list.size()];
		list.toArray(DEFAULT_HASH_FUNCS);
	}

	private final Function<String, Integer>[] hashFunctions;
	/** 哈希计算函数的个数 */
	protected final int hashCount;
	/** 当前过滤器预先开辟的最大空间,通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加） */
	protected final int hashCapacity;
	/** 预计所要包含的记录数 */
	protected final int expectSize;

	/**
	 * @param hashCount    哈希计算函数的个数
	 * @param hashCapacity 当前过滤器预先开辟的最大空间, 通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加）
	 */
	public AbstractMultiHashBloomFilter(int hashCount, int hashCapacity) {
		this(hashCount, hashCapacity, 0);
	}

	/**
	 * @param hashCount    哈希计算函数的个数
	 * @param hashCapacity 当前过滤器预先开辟的最大空间, 通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加）
	 * @param expectSize   预计所要包含的记录数
	 */
	protected AbstractMultiHashBloomFilter(int hashCount, int hashCapacity, int expectSize) {
		this(null, hashCount, hashCapacity, expectSize);
	}

	/**
	 * @param hashFunctions 哈希函数集
	 * @param hashCount     哈希计算函数的个数
	 * @param hashCapacity  当前过滤器预先开辟的最大空间, 通常要比预计存入的记录多一倍，同时要考虑哈希计算次数（同比增加）
	 * @param expectSize    预计所要包含的记录数
	 */
	public AbstractMultiHashBloomFilter(Function<String, Integer>[] hashFunctions, int hashCount, int hashCapacity, int expectSize) {
		this.hashFunctions = hashFunctions == null || hashFunctions.length == 0 ? DEFAULT_HASH_FUNCS : hashFunctions;
		this.hashCount = hashCount;
		this.hashCapacity = hashCapacity;
		this.expectSize = expectSize;
	}

	protected abstract boolean getBit(int position);

	protected abstract void setBit(int position);

	@Override
	public boolean contains(String data) {
		int[] hashes = hash(data);
		return contains(hashes);
	}

	@Override
	public boolean add(String data) {
		int[] hashes = hash(data);
		if (contains(hashes)) {
			return false;
		}
		for (int hash : hashes) {
			setBit(hash);
		}
		return true;
	}

	protected boolean contains(int[] hashes) {
		for (int hash : hashes) {
			if (!getBit(hash)) {
				return false;
			}
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
		return Math.pow((1 - Math.exp(-hashCount * (double) expectSize / hashCapacity)), hashCount);
	}

	/**
	 * 生成多种类型Hash值
	 *
	 * @param data 被计算Hash的字符串
	 * @return
	 */
	protected int[] hash(String data) {
		int[] rs = new int[hashCount];
		for (int i = 0; i < hashCount; i++) {
			int hash = hash(data, i);
			rs[i] = Math.abs(hash % hashCapacity);
		}
		return rs;
	}

	/**
	 * 计算Hash值
	 *
	 * @param data 被计算Hash的字符串
	 * @param seq  Hash算法序号
	 * @return Hash值
	 */
	protected int hash(String data, int seq) {
		if (seq >= 0 && seq < hashFunctions.length) {
			return hashFunctions[seq].apply(data);
		} else {
			return 0;
		}
	}

}

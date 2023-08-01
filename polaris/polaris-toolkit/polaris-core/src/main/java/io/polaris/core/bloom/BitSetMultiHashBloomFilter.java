package io.polaris.core.bloom;

import java.util.BitSet;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Aug 01, 2023
 */
public class BitSetMultiHashBloomFilter extends AbstractMultiHashBloomFilter implements BloomFilter {
	private final BitSet bitSet;

	public BitSetMultiHashBloomFilter(int hashCount, int hashCapacity) {
		super(hashCount, hashCapacity);
		this.bitSet = new BitSet(this.hashCapacity);
	}

	public BitSetMultiHashBloomFilter(int hashCount, int hashCapacity, int expectSize) {
		super(hashCount, hashCapacity, expectSize);
		this.bitSet = new BitSet(hashCapacity);
	}

	public BitSetMultiHashBloomFilter(Function<String, Integer>[] hashFunctions, int hashCount, int hashCapacity, int expectSize) {
		super(hashFunctions, hashCount, hashCapacity, expectSize);
		this.bitSet = new BitSet(hashCapacity);
	}

	@Override
	protected boolean getBit(int position) {
		return bitSet.get(position);
	}

	@Override
	protected void setBit(int position) {
		bitSet.set(position);
	}


}

package io.polaris.core.bloom;

import java.util.BitSet;
import java.util.function.Function;

/**
 * @author Qt
 * @since  Aug 01, 2023
 */
public class BitSetHashBloomFilter extends AbstractHashBloomFilter implements BloomFilter {
	private final BitSet bitSet;

	public BitSetHashBloomFilter(int hashCapacity) {
		super(hashCapacity);
		this.bitSet = new BitSet(hashCapacity);
	}

	public BitSetHashBloomFilter(int hashCapacity, int expectSize) {
		super(hashCapacity, expectSize);
		this.bitSet = new BitSet(hashCapacity);
	}

	public BitSetHashBloomFilter(Function<String, Integer> hashFunction, int hashCapacity, int expectSize) {
		super(hashFunction, hashCapacity, expectSize);
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

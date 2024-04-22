package io.polaris.core.guid;

/**
 * @author Qt
 * @since 1.8,  Apr 22, 2024
 */
public class FixedNodeStrategy implements GuidNodeStrategy {
	private final int nodeId;
	private final int bizSize;

	private FixedNodeStrategy(int nodeId, int bizSize) {
		if (bizSize > 12) {
			throw new IllegalArgumentException();
		}
		this.nodeId = nodeId;
		this.bizSize = bizSize;
	}

	public static FixedNodeStrategy newInstance(int nodeId, int bizSize) {
		return new FixedNodeStrategy(nodeId, bizSize);
	}


	@Override
	public int bitSize() {
		return bizSize;
	}

	@Override
	public int nodeId() {
		return nodeId;
	}
}

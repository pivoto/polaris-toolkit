package io.polaris.core.bloom;

/**
 * Bloom filter 是由 Howard Bloom 在 1970 年提出的二进制向量数据结构，它具有很好的空间和时间效率，被用来检测一个元素是不是集合中的一个成员。
 * 如果检测结果为是，该元素不一定在集合中；但如果检测结果为否，该元素一定不在集合中。
 * 因此Bloom filter具有100%的召回率。这样每个检测请求返回有“在集合内（可能错误）”和“不在集合内（绝对不在集合内）”两种情况。
 *
 * @author Qt
 * @since 1.8,  Aug 01, 2023
 */
public interface BloomFilter {

	/**
	 * 使用布隆过滤器判断字符串是否存在
	 */
	boolean contains(String data);

	/**
	 * 布隆过滤器中添加目标字符串。已存在则返回false
	 */
	boolean add(String data);
}

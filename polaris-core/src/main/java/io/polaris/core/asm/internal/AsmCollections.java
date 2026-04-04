package io.polaris.core.asm.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * ASM 集合工具类，提供集合相关的辅助方法
 * <p>
 * 包含分桶、反转、过滤、转换等集合操作
 *
 * @author Qt
 * @since May 10, 2024
 */
public class AsmCollections {
	// 私有构造器，防止实例化
	private AsmCollections() {
	}

	/**
	 * 将集合分桶，根据转换器生成的键值对集合元素进行分组
	 *
	 * @param c 待分桶的集合
	 * @param t 转换器，用于生成键值
	 * @return 分桶后的 Map，键为转换后的值，值为具有相同键的元素列表
	 */
	public static Map bucket(Collection c, Transformer t) {
		Map buckets = new HashMap();
		// 遍历集合中的每个元素
		for (Iterator it = c.iterator(); it.hasNext(); ) {
			Object value = (Object) it.next();
			// 使用转换器生成键值
			Object key = t.transform(value);
			// 获取该键对应的桶
			List bucket = (List) buckets.get(key);
			// 如果桶不存在，创建新桶
			if (bucket == null) {
				buckets.put(key, bucket = new LinkedList());
			}
			// 将元素添加到对应的桶中
			bucket.add(value);
		}
		return buckets;
	}

	/**
	 * 反转 Map，将源 Map 的键值对调后放入目标 Map
	 *
	 * @param source 源 Map
	 * @param target 目标 Map，用于存储反转后的结果
	 */
	public static void reverse(Map source, Map target) {
		// 遍历源 Map 的所有键
		for (Iterator it = source.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			// 将键值对调后存入目标 Map
			target.put(source.get(key), key);
		}
	}

	/**
	 * 根据谓词过滤集合
	 * <p>
	 * 移除集合中所有不满足谓词条件的元素
	 *
	 * @param c 待过滤的集合
	 * @param p 谓词，用于判断元素是否保留
	 * @return 过滤后的集合（原地修改）
	 */
	public static Collection filter(Collection c, Predicate p) {
		Iterator it = c.iterator();
		// 遍历并移除不满足条件的元素
		while (it.hasNext()) {
			if (!p.test(it.next())) {
				it.remove();
			}
		}
		return c;
	}

	/**
	 * 转换集合，将集合中的每个元素通过转换器进行转换
	 *
	 * @param c 待转换的集合
	 * @param t 转换器，用于转换每个元素
	 * @return 转换后的新列表
	 */
	public static List transform(Collection c, Transformer t) {
		// 创建与源集合容量相同的新列表
		List result = new ArrayList(c.size());
		// 遍历并转换每个元素
		for (Iterator it = c.iterator(); it.hasNext(); ) {
			result.add(t.transform(it.next()));
		}
		return result;
	}

	/**
	 * 获取列表的索引 Map，将列表元素映射到其索引位置
	 *
	 * @param list 待处理的列表
	 * @return Map，键为列表元素，值为元素的索引位置
	 */
	public static Map getIndexMap(List list) {
		Map indexes = new HashMap();
		int index = 0;
		// 遍历列表，建立元素到索引的映射
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			indexes.put(it.next(), Integer.valueOf(index++));
		}
		return indexes;
	}
}

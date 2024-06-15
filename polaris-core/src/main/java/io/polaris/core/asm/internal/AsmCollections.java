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
 * @author Qt
 * @since May 10, 2024
 */
public class AsmCollections {
	private AsmCollections() {
	}

	public static Map bucket(Collection c, Transformer t) {
		Map buckets = new HashMap();
		for (Iterator it = c.iterator(); it.hasNext(); ) {
			Object value = (Object) it.next();
			Object key = t.transform(value);
			List bucket = (List) buckets.get(key);
			if (bucket == null) {
				buckets.put(key, bucket = new LinkedList());
			}
			bucket.add(value);
		}
		return buckets;
	}

	public static void reverse(Map source, Map target) {
		for (Iterator it = source.keySet().iterator(); it.hasNext(); ) {
			Object key = it.next();
			target.put(source.get(key), key);
		}
	}

	public static Collection filter(Collection c, Predicate p) {
		Iterator it = c.iterator();
		while (it.hasNext()) {
			if (!p.test(it.next())) {
				it.remove();
			}
		}
		return c;
	}

	public static List transform(Collection c, Transformer t) {
		List result = new ArrayList(c.size());
		for (Iterator it = c.iterator(); it.hasNext(); ) {
			result.add(t.transform(it.next()));
		}
		return result;
	}

	public static Map getIndexMap(List list) {
		Map indexes = new HashMap();
		int index = 0;
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			indexes.put(it.next(), Integer.valueOf(index++));
		}
		return indexes;
	}
}

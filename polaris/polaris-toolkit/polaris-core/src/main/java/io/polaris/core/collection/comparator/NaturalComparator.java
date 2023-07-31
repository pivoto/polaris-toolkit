package io.polaris.core.collection.comparator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Qt
 * @since 1.8
 */
public class NaturalComparator implements Comparator<Comparable<?>>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final NaturalComparator INSTANCE = new NaturalComparator();

	private NaturalComparator() {
	}

	@Override
	public int compare(final Comparable obj1, final Comparable obj2) {
		return obj1.compareTo(obj2);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == null || this == obj;
	}

	private Object readResolve() throws ObjectStreamException {
		return NaturalComparator.INSTANCE;
	}
}

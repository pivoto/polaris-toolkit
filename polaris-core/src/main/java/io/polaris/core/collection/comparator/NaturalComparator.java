package io.polaris.core.collection.comparator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Comparator;

import io.polaris.core.annotation.Legacy;

/**
 * @author Qt
 */
@Legacy
public class NaturalComparator implements Comparator<Comparable<?>>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final NaturalComparator INSTANCE = new NaturalComparator();

	private NaturalComparator() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compare(final Comparable obj1, final Comparable obj2) {
		return obj1.compareTo(obj2);
	}

	@Override
	@SuppressWarnings("EqualsDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		return obj == null || this == obj;
	}

	private Object readResolve() throws ObjectStreamException {
		return NaturalComparator.INSTANCE;
	}
}

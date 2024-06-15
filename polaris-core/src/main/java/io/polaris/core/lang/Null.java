package io.polaris.core.lang;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * @author Qt
 * @since 1.8
 */
public class Null implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Null INSTANCE = new Null();

	private Null() {
	}

	public static Null getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == null || this == obj;
	}

	@Override
	public String toString() {
		return "null";
	}

	private Object readResolve() throws ObjectStreamException {
		return Null.INSTANCE;
	}

//	private Object writeReplace() throws ObjectStreamException {
//		return Null.instance;
//	}
//
//	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//		out.defaultWriteObject();
//	}
//
//	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
//		in.defaultReadObject();
//	}

}

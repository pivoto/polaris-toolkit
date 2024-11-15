package io.polaris.core.io;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import io.polaris.core.collection.Iterables;

/**
 * @author Qt
 * @since 1.8
 */
public class Serializations {

	public static void serialize(@Nullable Object object, OutputStream out) throws IOException {
		if (object == null) {
			return;
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(out);) {
			oos.writeObject(object);
			oos.flush();
		}
	}

	public static Object deserialize(InputStream in) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(in)) {
			return ois.readObject();
		}
	}

	@Nullable
	public static byte[] serialize(@Nullable Object object) {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return bos.toByteArray();
	}

	@Nullable
	public static Object deserialize(@Nullable byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
			return ois.readObject();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	@Nullable
	public static Object deserialize(@Nullable byte[] bytes, Class<?>... acceptClasses) {
		if (bytes == null) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				 ValidateObjectInputStream vis = new ValidateObjectInputStream(ois, acceptClasses);) {
			return vis.readObject();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	@Nullable
	public static Object deserializeWithBlacklist(@Nullable byte[] bytes, Class<?>... refuseClasses) {
		return deserialize(bytes, new Class[0], refuseClasses);
	}

	@Nullable
	public static Object deserialize(@Nullable byte[] bytes, Class<?>[] acceptClasses, Class<?>[] refuseClasses) {
		if (bytes == null) {
			return null;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				 ValidateObjectInputStream vis = new ValidateObjectInputStream(ois, acceptClasses);) {
			vis.refuse(refuseClasses);
			return vis.readObject();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	public static <T> T clone(T obj) {
		if (!(obj instanceof Serializable)) {
			return null;
		}
		return (T) deserialize(serialize(obj));
	}

	public static class ValidateObjectInputStream extends ObjectInputStream {

		private Set<String> whiteClassSet;
		private Set<String> blackClassSet;

		public ValidateObjectInputStream(InputStream inputStream, Class<?>... acceptClasses) throws IOException {
			super(inputStream);
			accept(acceptClasses);
		}

		public void refuse(Class<?>... refuseClasses) {
			if (refuseClasses == null) {
				return;
			}
			if (null == this.blackClassSet) {
				this.blackClassSet = new HashSet<>();
			}
			for (Class<?> acceptClass : refuseClasses) {
				this.blackClassSet.add(acceptClass.getName());
			}
		}

		public void accept(Class<?>... acceptClasses) {
			if (acceptClasses == null) {
				return;
			}
			if (null == this.whiteClassSet) {
				this.whiteClassSet = new HashSet<>();
			}
			for (Class<?> acceptClass : acceptClasses) {
				this.whiteClassSet.add(acceptClass.getName());
			}
		}

		/**
		 * 只允许反序列化SerialObject class
		 */
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			validateClassName(desc.getName());
			return super.resolveClass(desc);
		}

		private void validateClassName(String className) throws InvalidClassException {
			// 黑名单
			if (Iterables.isNotEmpty(this.blackClassSet)) {
				if (this.blackClassSet.contains(className)) {
					throw new InvalidClassException("Unauthorized deserialization attempt by black list", className);
				}
			}

			if (Iterables.isEmpty(this.whiteClassSet)) {
				return;
			}
			if (className.startsWith("java.")) {
				// java中的类默认在白名单中
				return;
			}
			if (this.whiteClassSet.contains(className)) {
				return;
			}

			throw new InvalidClassException("Unauthorized deserialization attempt", className);
		}
	}
}

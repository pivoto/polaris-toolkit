package io.polaris.core.io;

import io.polaris.core.collection.Iterables;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
public class Serializations {

	@Nullable
	public static byte[] serialize(@Nullable Object object) {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return baos.toByteArray();
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
			if (null == this.blackClassSet) {
				this.blackClassSet = new HashSet<>();
			}
			for (Class<?> acceptClass : refuseClasses) {
				this.blackClassSet.add(acceptClass.getName());
			}
		}

		public void accept(Class<?>... acceptClasses) {
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

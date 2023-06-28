package io.polaris.core.io;

import javax.annotation.Nullable;
import java.io.*;

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
}

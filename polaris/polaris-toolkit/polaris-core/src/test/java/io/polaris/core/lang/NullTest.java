package io.polaris.core.lang;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class NullTest {

	@Test
	void test01() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		oos.writeObject(Null.getInstance());
		oos.flush();

		byte[] bytes = bos.toByteArray();
		System.out.println(new String(bytes));
		System.out.println(Base64.getEncoder().encodeToString(bytes));

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object o = ois.readObject();
		System.out.println(o);
		System.out.println(o == Null.getInstance());
	}
}

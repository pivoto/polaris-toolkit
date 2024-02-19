package io.polaris.core.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NullTest {

	@Test
	void test01() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);

		oos.writeObject(Null.getInstance());
		oos.flush();

		byte[] bytes = bos.toByteArray();
		TestConsole.println(new String(bytes));
		TestConsole.println(Base64.getEncoder().encodeToString(bytes));

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object o = ois.readObject();
		Assertions.assertSame(Null.getInstance(), o);
	}
}

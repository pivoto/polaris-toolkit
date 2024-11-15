package io.polaris.core.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import io.polaris.core.io.Consoles;
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
		Consoles.println(new String(bytes));
		String msg = Base64.getEncoder().encodeToString(bytes);
		Consoles.println(msg);

		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		Object o = ois.readObject();
		Assertions.assertSame(Null.getInstance(), o);
	}
}

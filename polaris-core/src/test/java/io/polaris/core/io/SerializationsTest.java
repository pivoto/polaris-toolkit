package io.polaris.core.io;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.random.Randoms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SerializationsTest {

	String tmpDir = GlobalStdEnv.get(StdKeys.JAVA_IO_TMPDIR);
	String tmpFile = tmpDir + File.separator + "test.dat";

	@Test
	void test00() throws IOException {
		Consoles.log("file: {} | {}", new File(tmpFile).exists(), new File(tmpFile).getAbsoluteFile());
	}

	@Test
	void test01() throws IOException {
		byte[] bytes = Serializations.serialize(new S01());
		IO.writeBytes(new File(tmpFile), bytes);
	}

	@Test
	void test02() throws IOException {
		byte[] bytes = IO.toBytes(new File(tmpFile));
		Consoles.log("deserialize: {}", Serializations.deserialize(bytes));
	}

	@Test
	void test99() throws IOException {
		new File(tmpFile).delete();
	}


	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class S01 implements Serializable {
		private static final long serialVersionUID = 1L;
		static S01 instance = S01.builder().name("Singleton").build();
		private String name = Randoms.randomNumberString(5);

		private Object readResolve() throws ObjectStreamException {
			Consoles.log("readResolve...");
			return instance;
		}

		private Object writeReplace() throws ObjectStreamException {
			Consoles.log("writeReplace...");
			return instance;
		}

		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
			Consoles.log("writeObject...");
			out.defaultWriteObject();
		}

		private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
			Consoles.log("readObject...");
			in.defaultReadObject();
		}
	}
}

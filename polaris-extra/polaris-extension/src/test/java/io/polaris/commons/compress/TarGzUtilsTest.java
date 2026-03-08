package io.polaris.commons.compress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import io.polaris.core.collection.Lists;
import io.polaris.core.consts.SystemProperty;
import io.polaris.core.io.Consoles;
import io.polaris.core.io.IO;
import io.polaris.core.tuple.Tuple2;
import org.junit.jupiter.api.Test;

class TarGzUtilsTest {

	@Test
	void test_tgz_write1() throws IOException {
		Consoles.log(SystemProperty.USER_DIR.value());
		File targz = new File("/data/test1.tar.gz");
		try (OutputStream out = new FileOutputStream(targz);) {
			TarGzUtils.writeTarGzip(out, new File("/data/test"));
		}
	}

	@Test
	void test_tgz_write2() throws IOException {
		File targz = new File("/data/test2.tar.gz");
		try (OutputStream out = new FileOutputStream(targz);) {
			TarGzUtils.writeTarGzip(out,
				Lists.asList(
					Tuple2.of("test/a.txt", "测试".getBytes(StandardCharsets.UTF_8)),
					Tuple2.of("test/b/a.txt", "测试".getBytes(StandardCharsets.UTF_8)),
					Tuple2.of("test/b/c/a.txt", "测试".getBytes(StandardCharsets.UTF_8))
				)
			);
		}
	}

	@Test
	void test_tgz_read2() throws IOException {
		File targz = new File("/data/test2.tar.gz");
		TarGzUtils.readTarGz(targz, (name, input) -> {
			Consoles.println("{}", name);
			byte[] bytes = IO.toBytes(input);
			Consoles.println("{}", new String(bytes, StandardCharsets.UTF_8));
		});
		TarGzUtils.readTarGz(targz, new File("/data/test2"));
	}
}

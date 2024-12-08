package io.polaris.core.string;

import java.io.File;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvWriter;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvWriterSettings;

class KmpMatchersTest {
	public static final String KMP_MATCHER_TEST_CSV = "KmpMatcherTest.csv" ;

	@BeforeAll
	static void beforeAll() {
		String dir = KmpMatchersTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		dir = dir.replaceFirst("/target/test-classes/?$", "");
		File file = new File(dir + "/target/test-classes/"
			+ KmpMatchersTest.class.getPackage().getName().replace(".", "/") + "/"
			+ KMP_MATCHER_TEST_CSV);
		CsvWriterSettings settings = new CsvWriterSettings();
		CsvWriter writer = new CsvWriter(file, settings);
		writer.writeRow();
		String pattern = Strings.repeat(Randoms.randomString(50), 2);
		for (int i = 1; i <= 5; i++) {
			writer.writeRow(pattern, Randoms.randomString(100 * i) + pattern + Randoms.randomString(100 * i));
		}
		writer.flush();
		writer.close();
		Consoles.log("生成Csv文件：", file.getAbsolutePath());
	}


	@ParameterizedTest
	@CsvFileSource(resources = KMP_MATCHER_TEST_CSV)
	void test01(String pattern, String str) {
		Consoles.log("[KmpMatchers]indexOf:", KmpMatchers.indexOf(str, pattern), "pattern:", pattern, "str:", str);
		Consoles.log("[String]indexOf:", str.indexOf(pattern), "pattern:", pattern, "str:", str);
	}

	@ParameterizedTest
	@CsvFileSource(resources = KMP_MATCHER_TEST_CSV)
	void test02(String pattern, String str) {
		Consoles.log("[KmpMatchers]lastIndexOf:", KmpMatchers.lastIndexOf(str, pattern), "pattern:", pattern, "str:", str);
		Consoles.log("[String]lastIndexOf:", str.lastIndexOf(pattern), "pattern:", pattern, "str:", str);
	}


}

package io.polaris.core.os;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShellsTest {

	@Test
	void test01() throws IOException {
		TestConsole.println(Arrays.toString(Shells.parse("echo 12 34 56")));
		TestConsole.println(Arrays.toString(Shells.parse("echo '12 34' 56")));
		TestConsole.println(Arrays.toString(Shells.parse("echo '12 \"34' 56")));
		TestConsole.println(Arrays.toString(Shells.parse("echo \"12 34 \"56")));
		TestConsole.println(Arrays.toString(Shells.wrapCmd("echo \"12 34 \"56")));

		Assertions.assertEquals(4, Shells.parse("echo 12 34 56").length);
		Assertions.assertEquals(3, Shells.parse("echo '12 34' 56").length);
		Assertions.assertEquals(3, Shells.parse("echo '12 \"34' 56").length);
		Assertions.assertEquals(3, Shells.parse("echo \"12 34 \"56").length);
		Assertions.assertEquals(3, Shells.wrapCmd("echo \"12 34 \"56").length);

		TestConsole.println(new File(".").getAbsoluteFile());
		TestConsole.println(Shells.execute(new File("."), Shells.wrapCmd("echo %JAVA_HOME%")).getOutput());
		TestConsole.println(Shells.execute("gbk", new File("."), new String[]{"cmd", "/c", "dir"}).getOutput());
	}

	@Test
	void test02() throws IOException {
		File workDir = new File("d:/data/logs");
		String scriptFileName = "test.cmd";
		TestConsole.println(Shells.executeScriptContent("gbk", workDir, scriptFileName,
			new StringJoiner("\n")
				.add("@echo off")
				.add("echo ............................................")
				.add("dir")
				.add("echo ............................................")
				.toString()).getOutput());
		new File(workDir, scriptFileName).delete();
	}

	@Test
	void test03() {
		for (JStackThreadInfo info : JShells.getJStackInfo()) {
			TestConsole.println(info);
		}
	}

	@Test
	void test04() {
		for (JMapHistoInfo info : JShells.getJMapHistoInfo()) {
			TestConsole.println(info);
		}
	}

	@Test
	void test05() {
		TopExecutor.getInstance().start();
		TestConsole.println(TopExecutor.getInstance().getTopResult());
	}
}

package io.polaris.core.os;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ShellsTest {

	@Test
	void test01() throws IOException {
		String msg4 = Arrays.toString(Shells.parse("echo 12 34 56"));
		Consoles.println(msg4);
		String msg3 = Arrays.toString(Shells.parse("echo '12 34' 56"));
		Consoles.println(msg3);
		String msg2 = Arrays.toString(Shells.parse("echo '12 \"34' 56"));
		Consoles.println(msg2);
		String msg1 = Arrays.toString(Shells.parse("echo \"12 34 \"56"));
		Consoles.println(msg1);
		String msg = Arrays.toString(Shells.wrapCmd("echo \"12 34 \"56"));
		Consoles.println(msg);

		Assertions.assertEquals(4, Shells.parse("echo 12 34 56").length);
		Assertions.assertEquals(3, Shells.parse("echo '12 34' 56").length);
		Assertions.assertEquals(3, Shells.parse("echo '12 \"34' 56").length);
		Assertions.assertEquals(3, Shells.parse("echo \"12 34 \"56").length);
		Assertions.assertEquals(3, Shells.wrapCmd("echo \"12 34 \"56").length);

		Consoles.println(new File(".").getAbsoluteFile());
		Consoles.println(Shells.execute(new File("."), Shells.wrapCmd("echo %JAVA_HOME%")).getOutput());
		Consoles.println(Shells.execute("gbk", new File("."), new String[]{"cmd", "/c", "dir"}).getOutput());
	}

	@Test
	void test02() throws IOException {
		File workDir = new File("d:/data/logs");
		String scriptFileName = "test.cmd";
		Consoles.println(Shells.executeScriptContent("gbk", workDir, scriptFileName,
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
			Consoles.println(info);
		}
	}

	@Test
	void test04() {
		for (JMapHistoInfo info : JShells.getJMapHistoInfo()) {
			Consoles.println(info);
		}
	}

	@Test
	void test05() {
		TopExecutor.getInstance().start();
		Object[] args = new Object[]{TopExecutor.getInstance().getTopResult()};
		Consoles.println(args);
	}
}

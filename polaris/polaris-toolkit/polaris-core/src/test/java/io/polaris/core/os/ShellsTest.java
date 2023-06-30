package io.polaris.core.os;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringJoiner;

class ShellsTest {

	@Test
	void test01() throws IOException {
		System.out.println(Arrays.toString(Shells.parse("echo 12 34 56")));
		System.out.println(Arrays.toString(Shells.parse("echo '12 34' 56")));
		System.out.println(Arrays.toString(Shells.parse("echo '12 \"34' 56")));
		System.out.println(Arrays.toString(Shells.parse("echo \"12 34 \"56")));
		System.out.println(Arrays.toString(Shells.wrapCmd("echo \"12 34 \"56")));

		System.out.println(new File(".").getAbsoluteFile());
		System.out.println(Shells.execute(new File("."), Shells.wrapCmd("echo %JAVA_HOME%")).getOutput());
		System.out.println(Shells.execute("gbk", new File("."), new String[]{"cmd", "/c", "dir"}).getOutput());
	}

	@Test
	void test02() throws IOException {
		System.out.println(Shells.executeScriptContent("gbk", new File("d:/data/logs"), "test.cmd",
			new StringJoiner("\n")
				.add("@echo off")
				.add("echo ............................................")
				.add("dir")
				.add("echo ............................................")
				.toString()).getOutput());
	}

	@Test
	void test03() {
		for (JStackThreadInfo info : JShells.getJStackInfo()) {
			System.out.println(info);
		}
	}

	@Test
	void test04() {
		for (JMapHistoInfo info : JShells.getJMapHistoInfo()) {
			System.out.println(info);
		}
	}
	@Test
	void test05() {
		TopExecutor.getInstance().start();
		System.out.println(TopExecutor.getInstance().getTopResult());
	}
}

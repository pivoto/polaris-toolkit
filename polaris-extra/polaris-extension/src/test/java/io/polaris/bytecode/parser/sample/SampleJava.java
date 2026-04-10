package io.polaris.bytecode.parser.sample;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.polaris.core.io.IO;

/**
 * @author Qt
 * @since Apr 10, 2026
 */
public class SampleJava {

	public static void main(String[] args) throws IOException {
		System.out.println("测试");
		String text = IO.toString(new File("src/test/resources/sample.txt"), StandardCharsets.UTF_8);
	}
}

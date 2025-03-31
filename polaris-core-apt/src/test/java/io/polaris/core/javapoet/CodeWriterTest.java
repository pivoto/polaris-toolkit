package io.polaris.core.javapoet;


import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CodeWriterTest {

	@Test
	public void emptyLineInJavaDocDosEndings() throws IOException {
		CodeBlock javadocCodeBlock = CodeBlock.of("A\r\n\r\nB\r\n");
		StringBuilder out = new StringBuilder();
		new CodeWriter(out).emitJavadoc(javadocCodeBlock);
		assertEquals("/**\n" +
				" * A\n" +
				" *\n" +
				" * B\n" +
				" */\n",
			out.toString());
	}
}

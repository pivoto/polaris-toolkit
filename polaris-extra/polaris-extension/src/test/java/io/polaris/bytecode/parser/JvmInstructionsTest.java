package io.polaris.bytecode.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.polaris.bytecode.parser.sample.SampleJava;
import io.polaris.core.asm.consts.AsmInstructionConsts;
import io.polaris.core.consts.JvmInstructions;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.io.IO;
import io.polaris.core.regex.Patterns;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * @author Qt
 * @since Apr 10, 2026
 */
public class JvmInstructionsTest {


	@Test
	void test02() throws IOException {
		String root = JvmInstructions.class.getProtectionDomain().getCodeSource()
			.getLocation().getFile().replaceFirst("/target/classes/?$", "")
			+ "/src/main/java";

		File file1 = new File(root, JvmInstructions.class.getName().replace(".", "/") + ".java");
		File file2 = new File(root, AsmInstructionConsts.class.getName().replace(".", "/") + ".java");

		String java1 = IO.toString(file1, StandardCharsets.UTF_8);
		String java2 = IO.toString(file2, StandardCharsets.UTF_8);


		Pattern commentField = Pattern.compile("/\\*\\*([^\n]+)\\*/\r?\n\\s*int (\\w+) = ([^\n]+)\n");
		Pattern field = Pattern.compile("\tint (\\w+) = ([^\n]+)\n");

		Map<String, String> fields = new HashMap<>();
		Matcher matcher1 = commentField.matcher(java1);
		while (matcher1.find()) {
//			System.out.println(matcher1.group());
			fields.put(matcher1.group(2), matcher1.group(1));
		}
//		System.out.println(fields);

		Matcher matcher2 = field.matcher(java2);
		StringBuffer sb = new StringBuffer();
		while (matcher2.find()) {
			String key = matcher2.group(1);
			String comment = fields.get(key);
			if (comment != null) {
				String text = "\t/**" + comment + "*/" + "\n" + matcher2.group();
				matcher2.appendReplacement(sb, text);
			}else{
				matcher2.appendReplacement(sb, matcher2.group());
			}
		}
		matcher2.appendTail(sb);
		System.out.println(sb);
		IO.writeString(file2, StandardCharsets.UTF_8, sb.toString());
	}

	@Test
	void test01() throws FileNotFoundException {
		String root = JvmInstructions.class.getProtectionDomain().getCodeSource()
			.getLocation().getFile().replaceFirst("/target/classes/?$", "")
			+ "/src/main/java";

		File file = new File(root, JvmInstructions.class.getName().replace(".", "/") + ".java");

		Map<String, String> map = new HashMap<>();

		CompilationUnit unitJvmInstructions = StaticJavaParser.parse(file);
		unitJvmInstructions.accept(new VoidVisitorAdapter<File>() {

			@Override
			public void visit(FieldDeclaration n, File arg) {
				System.out.println(Strings.repeat('=', 80));
				String string = n.toString();
//				System.out.println(string);

				int begin = string.indexOf("/**");
				if (begin >= 0) {
					int end = string.indexOf("*/", begin);
					string = string.substring(begin, end).replace("/**", "")
						.replace("*/", "")
						.replace("\n * ", "")
						.replace("\r", "")
						.replace("\n", "")
					;
				}
				for (VariableDeclarator variable : n.getVariables()) {
					System.out.println(string);
					System.out.println(variable.getType().asString());
					System.out.println(variable.getName());
					map.put(variable.getName().asString(), string);
				}
			}
		}, file);


		file = new File(root, AsmInstructionConsts.class.getName().replace(".", "/") + ".java");
		CompilationUnit unitAsmInstructionConsts = StaticJavaParser.parse(file);
		unitAsmInstructionConsts.accept(new VoidVisitorAdapter<File>() {

			@Override
			public void visit(FieldDeclaration n, File arg) {
				System.out.println(Strings.repeat('=', 80));
				for (VariableDeclarator variable : n.getVariables()) {
					String key = variable.getName().asString();
					String comment = map.get(key);
					if (comment != null) {
						n.setJavadocComment(comment);
						System.out.println(key + " -> " + comment);
					}
				}
			}
		}, file);
		System.out.println(unitAsmInstructionConsts.toString());


	}

}

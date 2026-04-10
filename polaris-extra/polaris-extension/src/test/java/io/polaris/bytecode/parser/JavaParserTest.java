package io.polaris.bytecode.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.polaris.bytecode.parser.sample.SampleJava;
import io.polaris.core.string.Strings;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * @author Qt
 * @since Apr 10, 2026
 */
public class JavaParserTest {

	private static final List<String> strings = new ArrayList<>();

	public static void main(String[] args) {
		File src = new File(JavaParserTest.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceFirst("/target/test-classes/?$", "") + "/src/test/java/");
		// 目录或文件路径
		File dir = new File(src, SampleJava.class.getName().replace(".", "/") + ".java");

		if (dir.isDirectory()) {
			traverseDirectory(dir);
		} else {
			checkFile(dir);
		}

		if (strings.isEmpty()) {
			System.out.println("改造完成");
		} else {
			strings.forEach(System.out::println);
		}
	}

	/**
	 * 检查单个文件
	 *
	 * @param file Java 源文件
	 */
	public static void checkFile(File file) {
		try {
			CompilationUnit cu = StaticJavaParser.parse(file);
			// 获取所有导入的包
			//cu.getImports().forEach(System.out::println);
			// 方式1
			cu.accept(new MethodVisitor(), file.toPath());
			// 方式2
			// new MethodVisitor().visit(cu, file.toPath());
		} catch (FileNotFoundException e) {
			System.err.println("文件不存在或无法读取: " + file.getAbsolutePath());
		}
	}

	/**
	 * 递归遍历目录,处理所有 Java 文件
	 *
	 * @param directory 目录
	 */
	private static void traverseDirectory(File directory) {
		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				traverseDirectory(file);
			}
			if (file.isFile() && file.getName().endsWith(".java")) {
				checkFile(file);
			}
		}
	}

	// 自定义访问者,专注于处理方法声明
	static class MethodVisitor extends VoidVisitorAdapter<Path> {
		@Override
		public void visit(MethodDeclaration method, Path filePath) {
			super.visit(method, filePath);
			// 在这里可以进一步处理方法体
			method.getBody().ifPresent(body -> {
				//查找所有字符串字面量
				body.findAll(StringLiteralExpr.class).forEach(str -> {
					String value = str.getValue();
					if (containsChinese(value)) {
						if (!isI18nUtilGetArgument(str)) {
							String[] split = filePath.toString().split("/");
							String className = split[split.length - 1];
							// 输出警告
							String s = String.format("[警告] 文件: %s, 方法: %s, 行号: %d, 字符串: \"%s\"%n",
								className, method.getNameAsString(),
								str.getBegin().map(p -> p.line).orElse(-1),
								value);
							strings.add(s);
						}
					}
				});
			});
		}
	}

	/**
	 * 检查字符串是否包含中文字符
	 */
	private static boolean containsChinese(String s) {
		for (char c : s.toCharArray()) {
			if (c >= '\u4e00' && c <= '\u9fff') {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断一个字符串字面量节点是否作为 I18nUtil方法的参数
	 */
	private static boolean isI18nUtilGetArgument(StringLiteralExpr strLiteral) {
		Node parent = strLiteral.getParentNode().orElse(null);
		// 寻找最近的 MethodCallExpr 祖先节点
		while (parent != null && !(parent instanceof MethodCallExpr)) {
			parent = parent.getParentNode().orElse(null);
		}
		if (parent instanceof MethodCallExpr) {
			MethodCallExpr call = (MethodCallExpr) parent;
			// 检查调用者是否为 "I18nUtil" (可以是标识符或静态引用)
			Optional<Expression> scope = call.getScope();
			if (scope.isPresent()) {
				Expression scopeExpr = scope.get();
				// 忽略log日志
				if (Strings.equals(scopeExpr.toString(), "log")) {
					return true;
				}
				// 判断 I18nUtil
				if (Strings.equals(scopeExpr.toString(), "I18nUtil")) {
					// 确认该字符串字面量是调用的第一个参数(或者某个参数,通常 key 是第一个)
					// 这里简单判断是否在参数列表中
					return call.getArguments().stream()
						.anyMatch(arg -> arg == strLiteral);
				}
			}
		}
		return false;
	}

	/**
	 * parseBlock 解析代码块
	 * 注意:解析的代码块必须包含花括号,否则会报错
	 */
	private static void parseBlock() {
		// 注意最外层添加了 { 和 },内部语句保持原样
		String methodBody =
			"{\n"
				+ " int a = 10;\n" +
				" String b = \"hello\";\n" +
				" System.out.println(a + b);\n" +
				" // 使用 lambda 表达式\n" +
				" List<String> list = Arrays.asList(\"a\", \"b\", \"c\");\n" +
				" list.forEach(item -> System.out.println(item));\n" +
				" if (a > 5) {\n" +
				" System.out.println(\"a is greater than 5\");\n" +
				" }\n"
				+ "}";

		try {
			// 使用 parseBlock 解析代码块
			BlockStmt block = StaticJavaParser.parseBlock(methodBody);

			System.out.println("成功解析代码块!共包含 " + block.getStatements().size() + " 条语句。\n");

			// 遍历并分析每条语句
			for (Statement stmt : block.getStatements()) {
				System.out.println("语句类型: " + stmt.getClass().getSimpleName());
				System.out.println("语句内容: " + stmt);
				System.out.println("---");
			}

			// 示例:查找并修改变量声明
			block.findAll(VariableDeclarationExpr.class).forEach(varDecl -> {
				varDecl.getVariables().forEach(variable -> {
					if (variable.getNameAsString().equals("a")) {
						variable.setName("number");
						System.out.println("已将变量 'a' 重命名为 'number'");
					}
				});
			});

			// 输出修改后的代码
			System.out.println("\n修改后的代码块:");
			System.out.println(block);
		} catch (Exception e) {
			System.err.println("解析失败: " + e.getMessage());
		}
	}
}

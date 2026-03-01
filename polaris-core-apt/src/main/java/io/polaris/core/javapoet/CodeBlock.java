package io.polaris.core.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static io.polaris.core.javapoet.Util.checkArgument;

/**
 * .java 文件的一个片段，可能包含声明、语句和文档。
 * 代码块不一定是格式良好的 Java 代码，也不会进行验证。此类假设
 * javac 稍后会检查正确性！
 *
 * <p>代码块支持类似 {@link java.text.Format} 的占位符。{@link String#format}
 * 使用百分号 {@code %} 来引用目标值，而此类使用美元符号 {@code $} 并有一套
 * 自己允许的占位符：
 *
 * <ul>
 *   <li>{@code $L} 输出一个不转义的<em>字面量</em>值。字面量的参数可以是
 *       字符串、基本类型、{@linkplain TypeSpec 类型声明}、{@linkplain AnnotationSpec
 *       注解}，甚至是其他代码块。
 *   <li>{@code $N} 输出一个<em>名称</em>，必要时使用名称冲突避免机制。名称的参数可以是
 *       字符串（实际上是任何 {@linkplain CharSequence 字符序列}）、
 *       {@linkplain ParameterSpec 参数}、{@linkplain FieldSpec 字段}、{@linkplain
 *       MethodSpec 方法}和 {@linkplain TypeSpec 类型}。
 *   <li>{@code $S} 将值转义为<em>字符串</em>，用双引号包装并输出。
 *       例如，{@code 6" sandwich} 会被输出为 {@code "6\" sandwich"}。
 *   <li>{@code $T} 输出一个<em>类型</em>引用。如果可能，类型将被导入。类型的参数可以是
 *       {@linkplain Class 类}、{@linkplain javax.lang.model.type.TypeMirror
 *       类型镜像}和 {@linkplain javax.lang.model.element.Element 元素}。
 *   <li>{@code $$} 输出一个美元符号。
 *   <li>{@code $W} 根据其在行中的位置输出空格或换行符。它倾向于在 100 列之前换行。
 *   <li>{@code $Z} 作为零宽度空格。它倾向于在 100 列之前换行。
 *   <li>{@code $>} 增加缩进级别。
 *   <li>{@code $<} 减少缩进级别。
 *   <li>{@code $[} 开始一个语句。对于多行语句，第一行之后的每一行都会双倍缩进。
 *   <li>{@code $]} 结束一个语句。
 * </ul>
 */
public final class CodeBlock {
	private static final Pattern NAMED_ARGUMENT =
		Pattern.compile("\\$(?<argumentName>[\\w_]+):(?<typeChar>[\\w]).*");
	private static final Pattern LOWERCASE = Pattern.compile("[a-z]+[\\w_]*");

	/** 包含字符串字面量和值占位符的列表。 */
	final List<String> formatParts;
	final List<Object> args;

	private CodeBlock(Builder builder) {
		this.formatParts = Util.immutableList(builder.formatParts);
		this.args = Util.immutableList(builder.args);
	}

	public boolean isEmpty() {
		return formatParts.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		return toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		try {
			new CodeWriter(out).emit(this);
			return out.toString();
		} catch (IOException e) {
			throw new AssertionError();
		}
	}

	public static CodeBlock of(String format, Object... args) {
		return new Builder().add(format, args).build();
	}

	/**
	 * 将 {@code codeBlocks} 连接成一个单独的 {@link CodeBlock}，每个之间用 {@code separator} 分隔。
	 * 例如，使用 {@code ", "} 连接 {@code String s}、{@code Object o} 和 {@code int i}，
	 * 将产生 {@code String s, Object o, int i}。
	 */
	public static CodeBlock join(Iterable<CodeBlock> codeBlocks, String separator) {
		return StreamSupport.stream(codeBlocks.spliterator(), false).collect(joining(separator));
	}

	/**
	 * 一个 {@link Collector} 实现，将 {@link CodeBlock} 实例连接成一个，
	 * 并用 {@code separator} 分隔。例如，使用 {@code ", "} 连接 {@code String s}、{@code Object o} 和
	 * {@code int i} 将产生 {@code String s, Object o, int i}。
	 */
	public static Collector<CodeBlock, ?, CodeBlock> joining(String separator) {
		return Collector.of(
			() -> new CodeBlockJoiner(separator, builder()),
			CodeBlockJoiner::add,
			CodeBlockJoiner::merge,
			CodeBlockJoiner::join);
	}

	/**
	 * 一个 {@link Collector} 实现，将 {@link CodeBlock} 实例连接成一个，
	 * 并用 {@code separator} 分隔。例如，使用 {@code ", "} 连接 {@code String s}、{@code Object o} 和
	 * {@code int i} 将产生 {@code String s, Object o, int i}。
	 */
	public static Collector<CodeBlock, ?, CodeBlock> joining(
		String separator, String prefix, String suffix) {
		Builder builder = builder().add("$N", prefix);
		return Collector.of(
			() -> new CodeBlockJoiner(separator, builder),
			CodeBlockJoiner::add,
			CodeBlockJoiner::merge,
			joiner -> {
				builder.add(CodeBlock.of("$N", suffix));
				return joiner.join();
			});
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.formatParts.addAll(formatParts);
		builder.args.addAll(args);
		return builder;
	}

	public static final class Builder {
		final List<String> formatParts = new ArrayList<>();
		final List<Object> args = new ArrayList<>();

		private Builder() {
		}

		public boolean isEmpty() {
			return formatParts.isEmpty();
		}

		/**
		 * 使用命名参数添加代码。
		 *
		 * <p>命名参数在 '$' 后指定其名称，后跟 ':' 和相应的类型字符。
		 * 参数名称由 {@code a-z, A-Z, 0-9, 和 _} 中的字符组成，
		 * 且必须以小写字母开头。
		 *
		 * <p>例如，要引用参数名为 {@code clazz} 的类型 {@link java.lang.Integer}，
		 * 使用包含 {@code $clazz:T} 的格式字符串，并在参数映射中包含键 {@code clazz}，
		 * 值为 {@code java.lang.Integer.class}。
		 */
		public Builder addNamed(String format, Map<String, ?> arguments) {
			int p = 0;

			for (String argument : arguments.keySet()) {
				checkArgument(LOWERCASE.matcher(argument).matches(),
					"argument '%s' must start with a lowercase character", argument);
			}

			while (p < format.length()) {
				int nextP = format.indexOf("$", p);
				if (nextP == -1) {
					formatParts.add(format.substring(p));
					break;
				}

				if (p != nextP) {
					formatParts.add(format.substring(p, nextP));
					p = nextP;
				}

				Matcher matcher = null;
				int colon = format.indexOf(':', p);
				if (colon != -1) {
					int endIndex = Math.min(colon + 2, format.length());
					matcher = NAMED_ARGUMENT.matcher(format.substring(p, endIndex));
				}
				if (matcher != null && matcher.lookingAt()) {
					String argumentName = matcher.group("argumentName");
					checkArgument(arguments.containsKey(argumentName), "Missing named argument for $%s",
						argumentName);
					char formatChar = matcher.group("typeChar").charAt(0);
					addArgument(format, formatChar, arguments.get(argumentName));
					formatParts.add("$" + formatChar);
					p += matcher.regionEnd();
				} else {
					checkArgument(p < format.length() - 1, "dangling $ at end");
					checkArgument(isNoArgPlaceholder(format.charAt(p + 1)),
						"unknown format $%s at %s in '%s'", format.charAt(p + 1), p + 1, format);
					formatParts.add(format.substring(p, p + 2));
					p += 2;
				}
			}

			return this;
		}

		/**
		 * 添加带有位置或相对参数的代码。
		 *
		 * <p>相对参数与格式字符串中的占位符一一对应。
		 *
		 * <p>位置参数在占位符后使用索引来标识要使用的参数索引。
		 * 例如，要引用第 3 个参数的字面量："3L"（基于 1 的索引）
		 *
		 * <p>在调用 add 时混合相对和位置参数是无效的，并会导致错误。
		 */
		public Builder add(String format, Object... args) {
			boolean hasRelative = false;
			boolean hasIndexed = false;

			int relativeParameterCount = 0;
			int[] indexedParameterCount = new int[args.length];

			for (int p = 0; p < format.length(); ) {
				if (format.charAt(p) != '$') {
					int nextP = format.indexOf('$', p + 1);
					if (nextP == -1) nextP = format.length();
					formatParts.add(format.substring(p, nextP));
					p = nextP;
					continue;
				}

				p++; // '$'.

				// 消耗零个或多个数字，留下 'c' 作为 '$' 后的第一个非数字字符。
				int indexStart = p;
				char c;
				do {
					checkArgument(p < format.length(), "dangling format characters in '%s'", format);
					c = format.charAt(p++);
				} while (c >= '0' && c <= '9');
				int indexEnd = p - 1;

				// If 'c' doesn't take an argument, we're done.
				if (isNoArgPlaceholder(c)) {
					checkArgument(
						indexStart == indexEnd, "$$, $>, $<, $[, $], $W, and $Z may not have an index");
					formatParts.add("$" + c);
					continue;
				}

				// Find either the indexed argument, or the relative argument. (0-based).
				int index;
				if (indexStart < indexEnd) {
					index = Integer.parseInt(format.substring(indexStart, indexEnd)) - 1;
					hasIndexed = true;
					if (args.length > 0) {
						indexedParameterCount[index % args.length]++; // modulo is needed, checked below anyway
					}
				} else {
					index = relativeParameterCount;
					hasRelative = true;
					relativeParameterCount++;
				}

				checkArgument(index >= 0 && index < args.length,
					"index %d for '%s' not in range (received %s arguments)",
					index + 1, format.substring(indexStart - 1, indexEnd + 1), args.length);
				checkArgument(!hasIndexed || !hasRelative, "cannot mix indexed and positional parameters");

				addArgument(format, c, args[index]);

				formatParts.add("$" + c);
			}

			if (hasRelative) {
				checkArgument(relativeParameterCount >= args.length,
					"unused arguments: expected %s, received %s", relativeParameterCount, args.length);
			}
			if (hasIndexed) {
				List<String> unused = new ArrayList<>();
				for (int i = 0; i < args.length; i++) {
					if (indexedParameterCount[i] == 0) {
						unused.add("$" + (i + 1));
					}
				}
				String s = unused.size() == 1 ? "" : "s";
				checkArgument(unused.isEmpty(), "unused argument%s: %s", s, String.join(", ", unused));
			}
			return this;
		}

		private boolean isNoArgPlaceholder(char c) {
			return c == '$' || c == '>' || c == '<' || c == '[' || c == ']' || c == 'W' || c == 'Z';
		}

		private void addArgument(String format, char c, Object arg) {
			switch (c) {
				case 'N':
					this.args.add(argToName(arg));
					break;
				case 'L':
					this.args.add(argToLiteral(arg));
					break;
				case 'S':
					this.args.add(argToString(arg));
					break;
				case 'T':
					this.args.add(argToType(arg));
					break;
				default:
					throw new IllegalArgumentException(
						String.format("invalid format string: '%s'", format));
			}
		}

		private String argToName(Object o) {
			if (o instanceof CharSequence) return o.toString();
			if (o instanceof ParameterSpec) return ((ParameterSpec) o).name;
			if (o instanceof FieldSpec) return ((FieldSpec) o).name;
			if (o instanceof MethodSpec) return ((MethodSpec) o).name;
			if (o instanceof TypeSpec) return ((TypeSpec) o).name;
			throw new IllegalArgumentException("expected name but was " + o);
		}

		private Object argToLiteral(Object o) {
			return o;
		}

		private String argToString(Object o) {
			return o != null ? String.valueOf(o) : null;
		}

		private TypeName argToType(Object o) {
			if (o instanceof TypeName) return (TypeName) o;
			if (o instanceof TypeMirror) return TypeName.get((TypeMirror) o);
			if (o instanceof Element) return TypeName.get(((Element) o).asType());
			if (o instanceof Type) return TypeName.get((Type) o);
			throw new IllegalArgumentException("expected type but was " + o);
		}

		/**
		 * @param controlFlow 控制流结构及其代码，例如 "if (foo == 5)"。
		 *                    不应包含大括号或换行符。
		 */
		public Builder beginControlFlow(String controlFlow, Object... args) {
			add(controlFlow + " {\n", args);
			indent();
			return this;
		}

		/**
		 * @param controlFlow 控制流结构及其代码，例如 "else if (foo == 10)"。
		 *                    不应包含大括号或换行符。
		 */
		public Builder nextControlFlow(String controlFlow, Object... args) {
			unindent();
			add("} " + controlFlow + " {\n", args);
			indent();
			return this;
		}

		public Builder endControlFlow() {
			unindent();
			add("}\n");
			return this;
		}

		/**
		 * @param controlFlow 可选的控制流结构及其代码，例如
		 *                    "while(foo == 20)"。仅用于 "do/while" 控制流。
		 */
		public Builder endControlFlow(String controlFlow, Object... args) {
			unindent();
			add("} " + controlFlow + ";\n", args);
			return this;
		}

		public Builder addStatement(String format, Object... args) {
			add("$[");
			add(format, args);
			add(";\n$]");
			return this;
		}

		public Builder addStatement(CodeBlock codeBlock) {
			return addStatement("$L", codeBlock);
		}

		public Builder add(CodeBlock codeBlock) {
			formatParts.addAll(codeBlock.formatParts);
			args.addAll(codeBlock.args);
			return this;
		}

		public Builder indent() {
			this.formatParts.add("$>");
			return this;
		}

		public Builder unindent() {
			this.formatParts.add("$<");
			return this;
		}

		public Builder clear() {
			formatParts.clear();
			args.clear();
			return this;
		}

		public CodeBlock build() {
			return new CodeBlock(this);
		}
	}

	private static final class CodeBlockJoiner {
		private final String delimiter;
		private final Builder builder;
		private boolean first = true;

		CodeBlockJoiner(String delimiter, Builder builder) {
			this.delimiter = delimiter;
			this.builder = builder;
		}

		CodeBlockJoiner add(CodeBlock codeBlock) {
			if (!first) {
				builder.add(delimiter);
			}
			first = false;

			builder.add(codeBlock);
			return this;
		}

		CodeBlockJoiner merge(CodeBlockJoiner other) {
			CodeBlock otherBlock = other.builder.build();
			if (!otherBlock.isEmpty()) {
				add(otherBlock);
			}
			return this;
		}

		CodeBlock join() {
			return builder.build();
		}
	}
}

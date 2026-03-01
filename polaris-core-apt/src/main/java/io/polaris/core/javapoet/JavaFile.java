package io.polaris.core.javapoet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

import static io.polaris.core.javapoet.Util.checkArgument;
import static io.polaris.core.javapoet.Util.checkNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 包含单个顶级类的Java文件。
 *
 */
public final class JavaFile {
	private static final Appendable NULL_APPENDABLE = new Appendable() {
		@Override
		public Appendable append(CharSequence charSequence) {
			return this;
		}

		@Override
		public Appendable append(CharSequence charSequence, int start, int end) {
			return this;
		}

		@Override
		public Appendable append(char c) {
			return this;
		}
	};

	public final CodeBlock fileComment;
	public final String packageName;
	public final TypeSpec typeSpec;
	public final boolean skipJavaLangImports;
	private final Set<String> staticImports;
	private final Set<String> alwaysQualify;
	private final String indent;

	private JavaFile(Builder builder) {
		this.fileComment = builder.fileComment.build();
		this.packageName = builder.packageName;
		this.typeSpec = builder.typeSpec;
		this.skipJavaLangImports = builder.skipJavaLangImports;
		this.staticImports = Util.immutableSet(builder.staticImports);
		this.indent = builder.indent;

		Set<String> alwaysQualifiedNames = new LinkedHashSet<>();
		fillAlwaysQualifiedNames(builder.typeSpec, alwaysQualifiedNames);
		this.alwaysQualify = Util.immutableSet(alwaysQualifiedNames);
	}

	private void fillAlwaysQualifiedNames(TypeSpec spec, Set<String> alwaysQualifiedNames) {
		alwaysQualifiedNames.addAll(spec.alwaysQualifiedNames);
		for (TypeSpec nested : spec.typeSpecs) {
			fillAlwaysQualifiedNames(nested, alwaysQualifiedNames);
		}
	}

	public void writeTo(Appendable out) throws IOException {
		// 第一遍：输出整个类，仅收集需要导入的类型。
		CodeWriter importsCollector = new CodeWriter(
			NULL_APPENDABLE,
			indent,
			staticImports,
			alwaysQualify
		);
		emit(importsCollector);
		Map<String, ClassName> suggestedImports = importsCollector.suggestedImports();

		// 第二遍：编写代码，利用已收集的导入信息。
		CodeWriter codeWriter
			= new CodeWriter(out, indent, suggestedImports, staticImports, alwaysQualify);
		emit(codeWriter);
	}

	/**
	 * 使用标准目录结构将此文件以UTF-8编码写入{@code directory}。
	 */
	public void writeTo(Path directory) throws IOException {
		writeToPath(directory);
	}

	/**
	 * 使用标准目录结构和指定的{@code charset}将此文件写入{@code directory}。
	 */
	public void writeTo(Path directory, Charset charset) throws IOException {
		writeToPath(directory, charset);
	}

	/**
	 * 使用标准目录结构将此文件以UTF-8编码写入{@code directory}。
	 * 返回实际写入源码的{@link Path}实例。
	 */
	public Path writeToPath(Path directory) throws IOException {
		return writeToPath(directory, UTF_8);
	}

	/**
	 * 使用标准目录结构和指定的{@code charset}将此文件写入{@code directory}。
	 * 返回实际写入源码的{@link Path}实例。
	 */
	public Path writeToPath(Path directory, Charset charset) throws IOException {
		checkArgument(Files.notExists(directory) || Files.isDirectory(directory),
			"path %s exists but is not a directory.", directory);
		Path outputDirectory = directory;
		if (!packageName.isEmpty()) {
			for (String packageComponent : packageName.split("\\.")) {
				outputDirectory = outputDirectory.resolve(packageComponent);
			}
			Files.createDirectories(outputDirectory);
		}

		Path outputPath = outputDirectory.resolve(typeSpec.name + ".java");
		try (Writer writer = new OutputStreamWriter(Files.newOutputStream(outputPath), charset)) {
			writeTo(writer);
		}

		return outputPath;
	}

	/** 使用标准目录结构将此文件以UTF-8编码写入{@code directory}。 */
	public void writeTo(File directory) throws IOException {
		writeTo(directory.toPath());
	}

	/**
	 * 使用标准目录结构将此文件以UTF-8编码写入{@code directory}。
	 * 返回实际写入源码的{@link File}实例。
	 */
	public File writeToFile(File directory) throws IOException {
		final Path outputPath = writeToPath(directory.toPath());
		return outputPath.toFile();
	}

	/** 将此文件写入{@code filer}。 */
	public void writeTo(Filer filer) throws IOException {
		String fileName = packageName.isEmpty()
			? typeSpec.name
			: packageName + "." + typeSpec.name;
		List<Element> originatingElements = typeSpec.originatingElements;
		JavaFileObject filerSourceFile = filer.createSourceFile(fileName,
			originatingElements.toArray(new Element[originatingElements.size()]));
		try (Writer writer = filerSourceFile.openWriter()) {
			writeTo(writer);
		} catch (Exception e) {
			try {
				filerSourceFile.delete();
			} catch (Exception ignored) {
			}
			throw e;
		}
	}

	private void emit(CodeWriter codeWriter) throws IOException {
		codeWriter.pushPackage(packageName);

		if (!fileComment.isEmpty()) {
			codeWriter.emitComment(fileComment);
		}

		if (!packageName.isEmpty()) {
			codeWriter.emit("package $L;\n", packageName);
			codeWriter.emit("\n");
		}

		if (!staticImports.isEmpty()) {
			for (String signature : staticImports) {
				codeWriter.emit("import static $L;\n", signature);
			}
			codeWriter.emit("\n");
		}

		int importedTypesCount = 0;
		for (ClassName className : new TreeSet<>(codeWriter.importedTypes().values())) {
			// TODO 嵌套类型如java.util.Map.Entry如何处理？
			if (skipJavaLangImports
				&& className.packageName().equals("java.lang")
				&& !alwaysQualify.contains(className.simpleName)) {
				continue;
			}
			codeWriter.emit("import $L;\n", className.withoutAnnotations());
			importedTypesCount++;
		}

		if (importedTypesCount > 0) {
			codeWriter.emit("\n");
		}

		typeSpec.emit(codeWriter, null, Collections.emptySet());

		codeWriter.popPackage();
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
		try {
			StringBuilder result = new StringBuilder();
			writeTo(result);
			return result.toString();
		} catch (IOException e) {
			throw new AssertionError();
		}
	}

	public JavaFileObject toJavaFileObject() {
		URI uri = URI.create((packageName.isEmpty()
			? typeSpec.name
			: packageName.replace('.', '/') + '/' + typeSpec.name)
			+ Kind.SOURCE.extension);
		return new SimpleJavaFileObject(uri, Kind.SOURCE) {
			private final long lastModified = System.currentTimeMillis();

			@Override
			public String getCharContent(boolean ignoreEncodingErrors) {
				return JavaFile.this.toString();
			}

			@Override
			public InputStream openInputStream() throws IOException {
				return new ByteArrayInputStream(getCharContent(true).getBytes(UTF_8));
			}

			@Override
			public long getLastModified() {
				return lastModified;
			}
		};
	}

	public static Builder builder(String packageName, TypeSpec typeSpec) {
		checkNotNull(packageName, "packageName == null");
		checkNotNull(typeSpec, "typeSpec == null");
		return new Builder(packageName, typeSpec);
	}

	public Builder toBuilder() {
		Builder builder = new Builder(packageName, typeSpec);
		builder.fileComment.add(fileComment);
		builder.skipJavaLangImports = skipJavaLangImports;
		builder.indent = indent;
		return builder;
	}

	public static final class Builder {
		private final String packageName;
		private final TypeSpec typeSpec;
		private final CodeBlock.Builder fileComment = CodeBlock.builder();
		private boolean skipJavaLangImports;
		private String indent = "\t";

		public final Set<String> staticImports = new TreeSet<>();

		private Builder(String packageName, TypeSpec typeSpec) {
			this.packageName = packageName;
			this.typeSpec = typeSpec;
		}

		public Builder addFileComment(String format, Object... args) {
			this.fileComment.add(format, args);
			return this;
		}

		public Builder addStaticImport(Enum<?> constant) {
			return addStaticImport(ClassName.get(constant.getDeclaringClass()), constant.name());
		}

		public Builder addStaticImport(Class<?> clazz, String... names) {
			return addStaticImport(ClassName.get(clazz), names);
		}

		public Builder addStaticImport(ClassName className, String... names) {
			checkArgument(className != null, "className == null");
			checkArgument(names != null, "names == null");
			checkArgument(names.length > 0, "names array is empty");
			for (String name : names) {
				checkArgument(name != null, "null entry in names array: %s", Arrays.toString(names));
				staticImports.add(className.canonicalName + "." + name);
			}
			return this;
		}

		/**
		 * 调用此方法可省略 {@code java.lang} 包中的类导入，例如 {@code java.lang.String}。
		 *
		 * <p>默认情况下，JavaPoet 会显式导入 {@code java.lang} 中的类型以防止命名冲突。
		 * 假设有一个（不建议的）类命名为 {@code com.example.String}。当跳过 {@code java.lang}
		 * 导入时，在 {@code com.example} 包中生成的引用 {@code java.lang.String} 的代码
		 * 将会得到 {@code com.example.String}。
		 */
		public Builder skipJavaLangImports(boolean skipJavaLangImports) {
			this.skipJavaLangImports = skipJavaLangImports;
			return this;
		}

		public Builder indent(String indent) {
			this.indent = indent;
			return this;
		}

		public JavaFile build() {
			return new JavaFile(this);
		}
	}
}

package io.polaris.builder.code.config;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeTemplateBuilder {
	private final CodeTemplate target;
	private final CodeGroupBuilder groupBuilder;


	public CodeTemplateBuilder(CodeGroupBuilder groupBuilder, CodeTemplate target) {
		this.groupBuilder = groupBuilder;
		this.target = target;
	}

	public CodeTemplate build() {
		return target;
	}

	public CodeGroupBuilder end() {
		return groupBuilder;
	}

	public CodeTemplateBuilder path(String path) {
		target.setPath(path);
		return this;
	}

	public CodeTemplateBuilder outdir(String outdir) {
		target.setDirname(outdir);
		return this;
	}

	public CodeTemplateBuilder filename(String filename) {
		target.setFilename(filename);
		return this;
	}

	public CodeTemplateBuilder property(Map<String, String> property) {
		target.setProperty(property);
		return this;
	}

	public CodeTemplateBuilder property(Supplier<Map<String, String>> property) {
		target.setProperty(property.get());
		return this;
	}
	public CodeTemplateBuilder property(String key, String value) {
		if (target.getProperty() == null) {
			target.setProperty(new HashMap<>());
		}
		target.getProperty().put(key, value);
		return this;
	}

}

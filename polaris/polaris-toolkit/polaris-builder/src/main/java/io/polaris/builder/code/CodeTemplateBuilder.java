package io.polaris.builder.code;

import io.polaris.builder.code.config.CodeTemplate;

import java.util.Map;

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
		target.setOutdir(outdir);
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

}

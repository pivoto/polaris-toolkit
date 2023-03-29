package io.polaris.builder.code;

import io.polaris.builder.code.config.CodeTable;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeTableBuilder {
	private final CodeGroupBuilder groupBuilder;
	private final CodeTable target;

	public CodeTableBuilder(CodeGroupBuilder groupBuilder, CodeTable target) {
		this.groupBuilder = groupBuilder;
		this.target = target;
	}

	public CodeTable build() {
		return target;
	}

	public CodeGroupBuilder end() {
		return groupBuilder;
	}

	public CodeTableBuilder table(String table) {
		target.setTable(table);
		return this;
	}

	public CodeTableBuilder catalog(String catalog) {
		target.setCatalog(catalog);
		return this;
	}

	public CodeTableBuilder schema(String schema) {
		target.setSchema(schema);
		return this;
	}

	public CodeTableBuilder javaPackageName(String javaPackageName) {
		target.setJavaPackageName(javaPackageName);
		return this;
	}

	public CodeTableBuilder property(Map<String, String> property) {
		target.setProperty(property);
		return this;
	}
}

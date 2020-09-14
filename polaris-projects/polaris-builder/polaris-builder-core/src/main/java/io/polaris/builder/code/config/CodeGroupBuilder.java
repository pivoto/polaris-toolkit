package io.polaris.builder.code.config;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeGroupBuilder {

	private final CodeEnvBuilder envBuilder;
	private final CodeGroup target;

	public CodeGroupBuilder(CodeEnvBuilder envBuilder, CodeGroup target) {
		this.envBuilder = envBuilder;
		this.target = target;
	}

	public CodeGroup build() {
		return target;
	}

	public CodeEnvBuilder end() {
		return envBuilder;
	}

	public CodeGroupBuilder templates(List<CodeTemplate> templates) {
		target.setTemplates(templates);
		return this;
	}

	public CodeGroupBuilder tables(List<CodeTable> tables) {
		target.setTables(tables);
		return this;
	}

	public CodeGroupBuilder property(Map<String, String> property) {
		target.setProperty(property);
		return this;
	}

	public CodeGroupBuilder property(Supplier<Map<String, String>> property) {
		target.setProperty(property.get());
		return this;
	}

	public CodeGroupBuilder property(String key, String value) {
		if (target.getProperty() == null) {
			target.setProperty(new HashMap<>());
		}
		target.getProperty().put(key, value);
		return this;
	}

	public CodeGroupBuilder tablePrefix(String tablePrefix) {
		target.setTablePrefix(tablePrefix);
		return this;
	}

	public CodeGroupBuilder tableSuffix(String tableSuffix) {
		target.setTableSuffix(tableSuffix);
		return this;
	}

	public CodeGroupBuilder columnPrefix(String columnPrefix) {
		target.setColumnPrefix(columnPrefix);
		return this;
	}

	public CodeGroupBuilder columnSuffix(String columnSuffix) {
		target.setColumnSuffix(columnSuffix);
		return this;
	}

	public CodeGroupBuilder mappings(Set<TypeMapping> mappings) {
		target.setMappings(mappings);
		return this;
	}

	public CodeGroupBuilder mappings(Supplier<Set<TypeMapping>> mappings) {
		target.setMappings(mappings.get());
		return this;
	}

	public CodeGroupBuilder mapping(String jdbcType, String javaType) {
		if (target.getMappings() == null) {
			target.setMappings(new LinkedHashSet<>());
		}
		target.getMappings().add(new TypeMapping(jdbcType, javaType));
		return this;
	}


	public CodeGroupBuilder addTemplate(CodeTemplate template) {
		if (target.getTemplates() == null) {
			target.setTemplates(new ArrayList<>());
		}
		target.getTemplates().add(template);
		return this;
	}

	public CodeTemplateBuilder addTemplate() {
		CodeTemplate target = new CodeTemplate();
		addTemplate(target);
		return new CodeTemplateBuilder(this, target);
	}

	public CodeTemplateBuilder template() {
		return template(0);
	}

	public CodeTemplateBuilder template(int i) {
		if (target.getTemplates() == null || target.getTemplates().size() <= i) {
			addTemplate(new CodeTemplate());
		}
		return new CodeTemplateBuilder(this, target.getTemplates().get(i));
	}

	public CodeGroupBuilder addTable(CodeTable table) {
		if (target.getTables() == null) {
			target.setTables(new ArrayList<>());
		}
		target.getTables().add(table);
		return this;
	}


	public CodeTableBuilder addTable() {
		CodeTable target = new CodeTable();
		addTable(target);
		return new CodeTableBuilder(this, target);
	}

	public CodeTableBuilder table() {
		return table(0);
	}

	public CodeTableBuilder table(int i) {
		if (target.getTables() == null || target.getTables().size() <= i) {
			addTable(new CodeTable());
		}
		return new CodeTableBuilder(this, target.getTables().get(i));
	}

}

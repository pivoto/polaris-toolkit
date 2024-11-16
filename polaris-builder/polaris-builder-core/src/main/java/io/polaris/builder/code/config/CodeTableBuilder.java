package io.polaris.builder.code.config;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

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

	public CodeTableBuilder name(String name) {
		target.setName(name);
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

	public CodeTableBuilder javaPackage(String javaPackage) {
		target.setJavaPackage(javaPackage);
		return this;
	}

	public CodeTableBuilder property(Map<String, String> property) {
		target.setProperty(property);
		return this;
	}

	public CodeTableBuilder property(Supplier<Map<String, String>> property) {
		target.setProperty(property.get());
		return this;
	}

	public CodeTableBuilder property(String key, String value) {
		if (target.getProperty() == null) {
			target.setProperty(new HashMap<>());
		}
		target.getProperty().put(key, value);
		return this;
	}

	public CodeTableBuilder tablePrefix(String tablePrefix) {
		target.setTablePrefix(tablePrefix);
		return this;
	}

	public CodeTableBuilder tableSuffix(String tableSuffix) {
		target.setTableSuffix(tableSuffix);
		return this;
	}

	public CodeTableBuilder columnPrefix(String columnPrefix) {
		target.setColumnPrefix(columnPrefix);
		return this;
	}

	public CodeTableBuilder columnSuffix(String columnSuffix) {
		target.setColumnSuffix(columnSuffix);
		return this;
	}

	public CodeTableBuilder mappings(Set<TypeMapping> mappings) {
		target.setMappings(mappings);
		return this;
	}

	public CodeTableBuilder mappings(Supplier<Set<TypeMapping>> mappings) {
		target.setMappings(mappings.get());
		return this;
	}

	public CodeTableBuilder mapping(String jdbcType, String javaType) {
		if (target.getMappings() == null) {
			target.setMappings(new LinkedHashSet<>());
		}
		target.getMappings().add(new TypeMapping(jdbcType, javaType));
		return this;
	}

	public CodeTableBuilder columns(Set<ConfigColumn> columns) {
		target.setColumns(columns);
		return this;
	}

	public CodeTableBuilder columns(Supplier<Set<ConfigColumn>> columns) {
		target.setColumns(columns.get());
		return this;
	}

	public CodeTableBuilder column(String name, String javaType) {
		if (target.getColumns() == null) {
			target.setColumns(new LinkedHashSet<>());
		}
		ConfigColumn column = new ConfigColumn();
		column.setName(name);
		column.setJavaType(javaType);
		target.getColumns().add(column);
		return this;
	}

	public CodeTableBuilder ignoredColumns(Set<String> ignoredColumns) {
		target.setIgnoredColumns(ignoredColumns);
		return this;
	}

}

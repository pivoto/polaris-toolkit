package io.polaris.builder.code.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import io.polaris.builder.code.CodeGenerator;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeEnvBuilder {

	private final CodeGenerator generator;
	private final CodeEnv target;

	public CodeEnvBuilder(CodeGenerator generator, CodeEnv target) {
		this.generator = generator;
		this.target = target;
	}

	public CodeEnv build() {
		return target;
	}

	public CodeGenerator end() {
		return generator;
	}

	public CodeEnvBuilder outdir(String outdir) {
		target.setOutdir(outdir);
		return this;
	}

	public CodeEnvBuilder property(Map<String, String> property) {
		target.setProperty(property);
		return this;
	}

	public CodeEnvBuilder property(Supplier<Map<String, String>> property) {
		target.setProperty(property.get());
		return this;
	}

	public CodeEnvBuilder property(String key, String value) {
		if (target.getProperty() == null) {
			target.setProperty(new HashMap<>());
		}
		target.getProperty().put(key, value);
		return this;
	}

	public CodeEnvBuilder tablePrefix(String tablePrefix) {
		target.setTablePrefix(tablePrefix);
		return this;
	}

	public CodeEnvBuilder tableSuffix(String tableSuffix) {
		target.setTableSuffix(tableSuffix);
		return this;
	}

	public CodeEnvBuilder columnPrefix(String columnPrefix) {
		target.setColumnPrefix(columnPrefix);
		return this;
	}

	public CodeEnvBuilder columnSuffix(String columnSuffix) {
		target.setColumnSuffix(columnSuffix);
		return this;
	}

	public CodeEnvBuilder mappings(Set<TypeMapping> mappings) {
		target.setMappings(mappings);
		return this;
	}

	public CodeEnvBuilder mappings(Supplier<Set<TypeMapping>> mappings) {
		target.setMappings(mappings.get());
		return this;
	}

	public CodeEnvBuilder mapping(String jdbcType, String javaType) {
		if (target.getMappings() == null) {
			target.setMappings(new LinkedHashSet<>());
		}
		target.getMappings().add(new TypeMapping(jdbcType, javaType));
		return this;
	}

	public CodeEnvBuilder ignoredColumns(Set<String> ignoredColumns) {
		target.setIgnoredColumns(ignoredColumns);
		return this;
	}

	public CodeEnvBuilder groups(List<CodeGroup> groups) {
		target.setGroups(groups);
		return this;
	}


	public CodeEnvBuilder addGroup(CodeGroup group) {
		if (target.getGroups() == null) {
			target.setGroups(new ArrayList<>());
		}
		target.getGroups().add(group);
		return this;
	}

	public CodeGroupBuilder addGroup() {
		CodeGroup group = new CodeGroup();
		addGroup(group);
		return new CodeGroupBuilder(this, group);
	}

	public CodeGroupBuilder group() {
		return group(0);
	}

	public CodeGroupBuilder group(int i) {
		if (target.getGroups() == null || target.getGroups().size() <= i) {
			addGroup(new CodeGroup());
		}
		return new CodeGroupBuilder(this, target.getGroups().get(i));
	}


}

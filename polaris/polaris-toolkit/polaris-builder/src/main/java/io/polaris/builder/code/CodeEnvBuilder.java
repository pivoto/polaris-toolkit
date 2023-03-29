package io.polaris.builder.code;

import io.polaris.builder.code.config.CodeEnv;
import io.polaris.builder.code.config.CodeGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public CodeEnvBuilder groups(List<CodeGroup> groups) {
		target.setGroups(groups);
		return this;
	}

	public CodeEnvBuilder property(String key, String value) {
		if (target.getProperty() == null) {
			target.setProperty(new HashMap<>());
		}
		target.getProperty().put(key, value);
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

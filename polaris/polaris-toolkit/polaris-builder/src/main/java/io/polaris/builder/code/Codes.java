package io.polaris.builder.code;

import io.polaris.builder.code.annotation.*;
import io.polaris.builder.code.config.CodeEnvBuilder;
import io.polaris.builder.code.config.CodeGroupBuilder;
import io.polaris.builder.code.config.TypeMapping;
import io.polaris.builder.code.reader.impl.JdbcTablesReader;
import io.polaris.builder.dbv.cfg.DatabaseCfg;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
public class Codes {

	public static void generate(Class<?> clazz) throws IOException {
		Code code = clazz.getAnnotation(Code.class);
		if (!clazz.isAnnotationPresent(Code.class)) {
			throw new IllegalArgumentException();
		}
		generate(code);
	}

	public static void generate(Code code) throws IOException {
		Map<String, String> property = new LinkedHashMap<>();
		{
			DefaultProperty defaultProperty = code.annotationType().getAnnotation(DefaultProperty.class);
			for (Property p : defaultProperty.value()) {
				property.put(p.key(), p.value());
			}
			for (Property p : code.property()) {
				property.put(p.key(), p.value());
			}
		}
		Set<TypeMapping> mappings = new HashSet<>();
		{  // 优先自定义映射
			for (Mapping mapping : code.mapping()) {
				mappings.add(new TypeMapping(mapping.jdbcType(), mapping.javaType()));
			}
			DefaultMapping defaultMapping = code.annotationType().getAnnotation(DefaultMapping.class);
			for (Mapping mapping : defaultMapping.value()) {
				mappings.add(new TypeMapping(mapping.jdbcType(), mapping.javaType()));
			}
		}

		Template[] templates = code.templates();
		{
			DefaultTemplate defaultTemplate = code.annotationType().getAnnotation(DefaultTemplate.class);
			if (templates.length == 0) {
				templates = defaultTemplate.value();
			}
		}

		CodeGenerator generator = generator();
		CodeEnvBuilder codeEnvBuilder = generator.codeEnvBuilder();
		codeEnvBuilder
			.outdir(code.outDir())
			.property(property)
			.mappings(mappings)
			.tablePrefix(code.tablePrefix())
			.tableSuffix(code.tableSuffix())
			.columnPrefix(code.columnPrefix())
			.columnSuffix(code.columnSuffix());

		CodeGroupBuilder codeGroupBuilder = codeEnvBuilder.group()
			.property(property)
			.mappings(mappings)
			.tablePrefix(code.tablePrefix())
			.tableSuffix(code.tableSuffix())
			.columnPrefix(code.columnPrefix())
			.columnSuffix(code.columnSuffix());

		for (Template template : templates) {
			codeGroupBuilder.addTemplate()
				.path(template.path())
				.filename(template.filename())
				.outdir(template.dirname())
				.property(property)
				;
		}

		for (Table table : code.tables()) {
		codeGroupBuilder.addTable()
				.catalog(table.catalog())
			.schema(table.schema())
			.name(table.name())
			.javaPackage(table.javaPackage())
			;
		}

		DatabaseCfg cfg = new DatabaseCfg();
		cfg.setJdbcDriver(code.jdbcDriver());
		cfg.setJdbcUrl(code.jdbcUrl());
		cfg.setJdbcUsername(code.jdbcUsername());
		cfg.setJdbcPassword(code.jdbcPassword());
		generator.tablesReader(new JdbcTablesReader(cfg));


		generator.generate();
	}

	/**
	 * @param codeXmlPath 代码生成配置文件
	 * @param jdbcXmlPath jdbc数据源配置
	 * @param dataXmlPath xml文件数据源配置
	 * @throws IOException
	 */
	public static void generate(String codeXmlPath, String jdbcXmlPath, String dataXmlPath) throws IOException {
		generator().codeXmlPath(codeXmlPath).jdbcXmlPath(jdbcXmlPath).dataXmlPath(dataXmlPath).generate();
	}

	public static CodeGenerator generator() {
		return new CodeGenerator();
	}

}

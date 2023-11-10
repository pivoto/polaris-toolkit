package io.polaris.builder.code;

import io.polaris.builder.code.annotation.*;
import io.polaris.builder.code.config.CodeEnvBuilder;
import io.polaris.builder.code.config.CodeGroupBuilder;
import io.polaris.builder.code.config.ConfigColumn;
import io.polaris.builder.code.config.TypeMapping;
import io.polaris.builder.code.reader.impl.JdbcTablesReader;
import io.polaris.builder.dbv.cfg.DatabaseCfg;
import io.polaris.core.collection.Iterables;
import io.polaris.core.concurrent.Executors;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class Codes {

	public static void generate(Class<?> clazz, String... targets) throws IOException {
		Code code = clazz.getAnnotation(Code.class);
		if (!clazz.isAnnotationPresent(Code.class)) {
			throw new IllegalArgumentException();
		}
		generate(code, targets);
	}

	public static void generate(Class<?> clazz, int concurrent) throws IOException {
		Code code = clazz.getAnnotation(Code.class);
		if (!clazz.isAnnotationPresent(Code.class)) {
			throw new IllegalArgumentException();
		}
		generate(code, concurrent);
	}

	public static void generate(Code code, int concurrent) throws IOException {
		if (concurrent <= 1) {
			generate(code);
		} else {
			ThreadPoolExecutor pool = Executors.create(concurrent, "CodeGenerator");
			CountDownLatch latch = new CountDownLatch(concurrent);
			try {
				final List<IOException> errors = Collections.synchronizedList(new ArrayList<>());
				List<Table>[] tables = new List[concurrent];
				{
					int n = 0;
					for (Table table : code.tables()) {
						int idx = (n++) % concurrent;
						(tables[idx] == null ? tables[idx] = new ArrayList<>() : tables[idx]).add(table);
					}
				}
				for (List<Table> list : tables) {
					if (list != null && !list.isEmpty()) {
						Table[] array = list.toArray(new Table[list.size()]);
						pool.execute(() -> {
							try {
								generate(code, array);
							} catch (IOException e) {
								errors.add(e);
							} finally {
								latch.countDown();
							}
						});
					}
				}
				for (; ; ) {
					try {
						latch.await();
						break;
					} catch (InterruptedException e) {
					}
				}
				int errCount = errors.size();
				if (errCount > 0) {
					IOException e = errors.get(0);
					for (int idx = 1; idx < errCount; idx++) {
						e.addSuppressed(errors.get(idx));
					}
					throw e;
				}
			} finally {
				Executors.shutdown(pool);
			}
		}
	}

	public static void generate(Code code, String... targets) throws IOException {
		Table[] tables = code.tables();
		generate(code, tables, targets);
	}

	@SuppressWarnings("AlibabaMethodTooLong")
	public static void generate(Code code, Table[] tables, String... targets) throws IOException {
		if (tables.length == 0) {
			return;
		}
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
			if (templates.length == 0) {
				DefaultTemplate defaultTemplate = code.annotationType().getAnnotation(DefaultTemplate.class);
				List<Template> list = new ArrayList<>();
				String[] excludeTemplatePaths = code.excludeTemplatePaths();
				if (excludeTemplatePaths.length == 0) {
					list.addAll(Arrays.asList(defaultTemplate.value()));
				} else {
					loop:
					for (Template template : defaultTemplate.value()) {
						String path = template.path();
						for (String excludeTemplatePath : excludeTemplatePaths) {
							if (path.equals(excludeTemplatePath)) {
								continue loop;
							}
						}
						list.add(template);
					}
				}
				list.addAll(Arrays.asList(code.additionalTemplates()));
				templates = list.toArray(new Template[0]);
			}
		}

		CodeGenerator generator = generator().logWithStd(code.logWithStd());
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
			Map<String, String> templateProperty = new LinkedHashMap<>(property);
			for (Property p : template.property()) {
				templateProperty.put(p.key(), p.value());
			}
			codeGroupBuilder.addTemplate()
				.path(template.path())
				.filename(template.filename())
				.outdir(template.dirname())
				.property(templateProperty)
			;
		}

		Function<String, Boolean> includeTable;
		if (targets != null && targets.length > 0) {
			Set<String> set = Iterables.asSet(targets);
			includeTable = name -> set.contains(name);
		} else {
			includeTable = (name) -> true;
		}
		for (Table table : tables) {
			if (!includeTable.apply(table.name())) {
				continue;
			}
			Map<String, String> tableProperty = new LinkedHashMap<>(property);
			for (Property p : table.property()) {
				tableProperty.put(p.key(), p.value());
			}
			Set<ConfigColumn> columns = new HashSet<>();
			for (Column column : table.columns()) {
				columns.add(new ConfigColumn(column.name(), column.javaType()));
			}
			codeGroupBuilder.addTable()
				.catalog(table.catalog())
				.schema(table.schema())
				.name(table.name())
				.javaPackage(table.javaPackage())
				.property(tableProperty)
				.columns(columns)
				.mappings(mappings)
				.tablePrefix(code.tablePrefix())
				.tableSuffix(code.tableSuffix())
				.columnPrefix(code.columnPrefix())
				.columnSuffix(code.columnSuffix());
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

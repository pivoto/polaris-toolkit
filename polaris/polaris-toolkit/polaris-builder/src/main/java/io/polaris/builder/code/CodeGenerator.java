package io.polaris.builder.code;

import io.polaris.builder.code.config.CodeEnv;
import io.polaris.builder.code.config.CodeGroup;
import io.polaris.builder.code.config.CodeTable;
import io.polaris.builder.code.config.ConfigParser;
import io.polaris.builder.code.config.TypeMapping;
import io.polaris.builder.code.dto.TableDto;
import io.polaris.builder.code.reader.TablesReader;
import io.polaris.builder.code.reader.TablesReaders;
import io.polaris.dbv.toolkit.IOKit;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Qt
 * @version Jun 09, 2019
 */
public class CodeGenerator {
	private static final Logger log = LoggerFactory.getLogger("code.generator");
	private CodeEnvBuilder codeEnvBuilder;
	/** 代码生成配置信息 */
	@Accessors(fluent = true, chain = true)
	@Setter
	private CodeEnv codeEnv;
	/** 数据库表元数据读取器 */
	@Accessors(fluent = true, chain = true)
	@Setter
	private TablesReader tablesReader;
	/** 代码生成配置文件 */
	@Accessors(fluent = true, chain = true)
	@Setter
	private String codeXmlPath;
	/** jdbc数据源配置 */
	@Accessors(fluent = true, chain = true)
	@Setter
	private String jdbcXmlPath;
	/** xml文件数据源配置 */
	@Accessors(fluent = true, chain = true)
	@Setter
	private String dataXmlPath;

	private Map<CodeTable, TableDto> tables = new LinkedHashMap<>();


	public CodeGenerator() {
	}

	public void generate() throws IOException {
		readConfig();
		new CodeWriter(codeEnv, tables).write();
	}

	public CodeEnvBuilder codeEnvBuilder() {
		if (codeEnvBuilder == null) {
			codeEnvBuilder = new CodeEnvBuilder(this, new CodeEnv());
		}
		return codeEnvBuilder;
	}

	private void readConfig() throws IOException {
		try {
			buildConfig();
			readTables();
		} finally {
			if (tablesReader != null) {
				tablesReader.close();
			}
		}
	}

	private void readTables() {
		List<CodeGroup> groups = codeEnv.getGroups();
		if (groups == null) {
			return;
		}
		List<TypeMapping> mappings = codeEnv.getMappings();
		if (mappings != null) {
			JdbcTypes.createCustomMappings();
			mappings.forEach(m -> JdbcTypes.addCustomMapping(m.getJdbcType(), m.getJavaType()));
		}
		try {
			for (CodeGroup group : groups) {
				if (group.getTables() == null) {
					continue;
				}
				readTables(group);
			}
		} finally {
			if (mappings != null) {
				JdbcTypes.removeCustomMappings();
			}
		}
	}

	private void readTables(CodeGroup group) {
		List<TypeMapping> mappings = group.getMappings();
		if (mappings != null) {
			JdbcTypes.createCustomMappings();
			mappings.forEach(m -> JdbcTypes.addCustomMapping(m.getJdbcType(), m.getJavaType()));
		}
		try {
			for (CodeTable tableConfig : group.getTables()) {
				String catalogName = StringUtils.trimToNull(tableConfig.getCatalog());
				String schemaName = StringUtils.trimToNull(tableConfig.getSchema());
				String tableName = StringUtils.trimToNull(tableConfig.getTable());
				TableDto table = tablesReader.read(catalogName, schemaName, tableName);
				if (table == null) {
					log.error("找不到表信息：[{}]", tableConfig.getTable());
					continue;
				}

				readTables(group, tableConfig, table);
			}
		} finally {
			if (mappings != null) {
				JdbcTypes.removeCustomMappings();
			}
		}
	}

	private void readTables(CodeGroup group, CodeTable tableConfig, TableDto table) {
		List<TypeMapping> mappings = tableConfig.getMappings();
		if (mappings != null) {
			JdbcTypes.createCustomMappings();
			mappings.forEach(m -> JdbcTypes.addCustomMapping(m.getJdbcType(), m.getJavaType()));
		}
		try {
			// clone后再修改临时字段信息
			table = table.clone();
			if (StringUtils.isNotEmpty(tableConfig.getJavaPackageName())) {
				table.setJavaPackageName(tableConfig.getJavaPackageName());
			}
			table.setProperty(tableConfig.getProperty());

			Set<String> tablePrefix = splitToSet(tableConfig.getTablePrefix(), group.getTablePrefix(), codeEnv.getTablePrefix());
			Set<String> tableSuffix = splitToSet(tableConfig.getTableSuffix(), group.getTableSuffix(), codeEnv.getTableSuffix());
			Set<String> columnPrefix = splitToSet(tableConfig.getColumnPrefix(), group.getColumnPrefix(), codeEnv.getColumnPrefix());
			Set<String> columnSuffix = splitToSet(tableConfig.getColumnSuffix(), group.getColumnSuffix(), codeEnv.getColumnSuffix());
			table.prepare4Java(buildNameTrimmer(tablePrefix, tableSuffix), buildNameTrimmer(columnPrefix, columnSuffix));
			tables.put(tableConfig, table);
		} finally {
			if (mappings != null) {
				JdbcTypes.removeCustomMappings();
			}
		}
	}

	private Function<String, String> buildNameTrimmer(Set<String> prefixSet, Set<String> suffixSet) {
		return name -> {
			boolean handled = true;
			while (handled) {
				handled = false;
				if (prefixSet != null) {
					for (String prefix : prefixSet) {
						if (prefix.length() > 0) {
							if (name.startsWith(prefix)) {
								name = name.substring(prefix.length());
								handled = true;
							}
						}
					}
				}
				if (suffixSet != null) {
					for (String suffix : suffixSet) {
						if (suffix.length() > 0) {
							if (name.endsWith(suffix)) {
								name = name.substring(0, name.length() - suffix.length());
								handled = true;
							}
						}
					}
				}
			}
			return name;
		};
	}

	private Set<String> splitToSet(String... strs) {
		Set<String> set = new HashSet<>();
		for (String str : strs) {
			if (str == null || str.length() == 0) {
				continue;
			}
			String[] arr = str.split(",");
			for (String s : arr) {
				s = s.trim();
				if (s.length() > 0) {
					set.add(s);
				}
			}
		}
		return set;
	}

	private void buildConfig() throws IOException {
		if (codeEnv == null) {
			if (codeEnvBuilder != null) {
				codeEnv = codeEnvBuilder.build();
			} else {
				if (StringUtils.isBlank(codeXmlPath) || !new File(codeXmlPath).isFile()) {
					throw new IllegalArgumentException("代码生成配置信息未设置");
				}
				codeEnv = ConfigParser.parseXml(IOKit.getInputStream(codeXmlPath), new CodeEnv());
			}
		}
		if (tablesReader == null) {
			tablesReader = buildTablesReader();
		}
	}

	private TablesReader buildTablesReader() throws IOException {
		InputStream jdbcInput = null;
		try {
			jdbcInput = IOKit.getInputStream(jdbcXmlPath);
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage(), e);
		}
		TablesReader tablesReader = null;
		if (jdbcInput == null) {
			if (StringUtils.isBlank(dataXmlPath) || !new File(dataXmlPath).isFile()) {
				throw new IllegalArgumentException("Jdbc连接配置与Xml数据源文件均不存在");
			} else {
				tablesReader = TablesReaders.newXmlTablesReader(new File(dataXmlPath));
			}
		} else {
			if (StringUtils.isBlank(dataXmlPath)) {
				tablesReader = TablesReaders.newJdbcTablesReader(jdbcInput);
			} else {
				File dataXmlFile = new File(dataXmlPath);
				if (!dataXmlFile.exists()) {
					dataXmlFile.getAbsoluteFile().getParentFile().mkdirs();
					try (PrintWriter writer = new PrintWriter(dataXmlFile);) {
						writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
						writer.println("<tables></tables>");
						writer.flush();
					}
				}
				if (dataXmlFile.isFile()) {
					tablesReader = TablesReaders.newTablesReader(dataXmlFile, jdbcInput);
				} else {
					tablesReader = TablesReaders.newJdbcTablesReader(jdbcInput);
				}
			}
		}
		return tablesReader;
	}
}

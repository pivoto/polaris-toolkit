package io.polaris.builder;

import io.polaris.builder.bean.CodeEnv;
import io.polaris.builder.bean.CodeGroup;
import io.polaris.builder.bean.CodeTable;
import io.polaris.builder.bean.CodeTemplate;
import io.polaris.builder.bean.db.Table;
import io.polaris.builder.reader.TablesReader;
import io.polaris.builder.velocity.VelocityTemplate;
import io.polaris.dbv.toolkit.IOKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * @author Qt
 * @version Jun 09, 2019
 */
public class CodeGenerator {

	private static final Logger log = LoggerFactory.getLogger(CodeGenerator.class);

	Map<String, Table> tables = new LinkedHashMap<>();
	private CodeEnv codeEnv;
	private Map<CodeGroup, List<Table>> tableGroups = new LinkedHashMap<CodeGroup, List<Table>>();
	private TablesReader tablesReader;

	public CodeGenerator(TablesReader tablesReader, String codeEnvXmlPath) throws IOException {
		this(tablesReader, IOKit.getInputStream(codeEnvXmlPath));
	}

	public CodeGenerator(TablesReader tablesReader, InputStream codeEnvXml) throws IOException {
		this.tablesReader = tablesReader;
		this.codeEnv = CodeEnvParser.parseXml(codeEnvXml, new CodeEnv());
		readConfig();
	}

	private void readConfig() {
		tableGroups = new LinkedHashMap();
		List<CodeGroup> groups = codeEnv.getGroups();
		for (CodeGroup group : groups) {
			List<Table> tableList = new ArrayList<>();
			for (CodeTable tableConfig : group.getTables()) {
				String catalogName = StringUtils.trimToNull(tableConfig.getCatalog());
				String schemaName = StringUtils.trimToNull(tableConfig.getSchema());
				String tableName = StringUtils.trimToNull(tableConfig.getTable());
				Table table = tablesReader.read(catalogName, schemaName, tableName);
				if (table == null) {
					log.error("can't find table [{}]", tableConfig.getTable());
					continue;
				}
				if (StringUtils.isNotEmpty(tableConfig.getJavaPackageName())) {
					table.setJavaPackageName(tableConfig.getJavaPackageName());
				}
				if (StringUtils.isNotEmpty(tableConfig.getClassify())) {
					table.setClassify(tableConfig.getClassify());
				}
				table.setProperty(tableConfig.getProperty());
				table.prepare4Java();
				tableList.add(table);
			}
			tableGroups.put(group, tableList);
		}
	}

	public void generate() {
		try {
			List<CodeGroup> groups = codeEnv.getGroups();
			for (CodeGroup group : groups) {
				List<Table> tableList = tableGroups.get(group);
				for (Table table : tableList) {
					write(group, table);
				}
			}
		} catch (IOException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	private void mkdirs(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can't mkdir: " + dir.getAbsolutePath());
			}
		}
	}


	private void fetchVarToContextAndEnv(Map<String, String> vars, Context context, Map<String, String> env, String prefix) {
		if (vars != null && !vars.isEmpty()) {
			if (StringUtils.isNotBlank(prefix)) {
				Object c = context.get(prefix);
				if (c != null && c instanceof Map) {
					((Map) c).putAll(vars);
				} else if (c == null) {
					c = new LinkedHashMap<>(vars.size());
					((Map) c).putAll(vars);
					context.put(prefix, c);
				}
			}

			Set<Map.Entry<String, String>> entries = vars.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				String key = entry.getKey();
				String value = entry.getValue();
				// ref config-keys above
				value = VelocityTemplate.eval(context, value);

				env.put(key, value);
				context.put(key, value);

				String extKey;
				if (StringUtils.isBlank(prefix)) {
					extKey = key;
				} else {
					if (key.indexOf(".") < 0) {
						extKey = prefix;
						if (key.length() >= 1) {
							extKey += Character.toUpperCase(key.charAt(0));
							if (key.length() >= 2) {
								extKey += key.substring(1);
							}
						}
					} else {
						extKey = prefix + "." + key;
					}
				}
				env.put(extKey, value);
				context.put(extKey, value);
			}
		}
	}

	private void write(CodeGroup group, Table table) throws IOException {
		String baseOutdir = codeEnv.getOutdir();

		List<CodeTemplate> templates = group.getTemplates();
		for (CodeTemplate template : templates) {

			String path = template.getPath();
			String outdir = template.getOutdir();
			String filename = template.getFilename();

			String javaPackageName = table.getJavaPackageName();

			log.info("generate for table [{}] with template [{}]", table.getName(), path);

			Map<String, String> env = new LinkedHashMap<>();

			Context context = VelocityTemplate.createContext();

			context.put("sys", System.getProperties());
			context.put("env", env);
			context.put("table", table);

			fetchVarToContextAndEnv(codeEnv.getProperty(), context, env, "");
			fetchVarToContextAndEnv(group.getProperty(), context, env, "group");
			fetchVarToContextAndEnv(template.getProperty(), context, env, "template");
			fetchVarToContextAndEnv(table.getProperty(), context, env, "table");

			System.getProperties().forEach((key, value) -> {
				if (!env.containsKey(key)) {
					env.put((String) key, (String) value);
				}
			});

			path = VelocityTemplate.eval(context, path);
			outdir = VelocityTemplate.eval(context, outdir);
			filename = VelocityTemplate.eval(context, filename);

			try {
				String basedir = VelocityTemplate.eval(context, baseOutdir);
				File dir = StringUtils.isBlank(basedir) ? new File(outdir) : new File(basedir + "/" + outdir);
				log.info("generate dir:[{}] file:[{}]", filename, dir.getPath());
				write(path, context, dir, filename);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void write(String template, Context context, File dir, String file)
		throws IOException {
		mkdirs(dir);
		FileOutputStream fos = new FileOutputStream(new File(dir, file));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf8"));
		VelocityTemplate.write(context, bw, template);
		bw.flush();
		bw.close();
	}

}

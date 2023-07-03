package io.polaris.builder.code;

import io.polaris.builder.code.config.CodeEnv;
import io.polaris.builder.code.config.CodeGroup;
import io.polaris.builder.code.config.CodeTable;
import io.polaris.builder.code.config.CodeTemplate;
import io.polaris.builder.code.dto.TableDto;
import io.polaris.builder.velocity.VelocityTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeWriter {
	private final CodeEnv codeEnv;
	private final Map<CodeTable, TableDto> tables;
	public static final String VM_PREFIX = "#parse(\"/vm/include.vm\")";

	public CodeWriter(CodeEnv codeEnv, Map<CodeTable, TableDto> tables) {
		this.codeEnv = codeEnv;
		this.tables = tables;
	}


	public void write() {
		try {
			List<CodeGroup> groups = codeEnv.getGroups();
			if (groups == null) {
				return;
			}
			for (CodeGroup group : groups) {
				List<CodeTable> tableList = group.getTables();
				if (tableList == null) {
					continue;
				}
				for (CodeTable codeTable : tableList) {
					TableDto table = tables.get(codeTable);
					if (table == null) {
						continue;
					}
					write(group, table);
				}
			}
		} catch (IOException e) {
			CodeLogger.error("", e);
			throw new RuntimeException(e);
		}
	}

	private void write(CodeGroup group, TableDto table) throws IOException {
		String baseOutdir = codeEnv.getOutdir();

		List<CodeTemplate> templates = group.getTemplates();
		if (templates == null) {
			return;
		}
		for (CodeTemplate template : templates) {

			String path = template.getPath();
			String dirname = template.getDirname();
			String filename = template.getFilename();

			//String javaPackageName = table.getJavaPackageName();

			CodeLogger.info("生成表名[{}]的代码，模板：[{}]", table.getName(), path.replace('\\', '/'));

			Map<String, String> env = new LinkedHashMap<>();
			Map<String, Map<String, String>> property = new LinkedHashMap<>();

			Context context = VelocityTemplate.createContext();

			System.getenv().forEach((key, value) -> env.putIfAbsent((String) key, (String) value));
			System.getProperties().forEach((key, value) -> env.putIfAbsent((String) key, (String) value));

			context.put("sys", System.getProperties());
			context.put("env", env);
			context.put("property", property);
			context.put("table", table);
			property.put("code", codeEnv.getProperty());
			property.put("group", group.getProperty());
			property.put("template", template.getProperty());
			property.put("table", table.getProperty());

			fetchVarToContextAndEnv(codeEnv.getProperty(), context, env, "code");
			fetchVarToContextAndEnv(group.getProperty(), context, env, "group");
			fetchVarToContextAndEnv(template.getProperty(), context, env, "template");
			fetchVarToContextAndEnv(table.getProperty(), context, env, "table");

			path = VelocityTemplate.eval(context, VM_PREFIX + path);
			dirname = VelocityTemplate.eval(context, VM_PREFIX + dirname);
			filename = VelocityTemplate.eval(context, VM_PREFIX + filename);

			try {
				String basedir = VelocityTemplate.eval(context, VM_PREFIX + baseOutdir);
				File dir = StringUtils.isBlank(basedir) ? new File(dirname) : new File(basedir + "/" + dirname);
				CodeLogger.info("生成表名[{}]的代码，目录：[{}]，文件：[{}]", table.getName(), dir.getPath().replace('\\', '/'), filename);
				write(path, context, dir, filename);
			} catch (Exception e) {
				CodeLogger.error(e.getMessage(), e);
			}
		}
	}

	private void write(String template, Context context, File dir, String file)
		throws IOException {
		mkdirs(dir);
		FileOutputStream fos = new FileOutputStream(new File(dir, file));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, Charset.defaultCharset()));
		VelocityTemplate.write(context, bw, template);
		bw.flush();
		bw.close();
	}

	private void fetchVarToContextAndEnv(Map<String, String> vars, Context context, Map<String, String> env, String prefix) {
		if (vars == null || vars.isEmpty()) {
			return;
		}
		Set<Map.Entry<String, String>> entries = vars.entrySet();
		for (Map.Entry<String, String> entry : entries) {
			String key = entry.getKey();
			String value = entry.getValue();
			// ref config-keys above
			value = VelocityTemplate.eval(context, VM_PREFIX + value);
			env.put(key, value);

			if (StringUtils.isNotBlank(prefix) && key.length() >= 1) {
				String extKey;
				if (key.indexOf(".") < 0) {
					extKey = prefix + Character.toUpperCase(key.charAt(0));
					if (key.length() >= 2) {
						extKey += key.substring(1);
					}
				} else {
					extKey = prefix + "." + key;
				}
				env.putIfAbsent(extKey, value);
			}
		}
	}

	private void mkdirs(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can't mkdir: " + dir.getAbsolutePath());
			}
		}
	}


}

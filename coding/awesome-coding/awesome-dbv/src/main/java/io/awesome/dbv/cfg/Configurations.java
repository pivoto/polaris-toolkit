package io.awesome.dbv.cfg;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import io.awesome.dbv.toolkit.IOKit;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Qt
 */
@Slf4j
public class Configurations {

	private static final String DATABASE_XML = "/database.xml";

	public static XStream buildXStream() {
		XStream xs = new XStream();
		xs.ignoreUnknownElements();
		xs.autodetectAnnotations(true);
		xs.processAnnotations(DatabaseCfg.class);
		xs.processAnnotations(SqlCfg.class);
		xs.registerConverter(new Converter() {
			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				SqlCfg sql = (SqlCfg) source;
				writer.addAttribute("id", sql.getId());
				writer.setValue(sql.getSql());
			}

			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				SqlCfg sql = new SqlCfg();
				sql.setId(reader.getAttribute("id"));
				sql.setSql(reader.getValue());
				return sql;
			}

			@Override
			public boolean canConvert(Class type) {
				return SqlCfg.class.isAssignableFrom(type);
			}
		});
		return xs;
	}

	private static InputStream getDatabaseCfgInputStream() throws FileNotFoundException {
		return IOKit.getInputStream(DATABASE_XML);
	}

	private static InputStream getDatabaseCfgInputStream(String cfgPath) throws FileNotFoundException {
		return IOKit.getInputStream(cfgPath);
	}

	public static DatabaseCfg getDatabaseCfg(String cfgPath) throws FileNotFoundException {
		InputStream in = Configurations.getDatabaseCfgInputStream(cfgPath);
		return getDatabaseCfg(in);
	}

	public static DatabaseCfg getDatabaseCfg() throws FileNotFoundException {
		InputStream in = Configurations.getDatabaseCfgInputStream();
		return getDatabaseCfg(in);
	}

	public static DatabaseCfg getDatabaseCfg(InputStream in) {
		return getDatabaseCfg(in, new DatabaseCfg());
	}

	public static DatabaseCfg getDatabaseCfg(InputStream in, DatabaseCfg cfg) {
		try {
			XStream xs = buildXStream();
			xs.fromXML(in, cfg);
			loadJdbcInfoProperties(cfg);
			loadExtCfg(cfg, xs);
			return cfg;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			IOKit.closeQuietly(in);
		}
	}

	private static void loadExtCfg(DatabaseCfg cfg, XStream xs) {
		try {
			String databaseType = cfg.getDatabaseType();
			if (databaseType != null) {
				DatabaseType type = DatabaseType.valueOf(databaseType);
				if (type.getCfgFile() != null) {
					try (InputStream extCfgFile = getDatabaseCfgInputStream(type.getCfgFile())) {
						xs.fromXML(extCfgFile, cfg);
					}
				}
			}
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	private static void loadJdbcInfoProperties(DatabaseCfg cfg) throws IOException {
		try {
			// 配置参数再加工
			Properties jdbcInfoProperties = cfg.getJdbcInfoProperties();
			if (jdbcInfoProperties == null) {
				jdbcInfoProperties = new Properties();
				jdbcInfoProperties.setProperty("remarks", "true");
				jdbcInfoProperties.setProperty("remarksReporting", "true");
				jdbcInfoProperties.setProperty("useInformationSchema", "true");
				cfg.setJdbcInfoProperties(jdbcInfoProperties);
			}
			String jdbcInfoPropertiesPath = cfg.getJdbcInfoPropertiesPath();
			if (jdbcInfoPropertiesPath != null) {
				jdbcInfoProperties.load(IOKit.getInputStream(jdbcInfoPropertiesPath));
			}
			String jdbcUsername = cfg.getJdbcUsername();
			if (jdbcUsername != null) {
				jdbcInfoProperties.setProperty("user", jdbcUsername);
			}
			String jdbcPassword = cfg.getJdbcPassword();
			if (jdbcPassword != null) {
				jdbcInfoProperties.setProperty("password", jdbcPassword);
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}
}

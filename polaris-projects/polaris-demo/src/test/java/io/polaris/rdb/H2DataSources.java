package io.polaris.rdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.RunScript;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * @author Qt
 * @since 1.8,  Apr 24, 2024
 */
@Slf4j
public class H2DataSources {
	private static final AtomicInteger sequence = new AtomicInteger(0);
	private static final String MODE_ORACLE = "Oracle";

	public static void loadScripts(DataSource ds, String path, String encoding) throws IOException, SQLException {
		Thread.currentThread().getContextClassLoader().getResources(path);
		ResourcePatternResolver resolver = ResourcePatternUtils
			.getResourcePatternResolver(new ClassRelativeResourceLoader(H2DataSources.class));
		Resource[] resources = resolver.getResources(path);
		if (resources == null || resources.length == 0) return;
		try (Connection conn = ds.getConnection();) {
			for (Resource resource : resources) {
				try (InputStream in = resource.getInputStream();) {
					loadScript(conn, in, encoding);
				}
			}
		}
	}

	public static void loadScript(Connection conn, InputStream in, String encoding) throws SQLException, UnsupportedEncodingException {
		RunScript.execute(conn, new InputStreamReader(in, encoding));
	}

	public static void loadScript(Connection conn, Reader reader) throws SQLException, UnsupportedEncodingException {
		RunScript.execute(conn, reader);
	}

	public static DataSource buildDataSource() {
		return buildDataSource(null);
	}

	public static DataSource buildDataSource(String initSqlPath) {
		org.h2.jdbcx.JdbcDataSource dataSource = new org.h2.jdbcx.JdbcDataSource();
		String url = String.format("jdbc:h2:mem:db-%s;MODE=%s;DB_CLOSE_DELAY=-1;AUTO_RECONNECT=TRUE;TRACE_LEVEL_SYSTEM_OUT=1"
			, sequence.getAndIncrement(), MODE_ORACLE);
		dataSource.setURL(url);
		dataSource.setUser("sa");
		dataSource.setPassword("");
		if (StringUtils.isNotBlank(initSqlPath)) {
			try {
				loadScripts(dataSource, initSqlPath, "utf-8");
			} catch (IOException | SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		return dataSource;
	}

}

package io.polaris.builder.dbv;

import com.alibaba.fastjson2.JSON;
import io.polaris.builder.dbv.cfg.Configurations;
import io.polaris.builder.dbv.cfg.DatabaseCfg;
import io.polaris.core.io.IO;
import com.thoughtworks.xstream.XStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.FileNotFoundException;

/**
 * @author Qt
 * @since 1.8
 */
public class XstreamTest {

	@Test
	public void test02(TestInfo testInfo) throws FileNotFoundException {
		System.out.println(testInfo.getDisplayName());
		DatabaseCfg cfg = Configurations.getDatabaseCfg("/local.xml");
		System.out.println(Configurations.buildXStream().toXML(cfg));
		Object o = Configurations.buildXStream().fromXML(IO.getInputStream("/META-INF/dbv/mysql.xml", Configurations.class));
		System.out.println(o);
	}

	@Test
	public void test01() {
		DatabaseCfg cfg = new DatabaseCfg();

		XStream xs = new XStream();
		xs.ignoreUnknownElements();
		xs.alias("database", DatabaseCfg.class);
		xs.aliasField("database-type", DatabaseCfg.class, "databaseType");
		xs.aliasField("jdbc-driver", DatabaseCfg.class, "jdbcDriver");
		xs.aliasField("jdbc-url", DatabaseCfg.class, "jdbcUrl");
		xs.aliasField("jdbc-username", DatabaseCfg.class, "jdbcUsername");
		xs.aliasField("jdbc-password", DatabaseCfg.class, "jdbcPassword");
		xs.aliasField("jdbc-info-properties-path", DatabaseCfg.class, "jdbcInfoPropertiesPath");
		Object o = xs.fromXML(Thread.currentThread().getContextClassLoader()
			.getResourceAsStream("local.xml"), cfg);
		System.out.println(JSON.toJSONString(o));
		System.out.println(JSON.toJSONString(cfg));
	}
}

package io.polaris.dbv;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;
import io.polaris.dbv.cfg.Configurations;
import io.polaris.dbv.cfg.DatabaseCfg;
import io.polaris.dbv.toolkit.IOKit;
import org.junit.Test;

import java.io.FileNotFoundException;

/**
 * @author Qt
 * @version Jul 24, 2019
 */
public class XstreamTest {

	@Test
	public void test02() throws FileNotFoundException {
		DatabaseCfg cfg = Configurations.getDatabaseCfg("/local.xml");
		System.out.println(Configurations.buildXStream().toXML(cfg));
		Object o = Configurations.buildXStream().fromXML(IOKit.getInputStream("mysql.xml", Configurations.class));
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

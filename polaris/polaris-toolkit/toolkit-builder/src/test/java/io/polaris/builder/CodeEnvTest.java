package io.polaris.builder;

import com.thoughtworks.xstream.XStream;
import io.polaris.builder.bean.CodeGroup;
import io.polaris.builder.bean.CodeTable;
import io.polaris.builder.bean.CodeTemplate;
import org.junit.Test;

/**
 * @author Qt
 * @version Jul 25, 2019
 */
public class CodeEnvTest {

	@Test
	public void test01() {
		XStream xs = CodeEnvParser.buildXStream();
		CodeTable table = new CodeTable();
		table.setCatalog("catalog");

		CodeGroup group = new CodeGroup();
		group.getTables().add(table);

		group.getTemplates().add(new CodeTemplate());

		System.out.println(xs.toXML(table));
		System.out.println(xs.toXML(group));

	}
}
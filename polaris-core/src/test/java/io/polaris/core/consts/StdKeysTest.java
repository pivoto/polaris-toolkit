package io.polaris.core.consts;

import io.polaris.core.converter.Converters;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class StdKeysTest {

	@Test
	void test01() {
		Consoles.log("[{}]", Converters.convertQuietly(boolean.class, GlobalStdEnv.get(Consoles.class.getName() + ".log.enabled", "true"), true));
		Consoles.log("[{}]", GlobalStdEnv.get(StdKeys.JDBC_SQL_LINE_SEPARATOR, " "));
		Consoles.log("[{}]", GlobalStdEnv.getBoolean(StdKeys.JAVA_CLASS_GENERATOR_CACHE, true));
	}

}

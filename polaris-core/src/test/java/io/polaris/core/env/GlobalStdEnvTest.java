package io.polaris.core.env;

import java.util.List;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.io.Consoles;
import io.polaris.core.json.Jsons;
import lombok.Data;
import org.junit.jupiter.api.Test;

class GlobalStdEnvTest {


	@Test
	void test() {
		GlobalStdEnv.set(StdKeys.LOGGER_LEVEL + ".aaa", "info");
		GlobalStdEnv.set(StdKeys.LOGGER_LEVEL + ".bbb", "error");
		GlobalStdEnv.set(StdKeys.JDBC_SQL_SPACE_AS_LF, "false");
		GlobalStdEnv.set(StdKeys.JDBC_SQL_PARAM_EMPTY_AS_NULL, "false");
		GlobalStdEnv.set(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY, "true");
		GlobalStdEnv.set(StdKeys.BYTECODE_GENERATOR_CACHE, "false");
		GlobalStdEnv.set(StdKeys.BYTECODE_GENERATOR_TMPDIR, "/tmp/bytecode");
		GlobalStdEnv.set(StdKeys.ASYNC_POOL_CORE, "8");
		GlobalStdEnv.set(StdKeys.ASYNC_POOL_MAX, "16");
		GlobalStdEnv.set(StdKeys.ASYNC_POOL_SCHEDULER, "8");
		GlobalStdEnv.set(StdKeys.OS_IP_REGEX, "10\\.");
		GlobalStdEnv.set("polaris.list.1.a", "1");
		GlobalStdEnv.set("polaris.list.2.a", "2");
		GlobalStdEnv.set("polaris.list.x.a", "x");
		GlobalStdEnv.set("polaris.strings.0", "0");
		GlobalStdEnv.set("polaris.strings.2", "2");
		GlobalStdEnv.set("polaris.strings.x", "x");

		// 触发监听器
		GlobalStdEnvObservable.notifyListeners();

		TestProps o = GlobalStdEnv.bind("polaris", TestProps.class);
		Consoles.println(Jsons.serialize(o));
	}


	@Data
	public static class TestProps extends StdCfg {
		private List<A> list;
		private List<String> strings;

		@Data
		public static class A {
			private String a;
		}
	}

}

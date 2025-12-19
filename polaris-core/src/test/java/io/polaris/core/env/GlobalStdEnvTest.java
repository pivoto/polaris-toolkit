package io.polaris.core.env;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.converter.Converters;
import io.polaris.core.io.Consoles;
import io.polaris.core.json.Jsons;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.bean.MetaObject;
import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.log.Level;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.StringCases;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalStdEnvTest {


	@Test
	void test() {
		GlobalStdEnv.set(StdKeys.JDBC_SQL_SPACE_AS_LF, "false");
		GlobalStdEnv.set(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY_STRING, "true");
		GlobalStdEnv.set(StdKeys.JDBC_SQL_PARAM_INCLUDE_EMPTY_COLLECTION, "true");
		GlobalStdEnv.set(StdKeys.BYTECODE_GENERATOR_TMPDIR, "/tmp/bytecode");
		GlobalStdEnv.set(StdKeys.ASYNC_POOL_CORE, "8");
		GlobalStdEnv.set(StdKeys.ASYNC_POOL_MAX, "16");
		GlobalStdEnv.set(StdKeys.ASYNC_POOL_SCHEDULER, "8");
		TestProps o = GlobalStdEnv.bind("polaris" , TestProps.class);
		Consoles.println(Jsons.serialize(o));
	}


	@Data
	public static class TestProps {

		private Logger logger = new Logger();
		private Async async = new Async();
		private Jdbc jdbc = new Jdbc();
		private Bytecode bytecode = new Bytecode();
		private Os os = new Os();

		@Data
		public static class Async {
			private Pool pool = new Pool();

			@Data
			public static class Pool {
				private Integer core;
				private Integer max;
				private Integer scheduler;
			}
		}

		@Data
		public static class Logger {
			private Map<String, Level> level = new HashMap<>();
		}

		@Data
		public static class Jdbc {
			private Sql sql = new Sql();

			@Data
			public static class Sql {
				private Boolean spaceAsLf = true;
				private Param param = new Param();

				@Data
				public static class Param {
					private Boolean includeEmptyString = false;
					private Boolean includeEmptyCollection = false;
				}
			}
		}

		@Data
		public static class Bytecode {
			private Generator generator = new Generator();

			@Data
			public static class Generator {
				private String tmpdir;
				private Boolean cache = true;
			}
		}

		@Data
		public static class Os {
			private String ipRegex;
		}
	}
}

package io.polaris.core.env;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.log.Level;
import lombok.Data;
import org.apache.tools.ant.taskdefs.condition.Os;

/**
 * @author Qt
 * @since Dec 20, 2025
 */
@Data
public class StdCfg {
	private static volatile StdCfg instance;

	private Logger logger = new Logger();
	private Bytecode bytecode = new Bytecode();
	private Async async = new Async();
	private Jdbc jdbc = new Jdbc();
	private Os os = new Os();


	public static StdCfg instance() {
		if (instance == null) {
			synchronized (StdCfg.class) {
				if (instance == null) {
					instance = GlobalStdEnv.bind(StdKeys.POLARIS, StdCfg.class);
					GlobalStdEnvObservable.addListener(() -> {
						instance = GlobalStdEnv.bind(StdKeys.POLARIS, StdCfg.class);
					});
				}
			}
		}
		return instance;
	}

	@Data
	public static class Logger {
		private Map<String, Level> level = new HashMap<>();
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
	public static class Jdbc {
		private Sql sql = new Sql();

		@Data
		public static class Sql {
			private Boolean spaceAsLf = true;
			private Param param = new Param();

			@Data
			public static class Param {
				private Boolean includeEmpty = false;
				private Boolean emptyAsNull = true;
			}
		}
	}

}

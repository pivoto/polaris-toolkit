package io.polaris.core.env;

import java.util.HashMap;
import java.util.Map;

import io.polaris.core.annotation.Remarks;
import io.polaris.core.consts.StdKeys;
import io.polaris.core.log.Level;
import lombok.Data;
import org.apache.tools.ant.taskdefs.condition.Os;

/**
 * @author Qt
 * @since Dec 20, 2025
 */
@Data
@Remarks("工具库标准属性配置类，绑定 polaris.* 配置项")
public class StdCfg {
	private static volatile StdCfg instance;

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

	/** 控制台模式日志实现配置 */
	private Logger logger = new Logger();
	/** 生成字节码配置 */
	private Bytecode bytecode = new Bytecode();
	/** 操作系统相关工具类配置 */
	private Os os = new Os();
	/** 默认的异步处理实现配置 */
	private Async async = new Async();
	/** Jdbc工具类实现配置 */
	private Jdbc jdbc = new Jdbc();

	@Data
	public static class Logger {
		/** 控制台模式日志实现类使用的日志级别配置 */
		private Map<String, Level> level = new HashMap<>();
	}

	@Data
	public static class Bytecode {
		/** 生成字节码时的相关配置 */
		private Generator generator = new Generator();

		@Data
		public static class Generator {
			/** 生成字节码时的临时目录 */
			private String tmpdir;
			/** 生成字节码时是否开启缓存支持，默认开启 */
			private Boolean cache = true;
		}
	}

	@Data
	public static class Os {
		/** 获取本机IP地址的优先匹配模式，用于多网卡下配置以获取期望的网络地址，多个模式间可用逗号分隔 */
		private String ipRegex;
	}

	@Data
	public static class Async {
		/** 默认的异步处理实现的线程池配置 */
		private Pool pool = new Pool();

		@Data
		public static class Pool {
			/** 默认的异步处理实现的线程池的核心数 */
			private Integer core;
			/** 默认的异步处理实现的线程池的最大数 */
			private Integer max;
			/** 默认的异步处理实现的调度线程池大小 */
			private Integer scheduler;
		}
	}


	@Data
	public static class Jdbc {
		/** 动态SQL语句理时的相关配置 */
		private Sql sql = new Sql();

		@Data
		public static class Sql {
			/** 动态SQL语句中是否使用空格替换换行符 */
			private Boolean spaceAsLf = true;
			/** 动态SQL语句参数处理时的相关配置 */
			private Param param = new Param();

			@Data
			public static class Param {
				/** 动态SQL语句中是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
				private Boolean emptyAsNull = true;
				/** 动态SQL语句中是否允许空字符串作为有效参数，而不是忽略 */
				private Boolean includeEmpty = false;
			}
		}
	}

}

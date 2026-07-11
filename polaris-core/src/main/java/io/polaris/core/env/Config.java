package io.polaris.core.env;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.log.Level;
import io.polaris.core.string.Strings;
import lombok.Data;

/**
 * 不同于 StdCfg，每次访问都获取最新配置值，并支持优先使用直接覆盖的配置值
 *
 * @author Qt
 * @since Feb 26, 2026
 */
public enum Config {

	INSTANCE;

	/** 控制台模式日志实现配置 */
	private final Logger logger = new Logger();
	/** 生成字节码配置 */
	private final Bytecode bytecode = new Bytecode();
	/** 操作系统相关工具类配置 */
	private final Os os = new Os();
	/** 默认的异步处理实现配置 */
	private final Async async = new Async();
	/** Jdbc工具类实现配置 */
	private final Jdbc jdbc = new Jdbc();

	@Nonnull
	public Logger logger() {
		return logger;
	}

	@Nonnull
	public Bytecode bytecode() {
		return bytecode;
	}

	@Nonnull
	public Os os() {
		return os;
	}

	@Nonnull
	public Async async() {
		return async;
	}

	@Nonnull
	public Jdbc jdbc() {
		return jdbc;
	}

	public static class Logger {
		/** 控制台模式日志实现类使用的日志级别配置 */
		private final Map<String, Level> level = new HashMap<>();
		/** 默认的日志级别配置 */
		private Level defaultLevel = Level.DEBUG;

		/** 设置控制台模式默认的日志级别配置 */
		@Nonnull
		public Logger defaultLevel(@Nonnull Level level) {
			this.defaultLevel = level;
			return this;
		}

		/** 设置控制台模式日志级别配置 */
		@Nonnull
		public Logger level(@Nonnull String name, @Nonnull Level level) {
			this.level.put(name, level);
			return this;
		}

		/** 获取控制台模式日志实现类使用的日志级别配置 */
		@Nonnull
		public Level level(String name) {
			Level level = this.level.get(name);
			if (level == null) {
				try {
					String levelStr = GlobalStdEnv.get(StdKeys.LOGGER_LEVEL + "." + name);
					if (Strings.isBlank(levelStr)) {
						level = defaultLevel;
					} else {
						level = Level.valueOf(levelStr.toUpperCase());
					}
				} catch (Throwable e) {
					level = defaultLevel;
				}
			}
			return level;
		}
	}

	public static class Bytecode {
		/** 生成字节码时的相关配置 */
		private final Bytecode.Generator generator = new Bytecode.Generator();

		/** 生成字节码时的相关配置 */
		@Nonnull
		public Bytecode.Generator generator() {
			return generator;
		}

		public static class Generator {
			/** 生成字节码时的临时目录 */
			private String tmpdir;
			/** 生成字节码时是否开启缓存支持，默认开启 */
			private Boolean cache;

			/** 生成字节码时的临时目录 */
			@Nullable
			public String tmpdir() {
				if (Strings.isNotBlank(tmpdir)) {
					return tmpdir;
				}
				return GlobalStdEnv.get(StdKeys.BYTECODE_GENERATOR_TMPDIR);
			}

			/** 生成字节码时是否开启缓存支持，默认开启 */
			public boolean cache() {
				if (cache != null) {
					return cache;
				}
				return GlobalStdEnv.getBoolean(StdKeys.BYTECODE_GENERATOR_CACHE, true);
			}

			/** 生成字节码时的临时目录 */
			@Nonnull
			public Generator tmpdir(@Nullable String tmpdir) {
				this.tmpdir = tmpdir;
				return this;
			}

			/** 生成字节码时是否开启缓存支持 */
			@Nonnull
			public Generator cache(@Nullable Boolean cache) {
				this.cache = cache;
				return this;
			}
		}
	}

	@Data
	public static class Os {
		/** 获取本机IP地址的优先匹配模式，用于多网卡下配置以获取期望的网络地址，多个模式间可用逗号分隔 */
		private String ipRegex;

		@Nullable
		public String ipRegex() {
			if (Strings.isNotBlank(ipRegex)) {
				return ipRegex;
			}
			return GlobalStdEnv.get(StdKeys.OS_IP_REGEX);
		}

		@Nonnull
		public Os ipRegex(@Nullable String ipRegex) {
			this.ipRegex = ipRegex;
			return this;
		}
	}

	public static class Async {
		/** 默认的异步处理实现的线程池配置 */
		private final Async.Pool pool = new Async.Pool();

		@Nonnull
		public Pool pool() {
			return pool;
		}

		public static class Pool {
			/** 默认的异步处理实现的线程池的核心数 */
			private Integer core;
			/** 默认的异步处理实现的线程池的最大数 */
			private Integer max;
			/** 默认的异步处理实现的调度线程池大小 */
			private Integer scheduler;

			/** 默认的异步处理实现的线程池的核心数，默认为0，由使用者自行决定 */
			public int core() {
				if (core != null) {
					return core;
				}
				// 默认为0，由使用者自行决定
				return GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_CORE, 0);
			}

			/** 默认的异步处理实现的线程池的核心数 */
			@Nonnull
			public Pool core(@Nullable Integer core) {
				this.core = core;
				return this;
			}

			/** 默认的异步处理实现的线程池的最大数，默认为0，由使用者自行决定 */
			public int max() {
				if (max != null) {
					return max;
				}
				// 默认为0，由使用者自行决定
				return GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_MAX, 0);
			}

			/** 默认的异步处理实现的线程池的最大数 */
			@Nonnull
			public Pool max(@Nullable Integer max) {
				this.max = max;
				return this;
			}

			/** 默认的异步处理实现的调度线程池大小，默认为0，由使用者自行决定 */
			public int scheduler() {
				if (scheduler != null) {
					return scheduler;
				}
				// 默认为0，由使用者自行决定
				return GlobalStdEnv.getInt(StdKeys.ASYNC_POOL_SCHEDULER, 0);
			}

			/** 默认的异步处理实现的调度线程池大小 */
			@Nonnull
			public Pool scheduler(@Nullable Integer scheduler) {
				this.scheduler = scheduler;
				return this;
			}
		}
	}


	public static class Jdbc {
		/** 动态SQL语句理时的相关配置 */
		private final Jdbc.Sql sql = new Jdbc.Sql();

		@Nonnull
		public Sql sql() {
			return sql;
		}

		public static class Sql {
			/** 动态SQL语句中是否使用空格替换换行符 */
			private Boolean spaceAsLf;
			/** 动态SQL语句参数处理时的相关配置 */
			private final Jdbc.Sql.Param param = new Jdbc.Sql.Param();

			/** 动态SQL语句中是否使用空格替换换行符，默认为true */
			public boolean spaceAsLf() {
				if (spaceAsLf != null) {
					return spaceAsLf;
				}
				return GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_SPACE_AS_LF, true);
			}

			/** 设置动态SQL语句中是否使用空格替换换行符 */
			@Nonnull
			public Sql spaceAsLf(@Nullable Boolean spaceAsLf) {
				this.spaceAsLf = spaceAsLf;
				return this;
			}

			/** 动态SQL语句参数处理时的相关配置 */
			@Nonnull
			public Param param() {
				return param;
			}

			public static class Param {
				/** 动态SQL语句中的查询条件列值处理时是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
				private Boolean whereEmptyAsNull;
				/** 动态SQL语句中的查询条件列值是否允许空字符串作为有效参数，而不是忽略 */
				private Boolean whereIncludeEmpty;
				/** 动态SQL语句中的新增或更新列值处理时是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
				private Boolean entityEmptyAsNull;
				/** 动态SQL语句中的新增或更新列值是否允许空字符串作为有效参数，而不是忽略 */
				private Boolean entityIncludeEmpty;

				/** 动态SQL语句中的查询条件列值处理时是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式，默认为true */
				public boolean whereEmptyAsNull() {
					if (whereEmptyAsNull != null) {
						return whereEmptyAsNull;
					}
					return GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_WHERE_EMPTY_AS_NULL, true);
				}

				/** 动态SQL语句中的查询条件列值处理时是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
				@Nonnull
				public Param whereEmptyAsNull(@Nullable Boolean emptyAsNull) {
					this.whereEmptyAsNull = emptyAsNull;
					return this;
				}

				/** 动态SQL语句中的查询条件列值是否允许空字符串作为有效参数，而不是忽略，默认为false */
				public boolean whereIncludeEmpty() {
					if (whereIncludeEmpty != null) {
						return whereIncludeEmpty;
					}
					return GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_WHERE_INCLUDE_EMPTY, false);
				}

				/** 动态SQL语句中的查询条件列值是否允许空字符串作为有效参数，而不是忽略 */
				@Nonnull
				public Param whereIncludeEmpty(@Nullable Boolean includeEmpty) {
					this.whereIncludeEmpty = includeEmpty;
					return this;
				}

				/** 动态SQL语句中的新增或更新列值处理时是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式，默认为true */
				public boolean entityEmptyAsNull() {
					if (entityEmptyAsNull != null) {
						return entityEmptyAsNull;
					}
					return GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_ENTITY_EMPTY_AS_NULL, true);
				}

				/** 动态SQL语句中的新增或更新列值处理时是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
				@Nonnull
				public Param entityEmptyAsNull(@Nullable Boolean emptyAsNull) {
					this.entityEmptyAsNull = emptyAsNull;
					return this;
				}

				/** 动态SQL语句中的新增或更新列值是否允许空字符串作为有效参数，而不是忽略，默认为false */
				public boolean entityIncludeEmpty() {
					if (entityIncludeEmpty != null) {
						return entityIncludeEmpty;
					}
					return GlobalStdEnv.getBoolean(StdKeys.JDBC_SQL_PARAM_ENTITY_INCLUDE_EMPTY, false);
				}

				/** 动态SQL语句中的新增或更新列值是否允许空字符串作为有效参数，而不是忽略 */
				@Nonnull
				public Param entityIncludeEmpty(@Nullable Boolean includeEmpty) {
					this.entityIncludeEmpty = includeEmpty;
					return this;
				}
			}
		}
	}

}

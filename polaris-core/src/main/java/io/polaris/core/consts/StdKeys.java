package io.polaris.core.consts;

/**
 * @author Qt
 * @since May 10, 2024
 */
public interface StdKeys {

	// ========================================================================
	// 以下为虚拟机内置属性

	String OS_NAME = "os.name";
	String USER_DIR = "user.dir";
	String LINE_SEPARATOR = "line.separator";
	String JAVA_CLASS_PATH = "java.class.path";
	String JAVA_IO_TMPDIR = "java.io.tmpdir";
	String SUN_CPU_ENDIAN = "sun.cpu.endian";

	// ========================================================================
	// 以下为工具库配置属性

	String POLARIS = "polaris";
	String POLARIS_DOT = POLARIS + ".";

	/** 控制台模式日志实现类使用的日志级别配置 */
	String LOGGER_LEVEL = POLARIS_DOT + "logger.level";

	/** 生成字节码时的临时目录 */
	String BYTECODE_GENERATOR_TMPDIR = POLARIS_DOT + "bytecode.generator.tmpdir";
	/** 生成字节码时是否开启缓存支持，默认开启 */
	String BYTECODE_GENERATOR_CACHE = POLARIS_DOT + "bytecode.generator.cache";

	/** 获取本机IP地址的优先匹配模式，用于多网卡下配置以获取期望的网络地址，多个模式间可用逗号分隔 */
	String OS_IP_REGEX = POLARIS_DOT + "os.ip-regex";
	/** 默认的异步处理实现的线程池的核心数 */
	String ASYNC_POOL_CORE = POLARIS_DOT + "async.pool.core";
	/** 默认的异步处理实现的线程池的最大数 */
	String ASYNC_POOL_MAX = POLARIS_DOT + "async.pool.max";
	/** 默认的异步处理实现的调度线程池大小 */
	String ASYNC_POOL_SCHEDULER = POLARIS_DOT + "async.pool.scheduler";

	/** 动态SQL语句中是否使用空格替换换行符 */
	String JDBC_SQL_SPACE_AS_LF = POLARIS_DOT + "jdbc.sql.space-as-lf";
	/** 动态SQL语句中是否将空字符串转换为NULL值处理，而不是直接使用，可遵循具体数据库的处理方式 */
	String JDBC_SQL_PARAM_EMPTY_AS_NULL = POLARIS_DOT + "jdbc.sql.param.empty-as-null";
	/** 动态SQL语句中是否允许空字符串作为有效参数，而不是忽略 */
	String JDBC_SQL_PARAM_INCLUDE_EMPTY = POLARIS_DOT + "jdbc.sql.param.include-empty";

	// ========================================================================
}

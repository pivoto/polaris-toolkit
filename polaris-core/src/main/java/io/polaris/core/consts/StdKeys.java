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

	String LOGGER_LEVEL = POLARIS_DOT + "logger.level";

	String BYTECODE_GENERATOR_TMPDIR = POLARIS_DOT + "bytecode.generator.tmpdir";
	String BYTECODE_GENERATOR_CACHE = POLARIS_DOT + "bytecode.generator.cache";

	String OS_IP_REGEX = POLARIS_DOT + "os.ip-regex";
	String ASYNC_POOL_CORE = POLARIS_DOT + "async.pool.core";
	String ASYNC_POOL_MAX = POLARIS_DOT + "async.pool.max";
	String ASYNC_POOL_SCHEDULER = POLARIS_DOT + "async.pool.scheduler";

	String JDBC_SQL_SPACE_AS_LF = POLARIS_DOT + "jdbc.sql.space-as-lf";
	String JDBC_SQL_PARAM_EMPTY_AS_NULL = POLARIS_DOT + "jdbc.sql.param.empty-as-null";
	String JDBC_SQL_PARAM_INCLUDE_EMPTY = POLARIS_DOT + "jdbc.sql.param.include-empty";

	// ========================================================================
}

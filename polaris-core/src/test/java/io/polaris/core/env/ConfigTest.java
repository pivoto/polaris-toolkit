package io.polaris.core.env;

import io.polaris.core.consts.StdKeys;
import io.polaris.core.io.Consoles;
import io.polaris.core.json.Jsons;
import io.polaris.core.log.Level;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

	@Test
	void testConfig() {
		Consoles.println(Jsons.serialize(GlobalStdEnv.bind("polaris", StdCfg.class)));
		Consoles.println(Jsons.serialize(StdCfg.instance()));
		Consoles.println("logger.level:  aaa: {}, bbb: {}, ccc: {}",
			Config.INSTANCE.logger().level("aaa")
			, Config.INSTANCE.logger().level("bbb")
			, Config.INSTANCE.logger().level("ccc"));
		Consoles.println("jdbc.sql.spaceAsLf: {}", Config.INSTANCE.jdbc().sql().spaceAsLf());
		Consoles.println("jdbc.sql.emptyAsNull: {}", Config.INSTANCE.jdbc().sql().param().emptyAsNull());
		Consoles.println("jdbc.sql.includeEmpty: {}", Config.INSTANCE.jdbc().sql().param().includeEmpty());
		Consoles.println("bytecode.generator.cache: {}", Config.INSTANCE.bytecode().generator().cache());
		Consoles.println("bytecode.generator.tmpdir: {}", Config.INSTANCE.bytecode().generator().tmpdir());
		Consoles.println("async.pool.core: {}", Config.INSTANCE.async().pool().core());
		Consoles.println("async.pool.max: {}", Config.INSTANCE.async().pool().max());
		Consoles.println("async.pool.scheduler: {}", Config.INSTANCE.async().pool().scheduler());
		Consoles.println("os.ipRegex: {}", Config.INSTANCE.os().ipRegex());

		Assertions.assertEquals(Level.DEBUG, Config.INSTANCE.logger().level("aaa"));
		Assertions.assertEquals(Level.DEBUG, Config.INSTANCE.logger().level("bbb"));
		Assertions.assertEquals(Level.DEBUG, Config.INSTANCE.logger().level("ccc"));
		Assertions.assertTrue(Config.INSTANCE.jdbc().sql().spaceAsLf());
		Assertions.assertTrue(Config.INSTANCE.jdbc().sql().param().emptyAsNull());
		Assertions.assertFalse(Config.INSTANCE.jdbc().sql().param().includeEmpty());
		Assertions.assertTrue(Config.INSTANCE.bytecode().generator().cache());
		Assertions.assertNull(Config.INSTANCE.bytecode().generator().tmpdir());
		Assertions.assertEquals(0, Config.INSTANCE.async().pool().core());
		Assertions.assertEquals(0, Config.INSTANCE.async().pool().max());
		Assertions.assertEquals(0, Config.INSTANCE.async().pool().scheduler());

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

		// 触发监听器
		GlobalStdEnvObservable.notifyListeners();

		Consoles.println(Jsons.serialize(GlobalStdEnv.bind("polaris", StdCfg.class)));
		Consoles.println(Jsons.serialize(StdCfg.instance()));
		Consoles.println("logger.level:  aaa: {}, bbb: {}, ccc: {}",
			Config.INSTANCE.logger().level("aaa")
			, Config.INSTANCE.logger().level("bbb")
			, Config.INSTANCE.logger().level("ccc"));
		Consoles.println("jdbc.sql.spaceAsLf: {}", Config.INSTANCE.jdbc().sql().spaceAsLf());
		Consoles.println("jdbc.sql.emptyAsNull: {}", Config.INSTANCE.jdbc().sql().param().emptyAsNull());
		Consoles.println("jdbc.sql.includeEmpty: {}", Config.INSTANCE.jdbc().sql().param().includeEmpty());
		Consoles.println("bytecode.generator.cache: {}", Config.INSTANCE.bytecode().generator().cache());
		Consoles.println("bytecode.generator.tmpdir: {}", Config.INSTANCE.bytecode().generator().tmpdir());
		Consoles.println("async.pool.core: {}", Config.INSTANCE.async().pool().core());
		Consoles.println("async.pool.max: {}", Config.INSTANCE.async().pool().max());
		Consoles.println("async.pool.scheduler: {}", Config.INSTANCE.async().pool().scheduler());
		Consoles.println("os.ipRegex: {}", Config.INSTANCE.os().ipRegex());

		Assertions.assertEquals(Level.INFO, Config.INSTANCE.logger().level("aaa"));
		Assertions.assertEquals(Level.ERROR, Config.INSTANCE.logger().level("bbb"));
		Assertions.assertEquals(Level.DEBUG, Config.INSTANCE.logger().level("ccc"));
		Assertions.assertFalse(Config.INSTANCE.jdbc().sql().spaceAsLf());
		Assertions.assertFalse(Config.INSTANCE.jdbc().sql().param().emptyAsNull());
		Assertions.assertTrue(Config.INSTANCE.jdbc().sql().param().includeEmpty());
		Assertions.assertFalse(Config.INSTANCE.bytecode().generator().cache());
		Assertions.assertEquals("/tmp/bytecode", Config.INSTANCE.bytecode().generator().tmpdir());
		Assertions.assertEquals(8, Config.INSTANCE.async().pool().core());
		Assertions.assertEquals(16, Config.INSTANCE.async().pool().max());
		Assertions.assertEquals(8, Config.INSTANCE.async().pool().scheduler());
	}
}

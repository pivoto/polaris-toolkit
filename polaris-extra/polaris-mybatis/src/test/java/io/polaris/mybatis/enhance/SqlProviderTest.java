package io.polaris.mybatis.enhance;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import io.polaris.core.reflect.Methods;
import io.polaris.mybatis.mapper.AnySqlSelectMapper;
import org.junit.jupiter.api.Test;

public class SqlProviderTest {

	@Test
	void test01() throws Throwable {
		EnhancedProviderContext context = EnhancedProviderContext.ofLazy(AnySqlSelectMapper.class, null, "");


		MethodHandle methodHandle = Methods.findVirtual(SqlProvider.class, "provideSql", String.class, new Class[]{Map.class, EnhancedProviderContext.class});
		methodHandle = methodHandle.bindTo(new SqlProvider01());


		Map<String, Object> map = new HashMap<>();
		String sql = (String) methodHandle.invokeExact(map, context);

		Consoles.println("sql: ", sql);
	}

	public static class SqlProvider01 implements SqlProvider {

		@Override
		public String provideSql(Map<String, Object> map, EnhancedProviderContext ctx) {
			return "select * from user where id = #{id}";
		}
	}

}

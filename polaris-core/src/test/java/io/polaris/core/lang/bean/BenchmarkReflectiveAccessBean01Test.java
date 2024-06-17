package io.polaris.core.lang.bean;

import io.polaris.core.TestConsole;
import io.polaris.core.asm.reflect.MethodAccess;
import io.polaris.core.asm.reflect.ClassAccess;
import io.polaris.core.function.Executable;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author Qt
 * @since  Aug 05, 2023
 */
@BenchmarkMode(Mode.All) // 吞吐量
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 结果所使用的时间单位
@State(Scope.Thread) // 每个测试线程分配一个实例
@Fork(2) // Fork进行的数目
@Warmup(iterations = 1) // 先预热1轮
@Measurement(iterations = 2) // 进行2轮测试
@Threads(128)
public class BenchmarkReflectiveAccessBean01Test {


	Consumer<Executable> executable = (r) -> {
		try {
			r.execute();
		} catch (Throwable e) {
			TestConsole.println(e.getClass().getName() + ": " + e.getMessage());
		}
	};

	@Test
	void MethodAccess() throws IOException {
		MethodAccess methodAccess = MethodAccess.get(Bean01.class);
		executable.accept(() -> TestConsole.println(methodAccess.getClass()));
		executable.accept(() -> TestConsole.println(methodAccess.getClass().getClassLoader()));
		Class<BeanMetadataV1> metadataClass = BeanMetadatasV1.getMetadataClass(Bean01.class);
		executable.accept(() -> TestConsole.println(metadataClass));
		executable.accept(() -> TestConsole.println(metadataClass.getClassLoader()));
	}

	@Test
	void testBeanMap() {
		ClassAccess<Bean01> access = ClassAccess.get(Bean01.class);
		Bean01 bean01 = access.newInstance(false);
		BeanMap<Bean01> map = Beans.newBeanMap(bean01);
		executable.accept(() -> map.put("intProperty", 1));
		executable.accept(() -> map.put("booleanProperty", 1));
		executable.accept(() -> map.put("exceptionProperty", 1));
		executable.accept(() -> map.get("intProperty"));
		executable.accept(() -> map.get("booleanProperty"));
		executable.accept(() -> map.get("exceptionProperty"));
		executable.accept(() -> map.put("idParent", 1));
		executable.accept(() -> map.put("id", 1));
		executable.accept(() -> map.put("booleanVal", 1));
		executable.accept(() -> map.put("booleanValProtected", 1));
		executable.accept(() -> map.put("booleanValPrivate", 1));
		executable.accept(() -> TestConsole.println(map));
	}

	@Test
	void testReflectiveAccess() {
		ClassAccess<Bean01> access = ClassAccess.get(Bean01.class);
		Bean01 bean01 = access.newInstance(false);
		executable.accept(() -> access.invokeMethod(bean01, "testStaticVoid"));
		executable.accept(() -> access.invokeMethod(bean01, "testStaticVoidWithException"));
		executable.accept(() -> access.invokeMethod(bean01, "testStaticVoidWithArgs", "123", 123));

		executable.accept(() -> access.invokeMethod(bean01, "testVoid"));
		executable.accept(() -> access.invokeMethod(bean01, "testVoidWithException"));
		executable.accept(() -> access.invokeMethod(bean01, "testVoidWithArgs", "123", 123));
		executable.accept(() -> access.invokeMethod(bean01, "testReturnObjectWithArgs", "123", 123));
		executable.accept(() -> access.invokeMethod(bean01, "testReturnPrimitiveWithArgs", "123", 123));

		executable.accept(() -> access.invokeMethod(bean01, "testProtectedVoid"));
		executable.accept(() -> access.invokeMethod(bean01, "testProtectedVoidWithException"));
		executable.accept(() -> access.invokeMethod(bean01, "testProtectedVoidWithArgs", "123", 123));
		executable.accept(() -> access.invokeMethod(bean01, "testProtectedReturnObjectWithArgs", "123", 123));
		executable.accept(() -> access.invokeMethod(bean01, "testProtectedReturnPrimitiveWithArgs", "123", 123));

		executable.accept(() -> access.invokeMethod(bean01, "testDefaultVoid"));
		executable.accept(() -> access.invokeMethod(bean01, "testDefaultVoidWithException"));
		executable.accept(() -> access.invokeMethod(bean01, "testDefaultVoidWithArgs", "123", 123));
		executable.accept(() -> access.invokeMethod(bean01, "testDefaultReturnObjectWithArgs", "123", 123));
		executable.accept(() -> access.invokeMethod(bean01, "testDefaultReturnPrimitiveWithArgs", "123", 123));

		executable.accept(() -> TestConsole.println("clone: " + access.invokeMethod(bean01, "clone")));
		executable.accept(() -> TestConsole.println("clone: " + bean01.clone()));

		executable.accept(() -> access.invokeMethod(bean01, "setId", "123"));
		executable.accept(() -> access.invokeMethod(bean01, "setIdParent", "123"));
		executable.accept(() -> access.invokeMethod(bean01, "setNameParent", "123"));
		executable.accept(() -> access.invokeMethod(bean01, "setNamePrivateParent", "123"));
		executable.accept(() -> access.invokeMethod(bean01, "setNameProtectedParent", "123"));
		executable.accept(() -> TestConsole.println("staticIdParent: " + access.getField(bean01, "staticIdParent")));
		executable.accept(() -> TestConsole.println("staticId: " + access.getField(bean01, "staticId")));
		executable.accept(() -> TestConsole.println("idParent: " + access.getField(bean01, "idParent")));
		executable.accept(() -> TestConsole.println("id: " + access.getField(bean01, "id")));
		executable.accept(() -> TestConsole.println("toString: " + access.invokeMethod(bean01, "toString")));
	}
}

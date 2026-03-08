package io.polaris.bytecode.bytebuddy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodCall;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author Qt
 * @since Nov 03, 2025
 */
@SuppressWarnings("resource")
public class TestByteBuddy {

	static String path;

	@BeforeAll
	static void beforeAll() {
		ByteBuddyAgent.install();
		String dir = TestByteBuddy.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		dir = dir.replaceFirst("/target/test-classes/?$", "");
		File file = new File(dir + "/target/bytebuddy-classes/");
		path = file.getAbsolutePath();
	}


	@Test
	void test_subclass(TestInfo testInfo) throws IOException {
		ByteBuddy byteBuddy = new ByteBuddy();
		Class<OriginClass> originClass = OriginClass.class;
		DynamicType.Builder<OriginClass> builder = byteBuddy.subclass(originClass);
		builder = builder.name(originClass.getName() + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test"));
		builder = builder.implement(GeneratedInterface.class);

		DynamicType.Unloaded<?> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<?> load = unloaded.load(getClass().getClassLoader());
		load.saveIn(new File(path));
		load.getLoaded();
	}

	@Test
	void test_makeInterface(TestInfo testInfo) throws IOException {
		ByteBuddy byteBuddy = new ByteBuddy();
		Class<OriginClass> originClass = OriginClass.class;
		DynamicType.Builder<?> builder = byteBuddy.makeInterface();
		builder = builder.name(originClass.getName() + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test"));
		builder = builder.implement(GeneratedInterface.class);
		builder = builder.defineMethod("method01", void.class, Visibility.PUBLIC).withoutCode();

		DynamicType.Unloaded<?> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<?> load = unloaded.load(getClass().getClassLoader());
		load.saveIn(new File(path));
		load.getLoaded();
	}


	@Test
	void test_defineConstructor(TestInfo testInfo) throws IOException, NoSuchMethodException {
		ByteBuddy byteBuddy = new ByteBuddy();
		Class<OriginClass> originClass = OriginClass.class;
		DynamicType.Builder<OriginClass> builder = byteBuddy.subclass(originClass);
		builder = builder.name(originClass.getName() + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test"));
		builder = builder.implement(GeneratedInterface.class);

		builder = builder.defineConstructor(Visibility.PUBLIC)
			.withParameters(Object[].class)
//			.intercept(StubMethod.INSTANCE)
//			.intercept(SuperMethodCall.INSTANCE)
			.intercept(MethodCall.invoke(originClass.getDeclaredConstructor(String.class)).with("test"))
		;

		DynamicType.Unloaded<?> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<?> load = unloaded.load(getClass().getClassLoader());
		load.saveIn(new File(path));
		load.getLoaded();
	}


}

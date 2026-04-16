package io.polaris.bytecode.bytebuddy.enhance;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;

import io.polaris.bytecode.bytebuddy.GeneratedInterface;
import io.polaris.bytecode.bytebuddy.TestByteBuddy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author Qt
 * @since Apr 15, 2026
 */
public class SampleClassTest {
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
	void test_rebase(TestInfo testInfo) throws Exception {
		ByteBuddy byteBuddy = new ByteBuddy();
		Class<SampleClass> originClass = SampleClass.class;
		DynamicType.Builder<SampleClass> builder = byteBuddy.rebase(originClass);
//		builder = builder.name(originClass.getName() + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test"));
//		builder = builder.implement(GeneratedInterface.class);
		builder = builder.method(ElementMatchers.named("testStaticMethod"))
			.intercept(MethodDelegation.to(SampleClassStaticInterceptor.class));

		DynamicType.Unloaded<?> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<?> load = unloaded.load(originClass.getClassLoader(),
			// 类重加载策略：运行时直接替换已加载的类
			ClassReloadingStrategy.fromInstalledAgent());
		Class<?> clazz = load.getLoaded();
		SampleClass.testStaticMethod();
	}

	@Test
	void test_redefine(TestInfo testInfo) throws Exception {
		ByteBuddy byteBuddy = new ByteBuddy();
		Class<SampleClass> originClass = SampleClass.class;
		DynamicType.Builder<SampleClass> builder = byteBuddy.redefine(originClass);
//		builder = builder.name(originClass.getName() + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test"));
//		builder = builder.implement(GeneratedInterface.class);
		builder = builder.method(ElementMatchers.named("testStaticMethod"))
			.intercept(MethodDelegation.to(SampleClassStaticInterceptor.class));

		DynamicType.Unloaded<?> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<?> load = unloaded.load(originClass.getClassLoader(),
			// 类重加载策略：运行时直接替换已加载的类
			ClassReloadingStrategy.fromInstalledAgent());
		Class<?> clazz = load.getLoaded();
		SampleClass.testStaticMethod();
	}


	@Test
	void test_subclass(TestInfo testInfo) throws Exception {
		ByteBuddy byteBuddy = new ByteBuddy();
		Class<SampleClass> originClass = SampleClass.class;
		DynamicType.Builder<SampleClass> builder = byteBuddy.subclass(originClass);
		builder = builder.name(originClass.getName() + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test"));
		builder = builder.implement(GeneratedInterface.class);
//		builder = builder.method(ElementMatchers.named("testStaticMethod"))
//			.intercept(MethodDelegation.to(SampleClassStaticInterceptor.class));
		builder = builder.method(ElementMatchers.named("testMethod"))
			.intercept(MethodDelegation.to(new SampleClassInterceptor()));

		DynamicType.Unloaded<SampleClass> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<SampleClass> load = unloaded.load(getClass().getClassLoader());
		Class<? extends SampleClass> clazz = load.getLoaded();
		SampleClass sample = clazz.newInstance();
		String result = sample.testMethod("test", 1, "test");
		System.out.println(sample);
		System.out.println(result);
	}

}

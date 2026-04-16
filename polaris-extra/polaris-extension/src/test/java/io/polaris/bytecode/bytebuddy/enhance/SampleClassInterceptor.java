package io.polaris.bytecode.bytebuddy.enhance;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * @author Qt
 * @since Apr 15, 2026
 */
public class SampleClassInterceptor {
	@RuntimeType  // 必须加
	public Object intercept(
		@This Object target,               // 目标对象
		@Origin Class<?> clazz,             // 目标类
		@Origin Method method,             // 目标方法
		@AllArguments Object[] args,       // 所有参数
		@Argument(0) String firstArg,      // 第0个参数
		@SuperCall Callable<Object> call,  // 调用原方法
		@Morph OverrideCallable overrideCallable  // 修改参数
//		@FieldValue("name") String name,   // 读取对象字段
	) throws Throwable {

		System.out.println("===== 拦截开始 =====");
		System.out.println("对象：" + target);
		System.out.println("类：" + clazz);
		System.out.println("方法：" + method.getName());
		System.out.println("参数：" + args[0]);
		System.out.println("第一个参数：" + firstArg);
//		System.out.println("对象字段name：" + name);

		// 执行原方法
		Object rs0 = call.call();
		System.out.println("原方法返回：" + rs0);

		// 修改参数
		args[0] = "xxx";
		System.out.println("修改参数：" + args[0]);
		Object result = overrideCallable.call(args);
		System.out.println("修改参数后返回：" + result);
		System.out.println("===== 拦截结束 =====");

		return result;
	}
}

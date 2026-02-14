package io.polaris.agent.apm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记任务类在跨线程处理时继承调用链路，用于标记在常用的Runnable、Callable等对象类型中
 *
 * @author Qt
 * @since Nov 28, 2025
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceTransmittable {

}

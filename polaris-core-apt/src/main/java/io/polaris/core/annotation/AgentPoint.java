package io.polaris.core.annotation;

import java.lang.annotation.*;

/**
 * 标记为会被字节码或探针工具切入的类或方法，表明已公开应用，发生变更时须慎重
 *
 * @author Qt
 * @since 1.8
 */
@Published("agent")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface AgentPoint {

	/** 备注说明 */
	String[] value() default "";

}

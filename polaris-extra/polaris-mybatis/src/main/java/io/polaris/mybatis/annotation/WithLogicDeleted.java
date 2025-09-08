package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明此注解后，表示将检测目标实体的逻辑删除字段，并在查询或删除时，自动添加逻辑删除条件。
 *
 * @author Qt
 * @since Sep 08, 2025
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithLogicDeleted {

	/**
	 * @return 是否尽可能使用逻辑删除，默认为true
	 */
	boolean value() default true;
}

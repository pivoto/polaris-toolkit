package io.polaris.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否通过Count(*)查询存在性，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
 *
 * @author Qt
 * @since Apr 16, 2026
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsByCount {

	/**
	 * @return 使用Count语句查询存在性，默认为true
	 */
	boolean value() default true;

}

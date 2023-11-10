package io.polaris.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8,  Aug 24, 2023
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityMapperDeclared {

	Class<?> entity();


}

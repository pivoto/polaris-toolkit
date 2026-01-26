package io.polaris.mybatis.auth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.Relation;

/**
 * @author Qt
 * @since Jan 25, 2026
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataScope {

	String[] include() default {};

	String[] exclude() default {};

	Relation relation() default Relation.OR;

}

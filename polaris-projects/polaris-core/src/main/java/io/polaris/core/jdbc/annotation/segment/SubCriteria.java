package io.polaris.core.jdbc.annotation.segment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.polaris.core.jdbc.sql.consts.Relation;

/**
 * @author Qt
 * @since 1.8,  Jan 28, 2024
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Documented
@Inherited
public @interface SubCriteria {

	SubCriterion[] value() default {};

	JoinCriterion[] join() default {};

	Relation relation() default Relation.AND;

	SubCriteria1[] subset() default {};
}

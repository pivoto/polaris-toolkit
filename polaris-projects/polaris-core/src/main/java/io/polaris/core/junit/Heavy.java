package io.polaris.core.junit;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.*;

/**
 * @author Qt
 * @since 1.8,  Aug 31, 2023
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Tag(UnitTestTags.heavy)
public @interface Heavy {
}

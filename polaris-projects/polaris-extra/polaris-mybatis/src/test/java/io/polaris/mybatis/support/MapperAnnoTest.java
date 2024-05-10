package io.polaris.mybatis.support;

import io.polaris.core.reflect.Reflects;
import io.polaris.mybatis.provider.SqlSelectProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Qt
 * @since  Sep 11, 2023
 */
public class MapperAnnoTest {

	@Test
	void test01() throws NoSuchMethodException {
		SelectProvider[] annos = Method01.class.getMethod("select").getAnnotationsByType(SelectProvider.class);
		System.out.println(Arrays.toString(annos));
	}

	@Test
	void test02() throws ReflectiveOperationException {
		Configuration configuration = new Configuration();
		System.out.println(configuration.getMapperRegistry());
		Reflects.setFieldValue(configuration, "mapperRegistry",new MapperRegistry(configuration));
		System.out.println(configuration.getMapperRegistry());
	}

	public interface Method01 {

		@Anno01
		Map<String, Object> select();
	}

	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Anno01 {

		@SelectProvider(value = SqlSelectProvider.class)
		SelectProvider[] value() default {@SelectProvider(value = SqlSelectProvider.class)};
	}
}

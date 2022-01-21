package io.polaris.toolkit.spring.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * @author Qt
 * @version Jan 05, 2022
 * @since 1.8
 */
@Getter
@Setter
public class TypePatternClassPointcut extends StaticMethodMatcherPointcut {
	private final String classPattern;

	public TypePatternClassPointcut(String classPattern) {
		this.classPattern = classPattern;
		this.setClassFilter(new TypePatternClassFilter(classPattern));
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return true;
	}


}

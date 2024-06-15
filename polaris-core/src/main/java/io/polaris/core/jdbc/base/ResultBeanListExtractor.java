package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanListExtractor<T> extends ResultBeanCollectionExtractor<List<T>, T> {

	public ResultBeanListExtractor(Class<T> beanType) {
		this(beanType, true, true);
	}

	public ResultBeanListExtractor(Class<T> beanType, boolean caseInsensitive, boolean caseCamel) {
		super(ArrayList::new, beanType, caseInsensitive, caseCamel);
	}
	public ResultBeanListExtractor(Type beanType) {
		this(beanType, true, true);
	}

	public ResultBeanListExtractor(Type beanType, boolean caseInsensitive, boolean caseCamel) {
		super(ArrayList::new, beanType, caseInsensitive, caseCamel);
	}
}

package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.polaris.core.lang.bean.CaseModeOption;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanListExtractor<T> extends ResultBeanCollectionExtractor<List<T>, T> {

	public ResultBeanListExtractor(Class<T> beanType) {
		this(beanType, CaseModeOption.all());
	}

	public ResultBeanListExtractor(Class<T> beanType, CaseModeOption caseMode) {
		super(ArrayList::new, beanType, caseMode);
	}

	public ResultBeanListExtractor(Type beanType) {
		this(beanType, CaseModeOption.all());
	}

	public ResultBeanListExtractor(Type beanType, CaseModeOption caseMode) {
		super(ArrayList::new, beanType, caseMode);
	}

}

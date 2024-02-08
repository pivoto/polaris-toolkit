package io.polaris.core.jdbc.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanMappingListExtractor<T> extends ResultBeanMappingCollectionExtractor<List<T>,T> {

	public ResultBeanMappingListExtractor(BeanMapping<T> mapping) {
		super(ArrayList::new, mapping);
	}

}

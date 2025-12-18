package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 */
public class ResultSingleListExtractor<T> extends ResultSingleCollectionExtractor<List<T>, T> {

	public ResultSingleListExtractor() {
		super(ArrayList::new);
	}

	public ResultSingleListExtractor(Type type) {
		super(ArrayList::new, type);
	}

}

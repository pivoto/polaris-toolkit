package io.polaris.core.lang.bean.property;

import io.polaris.core.lang.bean.Beans;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Qt
 * @since  Dec 28, 2023
 */
public class ListPropertyBuilder<T> extends AbstractPropertyBuilder<List<T>> implements PropertyBuilder<List<T>> {
	private List<T> list;
	private Class<T> clazz;

	public ListPropertyBuilder(List<T> list, Class<T> clazz) {
		this(list, clazz, 0);
	}

	public ListPropertyBuilder(List<T> list, Class<T> clazz, int size) {
		if (list == null) {
			this.list = new ArrayList<>();
		} else {
			this.list = list;
		}
		if (clazz == null) {
			//this.clazz = (Class<T>) LinkedHashMap.class;
			throw new IllegalArgumentException("class is null");
		} else {
			this.clazz = clazz;
		}
		for (int i = this.list.size(); i < size; i++) {
			this.list.add(newOne());
		}
	}

	private T newOne() {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("class cannot be initialized", e);
		}
	}

	private T getIndexObj(int i) {
		T one;
		if (list.size() <= i) {
			one = newOne();
			list.add(one);
		} else {
			one = list.get(i);
		}
		return one;
	}

	@SuppressWarnings({"rawtypes"})
	@Override
	public void exec(Operation operation) {
		Object orig = operation.orig;
		if (orig != null) {
			if (orig instanceof Collection) {
				int i = 0;
				for (Object o : (Collection) orig) {
					Object val = Beans.getPathProperty(o, operation.origProperty);
					if (val != null || !operation.ignoredNull) {
						Object one = getIndexObj(i);
						Beans.setPathProperty(one, operation.destProperty, val);
					}
					i++;
				}
			} else if (orig.getClass().isArray()) {
				int len = Array.getLength(orig);
				for (int i = 0; i < len; i++) {
					Object o = Array.get(orig, i);
					Object val = Beans.getPathProperty(o, operation.origProperty);
					if (val != null || !operation.ignoredNull) {
						Object one = getIndexObj(i);
						Beans.setPathProperty(one, operation.destProperty, val);
					}
				}
			} else {
				for (Object one : list) {
					Object val = Beans.getPathProperty(orig, operation.origProperty);
					if (val != null || !operation.ignoredNull) {
						Beans.setPathProperty(one, operation.destProperty, val);
					}
				}
			}
		} else {
			for (Object one : list) {
				if (operation.propertyValue != null || !operation.ignoredNull) {
					Beans.setPathProperty(one, operation.destProperty, operation.propertyValue);
				}
			}
		}
	}

	@Override
	public List<T> done() {
		exec();
		return list;
	}

}

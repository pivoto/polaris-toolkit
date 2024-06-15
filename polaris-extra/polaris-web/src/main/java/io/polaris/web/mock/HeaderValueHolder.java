package io.polaris.web.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.polaris.core.collection.ObjectArrays;

class HeaderValueHolder {

	private final List<Object> values = new LinkedList<Object>();

	public static HeaderValueHolder getByName(Map<String, HeaderValueHolder> headers, String name) {
		for (String headerName : headers.keySet()) {
			if (headerName.equalsIgnoreCase(name)) {
				return headers.get(headerName);
			}
		}
		return null;
	}

	public void addValue(Object value) {
		this.values.add(value);
	}

	public void addValues(Collection<?> values) {
		this.values.addAll(values);
	}

	public void addValueArray(Object values) {
		this.values.addAll(ObjectArrays.toList(values));
	}

	public List<Object> getValues() {
		return Collections.unmodifiableList(this.values);
	}

	public List<String> getStringValues() {
		List<String> stringList = new ArrayList<String>(this.values.size());
		for (Object value : this.values) {
			stringList.add(value.toString());
		}
		return Collections.unmodifiableList(stringList);
	}

	public Object getValue() {
		return (!this.values.isEmpty() ? this.values.get(0) : null);
	}

	public void setValue(Object value) {
		this.values.clear();
		this.values.add(value);
	}

	public String getStringValue() {
		return (!this.values.isEmpty() ? this.values.get(0).toString() : null);
	}

}

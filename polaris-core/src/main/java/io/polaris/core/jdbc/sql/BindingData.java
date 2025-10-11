package io.polaris.core.jdbc.sql;

import java.util.HashMap;
import java.util.Map;

public class BindingData {
	private final Map<String, Object> bindings;
	private final Map<String, Object> extra;

	public BindingData(Map<String, Object> bindings, boolean mutable) {
		this.bindings = bindings;
		if (!mutable) {
			extra = new HashMap<>();
		} else {
			extra = null;
		}
	}

	public boolean isMutable() {
		return extra == null;
	}

	public Map<String, Object> getBindings() {
		return bindings;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}


	public boolean put(String key, Object value) {
		if (extra == null) {
			bindings.put(key, value);
			return true;
		} else {
			extra.put(key, value);
			return false;
		}
	}

	public Object get(String key) {
		if (extra != null) {
			Object val = extra.get(key);
			if (val != null) {
				return val;
			}
		}
		return bindings.get(key);
	}


}

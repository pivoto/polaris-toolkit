package io.polaris.core.jdbc.sql.node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import io.polaris.core.jdbc.sql.VarRef;

/**
 * @author Qt
 * @since Aug 11, 2023
 */
public abstract class VarNode implements SqlNode {
	/** 绑定变量名 */
	protected final String varName;
	/** 绑定变量值 */
	protected Object varValue;
	protected List<Object> varValues;

	public VarNode(String varName) {
		this.varName = varName;
	}

	@Override
	public String toString() {
		return asPreparedSql().getText();
	}

	@Override
	public boolean isVarNode() {
		return true;
	}

	@Override
	public String getVarName() {
		return varName;
	}

	@Override
	public void removeVarValue() {
		varValue = null;
		varValues = null;
	}

	@Override
	public Object getVarValue() {
		return varValue;
	}

	protected List<Object> getVarValues() {
		return varValues;
	}

	@Override
	public void bindVarValue(Object param) {
		this.varValue = param;
		this.varValues = new ArrayList<>();
		if (param instanceof VarRef) {
			String props = ((VarRef<?>) param).getProps();
			Object value = ((VarRef<?>) param).getValue();
			addVarValuesToList(value, this.varValues, props);
		} else {
			addVarValuesToList(param, this.varValues, null);
		}
	}


	private void addVarValuesToList(Object varValue, List<Object> list, String varProps) {
		if (varValue == null) {
			list.add(null);
			return;
		}
		if (varValue instanceof List && varValue instanceof RandomAccess) {
			int size = ((List<?>) varValue).size();
			for (int i = 0; i < size; i++) {
				Object o = ((List<?>) varValue).get(i);
				addVarValuesToList(o, list, varProps);
			}
			return;
		}
		if (varValue instanceof Iterable) {
			for (Object o : ((Iterable<?>) varValue)) {
				addVarValuesToList(o, list, varProps);
			}
			return;
		}
		if (varValue instanceof Iterator) {
			((Iterator<?>) varValue).forEachRemaining(parameter1 -> addVarValuesToList(parameter1, list, varProps));
			return;
		}
		if (varValue instanceof Map) {
			Collection<?> values = ((Map<?, ?>) varValue).values();
			values.forEach(parameter1 -> addVarValuesToList(parameter1, list, varProps));
			return;
		}
		if (varValue.getClass().isArray()) {
			int len = Array.getLength(varValue);
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					addVarValuesToList(Array.get(varValue, i), list, varProps);
				}
			}
			return;
		}
		if (varProps != null) {
			// 绑定变量附加属性
			list.add(VarRef.of(varValue, varProps));
			return;
		}
		list.add(varValue);
	}


}

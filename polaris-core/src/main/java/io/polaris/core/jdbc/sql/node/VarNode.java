package io.polaris.core.jdbc.sql.node;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
public abstract class VarNode implements SqlNode {
	/** 绑定变量名 */
	protected final String varName;
	/** 绑定变量值 */
	protected Object varValue;
	protected List<Object> varValues;
	/** 绑定变量附加属性，用于生成BoundSql占位符 */
	protected String varProperty;

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
	public String getVarProperty() {
		return varProperty;
	}

	@Override
	public void setVarProperty(String varProperty) {
		this.varProperty = varProperty;
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
		addVarValuesToList(param, this.varValues);
	}


	private void addVarValuesToList(Object varValue, List<Object> list) {
		if (varValue == null) {
			list.add(null);
			return;
		}
		if (varValue instanceof List && varValue instanceof RandomAccess) {
			int size = ((List<?>) varValue).size();
			for (int i = 0; i < size; i++) {
				Object o = ((List<?>) varValue).get(i);
				addVarValuesToList(o, list);
			}
			return;
		}
		if (varValue instanceof Iterable) {
			for (Object o : ((Iterable<?>) varValue)) {
				addVarValuesToList(o, list);
			}
			return;
		}
		if (varValue instanceof Iterator) {
			((Iterator<?>) varValue).forEachRemaining(parameter1 -> addVarValuesToList(parameter1, list));
			return;
		}
		if (varValue instanceof Map) {
			Collection<?> values = ((Map<?, ?>) varValue).values();
			values.forEach(parameter1 -> addVarValuesToList(parameter1, list));
			return;
		}
		if (varValue.getClass().isArray()) {
			int len = Array.getLength(varValue);
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					addVarValuesToList(Array.get(varValue, i), list);
				}
			}
			return;
		}
		list.add(varValue);
	}


}

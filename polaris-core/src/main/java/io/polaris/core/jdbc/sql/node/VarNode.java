package io.polaris.core.jdbc.sql.node;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import io.polaris.core.consts.SymbolConsts;
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
		// varName 不能包含逗号，逗号后面视为扩展属性串
		int i = varName.indexOf(SymbolConsts.COMMA);
		if (i >= 0) {
			this.varName = varName.substring(0, i);
		} else {
			this.varName = varName;
		}
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
			addVarValuesToList(((VarRef<?>) param).getValue(), this.varValues, ((VarRef<?>) param));
		} else {
			addVarValuesToList(param, this.varValues, null);
		}
	}


	private void addVarValuesToList(Object varValue, List<Object> list, VarRef<?> origVar) {
		if (varValue == null) {
			list.add(null);
			return;
		}
		if (varValue instanceof List && varValue instanceof RandomAccess) {
			int size = ((List<?>) varValue).size();
			for (int i = 0; i < size; i++) {
				Object o = ((List<?>) varValue).get(i);
				addVarValuesToList(o, list, origVar);
			}
			return;
		}
		if (varValue instanceof Iterable) {
			for (Object o : ((Iterable<?>) varValue)) {
				addVarValuesToList(o, list, origVar);
			}
			return;
		}
		if (varValue instanceof Iterator) {
			((Iterator<?>) varValue).forEachRemaining(parameter1 -> addVarValuesToList(parameter1, list, origVar));
			return;
		}
		if (varValue instanceof Map) {
			Collection<?> values = ((Map<?, ?>) varValue).values();
			values.forEach(parameter1 -> addVarValuesToList(parameter1, list, origVar));
			return;
		}
		if (varValue.getClass().isArray()) {
			int len = Array.getLength(varValue);
			if (len > 0) {
				for (int i = 0; i < len; i++) {
					addVarValuesToList(Array.get(varValue, i), list, origVar);
				}
			}
			return;
		}
		if (origVar != null) {
			// 绑定变量附加属性
			if (origVar.getValue() == varValue) {
				list.add(origVar);
			} else {
				list.add(VarRef.of(varValue, origVar.getProps()));
			}
			return;
		}
		list.add(varValue);
	}


}

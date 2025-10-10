package io.polaris.core.jdbc.sql.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.jdbc.sql.VarRef;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.PreparedSql;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Aug 11, 2023
 */
public class DynamicNode extends VarNode implements Cloneable {

	public DynamicNode(String varName) {
		super(varName);
	}

	@Override
	public PreparedSql asPreparedSql() {
		StringBuilder text = new StringBuilder();
		List<Object> list = new ArrayList<>();
		List<Object> parameters = getVarValues();
		if (parameters == null || parameters.isEmpty()) {
			text.append(SymbolConsts.QUESTION_MARK);
			list.add(null);
		} else {
			for (Object parameter : parameters) {
				if (text.length() > 0) {
					text.append(SymbolConsts.COMMA);
				}
				text.append(SymbolConsts.QUESTION_MARK);
				list.add(parameter);
			}
		}
		return new PreparedSql(text.toString(), list);
	}

	@Override
	public BoundSql asBoundSql(VarNameGenerator generator, String openVarToken, String closeVarToken) {
		List<Object> parameters = getVarValues();
		if (parameters == null || parameters.isEmpty()) {
			return new BoundSql(SqlNodes.NULL.getText(), Collections.emptyMap());
		} else {
			StringBuilder text = new StringBuilder();
			Map<String, Object> map = new LinkedHashMap<>();
			for (Object parameter : parameters) {
				if (text.length() > 0) {
					text.append(SymbolConsts.COMMA);
				}
				if (parameter == null) {
					text.append(SqlNodes.NULL.getText());
				} else {
					String key = generator.generate();
					text.append(openVarToken).append(key);
					if (parameter instanceof VarRef) {
						// 绑定变量附加属性，为如Mybatis等占位符增加配置项
						String varProperty = ((VarRef<?>) parameter).getProps();
						if (Strings.isNotBlank(varProperty)){
							text.append(SymbolConsts.COMMA).append(varProperty);
						}
						map.put(key, ((VarRef<?>) parameter).getValue());
					}else{
						map.put(key, parameter);
					}
					text.append(closeVarToken);
				}
			}
			return new BoundSql(text.toString(), map);
		}
	}

	@Override
	public boolean isDynamicNode() {
		return true;
	}

	@Override
	public DynamicNode copy() {
		return copy(true);
	}

	@Override
	public DynamicNode copy(boolean withVarValue) {
		DynamicNode clone = new DynamicNode(this.varName);
		if (withVarValue && this.varValues != null) {
			clone.varValue = this.varValue;
			clone.varValues = new ArrayList<>(this.varValues);
		}
		return clone;
	}

	@Override
	public DynamicNode clone() {
		return copy(true);
	}

}

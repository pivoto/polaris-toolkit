package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(Character[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class CharacterObjectArrayTypeHandler extends StringTokenizerTypeHandler<Character> {
	public CharacterObjectArrayTypeHandler() {
		super(Character.class);
	}

	@Override
	Character parseString(String value) {
		return value.charAt(0);
	}
}

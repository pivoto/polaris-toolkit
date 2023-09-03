package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@MappedTypes(Character[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class CharacterArrayTypeHandler extends StringTokenizerTypeHandler<Character> {
	public CharacterArrayTypeHandler() {
		super(Character.class);
	}

	@Override
	Character parseString(String value) {
		return value.charAt(0);
	}
}

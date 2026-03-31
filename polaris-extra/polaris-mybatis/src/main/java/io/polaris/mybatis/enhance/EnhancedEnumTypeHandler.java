package io.polaris.mybatis.enhance;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

import io.polaris.core.converter.Converters;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.consts.DbEnum;
import io.polaris.mybatis.type.AbstractTypeHandler;
import io.polaris.mybatis.type.BlankableEnumOrdinalTypeHandler;
import io.polaris.mybatis.type.BlankableEnumTypeHandler;
import io.polaris.mybatis.type.DynamicEnumOrdinalTypeHandler;
import io.polaris.mybatis.type.DynamicEnumTypeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class EnhancedEnumTypeHandler<E extends Enum<E>> implements TypeHandler<E> {

	private static Class<? extends TypeHandler> defaultEnumTypeHandler = DynamicEnumTypeHandler.class;
	private final Class<E> type;
	private final E[] enums;
	private final TypeHandler<E> delegate;

	public static void setDefaultEnumTypeHandler(Class<? extends TypeHandler> enumTypeHandler) {
		if (enumTypeHandler != null && !EnhancedEnumTypeHandler.DbEnumTypeHandler.class.isAssignableFrom(enumTypeHandler)) {
			EnhancedEnumTypeHandler.defaultEnumTypeHandler = enumTypeHandler;
		}
	}

	public EnhancedEnumTypeHandler(Class<E> type) {
		this.type = type;
		this.enums = type.getEnumConstants();
		if (DbEnum.class.isAssignableFrom(type)) {
			this.delegate = new DbEnumTypeHandler();
		} else {
			if (DynamicEnumTypeHandler.class.equals(defaultEnumTypeHandler)) {
				this.delegate = new DynamicEnumTypeHandler<>(type);
			} else if (DynamicEnumOrdinalTypeHandler.class.equals(defaultEnumTypeHandler)) {
				this.delegate = new DynamicEnumOrdinalTypeHandler<>(type);
			} else if (EnumTypeHandler.class.equals(defaultEnumTypeHandler)) {
				this.delegate = new EnumTypeHandler<>(type);
			} else if (EnumOrdinalTypeHandler.class.equals(defaultEnumTypeHandler)) {
				this.delegate = new EnumOrdinalTypeHandler<>(type);
			} else if (BlankableEnumTypeHandler.class.equals(defaultEnumTypeHandler)) {
				this.delegate = new BlankableEnumTypeHandler<>(type);
			} else if (BlankableEnumOrdinalTypeHandler.class.equals(defaultEnumTypeHandler)) {
				this.delegate = new BlankableEnumOrdinalTypeHandler<>(type);
			} else {
				this.delegate = getInstance(type, defaultEnumTypeHandler);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
		if (javaTypeClass != null) {
			try {
				Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
				return (TypeHandler<T>) c.newInstance(javaTypeClass);
			} catch (NoSuchMethodException ignored) {
				// ignored
			} catch (Exception e) {
				throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
			}
		}
		try {
			Constructor<?> c = typeHandlerClass.getConstructor();
			return (TypeHandler<T>) c.newInstance();
		} catch (Exception e) {
			throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
		}
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		delegate.setParameter(ps, i, parameter, jdbcType);
	}

	@Override
	public E getResult(ResultSet rs, String columnName) throws SQLException {
		return delegate.getResult(rs, columnName);
	}

	@Override
	public E getResult(ResultSet rs, int columnIndex) throws SQLException {
		return delegate.getResult(rs, columnIndex);
	}

	@Override
	public E getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return delegate.getResult(cs, columnIndex);
	}

	private class DbEnumTypeHandler extends AbstractTypeHandler<E> {

		@Override
		public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
			Object code = null;
			if (parameter != null) {
				code = ((DbEnum<?>) parameter).getCode();
			}
			if (code == null) {
				if (jdbcType == null) {
					ps.setNull(i, Types.VARCHAR);
				} else {
					ps.setNull(i, jdbcType.TYPE_CODE);
				}
			} else {
				if (jdbcType == null) {
					ps.setObject(i, code);
				} else {
					ps.setObject(i, code, jdbcType.TYPE_CODE);
				}
			}
		}

		private E toEnum(Object s) {
			try {
				if (s instanceof String) {
					s = Strings.trimToNull((String) s);
				}
				if (s == null) {
					return null;
				}
				for (E e : enums) {
					Serializable code = ((DbEnum<?>) e).getCode();
					if (code.equals(s)) {
						return e;
					}
					Object o = Converters.convertQuietly(code.getClass(), s);
					if (code.equals(o)) {
						return e;
					}
				}
				return null;
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				return null;
			}
		}

		@Override
		public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
			Object s = rs.getObject(columnName);
			return toEnum(s);
		}

		@Override
		public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
			Object s = rs.getObject(columnIndex);
			return toEnum(s);
		}

		@Override
		public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
			Object s = cs.getObject(columnIndex);
			return toEnum(s);
		}
	}
}

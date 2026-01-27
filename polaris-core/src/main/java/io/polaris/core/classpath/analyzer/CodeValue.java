package io.polaris.core.classpath.analyzer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.annotation.concurrent.NotThreadSafe;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NotThreadSafe
public class CodeValue {

	/**
	 * <ul>
	 * <li>基元类型：
	 * {@link Opcodes#TOP},
	 * {@link Opcodes#INTEGER},
	 * {@link Opcodes#FLOAT},
	 * {@link Opcodes#LONG},
	 * {@link Opcodes#DOUBLE},
	 * {@link Opcodes#NULL},
	 * {@link Opcodes#UNINITIALIZED_THIS}
	 * (long 和 double 由两个元素表示，第二个元素是 TOP).
	 * </li>
	 * <li>
	 * 引用类型: 由 String 对象表示（internal names, see {@link
	 * Type#getInternalName()})
	 * </li>
	 * <li>
	 * 未初始化的类型：由 Label 对象表示，表示 NEW 指令创建的未初始化值的类对象引用
	 * </li>
	 * </ul>
	 */
	private Object type;

	private boolean ldc;
	private Object ldcValue;

	private boolean getStatic;
	private String getStaticOwner;
	private String getStaticName;

	private boolean constant;
	/** byte/short/int/long/float/double */
	private Object constantValue;

	public CodeValue(Object type) {
		if (type instanceof String) {
			this.type = (String) type;
		} else if (type instanceof Label) {
			this.type = (Label) type;
		} else {
			this.type = (Integer) type;
		}
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();
		CodeValue val = this;
		if (val.isReference()) {
			sb.append(val.getType());
		} else if (val.isUninitialized()) {
			sb.append("Uninitialized:").append(val.getType());
		} else if (val.isPrimitive()) {
			if (Opcodes.TOP.equals(val.getType())) {
				sb.append("TOP");
			} else if (Opcodes.INTEGER.equals(val.getType())) {
				sb.append("INTEGER");
			} else if (Opcodes.LONG.equals(val.getType())) {
				sb.append("LONG");
			} else if (Opcodes.FLOAT.equals(val.getType())) {
				sb.append("FLOAT");
			} else if (Opcodes.DOUBLE.equals(val.getType())) {
				sb.append("DOUBLE");
			} else if (Opcodes.NULL.equals(val.getType())) {
				sb.append("NULL");
			} else if (Opcodes.UNINITIALIZED_THIS.equals(val.getType())) {
				sb.append("UNINITIALIZED_THIS");
			} else {
				sb.append("UNKNOWN:").append(val.getType());
			}
		}
		if (val.isLdc()) {
			sb.append("(LDC:").append(val.getLdcValue()).append(")");
		} else if (val.isGetStatic()) {
			sb.append("(GETSTATIC:").append(val.getGetStaticOwner()).append(".").append(val.getGetStaticName()).append(")");
		} else if (val.isConstant()) {
			sb.append("(CONSTANT:").append(val.getConstantValue()).append(")");
		}
		return sb.toString();
	}

	public boolean isReference() {
		return type instanceof String;
	}

	public boolean isUninitialized() {
		return type instanceof Label;
	}

	public boolean isPrimitive() {
		return type instanceof Integer;
	}

	public boolean isTop() {
		return Opcodes.TOP.equals(type);
	}

	public boolean isLongOrDouble() {
		return Opcodes.LONG.equals(type) || Opcodes.DOUBLE.equals(type);
	}

	public CodeValue clearValue() {
		this.ldc = false;
		this.ldcValue = null;
		this.getStatic = false;
		this.getStaticOwner = null;
		this.getStaticName = null;
		this.constant = false;
		this.constantValue = null;
		return this;
	}

	public CodeValue copyFrom(CodeValue value) {
		if (value.isLdc()) {
			this.withLdc(value.getLdcValue());
		} else if (value.isGetStatic()) {
			this.withGetStatic(value.getGetStaticOwner(), value.getGetStaticName());
		} else if (value.isConstant()) {
			this.withConstant(value.getConstantValue());
		}
		return this;
	}

	public CodeValue withConstant(Object value) {
		clearValue();
		this.constant = true;
		this.constantValue = value;
		return this;
	}

	public CodeValue withLdc(Object value) {
		clearValue();
		this.ldc = true;
		this.ldcValue = value;
		return this;
	}

	public CodeValue withGetStatic(String owner, String name) {
		clearValue();
		this.getStatic = true;
		this.getStaticOwner = owner;
		this.getStaticName = name;
		return this;
	}


	public String detectStringValue(ClassLoader classLoader) throws ClassNotFoundException, IllegalAccessException {
		Object val = detectValue(classLoader);
		if (val instanceof String) {
			return (String) val;
		}
		return null;
	}

	public Object detectValue(ClassLoader classLoader) throws ClassNotFoundException, IllegalAccessException {
		if (this.ldc) {
			if (this.ldcValue instanceof String) {
				return this.ldcValue;
			}
		} else if (this.constant) {
			if (this.constantValue instanceof String) {
				return this.constantValue;
			}
		} else if (this.getStatic) {
			Class<?> clazz = classLoader.loadClass(getStaticOwner.replace('/', '.'));
			Field field = getStaticField(clazz, getStaticName);
			assert field != null;
			field.setAccessible(true);
			Object val = field.get(null);
			return val;
		}
		return null;
	}

	static Field getStaticField(Class<?> clazz, String name) {
		Class<?> searchType = clazz;
		while (searchType != null) {
			{
				Field[] declaredFields = searchType.getDeclaredFields();
				for (Field field : declaredFields) {
					if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
						return field;
					}
				}
			}
			Class<?>[] interfaces = searchType.getInterfaces();
			for (Class<?> anInterface : interfaces) {
				Field field = getStaticField(anInterface, name);
				if (field != null) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}

		return null;
	}
}

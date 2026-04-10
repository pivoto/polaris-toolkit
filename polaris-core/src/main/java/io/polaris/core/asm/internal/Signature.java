package io.polaris.core.asm.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;

import io.polaris.core.reflect.Reflects;
import lombok.Getter;
import org.objectweb.asm.Type;

/**
 * 方法签名类，封装方法名和描述符
 * <p>
 * 用于唯一标识一个方法的签名信息
 *
 * @author Qt
 * @since May 09, 2024
 */
public class Signature {
	/** 方法名 */
	@Getter
	private final String name;
	/** 方法描述符 */
	@Getter
	private final String descriptor;

	private transient Type returnType;
	private transient Type[] argumentTypes;
	private transient String returnClassName;
	private transient String[] argumentTypeNames;
	private transient Class<?> returnClass;
	private transient Class<?>[] argumentClasses;

	public static Signature of(String name, Class<?> returnClass) {
		return of(name, returnClass, new Type[0]);
	}

	public static Signature of(String name, Class<?> returnClass, Type... argumentTypes) {
		Type type = Type.getType(returnClass);
		Signature signature = new Signature(name, Type.getMethodDescriptor(type, argumentTypes));
		signature.returnType = type;
		signature.returnClass = returnClass;
		signature.argumentTypes = argumentTypes;
		return signature;
	}

	public static Signature of(String name, Class<?> returnClass, Class<?>... argumentClasses) {
		Type type = Type.getType(returnClass);
		Type[] types = AsmTypes.getTypes(argumentClasses);
		Signature signature = new Signature(name, Type.getMethodDescriptor(type, types));
		signature.returnType = type;
		signature.argumentTypes = types;
		signature.returnClass = returnClass;
		signature.argumentClasses = argumentClasses;
		return signature;
	}

	public static Signature of(String name, String descriptor) {
		return new Signature(name, descriptor);
	}

	public static Signature of(Method member) {
		Signature signature = new Signature(member.getName(), Type.getMethodDescriptor(member));
		signature.returnType = Type.getReturnType(member);
		signature.argumentTypes = AsmTypes.getTypes(member.getParameterTypes());
		signature.returnClass = member.getReturnType();
		signature.argumentClasses = member.getParameterTypes();
		return signature;
	}

	public static Signature of(Constructor<?> member) {
		Type[] types = AsmTypes.getTypes(member.getParameterTypes());
		Signature signature = new Signature(AsmConsts.CONSTRUCTOR_NAME,
			Type.getMethodDescriptor(Type.VOID_TYPE, types));
		signature.returnType = Type.VOID_TYPE;
		signature.argumentTypes = types;
		signature.returnClass = void.class;
		signature.argumentClasses = member.getParameterTypes();
		return signature;
	}

	/**
	 * 解析方法签名字符串为 Signature 对象
	 * <p>
	 * 签名字符串格式："返回类型 方法名(参数类型1, 参数类型2, ...)"
	 * 例如："void init(String, int)" 或 "java.lang.String toString()"
	 * </p>
	 *
	 * @param methodStr 方法签名字符串
	 * @return 解析后的 Signature 对象
	 */
	public static Signature parseSignature(String methodStr) {
		return AsmTypes.parseSignature(methodStr);
	}


	/**
	 * 构造方法签名
	 *
	 * @param name 方法名称
	 * @param descriptor 方法描述符（JVM 格式）
	 */
	public Signature(String name, String descriptor) {
		this.name = name;
		this.descriptor = descriptor;
	}

	/**
	 * 构造方法签名（通过返回参数类型）
	 *
	 * @param name 方法名
	 * @param returnType 返回值类型
	 * @param argumentTypes 参数类型数组
	 */
	public Signature(String name, Type returnType, Type[] argumentTypes) {
		this(name, Type.getMethodDescriptor(returnType, argumentTypes));
	}

	/**
	 * 获取返回值类型
	 *
	 * @return ASM Type 对象，表示返回值类型
	 */
	public Type getReturnType() {
		if (returnType == null) {
			returnType = Type.getReturnType(descriptor);
		}
		return returnType;
	}

	/**
	 * 获取参数类型数组
	 *
	 * @return ASM Type 数组，表示所有参数类型
	 */
	public Type[] getArgumentTypes() {
		if (argumentTypes == null) {
			argumentTypes = Type.getArgumentTypes(descriptor);
		}
		return argumentTypes;
	}


	public String getReturnClassName() {
		if (returnClassName == null) {
			returnClassName = AsmTypes.getClassLoadedName(getReturnType());
		}
		return returnClassName;
	}

	public String[] getArgumentClassNames() {
		if (argumentTypeNames == null) {
			Type[] types = getArgumentTypes();
			argumentTypeNames = new String[types.length];
			for (int i = 0; i < types.length; i++) {
				argumentTypeNames[i] = AsmTypes.getClassLoadedName(types[i]);
			}
		}
		return argumentTypeNames;
	}

	public Class<?> getReturnClass() throws ClassNotFoundException {
		if (returnClass == null) {
			returnClass = Reflects.loadClass(getReturnClassName());
		}
		return returnClass;
	}

	public Class<?>[] getArgumentClasses() throws ClassNotFoundException {
		if (argumentClasses == null) {
			String[] names = getArgumentClassNames();
			Class<?>[] classes = new Class<?>[names.length];
			for (int i = 0; i < names.length; i++) {
				classes[i] = Reflects.loadClass(names[i]);
			}
			argumentClasses = classes;
		}
		return argumentClasses;
	}


	/**
	 * 转换为字符串
	 *
	 * @return 方法名 + 方法描述符的组合
	 */
	public String toString() {
		return name + descriptor;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Signature)) {
			return false;
		}
		Signature signature = (Signature) o;
		return Objects.equals(getName(), signature.getName())
			&& Objects.equals(getDescriptor(), signature.getDescriptor());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName(), getDescriptor());
	}

}

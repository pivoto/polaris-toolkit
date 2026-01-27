package io.polaris.core.classpath.analyzer;

import javax.annotation.concurrent.NotThreadSafe;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Aug 18, 2025
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NotThreadSafe
public class MethodCall {
	// 类静态方法
	private boolean staticMethod ;
	// 实例初始化方法、私有方法和父类方法
	private  boolean specialMethod ;
	// 实例方法
	private  boolean virtualMethod ;
	// 接口方法
	private  boolean interfaceMethod ;

	private  String className;
	private  String methodName;
	private  String methodDesc;

	private Type[] methodArgumentTypes;
	private Type methodReturnType;
	private CodeValue[] methodArgs;


}

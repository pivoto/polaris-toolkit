package io.polaris.core.classpath.reader;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public interface MethodMetadata extends AnnotatedElementMetadata {

	String dump();

	String dump(int level);

	AnnotationMetadata getDefaultValue();

	/**
	 * 返回方法的名称。
	 */
	String getMethodName();

	/**
	 * 返回声明此方法的类的完全限定名。
	 */
	String getDeclaringClassName();

	/**
	 * 返回此方法声明的返回类型的完全限定名。
	 */
	String getReturnTypeName();

	/**
	 * 返回底层方法是否实际上为抽象方法：
	 * 即在类中标记为 abstract，或在接口中声明为常规的非默认方法。
	 */
	boolean isAbstract();

	/**
	 * 返回底层方法是否被声明为 'static'。
	 */
	boolean isStatic();

	/**
	 * 返回底层方法是否被标记为 'final'。
	 */
	boolean isFinal();

	/**
	 * 返回底层方法是否可被重写， 即未标记为 static、final 或 private。
	 */
	boolean isOverridable();
}

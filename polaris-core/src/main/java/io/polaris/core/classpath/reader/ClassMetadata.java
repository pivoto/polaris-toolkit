package io.polaris.core.classpath.reader;

import java.util.List;

import javax.annotation.Nullable;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public interface ClassMetadata extends AnnotatedElementMetadata {

	String dump();

	String dump(int level);

	List<MethodMetadata> getMethods();

	/**
	 * 返回底层类的名称。
	 */
	String getClassName();

	/**
	 * 返回底层类是否为接口。
	 */
	boolean isInterface();

	/**
	 * 返回底层类是否为注解。
	 */
	boolean isAnnotation();

	/**
	 * 返回底层类是否为抽象类。
	 */
	boolean isAbstract();

	/**
	 * 返回底层类是否为具体类，即既不是接口也不是抽象类。
	 */
	default boolean isConcrete() {
		return !(isInterface() || isAbstract());
	}

	/**
	 * 返回底层类是否被标记为 'final'。
	 */
	boolean isFinal();

	/**
	 * 确定底层类是否是独立的类，即是否是顶级类，或者是可以独立于外部类构造的嵌套类（静态内部类）。
	 */
	boolean isIndependent();

	/**
	 * 返回底层类是否声明在一个外部类中（即底层类是内部/嵌套类，
	 * 或是方法中的局部类）。
	 * <p>如果此方法返回 {@code false}，则底层类是一个顶级类。
	 */
	default boolean hasEnclosingClass() {
		return (getEnclosingClassName() != null);
	}

	/**
	 * 返回底层类的外部类名称，
	 * 如果底层类是顶级类则返回 {@code null}。
	 */
	@Nullable
	String getEnclosingClassName();

	/**
	 * 返回底层类是否有父类。
	 */
	default boolean hasSuperClass() {
		return (getSuperClassName() != null);
	}

	/**
	 * 返回底层类的父类名称，
	 * 如果没有定义父类则返回 {@code null}。
	 */
	@Nullable
	String getSuperClassName();

	/**
	 * 返回底层类实现的所有接口的名称，
	 * 如果没有则返回空数组。
	 */
	String[] getInterfaceNames();

	/**
	 * 返回由此 ClassMetadata 对象表示的类中声明为成员的所有类的名称。
	 * 这包括由该类声明的 public、protected、默认（包）访问和 private 的类和接口， 但不包括继承的类和接口。
	 * 如果不存在成员类或接口，则返回空数组。
	 */
	String[] getMemberClassNames();

}

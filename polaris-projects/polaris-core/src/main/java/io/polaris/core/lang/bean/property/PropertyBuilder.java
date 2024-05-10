package io.polaris.core.lang.bean.property;

/**
 * @author Qt
 * @since  Aug 28, 2023
 */
public interface PropertyBuilder<T> {


	/**
	 * 指定数据来源对象
	 *
	 * @param orig
	 * @return
	 */
	PropertyBuilder<T> from(Object orig);

	/**
	 * 是否忽略空属性值
	 *
	 * @return
	 */
	PropertyBuilder<T> ignoreNull(boolean ignored);

	PropertyBuilder<T> mapAll();

	PropertyBuilder<T> mapAll(Class<?> clazz);

	/**
	 * 配置属性映射关系
	 *
	 * @param origProperty 来源对象属性表达式
	 * @param destProperty 目标对象属性表达式
	 * @return
	 */
	PropertyBuilder<T> map(String origProperty, String destProperty);

	/**
	 * 设置目标对象的属性的值
	 *
	 * @param destProperty 目标对象属性表达式
	 * @param value        属性值
	 * @return
	 */
	PropertyBuilder<T> set(String destProperty, Object value);

	/**
	 * 执行所有属性映射操作,已执行过的操作不会重复执行
	 *
	 * @return
	 */
	PropertyBuilder<T> exec();

	/**
	 * 执行所有属性映射操作并返回目标对象
	 *
	 * @return
	 */
	T done();
}

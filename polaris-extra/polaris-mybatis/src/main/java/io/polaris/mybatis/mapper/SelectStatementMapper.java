package io.polaris.mybatis.mapper;

/**
 * SelectStatementMapper 是一个泛型接口，继承自 SelectStatementDefaultMapper 和 SelectStatementDirectMapper。
 * 该接口用于定义选择语句相关的映射器功能。
 *
 * @param <R> 泛型参数，表示返回结果的类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementMapper<R> extends
	SelectStatementDefaultMapper<R>,
	SelectStatementDirectMapper<R> {

}

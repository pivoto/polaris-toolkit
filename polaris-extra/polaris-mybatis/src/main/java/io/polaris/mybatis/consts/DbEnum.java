package io.polaris.mybatis.consts;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * 数据库表的枚举字段接口，用于定义枚举字段在数据库的存储的内部值获取方法
 *
 * @author Qt
 * @since Mar 28, 2026
 */
public interface DbEnum<V extends Serializable> {

	@Nonnull
	V getCode();

}

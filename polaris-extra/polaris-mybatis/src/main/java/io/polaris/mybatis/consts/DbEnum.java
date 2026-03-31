package io.polaris.mybatis.consts;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
public interface DbEnum<V extends Serializable> {

	@Nonnull
	V getCode();

}

package io.polaris.mybatis.auth;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;

/**
 * @author Qt
 * @since Mar 25, 2026
 */
@Spi("数据权限上下文")
public interface DataAuthCtxProvider extends SpiLoadable {

	@Nonnull
	Relation getRelation();

	@Nonnull
	Set<String> getIncludedScopes();

	@Nonnull
	Set<String> getExcludedScopes();
}

package io.polaris.mybatis.auth.impl;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;

import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.spi.SpiDefault;
import io.polaris.mybatis.auth.DataAuthCtx;
import io.polaris.mybatis.auth.DataAuthCtxProvider;

/**
 * @author Qt
 * @since Mar 25, 2026
 */
@SpiDefault
public class DefaultDataAuthCtxProvider implements DataAuthCtxProvider {

	@Nonnull
	@Override
	public Relation getRelation() {
		return DataAuthCtx.getRelation();
	}

	@Nonnull
	@Override
	public Set<String> getIncludedScopes() {
		return DataAuthCtx.getIncludedScopes();
	}

	@Nonnull
	@Override
	public Set<String> getExcludedScopes() {
		return DataAuthCtx.getExcludedScopes();
	}
}

package io.polaris.mybatis.auth;

import java.util.Set;

import io.polaris.mybatis.auth.consts.DataScopeRelation;
import lombok.Getter;

/**
 * @author Qt
 * @since Mar 25, 2026
 */
public class DataAuthEnv {
	@Getter
	final DataScopeRelation relation;
	@Getter
	final Set<String> includedScopes;
	@Getter
	final Set<String> excludedScopes;

	public DataAuthEnv(DataScopeRelation relation, Set<String> includedScopes, Set<String> excludedScopes) {
		this.relation = relation;
		this.includedScopes = includedScopes;
		this.excludedScopes = excludedScopes;
	}

	public static DataAuthEnv of(DataScopeRelation relation, Set<String> includedScopes, Set<String> excludedScopes) {
		return new DataAuthEnv(relation, includedScopes, excludedScopes);
	}

}

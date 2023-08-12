package io.polaris.core.jdbc.sql.query;

import io.polaris.core.collection.Iterables;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 11, 2023
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Criterion {
	private CriteriaRelation relation;
	private List<Criterion> subset = null;

	private CriteriaOperator operator = CriteriaOperator.EQ;
	private Object value;
	private String reference;

	public boolean isValid() {
		if (!Iterables.isEmpty(subset)) {
			return true;
		}
		if (operator != null) {
			return true;
		}
		return false;
	}

	public Criterion operator(CriteriaOperator operator) {
		this.operator = operator;
		return this;
	}

	public Criterion value(Object value) {
		this.value = value;
		return this;
	}

	public Criterion reference(String reference) {
		this.reference = reference;
		return this;
	}

	public Criterion relation(CriteriaRelation relation) {
		this.relation = relation;
		return this;
	}

	public Criterion addSubset(List<Criterion> list) {
		(this.subset == null ? this.subset = new ArrayList<>() : this.subset).addAll(list);
		return this;
	}

	public Criterion addSubset(Criterion criterion) {
		(this.subset == null ? this.subset = new ArrayList<>() : this.subset).add(criterion);
		return this;
	}

	public Criterion clearSubset() {
		if (subset != null) {
			subset.clear();
		}
		return this;
	}



}

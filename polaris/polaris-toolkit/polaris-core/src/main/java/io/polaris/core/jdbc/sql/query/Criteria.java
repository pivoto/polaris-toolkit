package io.polaris.core.jdbc.sql.query;

import io.polaris.core.collection.Iterables;
import io.polaris.core.string.Strings;
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
public class Criteria {
	private CriteriaRelation relation;
	private List<Criteria> subset = null;

	private String field;
	private Criterion criterion;


	public boolean isValid() {
		if (!Iterables.isEmpty(subset)) {
			return true;
		}
		if (Strings.isNotBlank(field) && criterion != null && criterion.isValid()) {
			return true;
		}
		return false;
	}


	public Criteria field(String field) {
		this.field = field;
		return this;
	}

	public Criteria criterion(Criterion criterion) {
		this.criterion = criterion;
		return this;
	}


	public Criteria relation(CriteriaRelation relation) {
		this.relation = relation;
		return this;
	}

	public Criteria addSubset(List<Criteria> list) {
		(this.subset == null ? this.subset = new ArrayList<>() : this.subset).addAll(list);
		return this;
	}

	public Criteria addSubset(Criteria condition) {
		(this.subset == null ? this.subset = new ArrayList<>() : this.subset).add(condition);
		return this;
	}

	public Criteria clearSubset() {
		if (subset != null) {
			subset.clear();
		}
		return this;
	}

}

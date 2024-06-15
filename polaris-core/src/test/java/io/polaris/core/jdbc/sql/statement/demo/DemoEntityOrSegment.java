package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.segment.CriterionSegment;
import io.polaris.core.jdbc.sql.statement.segment.OrSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
public class DemoEntityOrSegment<O extends Segment<O>> extends OrSegment<O, DemoEntityOrSegment<O>> {

	public <T extends TableSegment<?>> DemoEntityOrSegment(O owner, T table) {
		super(owner, table);
	}

	@Override
	public DemoEntityAndSegment<DemoEntityOrSegment<O>> and() {
		DemoEntityAndSegment<DemoEntityOrSegment<O>> x = new DemoEntityAndSegment<>(getThis(), getTable());
		addCriterion(new CriterionSegment<>(getThis(), x));
		return x;
	}

	@Override
	public DemoEntityOrSegment<DemoEntityOrSegment<O>> or() {
		DemoEntityOrSegment<DemoEntityOrSegment<O>> x = new DemoEntityOrSegment<>(getThis(), getTable());
		addCriterion(new CriterionSegment<>(getThis(), x));
		return x;
	}




	public CriterionSegment<DemoEntityOrSegment<O>, ?> id() {
		return column("id");
	}

	public CriterionSegment<DemoEntityOrSegment<O>, ?> name() {
		return column("name");
	}


}

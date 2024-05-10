package io.polaris.core.jdbc.sql.statement.demo;

import io.polaris.core.jdbc.sql.statement.Segment;
import io.polaris.core.jdbc.sql.statement.segment.AndSegment;
import io.polaris.core.jdbc.sql.statement.segment.CriterionSegment;
import io.polaris.core.jdbc.sql.statement.segment.TableSegment;

/**
 * @author Qt
 * @since  Aug 23, 2023
 */
public class DemoEntityAndSegment<O extends Segment<O>> extends AndSegment<O, DemoEntityAndSegment<O>> {

	public <T extends TableSegment<?>> DemoEntityAndSegment(O owner, T table) {
		super(owner, table);
	}

	@Override
	public DemoEntityAndSegment<DemoEntityAndSegment<O>> and() {
		DemoEntityAndSegment<DemoEntityAndSegment<O>> x = new DemoEntityAndSegment<>(getThis(), getTable());
		addCriterion(new CriterionSegment<>(getThis(), x));
		return x;
	}

	@Override
	public DemoEntityOrSegment<DemoEntityAndSegment<O>> or() {
		DemoEntityOrSegment<DemoEntityAndSegment<O>> x = new DemoEntityOrSegment<>(getThis(), getTable());
		addCriterion(new CriterionSegment<>(getThis(), x));
		return x;
	}


	public CriterionSegment<DemoEntityAndSegment<O>, ?>  id() {
		return column("id");
	}

	public CriterionSegment<DemoEntityAndSegment<O>, ?> name() {
		return column("name");
	}


}

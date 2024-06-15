package io.polaris.core.jdbc.sql.query;

import io.polaris.core.jdbc.sql.consts.Direction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Qt
 * @since  Aug 29, 2023
 */
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class OrderBy {

	private final Set<Item> items = new LinkedHashSet<>();

	public OrderBy() {
	}

	public static OrderBy newOrderBy() {
		return new OrderBy();
	}

	public Set<Item> items() {
		return Collections.unmodifiableSet(items);
	}

	public OrderBy by(String field) {
		return by(Direction.ASC, field);
	}

	public OrderBy by(Direction direction, String field) {
		return by(new Item(direction, field));
	}

	public OrderBy by(Item item) {
		items.add(item);
		return this;
	}

	public OrderBy remove(String field) {
		return remove(Direction.ASC, field);
	}

	public OrderBy remove(Direction direction, String field) {
		return remove(new Item(direction, field));
	}

	public OrderBy remove(Item item) {
		items.remove(item);
		return this;
	}


	@Setter
	@Getter
	@EqualsAndHashCode
	@ToString
	public static class Item {
		private Direction direction = Direction.ASC;
		private String field;

		public Item() {
		}

		public Item(Direction direction, String field) {
			this.direction = direction;
			this.field = field;
		}
	}

}

package io.polaris.core.jdbc.sql.query;

import java.io.Serializable;

import io.polaris.core.lang.Objs;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since Nov 06, 2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ValueRange<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	private E start;
	private E end;

	public static <E> ValueRange<E> of(E start, E end) {
		return new ValueRange<>(start, end);
	}

	public ValueRange<E> start(E start) {
		this.start = start;
		return this;
	}

	public ValueRange<E> end(E end) {
		this.end = end;
		return this;
	}

}

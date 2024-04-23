package io.polaris.core.concurrent.pool;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
@Getter
@Setter
@ToString
public class ErrorRecord<E> {

	private E data;
	private Throwable error;

	public ErrorRecord() {
	}

	public ErrorRecord(E data, Throwable error) {
		this.data = data;
		this.error = error;
	}

}

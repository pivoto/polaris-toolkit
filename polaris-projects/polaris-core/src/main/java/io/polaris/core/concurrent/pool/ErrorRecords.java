package io.polaris.core.concurrent.pool;

import java.util.Collection;

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
public class ErrorRecords<E> {

	private Collection<ErrorRecord<E>> records;
	private Throwable error;

	public ErrorRecords() {
	}

	public ErrorRecords(Collection<ErrorRecord<E>> records) {
		this.records = records;
	}

	public ErrorRecords(Collection<ErrorRecord<E>> records, Throwable error) {
		this.records = records;
		this.error = error;
	}
}

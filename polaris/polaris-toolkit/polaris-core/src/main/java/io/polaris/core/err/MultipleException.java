package io.polaris.core.err;

import io.polaris.core.collection.Iterables;

import java.util.Collections;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class MultipleException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	private static final String EOL = System.getProperty("line.separator");
	private final List<Throwable> failures;

	public MultipleException(String message, List<Throwable> failures) {
		super(message);
		this.failures = failures == null ? Collections.emptyList() : failures;
	}

	public MultipleException(String message, Throwable... failures) {
		super(message);
		this.failures = failures == null ? Collections.emptyList() : Iterables.asList(failures);
	}

	public MultipleException(String message, Iterable<Throwable> failures) {
		super(message);
		this.failures = failures == null ? Collections.emptyList() : Iterables.asList(failures);
	}

	private static String nullSafeMessage(Throwable failure) {
		String str = failure.getMessage();
		if ((str == null || str.trim().length() == 0)) {
			return failure.getClass().getName() + ": <no message>";
		}
		return failure.getClass().getName() + ": " + failure.getMessage();
	}

	@Override
	public String getMessage() {
		int failureCount = this.failures.size();
		String heading = super.getMessage();
		if (failureCount == 0) {
			return heading;
		}
		StringBuilder builder = new StringBuilder(heading)
			.append(" (")
			.append(failureCount).append(" ")
			.append(failureCount == 1 ? "failure" : "failures")
			.append(")")
			.append(EOL);
		int lastIndex = failureCount - 1;
		for (Throwable failure : this.failures.subList(0, lastIndex)) {
			builder.append("\t").append(nullSafeMessage(failure)).append(EOL);
		}
		builder.append('\t').append(nullSafeMessage(this.failures.get(lastIndex)));

		return builder.toString();
	}

	public List<Throwable> getFailures() {
		return Collections.unmodifiableList(this.failures);
	}

	public boolean hasFailures() {
		return !this.failures.isEmpty();
	}

}

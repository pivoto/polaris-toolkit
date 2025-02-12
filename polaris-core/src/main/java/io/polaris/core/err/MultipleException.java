package io.polaris.core.err;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.StdConsts;

/**
 * @author Qt
 * @since 1.8
 */
public class MultipleException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
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
		if ((str == null || str.trim().isEmpty())) {
			return failure.getClass().getName() + ": <no message>";
		}
		return failure.getClass().getName() + ": " + failure.getMessage();
	}

	@Override
	public String getMessage() {
		getStackTrace();
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
			.append(StdConsts.EOL);
		int lastIndex = failureCount - 1;
		for (Throwable failure : this.failures.subList(0, lastIndex)) {
			builder.append("\t").append(nullSafeMessage(failure)).append(StdConsts.EOL);
		}
		builder.append('\t').append(nullSafeMessage(this.failures.get(lastIndex)));

		return builder.toString();
	}


	@Override
	public void printStackTrace(PrintStream s) {
		for (Throwable e : failures) {
			e.printStackTrace(s);
		}
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		for (Throwable e : failures) {
			e.printStackTrace(s);
		}
	}

	public List<Throwable> getFailures() {
		return Collections.unmodifiableList(this.failures);
	}

	public boolean hasFailures() {
		return !this.failures.isEmpty();
	}

}

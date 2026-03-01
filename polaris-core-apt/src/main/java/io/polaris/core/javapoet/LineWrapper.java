package io.polaris.core.javapoet;

import java.io.IOException;

import static io.polaris.core.javapoet.Util.checkNotNull;

/**
 * 在可追加对象上实现软换行。
 * 请使用 {@link #append} 追加字符，
 * 或使用 {@link #wrappingSpace} 追加软换行空格。
 */
final class LineWrapper {
	private final RecordingAppendable out;
	private final String indent;
	private final int columnLimit;
	private boolean closed;

	/** 自上次换行空格以来写入但尚未刷新的字符。 */
	private final StringBuilder buffer = new StringBuilder();

	/** 自最近一次换行以来的字符数。包括输出和缓冲区。 */
	private int column = 0;

	/**
	 * 如果没有缓冲则为 -1；否则为换行后要写入的 {@code indent} 数量。
	 */
	private int indentLevel = -1;

	/**
	 * 如果没有缓冲则为 null；否则为传递给下一次 {@link #flush} 调用的类型。
	 */
	private FlushType nextFlush;

	LineWrapper(Appendable out, String indent, int columnLimit) {
		checkNotNull(out, "out == null");
		this.out = new RecordingAppendable(out);
		this.indent = indent;
		this.columnLimit = columnLimit;
	}

	/** @return 最后发出的字符，如果尚未发出任何字符则返回 {@link Character#MIN_VALUE}。 */
	char lastChar() {
		return out.lastChar;
	}

	/** 发出 {@code s}。这可能会被缓冲以允许插入换行。 */
	void append(String s) throws IOException {
		if (closed) throw new IllegalStateException("closed");

		if (nextFlush != null) {
			int nextNewline = s.indexOf('\n');

			// 如果 s 不会导致当前行超出限制，则将其缓冲并返回。我们稍后决定是否需要换行。
			if (nextNewline == -1 && column + s.length() <= columnLimit) {
				buffer.append(s);
				column += s.length();
				return;
			}

			// 如果追加 s 会使当前行溢出，则换行。
			boolean wrap = nextNewline == -1 || column + nextNewline > columnLimit;
			flush(wrap ? FlushType.WRAP : nextFlush);
		}

		out.append(s);
		int lastNewline = s.lastIndexOf('\n');
		column = lastNewline != -1
			? s.length() - lastNewline - 1
			: column + s.length();
	}

	/** 发出空格或换行符。 */
	void wrappingSpace(int indentLevel) throws IOException {
		if (closed) throw new IllegalStateException("closed");

		if (this.nextFlush != null) flush(nextFlush);
		column++; // 即使空格推迟到下次调用 flush()，也要增加列数。
		this.nextFlush = FlushType.SPACE;
		this.indentLevel = indentLevel;
	}

	/** 如果行将超出其限制则发出换行符，否则不执行任何操作。 */
	void zeroWidthSpace(int indentLevel) throws IOException {
		if (closed) throw new IllegalStateException("closed");

		if (column == 0) return;
		if (this.nextFlush != null) flush(nextFlush);
		this.nextFlush = FlushType.EMPTY;
		this.indentLevel = indentLevel;
	}

	/** 刷新所有未完成的文本并禁止将来对此行包装器的写入。 */
	void close() throws IOException {
		if (nextFlush != null) flush(nextFlush);
		closed = true;
	}

	/** 写入空格，然后写入其后的任何缓冲文本。 */
	private void flush(FlushType flushType) throws IOException {
		switch (flushType) {
			case WRAP:
				out.append('\n');
				for (int i = 0; i < indentLevel; i++) {
					out.append(indent);
				}
				column = indentLevel * indent.length();
				column += buffer.length();
				break;
			case SPACE:
				out.append(' ');
				break;
			case EMPTY:
				break;
			default:
				throw new IllegalArgumentException("Unknown FlushType: " + flushType);
		}

		out.append(buffer);
		buffer.delete(0, buffer.length());
		indentLevel = -1;
		nextFlush = null;
	}

	private enum FlushType {
		WRAP, SPACE, EMPTY;
	}

	/** 记录通过它的字符信息的委托 {@link Appendable}。 */
	static final class RecordingAppendable implements Appendable {
		private final Appendable delegate;

		char lastChar = Character.MIN_VALUE;

		RecordingAppendable(Appendable delegate) {
			this.delegate = delegate;
		}

		@Override
		public Appendable append(CharSequence csq) throws IOException {
			int length = csq.length();
			if (length != 0) {
				lastChar = csq.charAt(length - 1);
			}
			return delegate.append(csq);
		}

		@Override
		public Appendable append(CharSequence csq, int start, int end) throws IOException {
			CharSequence sub = csq.subSequence(start, end);
			return append(sub);
		}

		@Override
		public Appendable append(char c) throws IOException {
			lastChar = c;
			return delegate.append(c);
		}
	}
}

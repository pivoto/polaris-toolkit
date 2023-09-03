package io.polaris.core.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author Qt
 * @since 1.8
 */
class MemoryStreamableJavaFileObject extends MemoryJavaFileObject {

	private final CharSequence source;
	private ByteArrayOutputStream bout;

	public MemoryStreamableJavaFileObject(final String classFullName, final CharSequence source) {
		super(URI.create(classFullName.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
		this.source = source;
	}

	public MemoryStreamableJavaFileObject(final URI uri, final CharSequence source) {
		super(uri, Kind.SOURCE);
		this.source = source;
	}

	public MemoryStreamableJavaFileObject(final URI uri, final Kind kind) {
		super(uri, kind);
		this.source = null;
	}


	@Override
	public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws UnsupportedOperationException {
		if (source == null) {
			throw new UnsupportedOperationException();
		}
		return source;
	}

	@Override
	public InputStream openInputStream() {
		return new ByteArrayInputStream(getByteCode());
	}

	@Override
	public OutputStream openOutputStream() {
		return bout = new ByteArrayOutputStream();
	}

	public byte[] getByteCode() {
		return bout == null ? null : bout.toByteArray();
	}
}

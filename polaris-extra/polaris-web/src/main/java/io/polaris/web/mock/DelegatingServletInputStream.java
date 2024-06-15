package io.polaris.web.mock;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;


public class DelegatingServletInputStream extends ServletInputStream {

	private final InputStream sourceStream;


	public DelegatingServletInputStream(InputStream sourceStream) {
		this.sourceStream = sourceStream;
	}


	public final InputStream getSourceStream() {
		return this.sourceStream;
	}


	@Override
	public int read() throws IOException {
		return this.sourceStream.read();
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.sourceStream.close();
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setReadListener(ReadListener readListener) {

	}
}

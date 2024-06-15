package io.polaris.web.mock;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class DelegatingServletOutputStream extends ServletOutputStream {

	private final OutputStream targetStream;


	public DelegatingServletOutputStream(OutputStream targetStream) {
		this.targetStream = targetStream;
	}


	public final OutputStream getTargetStream() {
		return this.targetStream;
	}


	@Override
	public void write(int b) throws IOException {
		this.targetStream.write(b);
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		this.targetStream.flush();
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.targetStream.close();
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {

	}
}

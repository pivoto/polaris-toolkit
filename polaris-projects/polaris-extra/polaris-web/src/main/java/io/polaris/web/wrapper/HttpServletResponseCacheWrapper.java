package io.polaris.web.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * @author Qt
 * @since 1.8
 */
public class HttpServletResponseCacheWrapper extends HttpServletResponseWrapper {

	private PrintWriter writer;
	private ServletOutputStream outputStream;

	public HttpServletResponseCacheWrapper(HttpServletResponse response) {
		super(response);
	}


	@Override
	public String getCharacterEncoding() {
		String characterEncoding = super.getCharacterEncoding();
		if (characterEncoding == null || characterEncoding.length() == 0 || !Charset.isSupported(characterEncoding)) {
			characterEncoding = "UTF-8";
		}
		return characterEncoding;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		//new WriterOutputStream(writer,getCharacterEncoding());
		if (outputStream == null) {
			outputStream = super.getOutputStream();
		}
		return outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (outputStream == null) {
			outputStream = super.getOutputStream();
		}
		if (outputStream != null) {
			writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharacterEncoding()));
		}
		if (writer == null) {
			writer = super.getWriter();
		}
		return writer;
	}
}

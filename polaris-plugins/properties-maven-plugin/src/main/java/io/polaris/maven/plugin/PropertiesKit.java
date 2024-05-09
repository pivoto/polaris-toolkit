package io.polaris.maven.plugin;

import java.io.*;
import java.util.StringJoiner;

import org.apache.maven.plugin.logging.Log;

/**
 * @author Qt
 * @since 1.8,  May 08, 2024
 */
public class PropertiesKit {

	public static String nativeToAscii(Log log, String string) {
		if (string == null) {
			return null;
		}
		if (log != null && log.isDebugEnabled()) {
			log.debug("native2ascii string: " + string);
		}
		return nativeToAscii(string);
	}

	public static void nativeToAscii(Log log, final File file, final String encoding) throws IOException {
		if (log != null && log.isDebugEnabled()) {
			log.debug("native2ascii file: " + file.getAbsoluteFile());
		}

		String lineSep = "\n";
		try (FileInputStream in = new FileInputStream(file);) {
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = in.read(bytes)) != -1) {
				for (int i = 0; i < len; i++) {
					byte b = bytes[i];
					if ((int) b == (int) '\r') {
						if (i < len - 1) {
							byte b1 = bytes[i + 1];
							if ((int) b1 == (int) '\n') {
								lineSep = "\r\n";
							} else {
								lineSep = "\r";
							}
						} else {
							int b1 = in.read();
							if ((int) b1 == (int) '\n') {
								lineSep = "\r\n";
							} else {
								lineSep = "\r";
							}
						}
						break;
					}
					if ((int) b == (int) '\n') {
						lineSep = "\n";
						break;
					}
				}
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (FileInputStream in = new FileInputStream(file);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));
			 BufferedWriter bw =
				 new BufferedWriter(new OutputStreamWriter(out, encoding))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) {
					bw.write(lineSep);
					continue;
				}
				if (line.charAt(0) == '#') {
					if (line.length() > 1) {
						if (line.charAt(1) != ' ') {
							bw.write('#');
							bw.write(' ');
							bw.write(line.substring(1));
						} else {
							bw.write(line);
						}
					} else {
						bw.write('#');
					}
					bw.write(lineSep);
					continue;
				}
				bw.write(nativeToAscii(line));
				bw.write(lineSep);
			}
			bw.flush();
		}

		byte[] bytes = out.toByteArray();
		writeBytes(file, bytes);
	}


	public static String nativeToAscii(String s) {
		StringBuilder sb = new StringBuilder();
		char[] chs = s.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] > 0 && chs[i] < 127) {
				sb.append(chs[i]);
			} else {
				String str = Integer.toString(chs[i], 16);
				sb.append("\\u").append("0000", 0, 4 - str.length()).append(str);
			}
		}
		return sb.toString();
	}

	public static void mkdirParent(File file) {
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void writeBytes(File file, byte[] bytes) throws IOException {
		mkdirParent(file);
		try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));) {
			out.write(bytes);
		}
	}

	public static int copy(File src, File dest) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			mkdirParent(dest);
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			int i = copy(in, out);
			out.flush();
			return i;
		} finally {
			close(out);
			close(in);
		}
	}

	public static int copy(InputStream input, OutputStream output)
		throws IOException {
		return copy(input, output, 4096);
	}

	public static int copy(InputStream input, OutputStream output, int bufferSize)
		throws IOException {
		byte[] buffer = new byte[bufferSize];
		int count = 0;
		int n;
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
			count += n;
		}
		output.flush();
		return count;
	}

	public static void close(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable ignore) {
			}
		}
	}


	public static String join(CharSequence delimiter, CharSequence... arr) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence s : arr) {
			if (s == null || s.length() == 0) {
				continue;
			}
			joiner.add(s);
		}
		return joiner.toString();
	}
}

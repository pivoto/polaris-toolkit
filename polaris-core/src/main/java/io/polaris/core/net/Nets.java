package io.polaris.core.net;

import io.polaris.core.io.IO;
import io.polaris.core.string.Strings;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * @author Qt
 * @since 1.8
 */
public class Nets {
	/** 默认最小端口，1024 */
	public static final int PORT_RANGE_MIN = 1024;
	/** 默认最大端口，65535 */
	public static final int PORT_RANGE_MAX = 65535;
	private static final String IPV4_BASIC_PATTERN_STRING =
		"(([1-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){1}" + // initial first field, 1-255
			"(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){2}" + // following 2 fields, 0-255 followed by .
			"([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"; // final field, 0-255
	private static final Pattern IPV4_PATTERN = Pattern.compile("^" + IPV4_BASIC_PATTERN_STRING + "$");
	private static final Pattern IPV4_MAPPED_IPV6_PATTERN = Pattern.compile("^::[fF]{4}:" + IPV4_BASIC_PATTERN_STRING + "$");
	private static final Pattern IPV6_STD_PATTERN = Pattern.compile("^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}$");
	private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = Pattern.compile(
		"^(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)" + // 0-6 hex fields
			"::" +
			"(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)$"); // 0-6 hex fields
	private static final char COLON_CHAR = ':';
	private static final int MAX_COLON_COUNT = 7;

	public static boolean isValidPort(int port) {
		return port >= 0 && port <= PORT_RANGE_MAX;
	}

	public static boolean isUsableLocalPort(int port) {
		if (!isValidPort(port)) {
			return false;
		}
		try (ServerSocket ss = new ServerSocket(port);) {
			ss.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static int getUsableLocalPort() {
		return getUsableLocalPort(PORT_RANGE_MIN);
	}

	public static int getUsableLocalPort(int minPort) {
		return getUsableLocalPort(minPort, PORT_RANGE_MAX);
	}

	public static int getUsableLocalPort(int minPort, int maxPort) {
		for (int i = minPort; i <= maxPort; i++) {
			if (isUsableLocalPort(i)) {
				return i;
			}
		}
		return -1;
	}

	public static int getRandomLocalPort() {
		try (ServerSocket ss = new ServerSocket(0);) {
			int port = ss.getLocalPort();
			ss.close();
			return port;
		} catch (Exception e) {
			return -1;
		}
	}

	public static int getRandomLocalPort(int minPort, int maxPort) {
		for (int i = minPort; i <= maxPort; i++) {
			int port = ThreadLocalRandom.current().nextInt(minPort, maxPort + 1);
			if (isUsableLocalPort(port)) {
				return port;
			}
		}
		return -1;
	}

	public static String getLocalMacAddress() throws UnknownHostException {
		return getMacAddress(InetAddress.getLocalHost());
	}

	public static String getMacAddress(InetAddress inetAddress) {
		return getMacAddress(inetAddress, "-");
	}

	public static String getMacAddress(InetAddress inetAddress, String separator) {
		if (null == inetAddress) {
			return null;
		}
		byte[] mac;
		try {
			mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
		} catch (SocketException e) {
			return null;
		}
		if (null != mac) {
			final StringBuilder sb = new StringBuilder();
			String s;
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append(separator);
				}
				s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}
			return sb.toString();
		}
		return null;
	}

	public static InetSocketAddress createAddress(String host, int port) {
		if (Strings.isBlank(host)) {
			return new InetSocketAddress(port);
		}
		return new InetSocketAddress(host, port);
	}

	public static String send(String host, final int port, final String content) throws IOException {
		try (
			Socket socket = new Socket(host, port);
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
		) {
			writer.write(content);
			writer.newLine();
			writer.flush();
			return IO.toString(reader);
		}
	}

	public static byte[] downloadBytes(final String url) throws IOException {
		try (InputStream inputStream = new URL(url).openStream()) {
			return IO.toBytes(inputStream);
		}
	}

	public static String downloadString(final String url, final String encoding) throws IOException {
		try (InputStream inputStream = new URL(url).openStream()) {
			return IO.toString(inputStream, encoding);
		}
	}

	public static String downloadString(final String url) throws IOException {
		try (InputStream inputStream = new URL(url).openStream()) {
			return IO.toString(inputStream);
		}
	}

	public static void downloadFile(final String url, final File file) throws IOException {
		try (
			InputStream inputStream = new URL(url).openStream();
			ReadableByteChannel rbc = Channels.newChannel(inputStream);
			FileChannel fileChannel = FileChannel.open(
				file.toPath(),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE)
		) {
			fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
		}
	}

	public static long getRemoteFileSize(String url) throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			return connection.getContentLengthLong();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static boolean isIPv4Address(final String input) {
		return IPV4_PATTERN.matcher(input).matches();
	}

	public static boolean isIPv4MappedIPv64Address(final String input) {
		return IPV4_MAPPED_IPV6_PATTERN.matcher(input).matches();
	}

	public static boolean isIPv6StdAddress(final String input) {
		return IPV6_STD_PATTERN.matcher(input).matches();
	}

	public static boolean isIPv6HexCompressedAddress(final String input) {
		int colonCount = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == COLON_CHAR) {
				colonCount++;
			}
		}
		return colonCount <= MAX_COLON_COUNT && IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
	}

	public static boolean isIPv6Address(final String input) {
		return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
	}

}

package io.polaris.core.guid;

import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.channels.FileLock;

/**
 * @author Qt
 * @since 1.8
 */
public class LocalNode {

	public static int nextNodeId(String appName, int bizSize) {
		if (bizSize <= 8 || bizSize > 16) {
			throw new IllegalArgumentException();
		}
		int nodeId = 0;
		String tmpdir = System.getProperty("java.io.tmpdir");
		final String file = tmpdir + "/.guid." + appName + ".lck";
		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			FileLock lock = null;
			while (true) {
				try {
					lock = raf.getChannel().lock();
					break;
				} catch (Throwable e) {
				}
			}
			try {
				if (raf.length() >= 4) {
					nodeId = raf.readInt() + 1;
					raf.seek(0);
					raf.writeInt(nodeId);
				} else {
					raf.writeInt(nodeId);
				}
			} finally {
				if (lock != null) {
					lock.release();
				}
			}
		} catch (Throwable e) {
			System.err.println("cannot access file : " + file);
			e.printStackTrace(System.err);
		}

		int addr = 0;
		try {
			byte[] address = InetAddress.getLocalHost().getAddress();
			addr = address[address.length - 1];
		} catch (Throwable e) {
			System.err.println("cannot read InetAddress!");
			e.printStackTrace(System.err);
		}
		return ((addr & 0xFF) << (bizSize - 8)) | (nodeId & (-1 ^ (-1 << (bizSize - 8))));
	}


}

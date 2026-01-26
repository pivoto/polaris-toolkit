package io.polaris.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.polaris.core.consts.SystemProperty;
import io.polaris.core.function.CallableWithArgs2;
import io.polaris.core.function.ExecutableWithArgs2;
import io.polaris.core.string.Strings;
import io.polaris.core.ulid.Ulid;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public class JarReaders {

	public static void read(String resource, CallableWithArgs2<String, InputStream, Boolean> consumer) throws IOException {
		String[] array = Strings.delimitedToArray(resource, "!/");
		if (array.length == 1) {
			read(new File(array[0]), consumer);
		} else {
			Deque<String> queue = new ArrayDeque<>();
			for (int i = 1; i < array.length; i++) {
				queue.addFirst(array[i]);
			}
			File baseFile = new File(array[0]);
			File file = baseFile;
			while (file != null && file.isFile()) {
				File[] targetFile = new File[1];
				CallableWithArgs2<String, InputStream, Boolean> filter = (name, input) -> {
					if (queue.isEmpty()) {
						return consumer.call(name, input);
					}
					String first = queue.peekFirst();
					if (!name.equals(first)) {
						return true;
					}
					queue.pollFirst();
					if (!queue.isEmpty() || name.endsWith(".jar") || name.endsWith(".zip")) {
						String fileSuffix = name.endsWith(".jar") ? ".jar" : ".zip";
						targetFile[0] = createTempFile(input, fileSuffix);
						return false;
					}
					return consumer.call(name, input);
				};
				try {
					read(file, filter);
				} finally {
					if (baseFile != file) {
						if (!file.delete()) {
							file.deleteOnExit();
						}
					}
				}
				file = targetFile[0];
			}
		}
	}

	public static void read(String resource, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		String[] array = Strings.delimitedToArray(resource, "!/");
		if (array.length == 1) {
			read(new File(array[0]), consumer);
		} else {
			Deque<String> queue = new ArrayDeque<>();
			for (int i = 1; i < array.length; i++) {
				queue.addFirst(array[i]);
			}
			File baseFile = new File(array[0]);
			File file = baseFile;
			while (file != null && file.isFile()) {
				File[] targetFile = new File[1];
				CallableWithArgs2<String, InputStream, Boolean> filter = (name, input) -> {
					if (queue.isEmpty()) {
						consumer.execute(name, input);
						return true;
					}
					String first = queue.peekFirst();
					if (!name.equals(first)) {
						return true;
					}
					queue.pollFirst();
					if (!queue.isEmpty() || name.endsWith(".jar") || name.endsWith(".zip")) {
						String fileSuffix = name.endsWith(".jar") ? ".jar" : ".zip";
						targetFile[0] = createTempFile(input, fileSuffix);
						return false;
					}
					consumer.execute(name, input);
					return true;
				};
				try {
					read(file, filter);
				} finally {
					if (baseFile != file) {
						if (!file.delete()) {
							file.deleteOnExit();
						}
					}
				}
				file = targetFile[0];
			}
		}
	}

	public static void read(File file, CallableWithArgs2<String, InputStream, Boolean> consumer) throws IOException {
		if (file.getName().endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file);) {
				read(zipFile, consumer);
			}
		} else {
			try (JarFile jarFile = new JarFile(file);) {
				read(jarFile, consumer);
			}
		}
	}

	private static void read(JarFile jarFile, CallableWithArgs2<String, InputStream, Boolean> consumer) throws IOException {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			if (jarEntry.isDirectory()) {
				continue;
			}
			String name = jarEntry.getName();
			try (InputStream input = jarFile.getInputStream(jarEntry)) {
				Boolean rs = consumer.call(name, input);
				if (rs != null && !rs) {
					break;
				}
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	private static void read(ZipFile zipFile, CallableWithArgs2<String, InputStream, Boolean> consumer) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.isDirectory()) {
				continue;
			}
			String name = zipEntry.getName();
			try (InputStream input = zipFile.getInputStream(zipEntry)) {
				Boolean rs = consumer.call(name, input);
				if (rs != null && !rs) {
					break;
				}
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	public static void read(File file, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		if (file.getName().endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file);) {
				read(zipFile, consumer);
			}
		} else {
			try (JarFile jarFile = new JarFile(file);) {
				read(jarFile, consumer);
			}
		}
	}

	private static void read(JarFile jarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			if (jarEntry.isDirectory()) {
				continue;
			}
			String name = jarEntry.getName();
			try (InputStream input = jarFile.getInputStream(jarEntry)) {
				consumer.execute(name, input);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	private static void read(ZipFile zipFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.isDirectory()) {
				continue;
			}
			String name = zipEntry.getName();
			try (InputStream input = zipFile.getInputStream(zipEntry)) {
				consumer.execute(name, input);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}


	public static void readRecursively(File file, CallableWithArgs2<String, InputStream, Boolean> consumer) throws IOException {
		readRecursively(file, "", consumer);
	}

	public static void readRecursively(File file, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		readRecursively(file, "", consumer);
	}

	public static void readRecursively(File file, String baseName, CallableWithArgs2<String, InputStream, Boolean> consumer) throws IOException {
		readRecursively(file, baseName, consumer, false);
	}

	public static void readRecursively(File file, String baseName, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		readRecursively(file, baseName, consumer, false);
	}

	public static void readRecursively(File file, String baseName, CallableWithArgs2<String, InputStream, Boolean> consumer, boolean includeNestedFile) throws IOException {
		if (file.getName().endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file);) {
				readRecursively(zipFile, baseName, consumer, includeNestedFile);
			}
		} else {
			try (JarFile jarFile = new JarFile(file);) {
				readRecursively(jarFile, baseName, consumer, includeNestedFile);
			}
		}
	}

	public static void readRecursively(File file, String baseName, ExecutableWithArgs2<String, InputStream> consumer, boolean includeNestedFile) throws IOException {
		if (file.getName().endsWith(".zip")) {
			try (ZipFile zipFile = new ZipFile(file);) {
				readRecursively(zipFile, baseName, consumer, includeNestedFile);
			}
		} else {
			try (JarFile jarFile = new JarFile(file);) {
				readRecursively(jarFile, baseName, consumer, includeNestedFile);
			}
		}
	}


	private static void readRecursively(JarFile jarFile, String baseName, CallableWithArgs2<String, InputStream, Boolean> consumer, boolean includeNestedFile) throws IOException {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			if (jarEntry.isDirectory()) {
				continue;
			}
			String entryName = jarEntry.getName();
			try (InputStream input = jarFile.getInputStream(jarEntry)) {
				boolean rs = readRecursively(baseName, entryName, input, consumer, includeNestedFile);
				if (!rs) {
					break;
				}
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}

		}
	}

	private static void readRecursively(JarFile jarFile, String baseName, ExecutableWithArgs2<String, InputStream> consumer, boolean includeNestedFile) throws IOException {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			if (jarEntry.isDirectory()) {
				continue;
			}
			String entryName = jarEntry.getName();
			try (InputStream input = jarFile.getInputStream(jarEntry)) {
				readRecursively(baseName, entryName, input, consumer, includeNestedFile);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}

		}
	}


	private static void readRecursively(ZipFile zipFile, String baseName, CallableWithArgs2<String, InputStream, Boolean> consumer, boolean includeNestedFile) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.isDirectory()) {
				continue;
			}
			String entryName = zipEntry.getName();
			try (InputStream input = zipFile.getInputStream(zipEntry)) {
				boolean rs = readRecursively(baseName, entryName, input, consumer, includeNestedFile);
				if (!rs) {
					break;
				}
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}

		}
	}

	private static void readRecursively(ZipFile zipFile, String baseName, ExecutableWithArgs2<String, InputStream> consumer, boolean includeNestedFile) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			if (zipEntry.isDirectory()) {
				continue;
			}
			String entryName = zipEntry.getName();
			try (InputStream input = zipFile.getInputStream(zipEntry)) {
				readRecursively(baseName, entryName, input, consumer, includeNestedFile);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}

		}
	}


	private static File createTempFile(InputStream input, String fileSuffix) {
		File tmpFile = new File(SystemProperty.JAVA_IO_TMPDIR.value(), Ulid.fast().toString() + fileSuffix);
		try {
			tmpFile.getParentFile().mkdirs();
			try (FileOutputStream fos = new FileOutputStream(tmpFile);) {
				IO.copy(input, fos);
				fos.flush();
			}
		} catch (IOException ignored) {
		}
		return tmpFile;
	}

	private static boolean readRecursively(String baseName, String entryName, InputStream input, CallableWithArgs2<String, InputStream, Boolean> consumer, boolean includeNestedFile) throws Exception {
		if (entryName.endsWith(".jar") || entryName.endsWith(".zip")) {
			String fileSuffix = entryName.endsWith(".jar") ? ".jar" : ".zip";
			File tmpFile = createTempFile(input, fileSuffix);
			if (tmpFile.isFile()) {
				if (includeNestedFile) {
					try (FileInputStream fis = new FileInputStream(tmpFile);) {
						Boolean rs = consumer.call((Strings.isBlank(baseName) ? "" : baseName + "!/") + entryName, fis);
						if (rs != null && !rs) {
							return false;
						}
					}
				}
				try {
					JarReaders.readRecursively(tmpFile, (Strings.isBlank(baseName) ? "" : baseName + "!/") + entryName, consumer);
				} finally {
					if (!tmpFile.delete()) {
						tmpFile.deleteOnExit();
					}
				}
			}
			return true;
		} else {
			Boolean rs = consumer.call((Strings.isBlank(baseName) ? "" : baseName + "!/") + entryName, input);
			return rs == null || rs;
		}
	}

	private static void readRecursively(String baseName, String entryName, InputStream input, ExecutableWithArgs2<String, InputStream> consumer, boolean includeNestedFile) throws Exception {
		if (entryName.endsWith(".jar") || entryName.endsWith(".zip")) {
			String fileSuffix = entryName.endsWith(".jar") ? ".jar" : ".zip";
			File tmpFile = createTempFile(input, fileSuffix);
			if (tmpFile.isFile()) {
				if (includeNestedFile) {
					try (FileInputStream fis = new FileInputStream(tmpFile);) {
						consumer.execute((Strings.isBlank(baseName) ? "" : baseName + "!/") + entryName, fis);
					}
				}
				try {
					JarReaders.readRecursively(tmpFile, (Strings.isBlank(baseName) ? "" : baseName + "!/") + entryName, consumer);
				} finally {
					if (!tmpFile.delete()) {
						tmpFile.deleteOnExit();
					}
				}
			}
		} else {
			consumer.execute((Strings.isBlank(baseName) ? "" : baseName + "!/") + entryName, input);
		}
	}


}

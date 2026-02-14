package io.polaris.commons.compress;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.polaris.core.function.ExecutableWithArgs2;
import io.polaris.core.io.IO;
import io.polaris.core.tuple.Tuple2;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 * @author Qt
 * @since Feb 14, 2026
 */
public class TarGzUtils {


	public static void writeTarGzip(File tarFile, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		writeTarGzip(StandardCharsets.UTF_8, tarFile, contents);
	}

	public static void writeTarGzip(File tarFile, File baseDir, Iterable<File> srcFiles) throws IOException {
		writeTarGzip(StandardCharsets.UTF_8, tarFile, baseDir, srcFiles);
	}

	public static void writeTarGzip(File tarFile, File srcDir) throws IOException {
		writeTarGzip(StandardCharsets.UTF_8, tarFile, srcDir);
	}

	public static void writeTarGzip(OutputStream out, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		writeTarGzip(StandardCharsets.UTF_8, out, contents);
	}

	public static void writeTarGzip(OutputStream out, File baseDir, Iterable<File> srcFiles) throws IOException {
		writeTarGzip(StandardCharsets.UTF_8, out, baseDir, srcFiles);
	}

	public static void writeTarGzip(OutputStream out, File srcDir) throws IOException {
		writeTarGzip(StandardCharsets.UTF_8, out, srcDir);
	}

	public static void writeTar(File tarFile, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		writeTar(StandardCharsets.UTF_8, tarFile, contents);
	}

	public static void writeTar(File tarFile, File baseDir, Iterable<File> srcFiles) throws IOException {
		writeTar(StandardCharsets.UTF_8, tarFile, baseDir, srcFiles);
	}

	public static void writeTar(File tarFile, File srcDir) throws IOException {
		writeTar(StandardCharsets.UTF_8, tarFile, srcDir);
	}

	public static void writeTar(OutputStream out, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		writeTar(StandardCharsets.UTF_8, out, contents);
	}

	public static void writeTar(OutputStream out, File baseDir, Iterable<File> srcFiles) throws IOException {
		writeTar(StandardCharsets.UTF_8, out, baseDir, srcFiles);
	}

	public static void writeTar(OutputStream out, File srcDir) throws IOException {
		writeTar(StandardCharsets.UTF_8, out, srcDir);
	}

	public static void writeTarGzip(Charset charset, File tarFile, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		try (OutputStream out = new FileOutputStream(tarFile)) {
			writeTarGzip(charset, out, contents);
		}
	}

	public static void writeTarGzip(Charset charset, File tarFile, File baseDir, Iterable<File> srcFiles) throws IOException {
		try (OutputStream out = new FileOutputStream(tarFile)) {
			writeTarGzip(charset, out, baseDir, srcFiles);
		}
	}

	public static void writeTarGzip(Charset charset, File tarFile, File srcDir) throws IOException {
		try (OutputStream out = new FileOutputStream(tarFile)) {
			writeTarGzip(charset, out, srcDir);
		}
	}

	public static void writeTarGzip(Charset charset, OutputStream out, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		try (
			BufferedOutputStream bos = new BufferedOutputStream(out);
			GzipCompressorOutputStream gz = new GzipCompressorOutputStream(bos);
			TarArchiveOutputStream tar = new TarArchiveOutputStream(gz, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			writeTar(tar, contents);
		}
	}


	public static void writeTarGzip(Charset charset, OutputStream out, File baseDir, Iterable<File> srcFiles) throws IOException {
		try (
			BufferedOutputStream bos = new BufferedOutputStream(out);
			GzipCompressorOutputStream gz = new GzipCompressorOutputStream(bos);
			TarArchiveOutputStream tar = new TarArchiveOutputStream(gz, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			writeTar(tar, baseDir, srcFiles);
		}
	}


	public static void writeTarGzip(Charset charset, OutputStream out, File srcDir) throws IOException {
		if (!srcDir.isDirectory()) {
			throw new IOException("请指定文件夹");
		}
		try (
			BufferedOutputStream bos = new BufferedOutputStream(out);
			GzipCompressorOutputStream gz = new GzipCompressorOutputStream(bos);
			TarArchiveOutputStream tar = new TarArchiveOutputStream(gz, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			writeTar(tar, srcDir.getAbsoluteFile(), srcDir);
		}
	}

	public static void writeTar(Charset charset, File tarFile, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		try (OutputStream out = new FileOutputStream(tarFile)) {
			writeTar(charset, out, contents);
		}
	}

	public static void writeTar(Charset charset, File tarFile, File baseDir, Iterable<File> srcFiles) throws IOException {
		try (OutputStream out = new FileOutputStream(tarFile)) {
			writeTar(charset, out, baseDir, srcFiles);
		}
	}

	public static void writeTar(Charset charset, File tarFile, File srcDir) throws IOException {
		try (OutputStream out = new FileOutputStream(tarFile)) {
			writeTar(charset, out, srcDir);
		}
	}

	public static void writeTar(Charset charset, OutputStream out, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		try (
			BufferedOutputStream bos = new BufferedOutputStream(out);
			TarArchiveOutputStream tar = new TarArchiveOutputStream(bos, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			writeTar(tar, contents);
		}
	}


	public static void writeTar(Charset charset, OutputStream out, File baseDir, Iterable<File> srcFiles) throws IOException {
		try (
			BufferedOutputStream bos = new BufferedOutputStream(out);
			TarArchiveOutputStream tar = new TarArchiveOutputStream(bos, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			writeTar(tar, baseDir, srcFiles);
		}
	}


	public static void writeTar(Charset charset, OutputStream out, File srcDir) throws IOException {
		if (!srcDir.isDirectory()) {
			throw new IOException("请指定文件夹");
		}
		try (
			BufferedOutputStream bos = new BufferedOutputStream(out);
			TarArchiveOutputStream tar = new TarArchiveOutputStream(bos, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			writeTar(tar, srcDir.getAbsoluteFile(), srcDir);
		}
	}

	private static void writeTar(TarArchiveOutputStream tar, Iterable<Tuple2<String, byte[]>> contents) throws IOException {
		tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

		for (Tuple2<String, byte[]> content : contents) {
			String name = content.getFirst();
			byte[] bytes = content.getSecond();

			TarArchiveEntry entry = new TarArchiveEntry(name);
			entry.setSize(bytes.length);
			tar.putArchiveEntry(entry);
			tar.write(bytes);
			tar.flush();
			tar.closeArchiveEntry();
		}
		tar.finish();
		tar.flush();
	}

	private static void writeTar(TarArchiveOutputStream tar, File baseDir, Iterable<File> srcFiles) throws IOException {
		tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
		for (File file : srcFiles) {
			addTarEntry(tar, baseDir, file);
		}
		tar.finish();
		tar.flush();
	}

	private static void writeTar(TarArchiveOutputStream tar, File baseDir, File srcDir) throws IOException {
		tar.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
		addTarEntry(tar, baseDir, srcDir);
		tar.finish();
	}

	private static void addTarEntry(TarArchiveOutputStream tar, File baseDir, File srcFile) throws IOException {
		if (srcFile.isDirectory()) {
			File[] files = srcFile.listFiles();
			if (files == null) {
				return;
			}
			for (File listFile : files) {
				addTarEntry(tar, baseDir, listFile);
			}
		} else if (srcFile.isFile()) {
			String name = srcFile.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
			TarArchiveEntry tarEntry = new TarArchiveEntry(srcFile, name);
			tar.putArchiveEntry(tarEntry);
			try (FileInputStream fis = new FileInputStream(srcFile);) {
				IO.copy(fis, tar);
			}
			tar.flush();
			tar.closeArchiveEntry();
		}
	}


	public static void readTarGz(File tarFile, File tarDir) throws IOException {
		readTarGz(StandardCharsets.UTF_8, tarFile, tarDir);
	}

	public static void readTarGz(InputStream tarFile, File tarDir) throws IOException {
		readTarGz(StandardCharsets.UTF_8, tarFile, tarDir);

	}

	public static void readTar(File tarFile, File tarDir) throws IOException {
		readTar(StandardCharsets.UTF_8, tarFile, tarDir);

	}

	public static void readTar(InputStream tarFile, File tarDir) throws IOException {
		readTar(StandardCharsets.UTF_8, tarFile, tarDir);
	}

	public static void readTarGz(File tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		readTarGz(StandardCharsets.UTF_8, tarFile, consumer);
	}

	public static void readTarGz(InputStream tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		readTarGz(StandardCharsets.UTF_8, tarFile, consumer);

	}

	public static void readTar(File tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		readTar(StandardCharsets.UTF_8, tarFile, consumer);

	}

	public static void readTar(InputStream tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		readTar(StandardCharsets.UTF_8, tarFile, consumer);
	}

	public static void readTarGz(Charset charset, File tarFile, File tarDir) throws IOException {
		if (!tarDir.isDirectory()) {
			tarDir.mkdirs();
		}
		if (!tarDir.isDirectory()) {
			throw new IOException("请指定文件夹");
		}
		readTarGz(charset, tarFile, (name, input) -> writeFile(tarDir, name, input));
	}

	public static void readTarGz(Charset charset, InputStream tarFile, File tarDir) throws IOException {
		if (!tarDir.isDirectory()) {
			tarDir.mkdirs();
		}
		if (!tarDir.isDirectory()) {
			throw new IOException("请指定文件夹");
		}
		readTarGz(charset, tarFile, (name, input) -> writeFile(tarDir, name, input));
	}

	public static void readTar(Charset charset, File tarFile, File tarDir) throws IOException {
		if (!tarDir.isDirectory()) {
			tarDir.mkdirs();
		}
		if (!tarDir.isDirectory()) {
			throw new IOException("请指定文件夹");
		}
		readTar(charset, tarFile, (name, input) -> writeFile(tarDir, name, input));
	}

	public static void readTar(Charset charset, InputStream tarFile, File tarDir) throws IOException {
		if (!tarDir.isDirectory()) {
			tarDir.mkdirs();
		}
		if (!tarDir.isDirectory()) {
			throw new IOException("请指定文件夹");
		}
		readTar(charset, tarFile, (name, input) -> writeFile(tarDir, name, input));
	}

	public static void readTarGz(Charset charset, File tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		try (FileInputStream fis = new FileInputStream(tarFile);) {
			readTarGz(charset, fis, consumer);
		}
	}

	public static void readTarGz(Charset charset, InputStream tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		try (
			GzipCompressorInputStream gz = new GzipCompressorInputStream(tarFile);
			TarArchiveInputStream tar = new TarArchiveInputStream(gz, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			readTar(tar, consumer);
		}
	}

	public static void readTar(Charset charset, File tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		try (FileInputStream fis = new FileInputStream(tarFile);) {
			readTar(charset, fis, consumer);
		}
	}

	public static void readTar(Charset charset, InputStream tarFile, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		try (
			TarArchiveInputStream tar = new TarArchiveInputStream(tarFile, (charset == null ? StandardCharsets.UTF_8 : charset).name());
		) {
			readTar(tar, consumer);
		}
	}


	private static void writeFile(File tarDir, String name, InputStream input) throws IOException {
		File file = new File(tarDir, name);
		File parentFile = file.getParentFile();
		if (!parentFile.isDirectory()) {
			parentFile.mkdirs();
		}
		if (!parentFile.isDirectory()) {
			throw new IOException("目录不存在且无法创建：" + parentFile.getAbsolutePath());
		}
		try (FileOutputStream fos = new FileOutputStream(file);) {
			IO.copy(input, fos);
		}
	}


	private static void readTar(TarArchiveInputStream tar, ExecutableWithArgs2<String, InputStream> consumer) throws IOException {
		ArchiveEntry entry;
		while ((entry = tar.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				continue;
			}
			String name = entry.getName();
			try {
				consumer.execute(name, tar);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

}

package io.polaris.changer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @version Mar 02, 2022
 * @since 1.8
 */
@Slf4j
public class PackageChanger {
	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	@Getter
	@Setter
	private Charset charset = Charset.defaultCharset();

	/** 来源根目录 */
	@Getter
	@Setter
	private File srcRoot;
	/** 目标根目录 */
	@Getter
	@Setter
	private File destRoot;

	/** 指定源代码相对路径 */
	private List<String> sourcePaths = new ArrayList<>();
	/** 需要处理文件的扩展名 */
	private List<String> extensions = new ArrayList<>();
	/** 包名映射关系 */
	private List<PackageMapping> mappings = new ArrayList<>();
	/** 是否复制未映射的文件 */
	@Getter
	@Setter
	private boolean copyAll = true;
	/** 是否同时处理符合映射关系的文件名 */
	@Getter
	@Setter
	private boolean mappingFileName = true;

	/*private static Consumer<String> logFunc = System.out::println;
	private static Supplier<Boolean> logEnabled = Boolean.TRUE::booleanValue;

	static {
		try {
			Logger logger = LoggerFactory.getLogger(PackageChanger.class);
			logFunc = msg -> logger.info(msg);
			logEnabled = () -> logger.isInfoEnabled();
		} catch (Throwable ignore) {
		}
	}*/

	public void execute() throws IOException {
		check();

		if (sourcePaths.isEmpty()) {
			doChange(srcRoot.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "")
					, destRoot.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "")
					, srcRoot);
		} else {
			for (String sourcePath : sourcePaths) {
				sourcePath = sourcePath.replace("\\", "/");
				File from = new File(srcRoot, sourcePath);
				File to = new File(destRoot, sourcePath);
				doChange(from.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "")
						, to.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "")
						, from);
			}
		}
	}

	private boolean matches(String fileName) {
		if (extensions.isEmpty()) {
			return true;
		}
		for (String extension : extensions) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}

	private void doChange(String srcPath, String destPath, File from) throws IOException {
		log("[Changing] {}", from.getPath());
		if (!from.isDirectory()) {
			return;
		}
		File[] files = from.listFiles();
		for (File file : files) {
			String filePath = file.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
			if (filePath.length() <= srcPath.length() + 1 || !filePath.startsWith(srcPath)) {
				throw new IllegalArgumentException("path:" + filePath);
			}
			if (file.isFile()) {
				String fileName = file.getName();
				filePath = filePath.substring(srcPath.length() + 1
						, filePath.length() - fileName.length());
				String mappingFile = createMappingFile(filePath, fileName);
				if (mappingFile != null) {
					File newFile = new File(destPath, mappingFile);
					newFile.getParentFile().mkdirs();
					if (matches(fileName)) {
						log("[Change] {}", file.getPath());
						doChange(file, newFile);
					} else {
						log("[Copy] {}", file.getPath());
						copy(file, newFile);
					}
				}
			} else {
				filePath = filePath.substring(srcPath.length() + 1);
				if (maybe(filePath)) {
					doChange(srcPath, destPath, file);
				}
			}
		}
	}

	private boolean maybe(String filePath) {
		if (copyAll) {
			return true;
		}
		for (PackageMapping mapping : mappings) {
			String src = mapping.getSrc();
			String dest = mapping.getDest();
			String srcPath = src.replace(".", "/");
			String destPath = dest.replace(".", "/");
			if (filePath.startsWith(srcPath)) {// match
				return true;
			}
			if (srcPath.startsWith(filePath)) {//maybe
				return true;
			}
		}
		return false;
	}

	private String createMappingFile(String filePath, String fileName) {
		boolean maybe = false;
		String newFileName = null;
		if (mappingFileName) {
			for (PackageMapping mapping : mappings) {
				String src = mapping.getSrc();
				String dest = mapping.getDest();
				if (fileName.equals(src)) {
					newFileName = "/" + dest;
					maybe = true;
					break;
				} else if (fileName.startsWith(src)) {
					newFileName = "/" + dest + fileName.substring(src.length());
					maybe = true;
					break;
				}
			}
		}
		if (newFileName == null) {
			newFileName = "/" + fileName;
		}

		for (PackageMapping mapping : mappings) {
			String src = mapping.getSrc();
			String dest = mapping.getDest();
			String srcPath = src.replace(".", "/");
			String destPath = dest.replace(".", "/");
			if (filePath.equals(srcPath)) {// equal
				return destPath + newFileName;
			} else if (filePath.startsWith(srcPath)) {// match
				return destPath + "/" + filePath.substring(srcPath.length()) + newFileName;
			}
			if (!maybe) {
				maybe = fileName == null && srcPath.startsWith(filePath);
			}
		}
		if (maybe || copyAll) {
			return filePath + newFileName;
		}
		return null;
	}

	private void doChange(final File src, final File dest) throws IOException {
		try (FileInputStream fis = new FileInputStream(src);
			 FileOutputStream fos = new FileOutputStream(dest);
			 BufferedReader br = new BufferedReader(new InputStreamReader(fis, charset));
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, charset));
		) {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String s = doChangeForLine(line);
				bw.write(s);
				bw.newLine();
			}
			bw.flush();
		}
	}

	private String doChangeForLine(String line) {
		String s = line;
		for (PackageMapping mapping : mappings) {
			s = s.replaceAll("\\b" + mapping.getSrc().replace(".", "\\.") + "\\b", mapping.getDest());
			s = s.replaceAll("\\b" + mapping.getSrc().replace(".", "/") + "\\b", mapping.getDest().replace(".", "/"));
		}
		return s;
	}

	private void check() {
		if (srcRoot == null || !srcRoot.exists()) {
			throw new IllegalArgumentException("srcRoot");
		}
		if (destRoot == null) {
			throw new IllegalArgumentException("destRoot");
		}
		if (!destRoot.exists()) {
			destRoot.mkdirs();
			if (!destRoot.exists()) {
				throw new IllegalArgumentException("destRoot");
			}
		}
	}


	public void log(String msg, Object... args) {
		if (log.isInfoEnabled()) {
			log.info(msg, args);
		}
		/*if (logEnabled.get()) {
			logFunc.accept(format(msg, args));
		}*/
	}


	public void addSourcePath(String... paths) {
		for (String path : paths) {
			this.sourcePaths.add(path);
		}
	}

	public void addExtension(String... extensions) {
		for (String extension : extensions) {
			this.extensions.add("." + extension.replaceFirst("\\.+", ""));
		}
	}

	public void addMapping(String src, String dest) {
		this.mappings.add(new PackageMapping(src, dest));
	}

	public void addMapping(PackageMapping... mappings) {
		for (PackageMapping mapping : mappings) {
			this.mappings.add(mapping);
		}
	}

	public void addMapping(Collection<PackageMapping> mappings) {
		this.mappings.addAll(mappings);
	}

	public void clearMapping() {
		this.mappings.clear();
	}


	static Pattern patternDigits = Pattern.compile("(?<!\\\\)\\{(\\d+)\\}");
	static Pattern patternEmpty = Pattern.compile("(?<!\\\\)\\{\\}");

	public static String format(String s, Object... args) {
		if (s == null || s.isEmpty()) {
			if (args.length == 0) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(args[0]);
			for (int i = 1; i < args.length; i++) {
				sb.append(", ").append(args[i]);
			}
			return sb.toString();
		}
		BitSet bitSet = new BitSet();
		StringBuffer sb = new StringBuffer();
		Matcher matcher = patternDigits.matcher(s);
		while (matcher.find()) {
			int i = Integer.parseInt(matcher.group(1));
			bitSet.set(i);
			matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null));
		}
		matcher.appendTail(sb);
		s = sb.toString();

		if (s.contains("{}")) {
			sb.setLength(0);
			matcher = patternEmpty.matcher(s);
			int i = 0;
			while (matcher.find()) {
				while (bitSet.get(i)) {
					i++;
				}
				matcher.appendReplacement(sb, String.valueOf(i < args.length ? args[i] : null));
				bitSet.set(i);
			}
			matcher.appendTail(sb);
			s = sb.toString();
		} else {
			if (bitSet.cardinality() >= args.length) {
				return s;
			}
		}
		if (bitSet.cardinality() < args.length) {
			for (int i = 0; i < args.length; i++) {
				if (!bitSet.get(i)) {
					sb.append(", ").append(String.valueOf(args[i]));
				}
			}
			return sb.toString();
		}
		return s;
	}

	public static int copy(final File src, final File dest) throws IOException {
		try (FileInputStream in = new FileInputStream(src);
			 FileOutputStream out = new FileOutputStream(dest);) {
			int i = copy(in, out);
			out.flush();
			return i;
		}
	}

	public static int copy(final InputStream input, final OutputStream output) throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	@Getter
	@AllArgsConstructor
	static class PackageMapping {
		private String src;
		private String dest;
	}
}
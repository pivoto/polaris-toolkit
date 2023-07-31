package io.polaris.builder.changer;

import io.polaris.core.crypto.digest.Digests;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
public class Changer {
	private static final Logger log = LoggerFactory.getLogger("code.changer");
	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	static Pattern patternDigits = Pattern.compile("(?<!\\\\)\\{(\\d+)\\}");
	static Pattern patternEmpty = Pattern.compile("(?<!\\\\)\\{\\}");
	@Getter
	@Setter
	private Charset charset = Charset.defaultCharset();
	/**
	 * 来源根目录
	 */
	@Getter
	@Setter
	private File srcRoot;
	/**
	 * 目标根目录
	 */
	@Getter
	@Setter
	private File destRoot;
	/**
	 * 指定源代码相对路径
	 */
	private List<String> sourcePaths = new ArrayList<>();
	/**
	 * 需要处理文件的扩展名
	 */
	private List<String> extensions = new ArrayList<>();
	/**
	 * 需要处理文件的匹配模式
	 */
	private List<Pattern> namePatterns = new ArrayList<>();
	/**
	 * 映射关系
	 */
	private List<Mapping> mappings = new ArrayList<>();

	/**
	 * 是否复制未映射的文件
	 */
	@Getter
	@Setter
	private boolean copyAll = true;
	/**
	 * 是否同时处理符合映射关系的文件名
	 */
	@Getter
	@Setter
	private boolean includeFileName = true;

	public String format(String s, Object... args) {
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

	private int copy(final File src, final File dest) throws IOException, NoSuchAlgorithmException {
		if (dest.exists() && src.length() == dest.length()) {
			byte[] sha1s;
			try (FileInputStream in = new FileInputStream(src);) {
				sha1s = Digests.sha1(in);
			}
			byte[] sha1t;
			try (FileInputStream in = new FileInputStream(dest);) {
				sha1t = Digests.sha1(in);
			}
			if (Arrays.equals(sha1s, sha1t)) {
				// 完全一致时忽略
				return 0;
			}
		}
		log.info("[Copy-Of] {}", src.getPath());
		log.info("[Copy-To] {}", dest.getPath());
		try (FileInputStream in = new FileInputStream(src);
				 FileOutputStream out = new FileOutputStream(dest);) {
			int i = copy(in, out);
			out.flush();
			return i;
		}
	}

	private int copy(final InputStream input, final OutputStream output) throws IOException {
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

	public void execute() throws IOException, NoSuchAlgorithmException {
		check();

		if (sourcePaths.isEmpty()) {
			String srcPath = srcRoot.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
			String destPath = destRoot.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
			log.info("[Changing] {}", srcRoot.getPath());
			doChange(srcPath, destPath, srcRoot);
		} else {
			for (String sourcePath : sourcePaths) {
				sourcePath = sourcePath.replace("\\", "/");
				File from = new File(srcRoot, sourcePath);
				File to = new File(destRoot, sourcePath);
				String srcPath = from.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
				String destPath = to.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
				log.info("[Changing] {}", from.getPath());
				doChange(srcPath, destPath, from);
			}
		}
	}

	private boolean matches(String filePath, String fileName) {
		if (extensions.isEmpty() && namePatterns.isEmpty()) {
			return true;
		}
		for (String extension : extensions) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}
		for (Pattern namePattern : namePatterns) {
			if (namePattern.matcher(filePath + fileName).find()) {
				return true;
			}
		}
		return false;
	}

	private void doChange(String srcPath, String destPath, File from) throws IOException, NoSuchAlgorithmException {
		if (!from.isDirectory()) {
			return;
		}
		File[] files = from.listFiles();
		if (files == null) {
			return;
		}
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
					if (matches(filePath, fileName)) {
						doChange(file, newFile);
					} else {
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
		for (Mapping mapping : mappings) {
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
		if (includeFileName) {
			for (Mapping mapping : mappings) {
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

		for (Mapping mapping : mappings) {
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

	private void doChange(final File src, final File dest) throws IOException, NoSuchAlgorithmException {
		String lineSeparator = "\n";
		try (RandomAccessFile raf = new RandomAccessFile(src, "r");) {
			String line = raf.readLine();
			raf.seek(line.getBytes("ISO-8859-1").length);
			if (raf.getFilePointer() < raf.length()) {
				byte b = raf.readByte();
				if (b == 0x0A) {
					lineSeparator = "\n";
				} else if (b == 0x0D) {
					lineSeparator = "\r";
					//lineSeparator = "\r\n";
					if (raf.getFilePointer() < raf.length()) {
						b = raf.readByte();
						if (b == 0x0A) {
							lineSeparator = "\r\n";
						}
					}
				}
			}
		}
		if (dest.exists()) {
			// 存在时判断一致性
			StringBuilder sb = new StringBuilder();
			try (FileInputStream fis = new FileInputStream(src);
					 BufferedReader br = new BufferedReader(new InputStreamReader(fis, charset));) {
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					String s = doChangeForLine(line);
					sb.append(s).append(lineSeparator);
				}
			}
			String data = sb.toString();
			byte[] sha1 = Digests.sha1(data);
			byte[] sha1t;
			try (FileInputStream fis = new FileInputStream(dest);) {
				sha1t = Digests.sha1(fis);
			}
			if (Arrays.equals(sha1, sha1t)) {
				// 存在文件且完全一致，忽略
				return;
			}
			log.info("[Change-Of] {}", src.getPath());
			log.info("[Change-To] {}", dest.getPath());
			try (FileOutputStream fos = new FileOutputStream(dest);
					 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, charset));) {
				bw.write(data);
				bw.flush();
			}
		} else {
			// 不存在则写入
			log.info("[Change-Of] {}", src.getPath());
			log.info("[Change-To] {}", dest.getPath());
			try (FileInputStream fis = new FileInputStream(src);
					 FileOutputStream fos = new FileOutputStream(dest);
					 BufferedReader br = new BufferedReader(new InputStreamReader(fis, charset));
					 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, charset));) {
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					String s = doChangeForLine(line);
					bw.write(s);
					bw.write(lineSeparator);
				}
				bw.flush();
			}
		}
	}

	private String doChangeForLine(String line) {
		String s = line;
		for (Mapping mapping : mappings) {
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

	public void addNamePatterns(Pattern... patterns) {
		for (Pattern pattern : patterns) {
			this.namePatterns.add(pattern);
		}
	}

	public void addMapping(String src, String dest) {
		this.mappings.add(new Mapping(src, dest));
	}

	public void addMapping(Mapping... mappings) {
		for (Mapping mapping : mappings) {
			this.mappings.add(mapping);
		}
	}

	public void addMapping(Collection<Mapping> mappings) {
		this.mappings.addAll(mappings);
	}

	public void clearMapping() {
		this.mappings.clear();
	}

	@Getter
	@AllArgsConstructor
	static class Mapping {
		private String src;
		private String dest;
	}
}

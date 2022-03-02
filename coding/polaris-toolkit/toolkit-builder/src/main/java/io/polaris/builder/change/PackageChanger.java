package io.polaris.builder.change;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Qt
 * @version Mar 02, 2022
 * @since 1.8
 */
public class PackageChanger {
	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private File srcDir;
	private File destDir;
	private String srcPath;
	private String destPath;
	private boolean copyForcedly = false;
	private Charset charset = Charset.defaultCharset();
	private List<PackageChangeMapping> packageChangeMappings = new ArrayList<PackageChangeMapping>();
	private FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			return file.getName().endsWith(".java")
					|| file.getName().endsWith(".properties")
					|| file.getName().endsWith(".xml")
					|| file.getName().endsWith(".yml")
					|| file.getName().endsWith(".yaml")
					|| file.getName().endsWith(".json");
		}
	};

	public PackageChanger(File srcDir, File destDir) {
		super();
		this.srcDir = srcDir;
		this.destDir = destDir;
		if (!this.destDir.exists()) {
			this.destDir.mkdirs();
		}
		srcPath = srcDir.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
		destPath = destDir.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
	}

	public static void close(final Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	public static int copy(final File src, final File dest) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
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

	public boolean addPackageChangeMapping(final PackageChangeMapping e) {
		return packageChangeMappings.add(e);
	}

	public boolean addPackageChangeMappings(final Collection<? extends PackageChangeMapping> c) {
		return packageChangeMappings.addAll(c);
	}

	public void clear() {
		packageChangeMappings.clear();
	}

	public void doChange() throws IOException {
		doLog("[Begin]: " + srcDir.getPath());
		doChange(srcDir);
		doLog("[End]: " + srcDir.getPath());
	}

	public List<PackageChangeMapping> getPackageChangeMappings() {
		return packageChangeMappings;
	}

	public void doLog(final String s) {
		System.out.println(s);
	}

	private void doChange(final File fromDir) throws IOException {
		doLog("[Changing]: " + fromDir.getPath());
		File[] files = fromDir.listFiles();
		for (File file : files) {
			File newFile = getNewFile(file);
			if (newFile != null) {
				if (file.isDirectory()) {
					doChange(file);// 递归子目录
				} else {
					newFile.getParentFile().mkdirs();
					if (fileFilter.accept(file)) {// 需要改变包名
						doLog("[Change]: " + file.getPath());
						doChange(file, newFile);
					} else {// 直接复制
						doLog("[Copy]:" + file.getPath());
						copy(file, newFile);
					}
				}
			}
		}
	}

	private void doChange(final File src, final File dest) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			br = new BufferedReader(new InputStreamReader(in, charset));
			bw = new BufferedWriter(new OutputStreamWriter(out, charset));
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				String s = doChangeForLine(line);
				bw.write(s);
				bw.newLine();
			}
			bw.flush();
		} finally {
			close(bw);
			close(br);
			close(out);
			close(in);
		}
	}

	private String doChangeForLine(final String line) {
		String s = line;
		for (PackageChangeMapping entry : packageChangeMappings) {
			s = s.replaceAll("\\b" + entry.getSrcPackage() + "\\b", entry.getDestPackage());
		}
		return s;
	}

	private File getNewFile(final File file) {
		String filePath = file.getAbsolutePath().replace("\\", "/").replaceFirst("/$", "");
		if (filePath.length() <= srcPath.length() + 1 || !filePath.startsWith(srcPath)) {
			throw new IllegalArgumentException("path:" + filePath);
		}
		filePath = filePath.substring(srcPath.length() + 1);
		boolean maybe = false;// maybe exists sub dirs
		for (PackageChangeMapping entry : packageChangeMappings) {
			String srcPackage = entry.getSrcPackage().replace(".", "/");
			String destPackage = entry.getDestPackage().replace(".", "/");
			if (filePath.equals(srcPackage)) {// equal
				return new File(destPath + "/" + destPackage);
			} else if (filePath.startsWith(srcPackage)) {// match
				return new File(destPath + "/" + destPackage
						+ filePath.substring(srcPackage.length()));
			}
			if (!maybe) {
				maybe = file.isDirectory() && srcPackage.startsWith(filePath);
			}
		}
		if (maybe || copyForcedly) {
			return new File(destPath + "/" + filePath);
		} else {
			return null;
		}
	}

	public boolean isCopyForcedly() {
		return copyForcedly;
	}

	public void setCopyForcedly(boolean copyForcedly) {
		this.copyForcedly = copyForcedly;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public void setPackageChangeMappings(List<PackageChangeMapping> packageChangeMappings) {
		this.packageChangeMappings = packageChangeMappings;
	}

}

package io.polaris.changer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author Qt
 * @version Mar 02, 2022
 * @since 1.8
 */
public class PackageChangerRunner {
	public static void main(String[] args) throws IOException, DocumentException {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		File file = new File(args[0]);
		change(file);
	}

	public static void change(File file) throws DocumentException, IOException {
		try (FileInputStream in = new FileInputStream(file);) {
			change(in);
		}
	}

	public static void change(InputStream in) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(in);
		Element root = doc.getRootElement();
		List<Element> changes = root.elements("change");

		Charset charset;
		try {
			charset = Charset.forName(root.elementTextTrim("charset"));
		} catch (Exception e) {
			charset = Charset.defaultCharset();
		}

		for (Element change : changes) {
			PackageChanger pc = new PackageChanger();
			pc.setCharset(charset);

			String charsetName = change.elementTextTrim("charset");
			if (charsetName != null && charsetName.length() > 0) {
				pc.setCharset(Charset.forName(charsetName));
			}

			pc.setSrcRoot(new File(change.elementTextTrim("src")));
			pc.setDestRoot(new File(change.elementTextTrim("dest")));

			String copyALl = change.elementTextTrim("copy-all");
			if (copyALl != null && copyALl.length() > 0) {
				pc.setCopyAll(Boolean.valueOf(copyALl));
			}
			String mappingFileName = change.elementTextTrim("mapping-file-name");
			if (mappingFileName != null && mappingFileName.length() > 0) {
				pc.setMappingFileName(Boolean.valueOf(mappingFileName));
			}

			String extensions = change.elementTextTrim("extensions");
			if (extensions != null && extensions.length() > 0) {
				String[] arr = extensions.split("[,;|\\s]+");
				for (String s : arr) {
					if (s.trim().length() > 0) {
						pc.addExtension(s.trim());
					}
				}
			}

			List<Element> sourcePathList = change.elements("source-path");
			for (Element element : sourcePathList) {
				String path = element.getTextTrim();
				if (path.trim().length() > 0) {
					pc.addSourcePath(path.trim());
				}
			}

			List<Element> packageList = change.elements("package");
			for (Element element : packageList) {
				pc.addMapping(element.attributeValue("name")
						, element.attributeValue("mapping"));
			}

			// execute
			pc.execute();
		}

	}

}

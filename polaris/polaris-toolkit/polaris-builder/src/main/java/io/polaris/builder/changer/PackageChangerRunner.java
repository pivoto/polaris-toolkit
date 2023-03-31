package io.polaris.builder.changer;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Qt
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


	private static ChangerDto parse(Element element) {
		ChangerDto dto = new ChangerDto();
		String charset = element.elementTextTrim("charset");
		if (StringUtils.isNotBlank(charset)) {
			dto.setCharset(Charset.forName(charset));
		}
		String src = element.elementTextTrim("src");
		if (StringUtils.isNotBlank(src)) {
			dto.setSrc(src);
		}
		String dest = element.elementTextTrim("dest");
		if (StringUtils.isNotBlank(dest)) {
			dto.setDest(dest);
		}
		String copyALl = element.elementTextTrim("copy-all");
		if (copyALl != null && copyALl.length() > 0) {
			dto.setCopyAll(Boolean.valueOf(copyALl));
		}
		String includeFileName = element.elementTextTrim("include-filename");
		if (includeFileName != null && includeFileName.length() > 0) {
			dto.setIncludeFilename(Boolean.valueOf(includeFileName));
		}
		String extensions = element.elementTextTrim("extensions");
		if (extensions != null && extensions.length() > 0) {
			dto.setExtensions(extensions);
		}
		List<Element> namePatterns = element.elements("name-pattern");
		for (Element e : namePatterns) {
			String s = e.getTextTrim();
			if (StringUtils.isBlank(s)) {
				continue;
			}
			if (dto.getNamePatterns() == null) {
				dto.setNamePatterns(new LinkedHashSet<>());
			}
			dto.getNamePatterns().add(s);
		}
		List<Element> sourcePathList = element.elements("source-path");
		for (Element e : sourcePathList) {
			String s = e.getTextTrim();
			if (StringUtils.isBlank(s)) {
				continue;
			}
			if (dto.getSourcePaths() == null) {
				dto.setSourcePaths(new LinkedHashSet<>());
			}
			dto.getSourcePaths().add(s);
		}
		List<Element> packageList = element.elements("package");
		for (Element e : packageList) {
			String name = e.attributeValue("name");
			String mapping = e.attributeValue("mapping");
			if (StringUtils.isBlank(name) || StringUtils.isBlank(mapping)) {
				continue;
			}
			if (dto.getPackageMapping() == null) {
				dto.setPackageMapping(new LinkedHashMap<>());
			}
			dto.getPackageMapping().put(name, mapping);
		}
		return dto;
	}


	public static void change(InputStream in) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(in);
		Element root = doc.getRootElement();
		ChangerDto parent = parse(root);
		List<Element> changes = root.elements("change");
		for (Element change : changes) {
			ChangerDto dto = parse(change);
			dto.mergeFrom(parent);

			PackageChanger pc = new PackageChanger();
			pc.setCharset(dto.getCharset());
			pc.setSrcRoot(new File(dto.getSrc()));
			pc.setDestRoot(new File(dto.getDest()));
			if (dto.getCopyAll() != null) {
				pc.setCopyAll(dto.getCopyAll());
			}
			if (dto.getIncludeFilename() != null) {
				pc.setIncludeFileName(dto.getIncludeFilename());
			}
			String extensions = dto.getExtensions();
			if (extensions != null && extensions.length() > 0) {
				String[] arr = extensions.split("[,;|\\s]+");
				for (String s : arr) {
					if (s.trim().length() > 0) {
						pc.addExtension(s.trim());
					}
				}
			}
			Set<String> namePatterns = dto.getNamePatterns();
			if (namePatterns != null) {
				for (String namePattern : namePatterns) {
					if (namePattern.trim().length() > 0) {
						pc.addNamePatterns(Pattern.compile(namePattern.trim()));
					}
				}
			}
			Set<String> sourcePaths = dto.getSourcePaths();
			if (sourcePaths != null) {
				for (String path : sourcePaths) {
					if (path.trim().length() > 0) {
						pc.addSourcePath(path.trim());
					}
				}
			}
			Map<String, String> packageMapping = dto.getPackageMapping();
			if (packageMapping != null) {
				packageMapping.forEach((k, v) -> pc.addMapping(k, v));
			}

			// execute
			pc.execute();
		}

	}


}

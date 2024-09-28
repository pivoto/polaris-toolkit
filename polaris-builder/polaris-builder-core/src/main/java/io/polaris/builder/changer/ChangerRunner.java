package io.polaris.builder.changer;

import lombok.extern.slf4j.Slf4j;
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
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Qt
 * @since 1.8
 */
@Slf4j
public class ChangerRunner {
	public static void main(String[] args) throws IOException, DocumentException, NoSuchAlgorithmException {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		File file = new File(args[0]);
		change(file);
	}

	public static void change(File file) throws DocumentException, IOException, NoSuchAlgorithmException {
		try (FileInputStream in = new FileInputStream(file);) {
			change(in);
		}
	}


	public static ChangerDto parse(Element element) {
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
		String copyAll = element.elementTextTrim("copy-all");
		if (copyAll != null && !copyAll.isEmpty()) {
			dto.setCopyAll(Boolean.valueOf(copyAll));
		}
		String includeFileName = element.elementTextTrim("include-filename");
		if (includeFileName != null && !includeFileName.isEmpty()) {
			dto.setIncludeFilename(Boolean.valueOf(includeFileName));
		}
		String extensions = element.elementTextTrim("extensions");
		if (extensions != null && !extensions.isEmpty()) {
			dto.setExtensions(extensions);
		}
		{
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
		}
		{
			List<Element> ignorePatterns = element.elements("ignore-pattern");
			for (Element e : ignorePatterns) {
				String s = e.getTextTrim();
				if (StringUtils.isBlank(s)) {
					continue;
				}
				if (dto.getIgnorePatterns() == null) {
					dto.setIgnorePatterns(new LinkedHashSet<>());
				}
				dto.getIgnorePatterns().add(s);
			}
		}
		{
			List<Element> ignoreMappingPatterns = element.elements("ignore-mapping-pattern");
			for (Element e : ignoreMappingPatterns) {
				String s = e.getTextTrim();
				if (StringUtils.isBlank(s)) {
					continue;
				}
				if (dto.getIgnoreMappingPatterns() == null) {
					dto.setIgnoreMappingPatterns(new LinkedHashSet<>());
				}
				dto.getIgnoreMappingPatterns().add(s);
			}
		}
		{
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
		}
		{
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
		}
		return dto;
	}


	public static void change(InputStream in) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(in);
		Element root = doc.getRootElement();
		ChangerDto parent = parse(root);
		List<Element> changes = root.elements("change");
		for (Element change : changes) {
			try {
				ChangerDto dto = parse(change);
				dto.mergeFrom(parent);

				change(dto);
			} catch (Exception e) {
				log.error("", e);
			}
		}

	}

	public static void change(ChangerDto dto) throws IOException, NoSuchAlgorithmException {
		Changer pc = new Changer();
		pc.setCharset(dto.getCharset());
		pc.setSrcRoot(new File(dto.getSrc()));
		pc.setDestRoot(new File(dto.getDest()));
		if (dto.getCopyAll() != null) {
			pc.setCopyAll(dto.getCopyAll());
		}
		if (dto.getIncludeFilename() != null) {
			pc.setIncludeFileName(dto.getIncludeFilename());
		}
		{
			String extensions = dto.getExtensions();
			if (extensions != null && !extensions.isEmpty()) {
				String[] arr = extensions.split("[,;|\\s]+");
				for (String s : arr) {
					if (!s.trim().isEmpty()) {
						pc.addExtension(s.trim());
					}
				}
			}
		}
		{
			Set<String> namePatterns = dto.getNamePatterns();
			if (namePatterns != null) {
				for (String namePattern : namePatterns) {
					if (!namePattern.trim().isEmpty()) {
						pc.addNamePatterns(Pattern.compile(namePattern.trim()));
					}
				}
			}
		}
		{
			Set<String> ignoreMappingPatterns = dto.getIgnoreMappingPatterns();
			if (ignoreMappingPatterns != null) {
				for (String ignoreMappingPattern : ignoreMappingPatterns) {
					if (!ignoreMappingPattern.trim().isEmpty()) {
						pc.addIgnoreMappingPatterns(Pattern.compile(ignoreMappingPattern.trim()));
					}
				}
			}
		}
		{
			Set<String> ignorePatterns = dto.getIgnorePatterns();
			if (ignorePatterns != null) {
				for (String ignorePattern : ignorePatterns) {
					if (!ignorePattern.trim().isEmpty()) {
						pc.addIgnorePatterns(Pattern.compile(ignorePattern.trim()));
					}
				}
			}
		}
		{
			Set<String> sourcePaths = dto.getSourcePaths();
			if (sourcePaths != null) {
				for (String path : sourcePaths) {
					if (!path.trim().isEmpty()) {
						pc.addSourcePath(path.trim());
					}
				}
			}
		}
		{
			Map<String, String> packageMapping = dto.getPackageMapping();
			if (packageMapping != null) {
				packageMapping.forEach(pc::addMapping);
			}
		}

		// execute
		pc.execute();
	}


}

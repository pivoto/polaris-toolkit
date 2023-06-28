package io.polaris.core.xml;

import io.polaris.core.collection.Iterables;
import io.polaris.core.map.FluentMap;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlTest {

	@Test
	public void parseTest() throws IOException {
		String result = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"//
			+ "<returnsms>"//
			+ "<returnstatus>Success</returnstatus>"//
			+ "<message>ok</message>"//
			+ "<remainpoint>1490</remainpoint>"//
			+ "<taskID>885</taskID>"//
			+ "<successCounts>1</successCounts>"//
			+ "</returnsms>";
		Document docResult = Xml.parseXml(result);
		String elementText = Xml.elementText(docResult.getDocumentElement(), "returnstatus");
		assertEquals("Success", elementText);
	}

	@Test
	public void writeTest() throws IOException {
		String result = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"//
			+ "<returnsms>"//
			+ "<returnstatus>Success（成功）</returnstatus>"//
			+ "<message>ok</message>"//
			+ "<remainpoint>1490</remainpoint>"//
			+ "<taskID>885</taskID>"//
			+ "<successCounts>1</successCounts>"//
			+ "</returnsms>";
		Document docResult = Xml.parseXml(result);
		System.out.println(Xml.toStr(docResult));
	}

	@Test
	public void xpathTest() throws IOException {
		String result = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"//
			+ "<returnsms>"//
			+ "<returnstatus>Success（成功）</returnstatus>"//
			+ "<message>ok</message>"//
			+ "<remainpoint>1490</remainpoint>"//
			+ "<taskID>885</taskID>"//
			+ "<successCounts>1</successCounts>"//
			+ "</returnsms>";
		Document docResult = Xml.parseXml(result);
		Object value = Xml.getByXPath("//returnsms/message", docResult, XPathConstants.STRING);
		assertEquals("ok", value);
	}

	@Test
	public void xmlToMapTest() throws IOException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"//
			+ "<returnsms>"//
			+ "<returnstatus>Success</returnstatus>"//
			+ "<message>ok</message>"//
			+ "<remainpoint>1490</remainpoint>"//
			+ "<taskID>885</taskID>"//
			+ "<successCounts>1</successCounts>"//
			+ "<newNode><sub>subText</sub></newNode>"//
			+ "</returnsms>";
		Map<String, Object> map = Xml.xmlToMap(xml);
		System.out.println(map);
	}

	@Test
	public void mapToXmlTest() throws IOException {
		Map<String, Object> map = FluentMap.of(new LinkedHashMap<String, Object>())//
			.put("name", "张三")//
			.put("age", 12)//
			.put("game", FluentMap.of(new LinkedHashMap<>()).put("昵称", "Looly").put("level", 14).get())//
			.put("Town", Iterables.asList("town1", "town2"))
			.get();
		Document doc = Xml.mapToXml(map, "user");
		System.out.println(Xml.toStr(doc, true));
	}


}

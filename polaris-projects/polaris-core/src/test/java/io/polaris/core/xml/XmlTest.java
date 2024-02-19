package io.polaris.core.xml;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.xpath.XPathConstants;

import io.polaris.core.TestConsole;
import io.polaris.core.collection.Iterables;
import io.polaris.core.map.FluentMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

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
		TestConsole.println(Xml.toStr(docResult));
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
			+ "<list>" +
			"<task id=\"test0\">0</task>" +
			"<task id=\"test1\">1</task>" +
			"<task id=\"test2\">1</task>" +
			"</list>" +
			"</returnsms>";
		Document docResult = Xml.parseXml(result);
		Object value = Xml.getByXPath("//returnsms/message", docResult, XPathConstants.STRING);
		assertEquals("ok", value);
		TestConsole.println(Xml.getByXPath("//returnsms/list/task[@id='test1']", docResult, XPathConstants.STRING));
		Assertions.assertEquals("1", Xml.getByXPath("//returnsms/list/task[@id='test1']", docResult, XPathConstants.STRING));
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
		TestConsole.println(map);
		Assertions.assertEquals("ok", map.get("message"));
		Assertions.assertEquals("1", map.get("successCounts"));
		Assertions.assertInstanceOf(Map.class, map.get("newNode"));
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
		TestConsole.println(Xml.toStr(doc, true));
	}


}

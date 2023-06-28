package io.polaris.core.xml;

import io.polaris.core.io.IO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * JAXB（Java Architecture for XML Binding），根据XML Schema 实现xml和Bean互转。
 *
 * @author Qt
 * @since 1.8
 */
public class Jaxb {

	public static String toXml(Object bean) {
		return toXml(bean, StandardCharsets.UTF_8, true);
	}

	public static String toXml(Object bean, Charset charset, boolean format) {
		StringWriter writer;
		try {
			JAXBContext context = JAXBContext.newInstance(bean.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, format);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, charset.name());
			writer = new StringWriter();
			marshaller.marshal(bean, writer);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}

	public static <T> T toBean(String xml, Class<T> c) {
		return toBean(new StringReader(xml), c);
	}

	public static <T> T toBean(File file, Charset charset, Class<T> c) throws IOException {
		return toBean(new BufferedReader(new InputStreamReader(new FileInputStream(file), charset)), c);
	}

	@SuppressWarnings("unchecked")
	public static <T> T toBean(Reader reader, Class<T> c) {
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} finally {
			IO.close(reader);
		}
	}
}

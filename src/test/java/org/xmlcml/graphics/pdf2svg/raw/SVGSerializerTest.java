package org.xmlcml.graphics.pdf2svg.raw;

import java.io.FileOutputStream;

import nu.xom.Document;
import nu.xom.Element;

import org.junit.Test;

public class SVGSerializerTest {

	@Test
	public void testSerializer() {
		Element element = new Element("myString");
		Document doc = new Document(element);
		String content = "char 945 is "+(char)945+" i.e. alpha ";
		System.out.println(content);
		element.appendChild(content);
		
		try {
			FileOutputStream os = new FileOutputStream("test.svg");
			SVGSerializer serializer = new SVGSerializer(os);
			serializer.write(doc);
		} catch (Exception e) {
			throw new RuntimeException("cannot serialize ", e);
		}
	}
}

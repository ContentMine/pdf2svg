package org.xmlcml.pdf2svg;

import java.io.File;
import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.pdf2svg.log.XMLLogger;

public class XMLLoggerTest {

	// private final static Logger LOG = Logger.getLogger(XMLLogger.class);
	private static final String page6pdf = "src/test/resources/page6.pdf";

	@Test
	public void testWriteXMLFile() {
		XMLLogger xmlLogger = new XMLLogger();
		xmlLogger.newPDFFile("testfile.pdf", 3);
		xmlLogger.newPDFPage(1);
		// xmlLogger.newCharacter("testFont1", "testChar1", "testValue1");
		xmlLogger.newPDFPage(2);
		xmlLogger.newPDFPage(3);
		xmlLogger.writeXMLFile("target", "testfile.pdf");

		File file = new File("target/testfile-log.xml");
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testRun() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-logger", "-outdir", "target", page6pdf);

		File file = new File("target/pdfLog.xml");
		Assert.assertTrue(file.exists());

		Document doc = null;
		try {
			doc = new Builder().build(file);
		} catch (ParsingException ex) {
			System.err
					.println("caught Parsing Exception while reading XML log file!");
		} catch (IOException ex) {
			System.err
					.println("caught I/O Exception while reading XML log file!");
		}

		if (doc != null) {
			Element root = doc.getRootElement();
			Assert.assertEquals("pdfLog", root.getQualifiedName());

			Elements fontlists = root.getChildElements("fontList");
			Assert.assertEquals(1, fontlists.size());

			Element fontlist = fontlists.get(0);
			Assert.assertEquals(21, fontlist.getChildCount());
			Assert.assertEquals(10, fontlist.getChildElements("font").size());

			Elements pdfs = root.getChildElements("pdf");
			Assert.assertEquals(1, pdfs.size());

			Element pdf = pdfs.get(0);
			Assert.assertEquals(2, pdf.getAttributeCount());

			Assert.assertEquals(new File(page6pdf).getAbsolutePath(),
					pdf.getAttributeValue("filename"));
			Assert.assertEquals(1,
					Integer.parseInt(pdf.getAttributeValue("pageCount")));
		}
	}
}

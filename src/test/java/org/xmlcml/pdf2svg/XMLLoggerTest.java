package org.xmlcml.pdf2svg;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.pdf2svg.util.XMLLogger;

public class XMLLoggerTest {

	private final static Logger LOG = Logger.getLogger(XMLLogger.class);

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
		converter.run("-logger", "-outdir", "target", "src/test/resources/page6.pdf");

		File file = new File("target/pdfLog.xml");
		Assert.assertTrue(file.exists());
	}
}

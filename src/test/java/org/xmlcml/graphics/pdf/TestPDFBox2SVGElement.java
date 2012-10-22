package org.xmlcml.graphics.pdf;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;

public class TestPDFBox2SVGElement {

	
	private static final String PDF_BASE = "org/xmlcml/graphics/pdf";

	@Test
	public void testMini() {
		Element element = parseElement("test4.mini.svg");
		Nodes paths = element.query("./*[local-name()='g']/*[local-name()='path']");
		Assert.assertEquals("paths", 71, paths.size());
	}

	@Test
	// this also helps to build the fo
	public void testGetSigs() {
		Element element = parseElement("test4.mini.svg");
		Nodes paths = element.query("./*[local-name()='g']/*[local-name()='path']");
		for (int i = 0; i < paths.size(); i++) {
			SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG((Element) paths.get(i));
			svgPath.normalizeOrigin();
			System.out.println(">> "+svgPath.toXML());
			try {
				String sig = svgPath.getSignature();
				System.out.println("> "+sig);
			} catch (Exception e) {
				System.out.println("ERROR "+e);
			}
		}
	}

	private Element parseElement(String filename) {
		Element element = null;
		try {
			element = new Builder().build(this.getClass().getClassLoader().getResourceAsStream(PDF_BASE+"/"+filename)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("cannot parse: "+filename, e);
		}
		return element;
	}
}

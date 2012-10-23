package org.xmlcml.graphics.pdf2svg.raw;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.graphics.svg.SVGElement;

public class PDF2SVGUtil {

	public final static String SVGX_NS = "http://www.xml-cml.org/schema/svgx";
	public final static String SVGX_PREFIX = "svgx";
	public static final String CHARACTER_WIDTH = "width";
	
	public static void setSVGXAttribute(SVGElement svgElement, String attName, String value) {
		Attribute attribute = new Attribute(SVGX_PREFIX+CMLConstants.S_COLON+attName, SVGX_NS, value);
		svgElement.addAttribute(attribute);
//		svgElement.addNamespaceDeclaration(CMLConstants.CMLX_PREFIX, SVGX_NS);
	}

}

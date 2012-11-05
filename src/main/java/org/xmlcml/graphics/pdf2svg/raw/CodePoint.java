package org.xmlcml.graphics.pdf2svg.raw;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

public class CodePoint {



	private final static Logger LOG = Logger.getLogger(CodePoint.class);
	
	// XML
	private static final String CODE_POINT = "codePoint";
	private static final String DECIMAL = "decimal";
	private static final String NAME = "name";
	private static final String NOTE = "note";
	private static final String REPLACE_BY_UNICODE = "replaceByUnicode";
	private static final String REPLACE_NAME = "replaceName";
	private static final String UNICODE = "unicode";
	
	private static final String HEX_PREFIX = "0X";
	private static final String UNICODE_PREFIX = "U+";

	private Integer decimal;
	private String name;
	private String note;
	private String replacementUnicode;
	private String replaceName;
	private String unicode; // always uppercase
	
	public CodePoint() {
		
	}

	public static CodePoint createFromElement(Element codePointElement) {
		CodePoint codePoint = null;
		try {
			codePoint = new CodePoint();
			if (!(CODE_POINT.equals(codePointElement.getLocalName()))) {
				throw new RuntimeException("CodePointSet children must be <codePoint>");
			}
			String decimalS = codePointElement.getAttributeValue(DECIMAL);
			if (decimalS == null) {
				throw new RuntimeException("<codePoint> must have decimal attribute");
			}
			codePoint.decimal = new Integer(decimalS); 
			codePoint.unicode = codePointElement.getAttributeValue(UNICODE);
			if (codePoint.unicode == null || !codePoint.unicode.startsWith(UNICODE_PREFIX)) {
				throw new RuntimeException("CodePointSet children must be <codePoint>");
			}
			codePoint.unicode = codePoint.unicode.toUpperCase();
			String hex = HEX_PREFIX+codePoint.unicode.substring(2);
			Integer codePointHex = Integer.decode(hex);
			if (!codePointHex.equals(codePoint.decimal)) {
				throw new RuntimeException(
						"<codePoint> integer ("+codePoint.decimal+") and unicode ("+codePoint.unicode+") values do not match; try: "+Integer.toHexString(codePoint.decimal));
			}
			codePoint.name = codePointElement.getAttributeValue(UNICODE);
			codePoint.replacementUnicode = codePointElement.getAttributeValue(REPLACE_BY_UNICODE);
			codePoint.replaceName = codePointElement.getAttributeValue(REPLACE_NAME);
			codePoint.note = codePointElement.getAttributeValue(NOTE);
		} catch (Exception e) {
			throw new RuntimeException("invalid codePointElement: "+((codePointElement == null) ? null : codePointElement.toXML()), e);
		}
		return codePoint;
	}

	public Integer getDecimal() {
		return decimal;
	}

	public String getUnicode() {
		return unicode;
	}

	public Element getElement() {
		Element codePointElement = new Element(CODE_POINT);
		if (decimal == null || unicode == null) {
			throw new RuntimeException("decimal and unicode must not be null");
		}
		codePointElement.addAttribute(new Attribute(DECIMAL, ""+decimal));
		codePointElement.addAttribute(new Attribute(UNICODE, unicode));
		if (name != null) {
			codePointElement.addAttribute(new Attribute(NAME, name));
		}
		if (note != null) {
			codePointElement.addAttribute(new Attribute(NOTE, note));
		}
		if (replaceName != null) {
			codePointElement.addAttribute(new Attribute(REPLACE_NAME, replaceName));
		}
		if (replacementUnicode != null) {
			codePointElement.addAttribute(new Attribute(REPLACE_BY_UNICODE, replacementUnicode));
		}
		return codePointElement;
	}

	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
		String hex = Integer.toHexString(decimal).toUpperCase();
		if (hex.startsWith(HEX_PREFIX)) {
			hex = hex.substring(HEX_PREFIX.length());
		}
		this.unicode = UNICODE_PREFIX+hex;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}

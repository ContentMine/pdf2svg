/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.pdf2svg;

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

	public static CodePoint createFromElement(Element codePointElement, String encoding) {
		CodePoint codePoint = null;
		try {
			codePoint = new CodePoint();
			if (!(CODE_POINT.equals(codePointElement.getLocalName()))) {
				throw new RuntimeException("CodePointSet children must be <codePoint>");
			}
			String decimalS = codePointElement.getAttributeValue(DECIMAL);
			codePoint.name = codePointElement.getAttributeValue(NAME);
			if (decimalS == null && codePoint.name == null) {
				throw new RuntimeException("<codePoint> must have decimal attribute and/or name");
			}
			if (decimalS != null) {
				codePoint.decimal = new Integer(decimalS); 
			}
			codePoint.unicode = codePointElement.getAttributeValue(UNICODE);
			if (codePoint.unicode == null || !codePoint.unicode.startsWith(UNICODE_PREFIX)) {
				throw new RuntimeException("missing or invalid unicode value in: "+codePointElement.toXML());
			}
			codePoint.unicode = codePoint.unicode.toUpperCase();
			checkUnicodeMatchesDecimal(encoding, codePoint);
			codePoint.replacementUnicode = codePointElement.getAttributeValue(REPLACE_BY_UNICODE);
			codePoint.replaceName = codePointElement.getAttributeValue(REPLACE_NAME);
			codePoint.note = codePointElement.getAttributeValue(NOTE);
		} catch (Exception e) {
			throw new RuntimeException("invalid codePointElement: "+((codePointElement == null) ? null : codePointElement.toXML()), e);
		}
		return codePoint;
	}

	private static void checkUnicodeMatchesDecimal(String encoding, CodePoint codePoint) {
		String hex = HEX_PREFIX+codePoint.unicode.substring(2);
		Integer codePointHex = Integer.decode(hex);
		if (CodePointSet.UNICODE.equals(encoding) && (codePoint.decimal != null && !codePointHex.equals(codePoint.decimal))) {
			throw new RuntimeException(
					"<codePoint> integer ("+codePoint.decimal+") and unicode ("+codePoint.unicode+") values do not match; try: "+Integer.toHexString(codePoint.decimal));
		}
	}

	public Integer getDecimal() {
		return decimal;
	}

	public String getUnicode() {
		return unicode;
	}

	public String getName() {
		return name;
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
	
	public static Integer getDecimal(String unicode) {
		Integer codepoint = null;
		if (unicode != null && unicode.startsWith(UNICODE_PREFIX)) {
			String hex = HEX_PREFIX+unicode.substring(UNICODE_PREFIX.length());
			try {
				codepoint = Integer.decode(hex);
			} catch (Exception e) {
				throw new RuntimeException("Bad hex: "+hex);
			}
		}
		return codepoint;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setUnicode(String unicode) {
		this.unicode = unicode;
	}
	
}

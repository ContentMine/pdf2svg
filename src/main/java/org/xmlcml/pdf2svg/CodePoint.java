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

public class CodePoint extends Element {

	private final static Logger LOG = Logger.getLogger(CodePoint.class);
	
	// XML
	static final String TAG = "codePoint";
	
	private static final String DECIMAL = "decimal";
	private static final String NAME = "name";
	private static final String NOTE = "note";
	private static final String REPLACE_BY_UNICODE = "replaceByUnicode";
	private static final String REPLACE_NAME = "replaceName";
	private static final String UNICODE = "unicode";
	private static final String UNICODE_NAME = "unicodeName";
	
	private Integer nonUnicodeDecimal; // may or may not be the decimal equivalent of unicode    
	private String  name;               // a mnemonic (origin unspecified , ?Adobe, ?HTML-ent
	private String  note;               // some explanatory or other note
	private UnicodePoint unicodePoint;
	
	public CodePoint() {
		super(TAG);
	}

	/** codePoint when we don't know the Unicode
	 * will create an UNKNOWN unicode
	 * @param charCode
	 * @param charname
	 */
	public CodePoint(Integer charCode, String charname) {
		this();
		this.nonUnicodeDecimal = charCode;
		this.name = charname;
		this.unicodePoint = UnicodePoint.UNKNOWN;
	}

	public static CodePoint createFromElement(Element codePointElement, String encoding) {
		CodePoint codePoint = null;
		try {
			codePoint = new CodePoint();
			if (!(TAG.equals(codePointElement.getLocalName()))) {
				throw new RuntimeException("CodePointSet children must be <codePoint>");
			}
			String decimalS = codePointElement.getAttributeValue(DECIMAL);
			codePoint.name = codePointElement.getAttributeValue(NAME);
			if (decimalS == null && codePoint.name == null) {
				throw new RuntimeException("<codePoint> must have decimal attribute and/or name");
			}
			if (decimalS != null) {
				codePoint.nonUnicodeDecimal = new Integer(decimalS); 
			}
			codePoint.unicodePoint = UnicodePoint.createUnicodeValue(codePointElement.getAttributeValue(UNICODE));
			if (codePoint.unicodePoint == null) {
				throw new RuntimeException("missing or invalid unicode value in: "+codePointElement.toXML());
				
			}
			codePoint.unicodePoint.setUnicodeName(codePointElement.getAttributeValue(UNICODE_NAME));
			codePoint.unicodePoint.addReplacmentPoints(codePointElement.getAttributeValue(REPLACE_BY_UNICODE));
			codePoint.note = codePointElement.getAttributeValue(NOTE);
		} catch (Exception e) {
			throw new RuntimeException("invalid codePointElement: "+((codePointElement == null) ? null : codePointElement.toXML()), e);
		}
		LOG.trace("Created "+codePoint);
		return codePoint;
	}

	public Element createElement() {
		Element codePointElement = new Element(TAG);
		if (unicodePoint == null) {
			throw new RuntimeException("unicode must not be null");
		}
		codePointElement.addAttribute(new Attribute(UNICODE, unicodePoint.getUnicodeValue()));
		if (nonUnicodeDecimal == null && name == null) {
			throw new RuntimeException("decimal and name must not both be null");
		}
		if (nonUnicodeDecimal != null) {
			codePointElement.addAttribute(new Attribute(DECIMAL, ""+nonUnicodeDecimal));
		}
		if (name != null) {
			codePointElement.addAttribute(new Attribute(NAME, name));
		}
		if (note != null) {
			codePointElement.addAttribute(new Attribute(NOTE, note));
		}
		if (unicodePoint.getUnicodeName() != null) {
			codePointElement.addAttribute(new Attribute(UNICODE_NAME, unicodePoint.getUnicodeName()));
		}
		String replacementPointString = unicodePoint.getReplacementPointString();
		if (replacementPointString != null) {
			codePointElement.addAttribute(new Attribute(REPLACE_BY_UNICODE, replacementPointString));
		}
		return codePointElement;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public Integer getDecimal() {
		return nonUnicodeDecimal;
	}

	public Integer getUnicodeDecimal() {
		return unicodePoint.getDecimalValue();
	}

	public String toString() {
		return "\n"+
		"decimal: "+nonUnicodeDecimal+"\n" +
		"name: "+name+"\n" +
		"note: "+note+"\n" +
		"unicode: "+unicodePoint+"\n";
	}

	public UnicodePoint getUnicodePoint() {
		return unicodePoint;
	}

	public String getUnicodeValue() {
		return unicodePoint == null ? null : unicodePoint.getUnicodeValue();
	}

	public String getUnicodeName() {
		return unicodePoint == null ? null : unicodePoint.getUnicodeName();
	}
}

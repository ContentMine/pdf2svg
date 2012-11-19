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
package org.xmlcml.graphics.pdf2svg.raw;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.util.TextPosition;
import org.xmlcml.euclid.Util;

/** local implementation of codePointSet
 * 
    <codePoint decimal="9702" unicode="U+25E6" charName="WHITE BULLET" note="probably for lists and graph symbols" 
        confusions="ring operator U+2218 inverse bullet U+25D8"/>
    <codePoint decimal="12296" unicode="U+3008" charName="LEFT ANGLE BRACKET" 
        note="quasi-synonym" replaceByUnicode="U+003C" replaceName="LESS-THAN SIGN"/>
        
 * @author pm286
 *
 */
public class CodePointSet {

	public static final String UNICODE = "Unicode";
	public static final String CODE_POINT_SET = "codePointSet";
	public static final String KNOWN_HIGH_CODE_POINT_SET_XML = "org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml";
	public static final String ENCODING = "encoding";

	private Map<String, CodePoint> codePointByUnicodeMap;
	private Map<Integer, CodePoint> codePointByIntegerMap;
	private Map<String, String> unicodeByCharnameMap;
	private Map<Integer, String> unicodeByCharCodeMap;
	private String encoding = null;

	public CodePointSet() {
		ensureMaps();
	}

	private void ensureMaps() {
		if (codePointByIntegerMap == null) {
			codePointByIntegerMap = new HashMap<Integer, CodePoint>();
			codePointByUnicodeMap = new HashMap<String, CodePoint>();
			unicodeByCharnameMap = new HashMap<String, String>();
			unicodeByCharCodeMap = new HashMap<Integer, String>();
		}
	}

	public static CodePointSet readCodePointSet(String codePointSetXmlResource) {
		CodePointSet codePointSet = new CodePointSet();
		try {
			Element codePointSetElement = new Builder().build(
					Util.getResourceUsingContextClassLoader(codePointSetXmlResource, CodePointSet.class)).getRootElement();
			codePointSet = createFromElement(codePointSetElement);

		} catch (Exception e) {
			throw new RuntimeException("Cannot read CodePointSet: "+codePointSetXmlResource, e);
		}
		return codePointSet;
	}

	public static CodePointSet createFromElement(Element codePointSetElement) {
		CodePointSet codePointSet = new CodePointSet();
		if (!(CODE_POINT_SET.equals(codePointSetElement.getLocalName()))) {
			throw new RuntimeException("CodePointSet must have rootElement: "+CODE_POINT_SET);
		}
		codePointSet.encoding = codePointSetElement.getAttributeValue(ENCODING);
		if (codePointSet.encoding == null) {
			throw new RuntimeException("Must give encoding on: "+CODE_POINT_SET);
		}
		Elements childElements = codePointSetElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element codePointElement = childElements.get(i);
			CodePoint codePoint = CodePoint.createFromElement(codePointElement, codePointSet.encoding);
			String unicode = codePoint.getUnicode();
			if (unicode == null) {
				throw new RuntimeException("codePoint must contain unicode value");
			}
			if (codePointSet.containsKey(unicode) && UNICODE.equals(codePointSet.encoding)) {
				throw new RuntimeException("Duplicate unicode in unicode encoding: "+unicode);
			}
			codePointSet.codePointByUnicodeMap.put(unicode, codePoint);
			Integer decimal = codePoint.getDecimal();
			codePointSet.add(codePoint);
		}
		return codePointSet;
	}

	private void add(CodePoint codePoint) {
		this.add(codePoint.getDecimal(), codePoint.getName(), codePoint.getUnicode());
	}

	private boolean containsKey(String unicode) {
		return codePointByUnicodeMap.containsKey(unicode);
	}
	
	boolean containsKey(Integer decimal) {
		return codePointByIntegerMap.containsKey(decimal);
	}
	
	public Element createElementWithSortedIntegers() {
		Element codePointsElement = new Element(CODE_POINT_SET);
		Integer[] codePointIntegers = codePointByIntegerMap.keySet().toArray(new Integer[0]);
		Arrays.sort(codePointIntegers);
		for (Integer codePointInteger : codePointIntegers) {
			CodePoint codePoint = codePointByIntegerMap.get(codePointInteger);
			Element codePointElement = (Element) codePoint.getElement().copy();
			codePointsElement.appendChild(codePointElement);
		}
		return codePointsElement;
		
	}

	public int size() {
		return codePointByIntegerMap.size();
	}

	public void add(Integer charCode, String charname, String unicode) {
		if (encoding == null) {
			throw new RuntimeException("CodePointSet must have encoding");
		}
		CodePoint codePoint = new CodePoint();
		codePoint.setUnicode(unicode);
		if (charCode != null) {
			codePoint.setDecimal(charCode);
		}
		if (charname != null) {
			codePoint.setName(charname);
		}
		if (unicode != null) {
			codePoint.setUnicode(unicode);
		}
		
		if (codePoint.getDecimal() != null) {
			this.codePointByIntegerMap.put(codePoint.getDecimal(), codePoint);
		}
		if (codePoint.getUnicode() != null) {
			this.codePointByUnicodeMap.put(codePoint.getUnicode(), codePoint);
		}
		if (charname != null) {
			this.unicodeByCharnameMap.put(charname, codePoint.getUnicode());
		}
		if (charCode != null) {
			this.unicodeByCharCodeMap.put(charCode, codePoint.getUnicode());
		}
	}

	public String convertCharnameToUnicode(String charname) {
		ensureMaps();
		String unicode = unicodeByCharnameMap.get(charname);
		return unicode;
	}

	public String convertCharCodeToUnicode(Integer codepoint) {
		ensureMaps();
		String unicode = unicodeByCharCodeMap.get(codepoint);
		return unicode;
	}

	public CodePoint getByUnicode(String unicode) {
		ensureMaps();
		return codePointByUnicodeMap.get(unicode);
	}
	
	public CodePoint getByDecimal(Integer decimal) {
		ensureMaps();
		return codePointByIntegerMap.get(decimal);
	}

	public void ensureEncoding(String encoding) {
		if (this.encoding == null) {
			this.encoding = encoding;
		}
	}
	
}

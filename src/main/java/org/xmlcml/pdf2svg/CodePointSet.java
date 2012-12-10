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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Util;
import org.xmlcml.pdf2svg.util.PConstants;

/** local implementation of codePointSet
 * 
    <codePoint decimal="9702" unicode="U+25E6" charName="WHITE BULLET" note="probably for lists and graph symbols" 
        confusions="ring operator U+2218 inverse bullet U+25D8"/>
    <codePoint decimal="12296" unicode="U+3008" charName="LEFT ANGLE BRACKET" 
        note="quasi-synonym" replaceByUnicode="U+003C" replaceName="LESS-THAN SIGN"/>
        
 * @author pm286
 *
 */
public class CodePointSet extends Element {

	private final static Logger LOG = Logger.getLogger(CodePointSet.class);
	
	public static final String UNICODE = "Unicode";
	public static final String TAG = "codePointSet";
//	public static final String KNOWN_HIGH_CODE_POINT_SET_XML = PConstants.PDF2SVG_ROOT+"/"+"highCodePoints.xml";
	public static final String KNOWN_HIGH_CODE_POINT_SET_XML = PConstants.PDF2SVG_ROOT+"/"+"unicode.xml";
	public static final String ENCODING = "encoding";

	private Map<UnicodePoint, CodePoint> codePointByUnicodePointMap;
	private Map<String, CodePoint> codePointByUnicodeValueMap;
	private Map<Integer, CodePoint> codePointByDecimalMap;
	private Map<String, CodePoint> codePointByUnicodeNameMap;
	private Map<String, CodePoint> codePointByNameMap;
	private String encoding = null;

	public CodePointSet() {
		super(TAG);
		ensureMaps();
	}

	private void ensureMaps() {
		if (codePointByDecimalMap == null) {
			codePointByDecimalMap =      new HashMap<Integer, CodePoint>();
			codePointByUnicodePointMap = new HashMap<UnicodePoint, CodePoint>();
			codePointByUnicodeNameMap =  new HashMap<String, CodePoint>();
			codePointByUnicodeValueMap =  new HashMap<String, CodePoint>();
			codePointByNameMap =         new HashMap<String, CodePoint>();
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
		if (!(TAG.equals(codePointSetElement.getLocalName()))) {
			throw new RuntimeException("CodePointSet must have rootElement: "+TAG);
		}
		codePointSet.encoding = codePointSetElement.getAttributeValue(ENCODING);
		if (codePointSet.encoding == null) {
			throw new RuntimeException("Must give encoding on: "+TAG);
		}
		Elements childElements = codePointSetElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element codePointElement = childElements.get(i);
			CodePoint codePoint = CodePoint.createFromElement(codePointElement, codePointSet.encoding);
			UnicodePoint unicodePoint = codePoint.getUnicodePoint();
			if (unicodePoint == null) {
				throw new RuntimeException("codePoint must contain unicode value");
			}
			if (codePointSet.containsKey(unicodePoint) && UNICODE.equals(codePointSet.encoding)) {
				throw new RuntimeException("Duplicate unicode in unicode encoding: "+unicodePoint);
			}
			codePointSet.codePointByUnicodePointMap.put(unicodePoint, codePoint);
			Integer decimal = codePoint.getDecimal();
			codePointSet.add(codePoint);
			LOG.trace("CodePoint "+codePoint);
		}
		return codePointSet;
	}

	private boolean containsKey(UnicodePoint unicodePoint) {
		return codePointByUnicodePointMap.containsKey(unicodePoint);
	}
	
	boolean containsKey(Integer decimal) {
		return codePointByDecimalMap.containsKey(decimal);
	}
	
	public Element createElementWithSortedIntegers() {
		Element codePointsElement = new Element(TAG);
		Integer[] codePointIntegers = codePointByDecimalMap.keySet().toArray(new Integer[0]);
		Arrays.sort(codePointIntegers);
		for (Integer codePointInteger : codePointIntegers) {
			CodePoint codePoint = codePointByDecimalMap.get(codePointInteger);
			Element codePointElement = (Element) codePoint.createElement().copy();
			codePointsElement.appendChild(codePointElement);
		}
		return codePointsElement;
		
	}

	public int size() {
		return codePointByDecimalMap.size();
	}

	/** adds and indexes codePoints checking for duplicates etc.
	*/
	public void add(CodePoint codePoint) {
		if (encoding == null) {
			throw new RuntimeException("CodePointSet must have encoding");
		}
		UnicodePoint unicodePoint = codePoint.getUnicodePoint();
		if (unicodePoint == null) {
			throw new RuntimeException("CodePoint must have unicodePoint");
		}
		makeIndexes(codePoint, unicodePoint);
	}

	private void makeIndexes(CodePoint codePoint, UnicodePoint unicodePoint) {
		if (codePoint.getDecimal() != null) {
			this.codePointByDecimalMap.put(codePoint.getDecimal(), codePoint);
		} else {
			Integer decimal = (unicodePoint == null) ? null : unicodePoint.getDecimalValue();
			if (decimal != null) {
				this.codePointByDecimalMap.put(decimal, codePoint);
			}
		}
		this.codePointByUnicodePointMap.put(unicodePoint, codePoint);
		this.codePointByUnicodeValueMap.put(unicodePoint.getUnicodeValue(), codePoint);
		if (codePoint.getName() != null) {
			this.codePointByNameMap.put(codePoint.getName(), codePoint);
		}
		if (codePoint.getUnicodeName() != null) {
			this.codePointByUnicodeNameMap.put(codePoint.getUnicodeName(), codePoint);
		}
	}

	public CodePoint getByUnicodePoint(UnicodePoint unicodePoint) {
		ensureMaps();
		return codePointByUnicodePointMap.get(unicodePoint);
	}
	
	public CodePoint getByUnicodeValue(String unicode) {
		ensureMaps();
		return codePointByUnicodeValueMap.get(unicode);
	}
	
	public CodePoint getByName(String name) {
		ensureMaps();
		return codePointByNameMap.get(name);
	}
	
	public CodePoint getByDecimal(Integer decimal) {
		ensureMaps();
		return codePointByDecimalMap.get(decimal);
	}

	public CodePoint getByUnicodeName(String unicodeName) {
		ensureMaps();
		return codePointByUnicodeNameMap.get(unicodeName);
	}
	public void ensureEncoding(String encoding) {
		if (this.encoding == null) {
			this.encoding = encoding;
		}
	}

	
}

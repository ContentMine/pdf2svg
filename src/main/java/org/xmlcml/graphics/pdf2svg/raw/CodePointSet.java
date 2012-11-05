package org.xmlcml.graphics.pdf2svg.raw;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

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

	private static final String CODE_POINTS = "codePoints";
	public static final String KNOWN_HIGH_CODE_POINT_SET_XML = "org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml";

	private Map<String, CodePoint> codePointByUnicodeMap;
	private Map<Integer, CodePoint> codePointByIntegerMap;

	public CodePointSet() {
		ensureMaps();
	}

	private void ensureMaps() {
		if (codePointByIntegerMap == null) {
			codePointByIntegerMap = new HashMap<Integer, CodePoint>();
			codePointByUnicodeMap = new HashMap<String, CodePoint>();
		}
	}

	public static CodePointSet readCodePointSet(String codePointSetXml) {
		CodePointSet codePointSet = new CodePointSet();
		try {
			Element codePoints = new Builder().build(
					Util.getResourceUsingContextClassLoader(codePointSetXml, CodePointSet.class)).getRootElement();
			if (!(CODE_POINTS.equals(codePoints.getLocalName()))) {
				throw new RuntimeException("CodePointSet must have rootElement <codePoints>");
			}
			Elements childElements = codePoints.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				Element codePointElement = childElements.get(i);
				CodePoint codePoint = CodePoint.createFromElement(codePointElement);
				String unicode = codePoint.getUnicode();
				if (codePointSet.containsKey(unicode)) {
					throw new RuntimeException("Duplicate unicode: "+unicode);
				}
				codePointSet.codePointByUnicodeMap.put(codePoint.getUnicode(), codePoint);
				Integer decimal = codePoint.getDecimal();
				codePointSet.codePointByIntegerMap.put(codePoint.getDecimal(), codePoint);
			}

		} catch (Exception e) {
			throw new RuntimeException("Cannot read CodePointSet: "+codePointSetXml, e);
		}
		return codePointSet;
	}

	private boolean containsKey(String unicode) {
		return codePointByUnicodeMap.containsKey(unicode);
	}
	
	boolean containsKey(Integer decimal) {
		return codePointByIntegerMap.containsKey(decimal);
	}
	
	public Element createElementWithSortedIntegers() {
		Element codePointsElement = new Element(CODE_POINTS);
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

	public void add(int charCode, String charname) {
		CodePoint codePoint = new CodePoint();
		codePoint.setDecimal((Integer)charCode);
		if (charname != null) {
			codePoint.setName(charname);
		}
		this.codePointByIntegerMap.put(codePoint.getDecimal(), codePoint);
		this.codePointByUnicodeMap.put(codePoint.getUnicode(), codePoint);
	}
	
}

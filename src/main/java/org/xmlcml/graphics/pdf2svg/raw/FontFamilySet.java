package org.xmlcml.graphics.pdf2svg.raw;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Util;

/** set of FontFamily
 * 
 * @author pm286
 *
 */
public class FontFamilySet {

	private final static Logger LOG = Logger.getLogger(FontFamilySet.class);
	
	private static final String FONT_FAMILY_SET = "fontFamilySet";
	public static final String STANDARD_FONT_FAMILY_SET_XML = "org/xmlcml/graphics/pdf2svg/raw/standardFontFamilySet.xml";
	public static final String NON_STANDARD_FONT_FAMILY_SET_XML = "org/xmlcml/graphics/pdf2svg/raw/nonStandardFontFamilySet.xml";

	private Map<String, FontFamily> fontFamilyByFamilyName;

	public FontFamilySet() {
		ensureMaps();
	}

	private void ensureMaps() {
		if (fontFamilyByFamilyName == null) {
			fontFamilyByFamilyName = new HashMap<String, FontFamily>();
		}
	}

	/**
      <font family="Courier" fontType="PDType1Font" note="a standard14 font" serif="yes" unicode="yes"/>
      
     * @param fontFamilySetXml
	 * @return
	 */
	public static FontFamilySet readFontFamilySet(String fontFamilySetXml) {
		FontFamilySet fontFamilySet = null;
		try {
			Element fontFamilySetElement = new Builder().build(
					Util.getResourceUsingContextClassLoader(fontFamilySetXml, FontFamilySet.class)).getRootElement();
			fontFamilySet = createFromElement(fontFamilySetElement);

		} catch (Exception e) {
			throw new RuntimeException("Cannot read FontFamilySet: "+fontFamilySetXml, e);
		}
		return fontFamilySet;
	}

	public static FontFamilySet createFromElement(Element fontFamilySetElement) {
		FontFamilySet fontFamilySet = new FontFamilySet();
		String rootName = fontFamilySetElement.getLocalName();
		if (!(FONT_FAMILY_SET.equals(rootName))) {
			throw new RuntimeException("FontFamilySet must have rootElement "+FONT_FAMILY_SET+"; found: "+rootName);
		}
		Elements childElements = fontFamilySetElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element fontFamilyElement = childElements.get(i);
			FontFamily fontFamily = FontFamily.createFromElement(fontFamilyElement);
			if (fontFamily == null) {
				throw new RuntimeException("Cannot read/parse fontFamilyElement: "+((fontFamilyElement == null) ? null : fontFamilyElement.toXML()));
			}
			String family = fontFamily.getName();
			if (fontFamilySet.containsKey(family)) {
				throw new RuntimeException("Duplicate name: "+family);
			}
			fontFamilySet.fontFamilyByFamilyName.put(family, fontFamily);
		}
		return fontFamilySet;
	}
	
	boolean containsKey(String name) {
		return fontFamilyByFamilyName.containsKey(name);
	}

	FontFamily getFontFamilyByName(String fontFamilyName) {
		return fontFamilyByFamilyName.get(fontFamilyName);
	}

	void add(String fontFamilyName, FontFamily fontFamily) {
		if (fontFamily == null) {
			throw new RuntimeException("Cannot add null fontFamily");
		}
		fontFamilyByFamilyName.put(fontFamilyName, fontFamily);
	}

	Element createElement() {
		Element fontsElement = new Element(FONT_FAMILY_SET);
		for (String fontFamilyName : fontFamilyByFamilyName.keySet()) {
			FontFamily fontFamily = fontFamilyByFamilyName.get(fontFamilyName);
			if (fontFamily == null) {
				throw new RuntimeException("BUG null fontFamily should never happen: ");
			}
			Element fontFamilyElement = fontFamily.createElement();
			fontsElement.appendChild(fontFamilyElement);
		}
		return fontsElement;
	}
}

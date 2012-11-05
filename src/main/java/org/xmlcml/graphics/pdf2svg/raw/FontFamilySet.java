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
	
	private static final String FONTS = "fonts";
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
		FontFamilySet fontFamilySet = new FontFamilySet();
		try {
			Element FontFamilys = new Builder().build(
					Util.getResourceUsingContextClassLoader(fontFamilySetXml, FontFamilySet.class)).getRootElement();
			if (!(FONTS.equals(FontFamilys.getLocalName()))) {
				throw new RuntimeException("FontFamilySet must have rootElement <FontFamilys>");
			}
			Elements childElements = FontFamilys.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				Element fontFamilyElement = childElements.get(i);
				FontFamily fontFamily = FontFamily.createFromElement(fontFamilyElement);
				if (fontFamily == null) {
					throw new RuntimeException("Cannot read/parse fontFamilyElement: "+((fontFamilyElement == null) ? null : fontFamilyElement.toXML()));
				}
				String family = fontFamily.getFamily();
				if (fontFamilySet.containsKey(family)) {
					throw new RuntimeException("Duplicate name: "+family);
				}
				fontFamilySet.fontFamilyByFamilyName.put(family, fontFamily);
			}

		} catch (Exception e) {
			throw new RuntimeException("Cannot read FontFamilySet: "+fontFamilySetXml, e);
		}
		return fontFamilySet;
	}
	
	private boolean containsKey(String name) {
		return fontFamilyByFamilyName.containsKey(name);
	}

	public FontFamily getFontByFamilyName(String fontFamilyName) {
		return fontFamilyByFamilyName.get(fontFamilyName);
	}

	public void add(String fontFamilyName, FontFamily fontFamily) {
		LOG.error("NYI add(String fontFamilyName, FontFamily fontFamily)");
	}
	
}

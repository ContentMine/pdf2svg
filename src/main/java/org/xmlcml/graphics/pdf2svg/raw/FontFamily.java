package org.xmlcml.graphics.pdf2svg.raw;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlcml.euclid.Util;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/** manages a generic set of fonts
 * should not depend on prefix, bold, italic, MT or PS suffixes, etc.
 * @author pm286
 *
 // standard
    <font family="Courier" fontType="PDType1Font" note="a standard14 font" serif="yes" unicode="yes"/>
    or
  // non-standard
    <font family="FooBar" fontType="PDType1Font" standardFont="Helvetica" note="" serif="" unicode="guessed"/>

 *
 */
public class FontFamily {

	// XML
	public final static String FONT = "font";
	public static final String FAMILY = "family";
	public static final String FONT_TYPE = "fontType";
	public static final String MONOSPACED = "monospaced";
	public static final String NOTE = "note";
	public static final String SERIF = "serif";
	public static final String STANDARD_FONT = "standardFont";
	public static final String UNICODE = "unicode";

	private String family;
	private String fontType;
	private String standardFont;
	private String unicode;
	private String serif;
	private String monospaced;
	private String note;

	public FontFamily() {
		
	}

	public static FontFamily createFromElement(Element fontFamilyElement) {
		FontFamily fontFamily = null;
		try {
			fontFamily = new FontFamily();
			if (!(FONT.equals(fontFamilyElement.getLocalName()))) {
				throw new RuntimeException("FontFamilySet children must be <font>");
			}
			fontFamily.family = fontFamilyElement.getAttributeValue(FAMILY);
			if (fontFamily.family == null) {
				throw new RuntimeException("<FontFamily> must have family attribute");
			}
			fontFamily.fontType = fontFamilyElement.getAttributeValue(FONT_TYPE);
			fontFamily.standardFont = fontFamilyElement.getAttributeValue(STANDARD_FONT);
			fontFamily.unicode = fontFamilyElement.getAttributeValue(UNICODE);
			fontFamily.serif = fontFamilyElement.getAttributeValue(SERIF);
			fontFamily.monospaced = fontFamilyElement.getAttributeValue(MONOSPACED);
			fontFamily.note = fontFamilyElement.getAttributeValue(NOTE);
		} catch (Exception e) {
			throw new RuntimeException("invalid FontFamilyElement: "+((fontFamilyElement == null) ? null : fontFamilyElement.toXML()), e);
		}
		return fontFamily;
	}

	public Element getElement() {
		Element FontFamilyElement = new Element(FONT);
		if (family == null) {
			throw new RuntimeException("family must not be null");
		}
		FontFamilyElement.addAttribute(new Attribute(FAMILY, ""+family));
		if (standardFont != null) {
			FontFamilyElement.addAttribute(new Attribute(STANDARD_FONT, standardFont));
		}
		if (note != null) {
			FontFamilyElement.addAttribute(new Attribute(NOTE, note));
		}
		if (unicode != null) {
			FontFamilyElement.addAttribute(new Attribute(UNICODE, unicode));
		}
		if (serif != null) {
			FontFamilyElement.addAttribute(new Attribute(SERIF, serif));
		}
		if (monospaced != null) {
			FontFamilyElement.addAttribute(new Attribute(MONOSPACED, monospaced));
		}
		if (fontType != null) {
			FontFamilyElement.addAttribute(new Attribute(FONT_TYPE, fontType));
		}
		return FontFamilyElement;
	}

	public String getUnicode() {
		return unicode;
	}

	public String getFamily() {
		return family;
	}

}

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.xmlcml.euclid.Util;
public class AMIFontManager {


	private final static Logger LOG = Logger.getLogger(AMIFontManager.class);
	
	public static final String N_NAME = "Name";
	public static final String N_BASE_FONT = "BaseFont";
	
	public static final String FONT_TRUE_TYPE = "TrueType";
	public static final String FONT_TYPE1 = "Type1";
	public static final String FONT_TYPE0 = "Type0";
	public static final String BADCHAR_E = "?}";
	public static final String BADCHAR_S = "{?";
	public static final String FONT_NAME = "fontName";
	public static final String BOLD = "bold";
	public static final String ITALIC = "italic";
	public static final String INCLINED = "inclined";
	public static final String OBLIQUE = "oblique";

	public static final String CHARNAME = "charname";
	public static final String CODEPOINT = "codepoint";

	private static final String NAME = "name";
	private static final String FONT_ENCODING = "fontEncoding";
	private static final String FONTS = "fonts";
	private static final String FONT = "font";
	private static final String IS_SYMBOL = "isSymbol";
	private static final String TYPE = "type";

	private Map<String, AMIFont> amiFontByFontNameMap;
	private FontFamilySet standardFontFamilySet;
	private FontFamilySet nonStandardFontFamilySet;
	private FontFamilySet newFontFamilySet;

	private Map<String, Integer> symbol2UnicodeHackMap;

	private boolean nullFontDescriptorReport = true;;
	
	public static final int UNKNOWN_CHAR = (char)0X274E; // black square with white cross

	public AMIFontManager() {
		ensureAMIFontMaps();
	}
	
	public void ensureAMIFontMaps() {
		if (amiFontByFontNameMap == null) {
			amiFontByFontNameMap = new HashMap<String, AMIFont>();
			standardFontFamilySet = FontFamilySet.readFontFamilySet(FontFamilySet.STANDARD_FONT_FAMILY_SET_XML);
			nonStandardFontFamilySet = FontFamilySet.readFontFamilySet(FontFamilySet.NON_STANDARD_FONT_FAMILY_SET_XML);
			newFontFamilySet = new FontFamilySet();
		}
	}

	
	public Map<String, AMIFont> getAmiFontByFontNameMap() {
		ensureAMIFontMaps();
		return amiFontByFontNameMap;
	}
	
	public AMIFont getAmiFontByFontName(String fontName) {
		getAmiFontByFontNameMap();
		return amiFontByFontNameMap.get(fontName);
	}

	private AMIFont getOrCreateFontDict(int level, COSDictionary dict) {
		AMIFont amiFont = null;
		/**
Type = COSName{Font}
Subtype = COSName{Type1}
BaseFont = COSName{Times-Roman}
Name = COSName{arXivStAmP}		
LastChar = COSInt{32}
Widths = COSArray{[COSInt{19}]}
FirstChar = COSInt{32}
FontMatrix = COSArray{[COSFloat{0.0121}, COSInt{0}, COSInt{0}, COSFloat{-0.0121}, COSInt{0}, COSInt{0}]}
ToUnicode = COSDictionary{(COSName{Length}:COSInt{212}) (COSName{Filter}:COSName{FlateDecode}) }
FontBBox = COSArray{[COSInt{0}, COSInt{0}, COSInt{1}, COSInt{1}]}
Resources = COSDictionary{(COSName{ProcSet}:COSArray{[COSName{PDF}, COSName{ImageB}]}) }
Encoding = COSDictionary{(COSName{Differences}:COSArray{[COSInt{32}, COSName{space}]}) (COSName{Type}:COSName{Encoding}) }
CharProcs = COSDictionary{(COSName{space}:COSDictionary{(COSName{Length}:COSInt{67}) (COSName{Filter}:COSName{FlateDecode}) }) }*/
		
		// This is messy until we work out what is null and what isn't
		String typeS = null;
		String subtypeS = null;
		String baseFontS = null;
		String fontName = null;
		boolean isSymbol = false;
		for (COSName key : dict.keySet()) {
			String keyName = key.getName();
			if (keyName == null) {
				LOG.error("Null key");
				continue;
			} else if (!(key instanceof COSName)) {
				LOG.error("key not COSName");
				continue;
			}
			String cosNameName = null;
			COSBase cosBase = dict.getDictionaryObject(key);
			if (cosBase instanceof COSName) {
				COSName cosName = (COSName) cosBase;
				cosNameName = cosName.getName();
				LOG.trace("Name:"+cosNameName);
			} else if (cosBase instanceof COSInteger) {
				COSInteger cosInteger = (COSInteger) cosBase;
				LOG.trace("Integer: "+cosInteger.intValue());
			} else if (cosBase instanceof COSArray) {
				COSArray cosArray = (COSArray) cosBase;
				LOG.trace("Array: "+cosArray.size()+" / "+cosArray);
			} else if (cosBase instanceof COSDictionary) {
				COSDictionary cosDictionary = (COSDictionary) cosBase;
				LOG.trace("Dictionary: "+cosDictionary);
			} else{
				LOG.error("COS "+cosBase);
			}
			if (cosNameName != null && keyName.equals(N_NAME)) {
				fontName = cosNameName;
			} else if(cosNameName != null && keyName.equals(N_BASE_FONT)) {
				baseFontS = cosNameName;
			}
		}
		if (fontName == null) {
			fontName = baseFontS;
		}
		
		amiFont = getAmiFontByFontName(fontName);
		if (amiFont == null) {
			amiFont = new AMIFont(fontName, null, typeS, isSymbol, dict);
			amiFont.setFontName(fontName);
			amiFontByFontNameMap.put(fontName, amiFont);
	
			String indent = "";
			for (int i = 0; i < level; i++) {
				indent += " ";
			}
	
			LOG.debug(String.format("%s****************** level %d font dict:",
					indent, level));
	
			level++;
			indent += "    ";
	
			for (COSName key : dict.keySet()) {
				String keyName = key.getName();
				Object object = dict.getDictionaryObject(key);
				LOG.debug(String.format("%s****************** %s = %s", indent,
						keyName, object));
			}
	
			COSArray array = (COSArray) dict
					.getDictionaryObject(COSName.DESCENDANT_FONTS);
			if (array != null) {
				LOG.debug(String.format(
						"%s****************** descendant fonts (%d):", indent,
						array.size()));
				getOrCreateFontDict(level, (COSDictionary) array.getObject(0));
			}
		}
		return amiFont;
	}


	public AMIFont getAmiFontByFont(PDFont pdFont) {
		ensureAMIFontMaps();
		String fontName = null;
		AMIFont amiFont = null;
		PDFontDescriptor fd = pdFont.getFontDescriptor();
//		COSBase cosBase = ((PDSimpleFont) pdFont).getToUnicode();
//		System.out.println("ToUnicode: "+((cosBase== null) ? "NULL" : cosBase));
		if (fd == null && pdFont instanceof PDType0Font) {
			COSDictionary dict = (COSDictionary) pdFont.getCOSObject();
			COSArray array = (COSArray) dict.getDictionaryObject(COSName.DESCENDANT_FONTS);
			PDFont descendantFont;
			try {
				descendantFont = PDFontFactory.createFont((COSDictionary) array.getObject(0));
				fd = descendantFont.getFontDescriptor();
			} catch (IOException e) {
				LOG.error("****************** Can't create descendant font!");
			}
		}
		if (fd == null) {
			if (nullFontDescriptorReport) {
				LOG.error("****************** Null Font Descriptor : "+pdFont+"\n       FURTHER ERRORS HIDDEN");
				nullFontDescriptorReport = false;
			}
			amiFont = getOrCreateFontDict(0, (COSDictionary) pdFont.getCOSObject());
			fontName = amiFont.getFontName();
			if (fontName == null) {
				throw new RuntimeException("No currentFontName");
			}
		} else {
			fontName = fd.getFontName();
		}
		if (fontName == null) {
			throw new RuntimeException("No currentFontName");
		}
		amiFont = amiFontByFontNameMap.get(fontName);
		if (amiFont == null) {
			if (pdFont instanceof PDType1Font ||
				pdFont instanceof PDTrueTypeFont || 
				pdFont instanceof PDType0Font ||
				pdFont instanceof PDType3Font) {
				amiFont = new AMIFont(pdFont);
				amiFontByFontNameMap.put(fontName, amiFont);
				String fontFamilyName = amiFont.getFontFamilyName();
				recordExistingOrAddNewFontFamily(fontFamilyName, amiFont);
			} else {
				throw new RuntimeException("Cannot find font type: "+pdFont+" / "+pdFont.getSubType()+", ");
			}
		}
		return amiFont;
	}

//	public void ensureSymbol2UnicodeHackMap() {
//		ensureSymbol2UnicodeHackMap(SYMBOL2UNICODE_HACK_XML);
//	}

	/** hopefully this map is independent of font; if not we may have to move it to
	 * individual AMIFonts
	 * @param symbol2UnicodeResource
	 */
	public void ensureSymbol2UnicodeHackMap(String symbol2UnicodeResource) {
		if (symbol2UnicodeHackMap == null) {
			symbol2UnicodeHackMap = new HashMap<String, Integer>();
			try {
				InputStream is = Util.getResourceUsingContextClassLoader(symbol2UnicodeResource, this.getClass());
				Element conversionElement = new Builder().build(is).getRootElement();
				Elements charDataElements = conversionElement.getChildElements();
				for (int i = 0; i < charDataElements.size(); i++) {
					Element charDataElement = charDataElements.get(i);
					String charname = charDataElement.getAttributeValue(CHARNAME);
					String unicodeS = charDataElement.getAttributeValue(CODEPOINT);
					Integer unicodePoint = new Integer(unicodeS);
					symbol2UnicodeHackMap.put(charname, unicodePoint);
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot read/parse symbolConverter: "+symbol2UnicodeResource, e);
			}
		}
	}
	
	/** convert a text symbol to Unicode codepoint via PDFBox's StandardEncoding.INSTANCE
	 * Thus "two" is converted to 50 (0X32) or character '2'
	 * we test that all StandardEncoding returns are single characters and assume this
	 * as test of correct interpretation. If any chars are ligatures ('ffl'we'll deal with that
	 * when it comes up)
	 * @param symbol e.g. "two", "A", "comma"
	 * @return codepoint 50, 65, 44 or null if not converted
	 */
	public static Integer convertSymbol2UnicodeStandard(String symbol) {
		Integer codePoint = null;
		String s = (symbol == null) ? null : convertToUnicodeWithPDFStandardEncoding(symbol);
		if (s != null) {
			// all converted characters should have length 1
			if (s.length() == 1) {
				LOG.trace(symbol+" => "+s);
				codePoint = new Integer((int) s.charAt(0));
			} else {
				// for ?unknown? charnames - we may need glyphs
				s = null; 
			}
		}
		return codePoint;
	}

	/** uses PDFBox list of standard symbols to convert to characters.
	 * e.g. "two" converts to "2" (unicode codePoint 50)
	 * some are identity ops - "a" converts to "a"
	 * @param symbol
	 * @return
	 */
	public static String convertToUnicodeWithPDFStandardEncoding(String symbol) {
		return StandardEncoding.INSTANCE.getCharacter(symbol);
	}
	
	/** has a messy collection of character names from MathematicalPi, Cddd and elsewhere
	 * no guarantee of uniqueness
	 * FIXME should be tied to individual fonts asap
	 * 
	 * @param symbol
	 * @return
	 */
	public Integer convertSymbol2UnicodeHack(String symbol, String fontF) {
		Integer codePoint = null;
		if (symbol != null) {
			codePoint = symbol2UnicodeHackMap.get(symbol);
			LOG.trace("Used lashed-up symbol2UnicodeMap FIX THIS : "+symbol+" => "+((codePoint == null) ? null : (char)(int)codePoint));
		}
		return codePoint;
	}

	public static Map<String, AMIFont> readAmiFonts() {
		return readAmiFonts(FontFamilySet.STANDARD_FONT_FAMILY_SET_XML);
	}

	public static Map<String, AMIFont> readAmiFonts(String resourceName) {
		Map<String, AMIFont> fontMap = new HashMap<String, AMIFont>();
		try {
			InputStream is = Util.getResourceUsingContextClassLoader(resourceName, AMIFontManager.class);
			Element amiFontList = new Builder().build(is).getRootElement();
			for (int i = 0; i < amiFontList.getChildElements().size(); i++) {
				Element amiFontElement = amiFontList.getChildElements().get(i);
				String familyName = amiFontElement.getAttributeValue(NAME);
				String encoding = amiFontElement.getAttributeValue(FONT_ENCODING); 
				String type = amiFontElement.getAttributeValue(TYPE);
				if (
						familyName == null 
//						|| encoding == null 
						|| type == null) {
					throw new RuntimeException("Must have family and type for font");
				}
				if (fontMap.get(familyName) != null) {
					throw new RuntimeException("AMIFont map ("+resourceName+") already contains family: "+familyName);
				}
				String symbol = amiFontElement.getAttributeValue(IS_SYMBOL);
				Boolean isSymbol = (symbol == null) ? false : new Boolean(symbol);
				AMIFont amiFont = new AMIFont(familyName, encoding, type, isSymbol);
				fontMap.put(familyName, amiFont);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/parse AMI fonts: "+resourceName, e);
		}
		return fontMap;
	}
			
	public static Element createAmiFontList(String resourceName, Map<String, AMIFont> fontMap) {
		Element fontList = new Element(FONTS);
		String[] families = fontMap.keySet().toArray(new String[0]);
		Arrays.sort(families);
		for (String family : families) {
			AMIFont amiFont = fontMap.get(family);
			Element font = new Element(FONT);
			fontList.appendChild(font);
			font.addAttribute(new Attribute(NAME, family));
			String encoding = amiFont.getFontEncoding();
			if (encoding != null) {
				font.addAttribute(new Attribute(FONT_ENCODING, encoding));
			}
			font.addAttribute(new Attribute(TYPE, amiFont.getFontType()));
			Boolean isSymbol = amiFont.isSymbol();
			if (isSymbol != null) {
				font.addAttribute(new Attribute(IS_SYMBOL, isSymbol.toString()));
			}
		}
		return fontList;
	}

	public FontFamily getFontFamily(String fontFamilyName) {
		FontFamily fontFamily = standardFontFamilySet.getFontFamilyByName(fontFamilyName);
		if (fontFamily == null) {
			fontFamily = nonStandardFontFamilySet.getFontFamilyByName(fontFamilyName);
		}
		if (fontFamily == null) {
			fontFamily = newFontFamilySet.getFontFamilyByName(fontFamilyName);
		}
		return fontFamily;
	}

	public FontFamily recordExistingOrAddNewFontFamily(String fontName, AMIFont amiFont) {
		String fontFamilyName = amiFont.getFontFamilyName();
		FontFamily fontFamily = amiFont.getFontFamily();
		if (standardFontFamilySet.containsKey(fontFamilyName)) {
			LOG.trace(fontFamilyName+" is a standard FontFamily");
		} else if (nonStandardFontFamilySet.containsKey(fontFamilyName)) {
			LOG.trace(fontFamilyName+" is a known non-standard FontFamily");
		} else if (newFontFamilySet.containsKey(fontFamilyName)) {
			LOG.trace(fontFamilyName+" is a known newFontFamily");
		} else {
			LOG.trace(fontName+" is being added as new FontFamily ("+fontFamilyName+")");
			if (fontFamily == null) {
				LOG.trace("ami: "+amiFont.toString());
				fontFamily = new FontFamily();
				fontFamily.setName(""+fontName);
				LOG.trace("created new FontFamily: "+fontFamilyName);
			}
			newFontFamilySet.add(fontName, fontFamily);
		}
		return fontFamily;
	}
	
	public FontFamilySet getNewFontFamilySet() {
		return newFontFamilySet;
	}

	public static String getUnknownCharacterSymbol() {
		return ""+(char)UNKNOWN_CHAR;
	}

	public void setNullFontDescriptorReport(boolean b) {
		this.nullFontDescriptorReport = b;
	}
	
}

package org.xmlcml.graphics.pdf2svg.raw;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.xmlcml.euclid.Util;
public class AMIFontManager {
	



	private final static Logger LOG = Logger.getLogger(AMIFontManager.class);

	private static final String SYMBOL2UNICODE_XML = "org/xmlcml/graphics/pdf2svg/raw/symbol2unicode.xml";

	public static final String COMM_PI = "Universal-NewswithCommPi";
	public static final String GREEK_WITH_MATH_PI = "Universal-GreekwithMathPi";
	public static final String MTSYN = "MTSYN";
	public static final String TIMES_ROMAN_GROTTY = "Times-Roman";
	public static final String SYMBOL_PS = "SymbolPS";
	public static final String TIMES_GREEK_SF = "TimesGreekSF";
	public static final String TIMES_NR_EXPERT_MT = "TimesNRExpertMT";
	
	public static final String MATHEMATICAL_PI_ONE = "MathematicalPi-One";
	public static final String MATHEMATICAL_PI_FOUR = "MathematicalPi-Four";
	
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

	private static final String CHARNAME = "charname";
	private static final String CODEPOINT = "codepoint";

	private Map<String, AMIFont> amiFontByFontNameMap;

	private HashSet<String> badFontNameSet;
	private Map<String, Integer> symbol2UnicodeMap;
	private Map<String, Map<String, Integer>> foreign2UnicodeMapByFontFamily;//may be obsolete
	private Map<String, Map<Integer, String>> char2UnicodeStringMapByFontFamilyMap;

	public AMIFontManager() {
	}
	
	public void ensureAMIFontByFontNameMap() {
		if (amiFontByFontNameMap == null) {
			amiFontByFontNameMap = new HashMap<String, AMIFont>();
			
		}
	}

	public Set<String> getBadFontNameSet() {
		ensureBadFontNameSet();
		return badFontNameSet;
	}


	private void ensureBadFontNameSet() {
		if (badFontNameSet == null) {
			badFontNameSet = new HashSet<String>();
		}
	}
	
	Map<String, Integer> getForeign2UnicodeMap(String fontFamily) {
		ensureForeign2UnicodeMapByFontFamily();
		return foreign2UnicodeMapByFontFamily.get(fontFamily);
	}

	private void ensureForeign2UnicodeMapByFontFamily() {
		if (foreign2UnicodeMapByFontFamily == null) {
			foreign2UnicodeMapByFontFamily = new HashMap<String, Map<String, Integer>>();
			createMathematicalPiOneMap();
			createMathematicalPiFourMap();
			createCommPiMap();
			createGreekWithMathPiMap();
		}
	}

	private void createTimesNRExpertMTMap() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put((Integer)63337, ""+(char)63337);  //
		map.put((Integer)64256, "ff");  // ligature
		map.put((Integer)64257, "fi");  // ligature
		map.put((Integer)64258, "fl");  // ligature
		map.put((Integer)64259, "ffi");  // ligature
		map.put((Integer)64260, "ffl");  // ligature (guessed)
		char2UnicodeStringMapByFontFamilyMap.put(TIMES_NR_EXPERT_MT.toLowerCase(), map);
	}

	private void createTimesGreekSFMap() {
//		alpha, beta gamma delta epsilon iota kappa lambda mu nu omicron pi rho sigma tau upsilon phi chi psi omega
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put((Integer)913, ""+(char)913);  //  GREEK CAPITAL LETTER Alpha
		map.put((Integer)914, ""+(char)914);  //  GREEK CAPITAL LETTER Beta
		map.put((Integer)915, ""+(char)915);  //  GREEK CAPITAL LETTER Gamma
		map.put((Integer)916, ""+(char)916);  //  GREEK CAPITAL LETTER Delta
		map.put((Integer)917, ""+(char)917);  //  GREEK CAPITAL LETTER Epsilon
		map.put((Integer)918, ""+(char)918);  //  GREEK CAPITAL LETTER Zeta
		map.put((Integer)919, ""+(char)919);  //  GREEK CAPITAL LETTER Eta
		map.put((Integer)920, ""+(char)920);  //  GREEK CAPITAL LETTER Theta
		map.put((Integer)921, ""+(char)921);  //  GREEK CAPITAL LETTER Iota
		map.put((Integer)922, ""+(char)922);  //  GREEK CAPITAL LETTER Kappa
		map.put((Integer)923, ""+(char)923);  //  GREEK CAPITAL LETTER Lambda
		map.put((Integer)924, ""+(char)924);  //  GREEK CAPITAL LETTER Mu
		map.put((Integer)925, ""+(char)925);  //  GREEK CAPITAL LETTER Nu
		map.put((Integer)926, ""+(char)926);  //  GREEK CAPITAL LETTER Xi
		map.put((Integer)927, ""+(char)927);  //  GREEK CAPITAL LETTER Omicron
		map.put((Integer)928, ""+(char)928);  //  GREEK CAPITAL LETTER Pi
		map.put((Integer)929, ""+(char)929);  //  GREEK CAPITAL LETTER Rho
		map.put((Integer)931, ""+(char)931);  //  GREEK CAPITAL LETTER SIGMA
		map.put((Integer)932, ""+(char)932);  //  GREEK CAPITAL LETTER TAU
		map.put((Integer)933, ""+(char)933);  //  GREEK CAPITAL LETTER UPSILON
		map.put((Integer)934, ""+(char)934);  //  GREEK CAPITAL LETTER PHI
		map.put((Integer)935, ""+(char)935);  //  GREEK CAPITAL LETTER CHI
		map.put((Integer)936, ""+(char)936);  //  GREEK CAPITAL LETTER PSI
		map.put((Integer)937, ""+(char)937);  //  GREEK CAPITAL LETTER OMEGA
		
		map.put((Integer)945, ""+(char)945);  //  GREEK SMALL LETTER alpha
		map.put((Integer)946, ""+(char)946);  //  GREEK SMALL LETTER beta
		map.put((Integer)947, ""+(char)947);  //  GREEK SMALL LETTER gamma
		map.put((Integer)948, ""+(char)948);  //  GREEK SMALL LETTER delta
		map.put((Integer)949, ""+(char)949);  //  GREEK SMALL LETTER epsilon
		map.put((Integer)950, ""+(char)950);  //  GREEK SMALL LETTER zeta
		map.put((Integer)951, ""+(char)951);  //  GREEK SMALL LETTER eta
		map.put((Integer)952, ""+(char)952);  //  GREEK SMALL LETTER theta
		map.put((Integer)953, ""+(char)953);  //  GREEK SMALL LETTER iota
		map.put((Integer)954, ""+(char)954);  //  GREEK SMALL LETTER kappa
		map.put((Integer)955, ""+(char)955);  //  GREEK SMALL LETTER lambda
		map.put((Integer)956, ""+(char)956);  //  GREEK SMALL LETTER mu
		map.put((Integer)957, ""+(char)957);  //  GREEK SMALL LETTER nu
		map.put((Integer)958, ""+(char)958);  //  GREEK SMALL LETTER xi
		map.put((Integer)959, ""+(char)959);  //  GREEK SMALL LETTER omicron
		map.put((Integer)960, ""+(char)960);  //  GREEK SMALL LETTER pi
		map.put((Integer)961, ""+(char)961);  //  GREEK SMALL LETTER rho
		map.put((Integer)962, ""+(char)962);  //  GREEK SMALL LETTER FINAL SIGMA
		map.put((Integer)963, ""+(char)963);  //  GREEK SMALL LETTER SIGMA
		map.put((Integer)964, ""+(char)964);  //  GREEK SMALL LETTER TAU
		map.put((Integer)965, ""+(char)965);  //  GREEK SMALL LETTER UPSILON
		map.put((Integer)966, ""+(char)966);  //  GREEK SMALL LETTER PHI
		map.put((Integer)967, ""+(char)967);  //  GREEK SMALL LETTER CHI
		map.put((Integer)968, ""+(char)968);  //  GREEK SMALL LETTER PSI
		map.put((Integer)969, ""+(char)969);  //  GREEK SMALL LETTER OMEGA
		
		map.put((Integer)8710, ""+(char)916);  // large delta //needs changing
		map.put((Integer)181, ""+(char)956);  //  mu
		char2UnicodeStringMapByFontFamilyMap.put(TIMES_GREEK_SF.toLowerCase(), map);
	}

	private void createSymbolPSMap() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put((Integer)61, "=");  // equals
		map.put((Integer)8764, ""+(char)'~');  // tilde
		char2UnicodeStringMapByFontFamilyMap.put(SYMBOL_PS.toLowerCase(), map);
	}

	private void createMTSYNMap() {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put((Integer)48, ""+(char)'\'');  // prime
		map.put((Integer)8677, ""+(char)215);  // multiply
		char2UnicodeStringMapByFontFamilyMap.put(MTSYN.toLowerCase(), map);
	}

	private void createCommPiMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("H18456", (int)'!');  // bang
		foreign2UnicodeMapByFontFamily.put(COMM_PI.toLowerCase(), map);
	}

	private void createGreekWithMathPiMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("H11001", (int)'+');  // plus
		map.put("H11002", (int)'-');  // minus 
		map.put("H11003", (int)215);  // multiply 
		map.put("H11005", (int)'=');  // equals 
		map.put("H11021", (int)'<');  // LT 
		foreign2UnicodeMapByFontFamily.put(GREEK_WITH_MATH_PI.toLowerCase(), map);
	}


	private void createMathematicalPiOneMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("H11001", (int)'+');  // plus -> plus
		map.put("H11002", (int)'-');  // minus -> hyphenminus
		map.put("H11032", (int)'\'');  // Prime -> apos
		map.put("H11034", (int)176);  // Degree sign -> degree
		foreign2UnicodeMapByFontFamily.put(MATHEMATICAL_PI_ONE.toLowerCase(), map);
	}
	
	private void createMathematicalPiFourMap() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("H11554", 183);  // middot
		foreign2UnicodeMapByFontFamily.put(MATHEMATICAL_PI_FOUR.toLowerCase(), map);
	}

	String getUnicodeEquivalent(String fontFamily, Integer charCode) {
		String unicodeString = null;
		fontFamily = fontFamily.toLowerCase();
		Map<Integer, String> char2UnicodeStringMap = getChar2UnicodeStringMap(fontFamily);
		if (char2UnicodeStringMap != null) {
			unicodeString = char2UnicodeStringMap.get(charCode);
		}
		return unicodeString;
	}

	private Map<Integer, String> getChar2UnicodeStringMap(String fontFamily) {
		ensureChar2UnicodeStringMapByFontFamily();
		return char2UnicodeStringMapByFontFamilyMap.get(fontFamily);
	}

	private void ensureChar2UnicodeStringMapByFontFamily() {
		if (char2UnicodeStringMapByFontFamilyMap == null) {
			char2UnicodeStringMapByFontFamilyMap = new HashMap<String, Map<Integer, String>>();
			createTimesNRExpertMTMap();
			createTimesGreekSFMap();
			createMTSYNMap();
			createSymbolPSMap();
		}
	}

	public Map<String, AMIFont> getAmiFontByFontNameMap() {
		ensureAMIFontByFontNameMap();
		return amiFontByFontNameMap;
	}
	
	public AMIFont getAmiFontByFontName(String fontName) {
		getAmiFontByFontNameMap();
		return amiFontByFontNameMap.get(fontName);
	}

	static void analyzeFontSubType(String fontSubType) {
		if (FONT_TYPE1.equals(fontSubType)) {
			//  
		} else if (FONT_TRUE_TYPE.equals(fontSubType)) {
			//  
		} else if (FONT_TYPE0.equals(fontSubType)) {
			//  
		} else {
			System.out.println(fontSubType);
		}
	}

	private void logFontDict(int level, COSDictionary dict) {

		String indent = "";
		for (int i = 0; i < level; i++)
			indent += " ";

		LOG.debug(String.format("%s****************** level %d font dict:",
				indent, level));

		level++;
		indent += "    ";

		for (COSName key : dict.keySet())
			LOG.debug(String.format("%s****************** %s = %s", indent,
					key.getName(), dict.getDictionaryObject(key)));

		COSArray array = (COSArray) dict
				.getDictionaryObject(COSName.DESCENDANT_FONTS);
		if (array != null) {
			LOG.debug(String.format(
					"%s****************** descendant fonts (%d):", indent,
					array.size()));

			logFontDict(level, (COSDictionary) array.getObject(0));
		}
	}

	public AMIFont getAmiFontByFont(PDFont pdFont) {
		ensureAMIFontByFontNameMap();
		String fontName = null;
		AMIFont amiFont = null;
		PDFontDescriptor fd = pdFont.getFontDescriptor();
		if (fd == null && pdFont instanceof PDType0Font) {
			COSDictionary dict = (COSDictionary) pdFont.getCOSObject();
			COSArray array = (COSArray) dict
					.getDictionaryObject(COSName.DESCENDANT_FONTS);
			PDFont descendantFont;
			try {
				descendantFont = PDFontFactory.createFont((COSDictionary) array
						.getObject(0));
				fd = descendantFont.getFontDescriptor();
			} catch (IOException e) {
				LOG.error("****************** Can't create descendant font!");
			}
		}
		if (fd == null) {
			LOG.error("****************** Null Font Descriptor : "+pdFont);
			logFontDict(0, (COSDictionary) pdFont.getCOSObject());
		} else {
			fontName = fd.getFontName();
			if (fontName == null) {
				throw new RuntimeException("No currentFontName");
			}
		}
		amiFont = amiFontByFontNameMap.get(fontName);
		if (amiFont == null) {
			if (pdFont instanceof PDType1Font || pdFont instanceof PDTrueTypeFont || pdFont instanceof PDType0Font) {
				amiFont = new AMIFont(pdFont);
				amiFontByFontNameMap.put(fontName, amiFont);
			} else {
				throw new RuntimeException("Cannot find font type: "+pdFont+" / "+pdFont.getSubType()+", ");
			}
		}
		return amiFont;
	}

	public void ensureSymbol2UnicodeMap() {
		ensureSymbol2UnicodeMap(SYMBOL2UNICODE_XML);
	}

	/** hopefully this map is independent of font; if not we may have to move it to
	 * individual AMIFonts
	 * @param symbol2UnicodeResource
	 */
	public void ensureSymbol2UnicodeMap(String symbol2UnicodeResource) {
		if (symbol2UnicodeMap == null) {
			symbol2UnicodeMap = new HashMap<String, Integer>();
			try {
				InputStream is = Util.getResourceUsingContextClassLoader(symbol2UnicodeResource, this.getClass());
				Element conversionElement = new Builder().build(is).getRootElement();
				Elements charDataElements = conversionElement.getChildElements();
				for (int i = 0; i < charDataElements.size(); i++) {
					Element charDataElement = charDataElements.get(i);
					String charname = charDataElement.getAttributeValue(CHARNAME);
					String unicodeS = charDataElement.getAttributeValue(CODEPOINT);
					Integer unicodePoint = new Integer(unicodeS);
					symbol2UnicodeMap.put(charname, unicodePoint);
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot read/parse symbolConverter: "+symbol2UnicodeResource, e);
			}
		}
	}
	
	public Integer convertSymbol2Unicode(String symbol) {
		Integer codePoint = null;
		ensureSymbol2UnicodeMap();
		String s = (symbol == null) ? null : StandardEncoding.INSTANCE.getCharacter(symbol);
		// for ?unknown? charnames (e.g. "H1101") appears to return the charname unchanged
		if (s != null) {
			if (!s.equals(symbol) && s.length() == 1) {
				LOG.trace(symbol+" => "+s);
				codePoint = new Integer((int) s.charAt(0));
			} else {
				s = null; 
			}
		}
		if (s == null) {
			codePoint = symbol2UnicodeMap.get(symbol);
			LOG.trace("Used symbol2UnicodeMap to translate: "+symbol+" => "+((codePoint == null) ? null : (char)(int)codePoint));
		}
		return codePoint;
	}

}

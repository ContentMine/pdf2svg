package org.xmlcml.graphics.pdf2svg.raw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.encoding.DictionaryEncoding;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMatrix;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.graphics.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorState;
import org.apache.pdfbox.pdmodel.text.PDTextState;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.TextPosition;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.GraphicsElement.FontStyle;
import org.xmlcml.graphics.svg.GraphicsElement.FontWeight;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;

/** converts a PDPage to SVG
 * Originally used PageDrawer to capture the PDF operations.These have been
 * largely intercepted and maybe PageDrawer could be retired at some stage
 * @author pm286 and Murray Jensen
 *
 */
public class PDFPage2SVGConverter extends PageDrawer {

	
	private static final String COMM_PI = "Universal-NewswithCommPi";
	private static final String GREEK_WITH_MATH_PI = "Universal-GreekwithMathPi";
	private static final String MTSYN = "MTSYN";
	private static final String TIMES_ROMAN_GROTTY = "Times-Roman";
	private static final String SYMBOL_PS = "SymbolPS";
	private static final String TIMES_GREEK_SF = "TimesGreekSF";
	private static final String TIMES_NR_EXPERT_MT = "TimesNRExpertMT";
	
	private static final String MATHEMATICAL_PI_ONE = "MathematicalPi-One";
	private static final String MATHEMATICAL_PI_FOUR = "MathematicalPi-Four";
	
	private static final String FONT_TRUE_TYPE = "TrueType";
	private static final String FONT_TYPE1 = "Type1";
	private static final String FONT_TYPE0 = "Type0";
	private static final String BADCHAR_E = "?}";
	private static final String BADCHAR_S = "{?";
	private static final String FONT_NAME = "fontName";
	private static final String BOLD = "bold";
	private static final String ITALIC = "italic";
	private static final String INCLINED = "inclined";
	private static final String OBLIQUE = "oblique";

	private final static Logger LOG = Logger.getLogger(PDF2SVGConverter.class);

	private static final Dimension DEFAULT_DIMENSION = new Dimension(800, 800);
	private static final int BADCHAR = (char)0X2775;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static double eps = 0.001;

	private BasicStroke basicStroke;
	private SVGSVG svg;
	private Composite composite;
	private Paint paint;
	private PDGraphicsState graphicsState;
	private Matrix textPos;
	private PDFont font;

	private Composite currentComposite;
	private String fontFamily;
	private String fontName;
	private Double currentFontSize;
	private String currentFontStyle;
	private String currentFontWeight;
	private Paint currentPaint;
	private String currentStroke;
	private SVGText currentSvgText;
	
	private int nPlaces = 3;
//	private String renderIntent;
	private PDLineDashPattern dashPattern;
	private Double lineWidth;
	private PDTextState textState;
	private Set<String> clipStringSet;
	private String clipString;
	private PDF2SVGConverter converter;
	private Encoding encoding;
	private String charname;
	private Real2 currentXY;
	private String fontSubType;
	private DictionaryEncoding dictionaryEncoding;
	private String textContent;
	private Map<String, Map<String, Integer>> foreign2UnicodeMapByFontFamily;
	private Map<String, Map<Integer, String>> char2UnicodeStringMapByFontFamilyMap;
	private String lastFontFamily = "Helvetica";

	public PDFPage2SVGConverter() throws IOException {
		super();
	}

	public void drawPage(PDPage p) {
		ensurePageSize();
		page = p;

		try {
			if (page.getContents() != null) {
				PDResources resources = page.findResources();
				processStream(page, resources, page.getContents().getStream());
			}
		} catch (Exception e) {
			// PDFBox routines have a very bad feature of trapping exceptions
			// this is the best we can do to alert you at this stage
			e.printStackTrace();
			LOG.error("***FAILED " + e);
			throw new RuntimeException("drawPage", e);
		}
		reportClipPaths();

	}

	private void reportClipPaths() {
		ensureClipStringSet();
		String[] color = {"yellow", "blue", "red", "green", "magenta", "cyan"};
		LOG.debug("Clip paths: "+clipStringSet.size());
		int icol = 0;
		for (String shapeString : clipStringSet) {
			LOG.debug(shapeString);
			SVGPath path = new SVGPath(shapeString);
			SVGRect box = new SVGRect(path.getBoundingBox());
			box.setFill("none");
			box.setStroke(color[icol]);
			box.setOpacity(1.0);
			box.setStrokeWidth(5.0);
			svg.appendChild(box);
			icol = (icol+1) % 6;
		}
		
	}


	/** adds a default pagesize if not given
	 * 
	 */
	private void ensurePageSize() {
		if (pageSize == null) {
			pageSize = DEFAULT_DIMENSION;
		}
	}
	

	@Override
	protected void processTextPosition(TextPosition text) {

		charname = null;
		fontFamily = null;
		fontName = null;
		font = text.getFont();
		fontSubType = font.getSubType();
		normalizeFontFamilyNameStyleWeight();
		if (FONT_TYPE1.equals(fontSubType)) {
			//  
		} else if (FONT_TRUE_TYPE.equals(fontSubType)) {
			//  
		} else if (FONT_TYPE0.equals(fontSubType)) {
			//  
		} else {
			System.out.println(fontSubType);
		}
		encoding = font.getFontEncoding();
		dictionaryEncoding = null;
		castEncodingSubclassEspeciallyDictionary();
		textContent = text.getCharacter();
		if (textContent.length() > 1) {
			// this can happen for ligatures
			LOG.warn("multi-char string: "+text.getCharacter());
		}
		int charCode = text.getCharacter().charAt(0);
		if (charCode > 255) {
			converter.getHighCodePointSet().add(charCode);
		}
		if (encoding == null) {
			LOG.warn("Null encoding for character: "+charCode+" at "+currentXY);
		} else {
			try {
//				charname = encoding.getNameFromCharacter((char)charCode);
				// NNOTE: charname is the formal name for the character such as "period", "bracket" or "a", "two"
				charname = encoding.getName(charCode);
				LOG.trace("code "+charCode+" (font: "+fontSubType+" "+fontName+") "+charname);
			} catch (IOException e1) {
				LOG.warn("cannot get char encoding "+" at "+currentXY, e1);
			}
		}
		float width = getCharacterWidth(font, textContent);
		
		ensurePageSize();
		SVGText svgText = new SVGText();
		createAndReOrientateTextPosition(text, svgText);
		normalizeFontFamilyNameStyleWeight();
		if (currentFontWeight != null) {
			svgText.setFontWeight(currentFontWeight);
		}
		if (dictionaryEncoding != null) {
			LOG.trace("DICT_ENCODE "+fontName+" / "+fontFamily+" / "+fontSubType+" / "+charCode+" / "+charname);
			Integer codePoint = convertDictionaryFontCharactersToUnicode(fontFamily, charname);
			if (codePoint != null) {
				textContent = ""+(char)(int)codePoint;
				noteUpdated(svgText, textContent);
			} else {
				String unicodeContent = getUnicodeEquivalent(fontFamily, charCode);
				if (unicodeContent != null) {
					textContent = unicodeContent;
					if (unicodeContent.length() > 1) {
						LOG.trace("X: "+unicodeContent);
					}
					noteUpdated(svgText, unicodeContent);
				} else {
					LOG.debug("DICT_ENCODE uncoverted "+fontName+" / "+fontFamily+" / "+fontSubType+" / "+charCode+" / "+charname +" / "+(char) charCode);
					svgText.setFontSize(20.0);
					svgText.setFill("blue");
				}
			}
		}

		try {
			svgText.setText(textContent);
		} catch (RuntimeException e) {
			// drops here if cannot encode as XML character
			tryToConvertStrangeCharactersOrFonts(text, svgText);
		}
		createGraphicsStateAndPaintAndComposite();
		
		getFontSizeAndSetNotZeroRotations(svgText);
		getClipPath();
		svgText.setClipPath(clipString);
		svgText.setFontSize(currentFontSize);
		String stroke = getCSSColor((Color) paint);
		svgText.setStroke(stroke);
		svgText.setFontStyle(currentFontStyle);
		svgText.setFontFamily(fontFamily);
		setFontName(svgText, fontName);
		setCharacterWidth(svgText, width);
		svgText.format(nPlaces);
		svg.appendChild(svgText);
		lastFontFamily = fontFamily;
	}

	private void noteUpdated(SVGText svgText, String unicodeContent) {
		svgText.setText(unicodeContent);
		svgText.setFill("red");
		svgText.setStrokeWidth(1.0);
		svgText.setStroke("blue");
		svgText.setFontSize(20.0);
		fontFamily = lastFontFamily;
	}

	private String getUnicodeEquivalent(String fontFamily, Integer charCode) {
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
		};
	}

	private Integer convertDictionaryFontCharactersToUnicode(String fontFamily, String charname) {
		fontFamily = fontFamily.toLowerCase();
		Integer codePoint = null;
		Map<String, Integer> foreign2UnicodeMap = getForeign2UnicodeMap(fontFamily);
		if (foreign2UnicodeMap != null) {
			codePoint = foreign2UnicodeMap.get(charname);
		}
		return codePoint;
	}

	private void debugMap(Map<String, Integer> map) {
		String keys[] = map.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		for (String key : keys) {
			LOG.debug(key+": "+map.get(key));
		}
	}

	private Map<String, Integer> getForeign2UnicodeMap(String fontFamily) {
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
		};
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

	private void castEncodingSubclassEspeciallyDictionary() {
		if (encoding == null) {
			LOG.warn("NO ENCODING");
		} else if (encoding instanceof org.apache.pdfbox.encoding.MacRomanEncoding) {
			LOG.trace("mac "+encoding.toString());
		} else if (encoding instanceof org.apache.pdfbox.encoding.WinAnsiEncoding) {
			LOG.trace("win "+encoding.toString());
		} else if (encoding instanceof org.apache.pdfbox.encoding.StandardEncoding) {
			LOG.trace("std "+encoding.toString());
		} else if (encoding instanceof org.apache.pdfbox.encoding.DictionaryEncoding) {
			dictionaryEncoding = (DictionaryEncoding) encoding;
			LOG.trace("DICTENC "+fontName);
		} else {
			LOG.warn("UNKNOWN ENCODING "+encoding.toString());
		}
	}

	private String getClipPath() {
		Shape shape = getGraphicsState().getCurrentClippingPath();
		PathIterator pathIterator = shape.getPathIterator(new AffineTransform());
		clipString = SVGPath.getPathAsDString(pathIterator);
		ensureClipStringSet();
		clipStringSet.add(clipString);
		return clipString;
	}

	private void ensureClipStringSet() {
		if (clipStringSet == null) {
			clipStringSet = new HashSet<String>();
		}
	}

	private float getCharacterWidth(PDFont font, String textContent) {
		float width = 0.0f;
		try {
			width = font.getStringWidth(textContent);
		} catch (IOException e) {
			throw new RuntimeException("PDFBox exception ", e);
		}
		return width;
	}

	private void tryToConvertStrangeCharactersOrFonts(TextPosition text, SVGText svgText) {
		char cc = text.getCharacter().charAt(0);
		String s = BADCHAR_S+(int)cc+BADCHAR_E;
		LOG.debug(s+" "+fontName+" ("+fontSubType+") charname: "+charname);
		s = ""+(char)(BADCHAR+Math.min(9, cc));
		svgText.setText(s);
		svgText.setStroke("red");
		svgText.setFill("red");
		svgText.setFontFamily("Helvetica");
		svgText.setStrokeWidth(0.5);
	}

	private String interpretCharacter(String fontFamily, char cc) {
		String s = null;
		if (MATHEMATICAL_PI_ONE.toLowerCase().equals(fontFamily)) {
			if (cc == 1) {
				s = CMLConstants.S_PLUS;
			}
		} else {
			System.out.println("font "+fontFamily+" point "+(int)cc);
		}
		return s;
	}

	private void normalizeFontFamilyNameStyleWeight() {
		PDFontDescriptor fd = font.getFontDescriptor();
		if (fd == null) {
			LOG.warn("Null Font Descriptor : "+font+" ("+fontSubType+") at "+currentXY);
		} else {
			fontFamily = fd.getFontFamily();
			fontName = fd.getFontName();
			// fontFamily may be null?
			if (fontName == null) {
				throw new RuntimeException("No currentFontName");
			}
			normalizeGrottyFontNames();
			if (fontFamily == null) {
				fontFamily = fontName;
			}
			// strip leading characters (e.g. KAIKCD+Helvetica-Oblique);
			stripFamilyPrefix();
			createFontMatch();
			createFontStyle();
			createFontWeight();
		}
	}

	private void normalizeGrottyFontNames() {
		if (TIMES_ROMAN_GROTTY.equalsIgnoreCase(fontName)) {
			fontName = "UNKNWN+TimesNewRoman";
		}
	}

	private void stripFamilyPrefix() {
		int index = fontName.indexOf("+");
		LOG.trace(fontName);
		if (index == 6) {
			fontFamily = fontName.substring(index+1);
		} else {
			LOG.warn("FontName lacks XXXXXX prefix:"+fontName+" / "+index);
		}
	}
	
	private void createFontMatch() {
//		Pattern fontNamePattern = Pattern.compile("([A-Z]{6}\\+)?([\\-\\.][^\\-\\.]+)*");
		Pattern fontNamePattern = Pattern.compile("([A-Z]{6}\\+).*");
		Matcher matcher = fontNamePattern.matcher(fontName);
		if (!matcher.matches()) {
			LOG.warn("bad fontName/Family: "+fontName+" / "+fontFamily);
		} else {
			LOG.trace(matcher.group(1));
		}
//		LOG.trace(matcher.group(2));
	}
	
	private void createFontStyle() {
		String ff = fontFamily.toLowerCase();
		int idx = ff.indexOf("-");
		String suffix = ff.substring(idx+1).toLowerCase();
		if (suffix.equals(OBLIQUE) || suffix.equals(ITALIC) || suffix.equals(INCLINED)) {
			currentFontStyle = FontStyle.ITALIC.toString().toLowerCase();
			fontFamily = fontFamily.substring(0, idx);
		} else {
			currentFontStyle = null;
		}
	}

	/** looks for -bold in font name
	 * 
	 */
	private void createFontWeight() {
		String ff = fontFamily.toLowerCase();
		int idx = ff.indexOf("-");
		String suffix = ff.substring(idx+1);
		if (suffix.equals(BOLD)) {
			currentFontWeight = FontWeight.BOLD.toString().toLowerCase();
			fontFamily = fontFamily.substring(0, idx);
		} else {
			currentFontWeight = null;
		}
	}

	/** translates java color to CSS RGB
	 * 
	 * @param paint
	 * @return CCC as #rrggbb (alpha is currently discarded)
	 */
	private static String getCSSColor(Paint paint) {
		int r = ((Color) paint).getRed();
		int g = ((Color) paint).getGreen();
		int b = ((Color) paint).getBlue();
		int a = ((Color) paint).getAlpha();
		int rgb = (r<<16)+(g<<8)+b;
		String colorS = String.format("#%06x", rgb);
		if (rgb != 0) {
			LOG.trace("Paint "+rgb+" "+colorS);
		}
		return colorS;
	}

	private double getFontSizeAndSetNotZeroRotations(SVGText svgText) {
		AffineTransform at = textPos.createAffineTransform();
		PDMatrix fontMatrix = font.getFontMatrix();
		at.scale(fontMatrix.getValue(0, 0) * 1000f,
				fontMatrix.getValue(1, 1) * 1000f);
		double scalex = at.getScaleX();
		double scaley = at.getScaleY();
		double scale = Math.sqrt(scalex * scaley);
		Transform2 t2 = new Transform2(at);
		
		Angle angle = t2.getAngleOfRotation();
		int angleDeg = Math.round((float)angle.getDegrees());
		if (angleDeg != 0) {
			LOG.trace("Transform "+t2+" "+svgText.getText()+" "+at+" "+getRealArray(fontMatrix));
			// do this properly later
			scale = Math.sqrt(Math.abs(t2.elementAt(0, 1)*t2.elementAt(1, 0)));
			Transform2 t2a = Transform2.getRotationAboutPoint(angle, svgText.getXY());
			svgText.setTransform(t2a);
		}
		currentFontSize = scale;
		return currentFontSize;
	}

	private RealArray getRealArray(PDMatrix fontMatrix) {
		double[] dd = new double[9];
		int kk = 0;
		int nrow = 2;
		int ncol = 3;
		for (int irow = 0; irow < nrow; irow++) {
			for (int jcol = 0; jcol < ncol; jcol++) {
				dd[kk++] = fontMatrix.getValue(irow, jcol);
			}
		}
		RealArray ra = new RealArray(dd);
		return ra;
	}

	/** changes coordinates because AWT and SVG use top-left origin while PDF uses bottom left
	 * 
	 * @param text
	 * @param svgText
	 */
	private void createAndReOrientateTextPosition(TextPosition text, SVGText svgText) {
		textPos = text.getTextPos().copy();
		float x = textPos.getXPosition();
		// the 0,0-reference has to be moved from the lower left (PDF) to
		// the upper left (AWT-graphics)
		float y = pageSize.height - textPos.getYPosition();
		// Set translation to 0,0. We only need the scaling and shearing
		textPos.setValue(2, 0, 0);
		textPos.setValue(2, 1, 0);
		// because of the moved 0,0-reference, we have to shear in the
		// opposite direction
		textPos.setValue(0, 1, (-1) * textPos.getValue(0, 1));
		textPos.setValue(1, 0, (-1) * textPos.getValue(1, 0));
		currentXY = new Real2(x, y);
		svgText.setXY(currentXY);
	}

	private void createGraphicsStateAndPaintAndComposite() {
		try {
			graphicsState = getGraphicsState();
			switch (graphicsState.getTextState().getRenderingMode()) {
			case PDTextState.RENDERING_MODE_FILL_TEXT:
				composite = graphicsState.getNonStrokeJavaComposite();
				paint = graphicsState.getNonStrokingColor().getJavaColor();
				if (paint == null) {
					paint = graphicsState.getNonStrokingColor().getPaint(
							pageSize.height);
				}
				break;
			case PDTextState.RENDERING_MODE_STROKE_TEXT:
				composite = graphicsState.getStrokeJavaComposite();
				paint = graphicsState.getStrokingColor().getJavaColor();
				if (paint == null) {
					paint = graphicsState.getStrokingColor().getPaint(
							pageSize.height);
				}
				break;
			case PDTextState.RENDERING_MODE_NEITHER_FILL_NOR_STROKE_TEXT:
				// basic support for text rendering mode "invisible"
				Color nsc = graphicsState.getStrokingColor().getJavaColor();
				float[] components = { Color.black.getRed(),
						Color.black.getGreen(), Color.black.getBlue() };
				paint = new Color(nsc.getColorSpace(), components, 0f);
				composite = graphicsState.getStrokeJavaComposite();
				break;
			default:
				// TODO : need to implement....
				System.out.println("Unsupported RenderingMode "
						+ this.getGraphicsState().getTextState()
								.getRenderingMode()
						+ " in PageDrawer.processTextPosition()."
						+ " Using RenderingMode "
						+ PDTextState.RENDERING_MODE_FILL_TEXT + " instead");
				composite = graphicsState.getNonStrokeJavaComposite();
				paint = graphicsState.getNonStrokingColor().getJavaColor();
			}
		} catch (IOException e) {
			throw new RuntimeException("graphics state error???", e);
		}
	}

	/** traps any remaining unimplemented PDDrawer calls
	 * 
	 */
	public Graphics2D getGraphics() {
		System.err.printf("getGraphics was called!!!!!!! (May mean method was not overridden) %n");
		return null;
	}

	public void fillPath(int windingRule) throws IOException {
		PDColorState colorState = getGraphicsState().getNonStrokingColor();
		Paint currentPaint = getCurrentPaint(colorState, "non-stroking");
		createAndAddSVGPath(windingRule, currentPaint);
	}

	public void strokePath() throws IOException {
		PDColorState colorState = getGraphicsState().getStrokingColor(); 
		Paint currentPaint = getCurrentPaint(colorState, "stroking");
		Integer windingRule = null;
		createAndAddSVGPath(windingRule, currentPaint);
	}

	/** processes both stroke and fill for paths
	 * 
	 * @param windingRule if not null implies fill else stroke
	 * @param currentPaint
	 */
	private void createAndAddSVGPath(Integer windingRule, Paint currentPaint) {
//		renderIntent = getGraphicsState().getRenderingIntent(); // probably ignorable at first (converts color maps)
		dashPattern = getGraphicsState().getLineDashPattern();
		lineWidth = getGraphicsState().getLineWidth();
		textState = getGraphicsState().getTextState();  // has things like character and word spacings // not yet used
		GeneralPath generalPath = getLinePath();
		if (windingRule != null) {
			generalPath.setWindingRule(windingRule);
		}
		SVGPath svgPath = new SVGPath(generalPath);
		getClipPath();
		svgPath.setClipPath(clipString);
		if (windingRule != null) {
			svgPath.setFill(getCSSColor(currentPaint));
		} else {
			svgPath.setStroke(getCSSColor(currentPaint));
		}
		if (dashPattern != null) {
			setDashArray(svgPath);
		}
		if (lineWidth > 0.00001) {
			svgPath.setStrokeWidth(lineWidth);
			LOG.trace("stroke "+lineWidth);
		}
		svgPath.format(nPlaces);
		svg.appendChild(svgPath);
		generalPath.reset();
	}

	private void setDashArray(SVGPath svgPath) {
		List<Integer> dashIntegerList = (List<Integer>) dashPattern.getDashPattern();
		StringBuilder sb = new StringBuilder("");
		LOG.trace("COS ARRAY "+dashIntegerList.size());
		if (dashIntegerList.size() > 0) {
			for (int i = 0; i < dashIntegerList.size(); i++) {
				if (i > 0) {
					sb.append(" ");
				}
				sb.append(dashIntegerList.get(i));
			}
			svgPath.setStrokeDashArray(sb.toString());
			LOG.debug("dash "+dashPattern);
		}
	}

	private Paint getCurrentPaint(PDColorState colorState, String type) throws IOException {
		Paint currentPaint = colorState.getJavaColor();
		if (currentPaint == null) {
			currentPaint = colorState.getPaint(pageSize.height);
		}
		if (currentPaint == null) {
			LOG.warn("ColorSpace "
					+ colorState.getColorSpace().getName()
					+ " doesn't provide a " + type
					+ " color, using white instead!");
			currentPaint = Color.WHITE;
		}
		return currentPaint;
	}

	/** maye be removed later
	 * 
	 */
	public void drawImage(Image awtImage, AffineTransform at) {
		System.out
				.printf("\tdrawImage: awtImage='%s', affineTransform='%s', composite='%s', clip='%s'%n",
						awtImage.toString(), at.toString(), getGraphicsState()
								.getStrokeJavaComposite().toString(),
						getGraphicsState().getCurrentClippingPath().toString());
		// LOG.error("drawImage Not yet implemented");
	}

	/** used in pageDrawer - shaded type of fill
	 * 
	 */
	public void shFill(COSName shadingName) throws IOException {
		throw new IOException("Not Implemented");
	}

	/** creates new <svg> and removes/sets some defaults
	 * this is partly beacuse SVGFoo may have defaults (bad idea?)
	 */
	public void resetSVG() {
		this.svg = new SVGSVG();
		svg.setStroke("none");
		svg.setStrokeWidth(0.0);
		svg.addNamespaceDeclaration(PDF2SVGUtil.SVGX_PREFIX, PDF2SVGUtil.SVGX_NS);
		clipStringSet = new HashSet<String>();
	}

	public SVGSVG getSVG() {
		return svg;
	}

	void convertPageToSVG(PDPage page, PDF2SVGConverter converter) {
		this.converter = converter;
		resetSVG();
		drawPage(page);
	}
	
	private void setFontName(SVGElement svgElement, String fontName) {
		PDF2SVGUtil.setSVGXAttribute(svgElement, FONT_NAME, fontName);
	}
	
	private void setCharacterWidth(SVGElement svgElement, double width) {
		PDF2SVGUtil.setSVGXAttribute(svgElement, PDF2SVGUtil.CHARACTER_WIDTH, ""+width);
	}
	
	@Override
	public void setStroke(BasicStroke basicStroke) {
		this.basicStroke = basicStroke;
	}

	@Override
	public BasicStroke getStroke() {
		return basicStroke;
	}

	private static String fmtFont(PDFont font) {
		PDFontDescriptor fd = font.getFontDescriptor();
		String format = null;
		try {
			format = String
					.format("[family=%s,name:%s,weight=%f,angle=%f,charset=%s,avg-width=%f]",
							fd.getFontFamily(), fd.getFontName(),
							fd.getFontWeight(), fd.getItalicAngle(),
							fd.getCharSet(), fd.getAverageWidth());
		} catch (IOException e) {
			throw new RuntimeException("Average width problem", e);
		}
		return format;
	}


}

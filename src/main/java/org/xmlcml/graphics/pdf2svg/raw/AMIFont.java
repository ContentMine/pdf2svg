package org.xmlcml.graphics.pdf2svg.raw;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.pdfbox.encoding.DictionaryEncoding;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.encoding.MacRomanEncoding;
import org.apache.pdfbox.encoding.StandardEncoding;
import org.apache.pdfbox.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/** wrapper for PDType1Font. is meant to manage the badFontnames, other
 * fontTypes, etc and try to convert them to a standard approach. 
 * Splits all Italic, etc. to attributes of AMIFont. 
 * May ultimately be unneccessary
 * 
 * @author pm286
 *
 */
public class AMIFont {

	private final static Logger LOG = Logger.getLogger(AMIFont.class);
	
	// order may matter
	private static final String[] BOLD_SUFFIXES = new String[]{
		"-Bold", 
		".Bold", 
		".B", 
		"Bold"
		};
	private static final String[] ITALIC_SUFFIXES = new String[]{
		"-Italic", 
		".Italic", 
		".I", 
		"Italic",
		"-Oblique", 
		".Oblique", 
//		".I", // unlikely
		"Oblique",
		};
	
	private static final String MONOTYPE_SUFFIX = "MT";
	private static final String POSTSCRIPT_SUFFIX = "PS";
	
	private Boolean isBold = null;
	private Boolean isItalic = null;
	private Boolean isSymbol = null;
	private String fontFamily;
	private String fontName;
	
	private PDFont pdFont;
	private PDType0Font type0Font;
	private PDType1Font type1Font;
	private PDTrueTypeFont trueTypeFont;
	private PDFontDescriptor fontDescriptor;
	
	private int currentIndex;
	
	static Pattern leaderPattern = Pattern.compile("[A-Z]{6}\\+.*");
	private boolean hasPrefix;
	private String finalSuffix;

	private Encoding encoding;
	private DictionaryEncoding dictionaryEncoding;
	private MacRomanEncoding macRomanEncoding;
	private StandardEncoding standardEncoding;
	private WinAnsiEncoding winAnsiEncoding;

	private String baseFont;



	
	static {
		String[] standard14Names = PDType1Font.getStandard14Names();
		for (String standard14Name : standard14Names) {
//			System.out.println(standard14Name);
		}
	}

	/**
        addFontMapping("Times-Roman","TimesNewRoman");
        addFontMapping("Times-Italic","TimesNewRoman,Italic");
        addFontMapping("Times-Italic","TimesNewRoman,Italic");
        addFontMapping("Times-ItalicItalic","TimesNewRoman,Italic,Italic");
        addFontMapping("Helvetica-Oblique","Helvetica,Italic");
        addFontMapping("Helvetica-ItalicOblique","Helvetica,Italic,Italic");
        addFontMapping("Courier-Oblique","Courier,Italic");
        addFontMapping("Courier-ItalicOblique","Courier,Italic,Italic");
        
and
    // TODO move the Map to PDType1Font as these are the 14 Standard fonts
    // which are definitely Type 1 fonts
    private static Map<String, FontMetric> getAdobeFontMetrics()
    {
        Map<String, FontMetric> metrics = new HashMap<String, FontMetric>();
        addAdobeFontMetric( metrics, "Courier-Italic" );
        addAdobeFontMetric( metrics, "Courier-ItalicOblique" );
        addAdobeFontMetric( metrics, "Courier" );
        addAdobeFontMetric( metrics, "Courier-Oblique" );
        addAdobeFontMetric( metrics, "Helvetica" );
        addAdobeFontMetric( metrics, "Helvetica-Italic" );
        addAdobeFontMetric( metrics, "Helvetica-ItalicOblique" );
        addAdobeFontMetric( metrics, "Helvetica-Oblique" );
        addAdobeFontMetric( metrics, "Symbol" );
        addAdobeFontMetric( metrics, "Times-Italic" );
        addAdobeFontMetric( metrics, "Times-ItalicItalic" );
        addAdobeFontMetric( metrics, "Times-Italic" );
        addAdobeFontMetric( metrics, "Times-Roman" );
        addAdobeFontMetric( metrics, "ZapfDingbats" );
        return metrics;
    }
        
	 */
	
	/** try to create font from name
	 * usually accessed through createFontFromName()
	 * @param fontName
	 */
	private AMIFont(String fontName) {
		this();
		fontFamily = null;
		this.fontName = fontName;
		Matcher matcher = leaderPattern.matcher(fontName);
		hasPrefix = false;
		if (matcher.matches()) {
			fontName = fontName.substring(7);
			hasPrefix = true;
		}
		processStandardFamilies();
		processIsBoldInName();
		processIsItalicInName();
		processFinalSuffix();
		LOG.debug(fontName);
	}

	private void processFinalSuffix() {
		finalSuffix = null;
		if (fontName.endsWith(MONOTYPE_SUFFIX)) {
			finalSuffix = MONOTYPE_SUFFIX;
		} else if (fontName.endsWith(POSTSCRIPT_SUFFIX)) {
			finalSuffix = POSTSCRIPT_SUFFIX;
		}
		if (finalSuffix != null) {
			int lf = fontName.length();
			fontName =  fontName.substring(lf-finalSuffix.length());
		}
	}

	public AMIFont(PDFont pdFont) {
		this(pdFont, pdFont.getFontDescriptor());
	}

	public AMIFont(PDFont pdFont, PDFontDescriptor fd) {
		this.baseFont = pdFont.getBaseFont();
		this.type1Font = (pdFont instanceof PDType1Font) ? (PDType1Font) pdFont : null;
		this.type0Font = (pdFont instanceof PDType0Font) ? (PDType0Font) pdFont : null;
		this.trueTypeFont = (pdFont instanceof PDTrueTypeFont) ? (PDTrueTypeFont) pdFont : null;
		processFont(pdFont, fd);
	}

	private void processFont(PDFont pdFont, PDFontDescriptor fd) {
		this.pdFont = pdFont;
		fontDescriptor = fd;
		if (fontDescriptor != null) {
			fontName = fontDescriptor.getFontName();
			fontFamily = fontDescriptor.getFontFamily();
			if (fontFamily == null) {
				fontFamily = fontName;
			}
			processStandardFamilies();
			processIsBoldInName();
			processIsItalicInName();
			processFinalSuffix();
			// take fontDescriptor over name extraction
			isBold = fontDescriptor.isForceBold() ? true : isBold;
			isItalic = fontDescriptor.isItalic() ? true : isItalic;
			isSymbol = fontDescriptor.isSymbolic();
			
			processEncoding();
			fontName = fontDescriptor.getFontName();
			LOG.debug("name="+fontName+" fam="+fontFamily+" bold="+isBold +" it="+isItalic+" face="+finalSuffix+" sym="+isSymbol+ " enc="+(encoding == null ? "null" : encoding.getClass().getName()));
		} else {
			LOG.warn("font had no descriptor: "+baseFont);
			fontName = baseFont;
		}
	}

	private void processEncoding() {
		encoding = pdFont.getFontEncoding();
		dictionaryEncoding = (encoding instanceof DictionaryEncoding) ? (DictionaryEncoding) encoding : null;
		macRomanEncoding   = (encoding instanceof MacRomanEncoding)   ? (MacRomanEncoding)   encoding : null;
		standardEncoding   = (encoding instanceof StandardEncoding)   ? (StandardEncoding)   encoding : null;
		winAnsiEncoding    = (encoding instanceof WinAnsiEncoding)    ? (WinAnsiEncoding)    encoding : null;
	}
	
	/** do not call without fontName or PDType1Font
	 * 
	 */
	private AMIFont() {
	}

	public static AMIFont createAMIFontFromName(String fontName) {
		AMIFont amiFont = new AMIFont(fontName);
		return (amiFont.isOK()) ? amiFont : null;
	}
	
	private void processStandardFamilies() {
		processAsFamily("TimesNewRoman");
		if (fontFamily != null) return;
		processAsFamily("Courier");
		if (fontFamily != null) return;
		processAsFamily("Helvetica");
		if (fontFamily != null) return;
		processAsFamily("Symbol");
		if (fontFamily != null) return;
		processAsFamily("ZapfDingbats");
	}
	
	private void processIsBoldInName() {
		// syntactic variants 
		for (String bString : BOLD_SUFFIXES) {
			isBold = isIncluded(bString);
			if (isBold) break;
		}
	}
	
	private void processIsItalicInName() {
		// syntactic variants 
		for (String iString : ITALIC_SUFFIXES) {
			isItalic = isIncluded(iString);
			if (isItalic) break;
		}
	}
	
	private boolean isOK() {
		return 
		isBold != null &&
		isItalic != null &&
		isSymbol != null &&
		fontFamily != null &&
		fontName != null;
	}

	private void processAsFamily(String standardFamilyName) {
		String fontNameLower = fontName.toLowerCase();
		currentIndex = fontNameLower.indexOf(standardFamilyName.toLowerCase());
		if (currentIndex != -1) {
			removeFromFontName(standardFamilyName, currentIndex);
			fontFamily = standardFamilyName;
		}
	}

	private Boolean isIncluded(String suffix) {
		boolean isIncluded = false;
		String fontNameLower = fontName.toLowerCase();
		currentIndex = fontNameLower.indexOf(suffix.toLowerCase());
		if (currentIndex != -1) {
			removeFromFontName(suffix, currentIndex);
			isIncluded = true;
		}
		return isIncluded;
	}

	private void removeFromFontName(String subName, int idx) {
		if (fontName.substring(idx, idx+subName.length()).equalsIgnoreCase(subName)) {
			fontName = fontName.substring(0,  idx)+fontName.substring(idx+1);
		}
	}

	/** should only be used once for each new fontName
	 * 
	 * @param fontName
	 * @return
	 */
	AMIFont createAMIFont(String fontName) {
		AMIFont amiFont = null;
		PDType1Font standardFont = PDType1Font.getStandardFont(fontName);
		if (standardFont != null) {
			amiFont = new AMIFont(standardFont);
		} else {
			amiFont = createAMIFontFromName(fontName);
		}
		return amiFont;
	}

	public Encoding getEncoding() {
		return encoding;
	}

	public DictionaryEncoding getDictionaryEncoding() {
		return dictionaryEncoding;
	}

	public String getFontWeight() {
		return (isBold != null && isBold) ? AMIFontManager.BOLD : null;
	}

	public String getFontName() {
		return fontName;
	}
	
	public String getFontFamily() {
		return fontFamily;
	}
	
	public boolean isSymbol() {
		return (isSymbol == null) ? false : isSymbol;
	}
	
}

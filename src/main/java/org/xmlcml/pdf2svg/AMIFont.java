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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.pdfbox.encoding.DictionaryEncoding;
import org.apache.pdfbox.encoding.Encoding;
import org.apache.pdfbox.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
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
	
	// order may matter - longest/unique strings first
	private static final String[] BOLD_SUFFIXES = new String[]{
		"-SemiBold",
		"SemiBold",
		"-Bold", 
		".Bold", 
		".B", 
		"-B", 
		"Bold",
		};
	private static final String[] ITALIC_SUFFIXES = new String[]{
		"-Italic", 
		".Italic", 
		".I", 
		"-I", 
		"Italic",
		"-Oblique", 
		".Oblique", 
		"Oblique",
		"-Inclined", 
		".Inclined", 
		};
	
	public static final String MONOTYPE_SUFFIX = "MT";
	public static final String POSTSCRIPT_SUFFIX = "PS";
	public static final String ENCODING = "Encoding";
	static Pattern LEADER_PATTERN = Pattern.compile("[A-Z]{6}\\+.*");
	
	private Boolean isBold = null;
	private Boolean isItalic = null;
	private Boolean isSymbol = null;
	private String fontFamilyName;
	private String fontName;
	
	private PDFont pdFont;
	private PDFontDescriptor fontDescriptor;
	private String fontType;
	
	private int currentIndex;
	
	
	private boolean hasPrefix;
	private String finalSuffix;

	private Encoding encoding;
	private String fontEncoding;

	private String baseFont;

	private Map<String, String> pathStringByCharnameMap;
	
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
		fontFamilyName = null;
		fontName = noteAndRemovePrefix(fontName);
		processStandardFamilies();
		processIsBoldInName();
		processIsItalicInName();
		processFinalSuffix();
		LOG.trace(fontName);
	}

	/** create font from family and key attributes
	 * currently used when compiling an external table
	 */
	public AMIFont(String fontFamilyName, String encoding, String type, boolean isSymbol) {
		this();
		this.fontFamilyName = fontFamilyName;
		this.fontEncoding = encoding;
		this.fontType = type;
		this.isSymbol = isSymbol;
	}

	public AMIFont(PDFont pdFont) {
		this(pdFont, pdFont.getFontDescriptor());
	}

	public AMIFont(PDFont pdFont, PDFontDescriptor fd) {
		this.baseFont = pdFont.getBaseFont();
		this.fontType = pdFont.getClass().getSimpleName();
		encoding = pdFont.getFontEncoding();
		fontEncoding = (encoding == null) ? null : encoding.getClass().getSimpleName();
		processFont(pdFont, fd);
	}

	/** do not call without fontName or PDType1Font
	 * 
	 */
	private AMIFont() {
		encoding = null;
	}

	public static AMIFont createAMIFontFromName(String fontName) {
		AMIFont amiFont = new AMIFont(fontName);
		return (amiFont.isOK()) ? amiFont : null;
	}
	
	private void processFont(PDFont pdFont, PDFontDescriptor fd) {
		this.pdFont = pdFont;
		fontDescriptor = fd;
		fontFamilyName = null;
		if (fontDescriptor != null) {
			fontName = fontDescriptor.getFontName();
//			fontFamilySave = fontDescriptor.getFontFamily();
			
			stripFontNameComponents();
			if (fontFamilyName == null) {
				fontFamilyName = fontName;
			}
			LOG.trace("FFFFF "+fontFamilyName);
			// take fontDescriptor over name extraction
			isBold = fontDescriptor.isForceBold() ? true : isBold;
			if (isBold) {
				LOG.trace("bold from Font Descriptor");
			}
			isItalic = fontDescriptor.isItalic() ? true : isItalic;
			if (isItalic) {
				LOG.trace("italic from Font Descriptor");
			}
			isSymbol = fontDescriptor.isSymbolic();
			if (isSymbol) {
				LOG.trace("symbol from Font Descriptor");
			}
			
			fontName = fontDescriptor.getFontName();
			LOG.trace("name="+fontName+" fam="+fontFamilyName+" type="+pdFont.getSubType()+" bold="+isBold +" it="+isItalic+" face="+finalSuffix+" sym="+isSymbol+ " enc="+(encoding == null ? "null" : encoding.getClass().getSimpleName()));
		} else {
			fontName = baseFont;
			stripFontNameComponents();
			if (fontFamilyName == null) {
				fontFamilyName = fontName;
			}
			LOG.debug(this.toString());
			LOG.warn("font had no descriptor: "+baseFont+" / "+fontFamilyName);
			if (fontName.contains("Arial") ||
					fontName.contains("Unicode")) {
				LOG.warn("Encoding in ("+fontName+") forcibly set to "+WinAnsiEncoding.class);
				encoding = new WinAnsiEncoding();
			}
		}
	}

	private void stripFontNameComponents() {
		fontName = noteAndRemovePrefix(fontName);
		processStandardFamilies();
		processIsBoldInName();
		processIsItalicInName();
		processFinalSuffix();
	}

	private String noteAndRemovePrefix(String fontName) {
		this.fontName = fontName;
		Matcher matcher = LEADER_PATTERN.matcher(fontName);
		hasPrefix = false;
		if (matcher.matches()) {
			fontName = fontName.substring(7);
			hasPrefix = true;
		}
		return fontName;
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

	private void processStandardFamilies() {
		processAsFamily("TimesNewRoman");
		if (fontFamilyName != null) return;
		processAsFamily("Courier");
		if (fontFamilyName != null) return;
		processAsFamily("Helvetica");
		if (fontFamilyName != null) return;
		processAsFamily("Symbol");
		if (fontFamilyName != null) return;
		processAsFamily("ZapfDingbats");
	}
	
	private void processIsBoldInName() {
		// syntactic variants 
		boolean isBoldInName = false;
		for (String bString : BOLD_SUFFIXES) {
			isBoldInName = isIncluded(bString);
			if (isBoldInName) break;
		}
		isBold = (isBold != null) ? isBold : isBoldInName;
	}
	
	private void processIsItalicInName() {
		// syntactic variants 
		boolean isItalicInName = false;
		for (String iString : ITALIC_SUFFIXES) {
			isItalicInName = isIncluded(iString);
			if (isItalicInName) break;
		}
		isItalic = (isItalic != null) ? isItalic : isItalicInName;
	}
	
	private boolean isOK() {
		return 
		isBold != null &&
		isItalic != null &&
		isSymbol != null &&
		fontFamilyName != null &&
		fontName != null;
	}

	private void processAsFamily(String standardFamilyName) {
		String fontNameLower = fontName.toLowerCase();
		currentIndex = fontNameLower.indexOf(standardFamilyName.toLowerCase());
		if (currentIndex != -1) {
			removeFromFontName(standardFamilyName, currentIndex);
			fontFamilyName = standardFamilyName;
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

	public String getFontEncoding() {
		return fontEncoding;
	}

	public DictionaryEncoding getDictionaryEncoding() {
		return (encoding instanceof DictionaryEncoding) ? (DictionaryEncoding) encoding : null;
	}

	public String getFontWeight() {
		return (isBold != null && isBold) ? AMIFontManager.BOLD : null;
	}

	public String getFontName() {
		return fontName;
	}
	
	public String getFontFamilyName() {
		return fontFamilyName;
	}
	
	public String getFontType() {
		return fontType;
	}
	
	public boolean isSymbol() {
		return (isSymbol == null) ? false : isSymbol;
	}
	
	public String getBaseFont() {
		return baseFont;
	}

	public FontFamily getFontFamily() {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isItalic() {
		return isItalic;
	}
	
	public Boolean isBold() {
		return isBold;
	}

	public Map<String, String> getPathStringByCharnameMap() {
		ensurePathStringByCharnameMap();
		return pathStringByCharnameMap;
	}

	private void ensurePathStringByCharnameMap() {
		if (this.pathStringByCharnameMap == null) {
			pathStringByCharnameMap = new HashMap<String, String>();
		}
	}
	
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("isBold: ");
		sb.append(isBold);
		sb.append("; isItalic: ");
		sb.append(isItalic);
		sb.append("; isSymbol: ");
		sb.append(isSymbol);
		sb.append("; fontFamilyName: ");
		sb.append(fontFamilyName);
		sb.append("; fontName: ");
		sb.append(fontName);
		sb.append("; pdFont: ");
		sb.append(pdFont);
		sb.append("; fontDescriptor: ");
		sb.append(fontDescriptor);
		sb.append("; fontType: ");
		sb.append(fontType);
		sb.append("; encoding: ");
		sb.append(encoding);
		sb.append("; fontEncoding: ");
		sb.append(fontEncoding);
		sb.append("; baseFont: ");
		sb.append(baseFont);
		
		return sb.toString();
	}
	
}

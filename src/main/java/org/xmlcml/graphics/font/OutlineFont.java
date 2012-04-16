package org.xmlcml.graphics.font;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;

public class OutlineFont {
	
	private static final String CHAR = "char";
	private static final String STROKE_COUNT = "strokeCount";
	public static final String GENERIC_FAMILY = "genericFamily";
	public static final String FONT_STYLE = "fontStyle";
	public static final String FONT_NAME = "fontName";
	public static final String GLYPH = "glyph";
	
	private Map<String, String> glyphMap = null;
	private Map<String, Glyph> glyphBySigMap = new HashMap<String, Glyph>();
	private Element fontRoot;
	private String fontName;
	private String fontStyle;
	private String genericFamily;
	private Elements glyphs;
	private Map<Integer, Glyph> glyphByCodePoint = new HashMap<Integer, Glyph>();

	private OutlineFont() {
		glyphMap = new HashMap<String, String>();
	}
	
	public Map<String, String> getGlyphMap() {
		return glyphMap;
	}

	public void setGlyphMap(Map<String, String> glyphMap) {
		this.glyphMap = glyphMap;
	}

	public Element getFontRoot() {
		return fontRoot;
	}

	public void setFontRoot(Element fontRoot) {
		this.fontRoot = fontRoot;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public String getGenericFamily() {
		return genericFamily;
	}

	public void setGenericFamily(String genericFamily) {
		this.genericFamily = genericFamily;
	}

	public OutlineFont(Element fontRoot) {
		this();
		this.fontRoot = (Element) fontRoot.copy();
		analyzeFont();
	}
	
	public static OutlineFont readAndCreateFont(String resource) {
		InputStream inputStream = OutlineFont.class.getClassLoader().getResourceAsStream(resource);
		if (inputStream == null) {
			throw new RuntimeException("Cannot find resource: "+resource);
		}
		OutlineFont outlineFont = null;
		try {
			Element fontRoot = new Builder().build(inputStream).getRootElement();
			outlineFont = new OutlineFont(fontRoot);
		} catch (Exception e) {
			throw new RuntimeException("Cannot find/parse font", e);
		}
		return outlineFont;
	}
	
	private void analyzeFont() {
		fontName = fontRoot.getAttributeValue(FONT_NAME);
		fontStyle = fontRoot.getAttributeValue(FONT_STYLE);
		genericFamily = fontRoot.getAttributeValue(GENERIC_FAMILY);
		glyphs = fontRoot.getChildElements(GLYPH);
		registerGlyphs();
	}
	
	private void registerGlyphs() {
		for (int i = 0; i < glyphs.size(); i++) {
			Glyph glyph = new Glyph((Element) glyphs.get(i));
			registerGlyph(glyph, i);
		}
	}
	
	private void registerGlyph(Glyph glyph, int serial) {
		String character = glyph.getCharacter();
		if (character == null) {
			throw new RuntimeException("missing char for glyph, element "+serial);
		}
		Integer codePoint = (int) character.codePointAt(0);
		if (glyphByCodePoint.get(codePoint) != null) {
			throw new RuntimeException("Duplicate glyph for codePoint: "+codePoint+"/"+(char)(int)codePoint);
		}
		glyphByCodePoint.put(codePoint, glyph);
	}
	
	public SVGText lookupGlyph(SVGPath path, double factor, double factor0) {
		Real2Range boundingBox = path.getBoundingBox();
		RealRange xr = boundingBox.getXRange();
		double xmin = xr.getMin();
		int ix = (int) (factor0*xmin);
		double xmax = xr.getMax();
		RealRange yr = boundingBox.getYRange();
		double ymin = yr.getMin();
		int iy = (int) (factor0*ymin);
		double ratio = yr.getRange()/xr.getRange();
		double geomean = Math.sqrt(yr.getRange()*xr.getRange());
		String ratios = ""+((int)(ratio*factor))/factor+"/"+((int)(geomean*factor))/factor+"/"+path.getCoords().size();
		String coords = "("+(((int)(factor*xmin))/factor)+"/"+(((int)(factor*ymin))/factor)+")";
		String glyphString = glyphMap.get(ratios);
		SVGText text = new SVGText(new Real2(xmin, ymin), glyphString);
		return text;
	}
	
	public void processEquivalence(Equivalence equivalence) {
		equivalence.checkAgainstFont(this);
	}
	
	public String toString() {
		return "fontName: "+fontName+"; style: "+fontStyle+"; family "+genericFamily;
	}

	public Glyph getGlyphBySig(String sig) {
		return glyphBySigMap.get(sig);
	}

	public void addGlyph(Glyph glyph) {
		Element glyphElement = (Element) glyph.glyphElement.copy();
		fontRoot.appendChild(glyphElement);
	}
	
	public void debug(String msg) {
		CMLUtil.debug(fontRoot, msg);
	}

}

package org.xmlcml.graphics.font;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGG;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.cml.graphics.SVGSVG;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.Transform2;

public class OutlineFont {
	
	private static final String CHAR = "char";
	private static final String STROKE_COUNT = "strokeCount";
	public static final String GENERIC_FAMILY = "genericFamily";
	public static final String FONT_STYLE = "fontStyle";
	public static final String FONT_NAME = "fontName";
	public static final String GLYPH = "glyph";
	
	private Map<String, Glyph> glyphBySigMap = new HashMap<String, Glyph>();
	private Element fontRoot;
	private String fontName;
	private String fontStyle;
	private String genericFamily;
	private Elements glyphs;
	private Map<Integer, List<Glyph>> glyphByCodePoint = new HashMap<Integer, List<Glyph>>();

	private OutlineFont() {
	}
	
	public OutlineFont(String name, String genericFamily) {
		fontRoot = new Element("font");
		fontRoot.addAttribute(new Attribute(FONT_NAME, name));
		fontRoot.addAttribute(new Attribute(GENERIC_FAMILY, genericFamily));
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
		return readAndCreateStream(inputStream);
	}

	public static OutlineFont readAndCreateStream(InputStream inputStream) {
		OutlineFont outlineFont = null;
		try {
			Element fontRoot = new Builder().build(inputStream).getRootElement();
			outlineFont = new OutlineFont(fontRoot);
		} catch (Exception e) {
			throw new RuntimeException("Cannot find/parse font", e);
		}
		return outlineFont;
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

	private void analyzeFont() {
		fontName = fontRoot.getAttributeValue(FONT_NAME);
		fontStyle = fontRoot.getAttributeValue(FONT_STYLE);
		genericFamily = fontRoot.getAttributeValue(GENERIC_FAMILY);
		glyphs = fontRoot.getChildElements(GLYPH);
		registerGlyphs();
	}
	
	public void sortByCodePoint() {
		for (int i = 127; i> 32; i--) {
			List<Glyph> glyphList = glyphByCodePoint.get((Integer)i);
			if (glyphList != null) {
				for (Glyph glyph : glyphList) {
					glyph.glyphElement.detach();
					fontRoot.insertChild(glyph.glyphElement, 0);
				}
			} else {
//				System.out.println("Null "+i);
			}
		}
		CMLUtil.debug(fontRoot, "SSSSSSSSS");
	}
	
	private void registerGlyphs() {
		for (int i = 0; i < glyphs.size(); i++) {
			Glyph glyph = new Glyph((Element) glyphs.get(i));
			registerGlyph(glyph, i);
		}
	}
	
	private void registerGlyph(Glyph glyph, int serial) {
		glyph.validate();
		String character = glyph.getCharacter();
		Integer codePoint = (int) character.codePointAt(0);
		List<Glyph> glyphList = glyphByCodePoint.get(codePoint);
		if (glyphList == null) {
			glyphList = new ArrayList<Glyph>();
			glyphByCodePoint.put(codePoint, glyphList);
		}
		if (!glyphList.contains(glyph)) {
			glyphList.add(glyph);
		}
		glyphBySigMap.put(glyph.getSignature(), glyph);
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
		glyphBySigMap.put(glyph.getSignature(), glyph);
	}
	
	public void analyzePaths(List<SVGPath> svgPathList) {
		Map<String, Integer> countBySigMap = new HashMap<String, Integer>();
		int unknown = 0;
		for (SVGPath svgPath : svgPathList) {
			String sig = svgPath.getSignature();
			Glyph glyph = glyphBySigMap.get(sig);
			if (glyph == null) {
				glyph = addNewGlyph("?"+(++unknown), svgPath);
//				System.out.println(glyph.glyphElement.toXML());
			}
			Integer count = countBySigMap.get(sig);
			if (count == null) {
				count = new Integer(0);
				countBySigMap.put(sig, count);
			}
			count = count+1;
			countBySigMap.put(sig, count);
		}
		for (String sig : countBySigMap.keySet()) {
			Glyph glyph = glyphBySigMap.get(sig);
			System.out.println(""+countBySigMap.get(sig)+"/"+glyph.getCharacter()+"/"+sig);
		}
		
		// print corpus
		for (SVGPath svgPath : svgPathList) {
			String sig = svgPath.getSignature();
			Glyph glyph = glyphBySigMap.get(sig);
			System.out.print(glyph.getCharacter());
		}
		
	}

	public void plotGlyphs(OutputStream glyphStream) {
		SVGSVG svg = new SVGSVG();
		double xmin = 20;
		double xmax = 500;
		double x = 0;
		double y = 20;
		double dx = 40;
		double dy = 75;
		double sx = 30.;
		double sy = 30.;
		this.sortByCodePoint();
		//get scales
		Real2Range maxbox = new Real2Range();
		for (String sig : glyphBySigMap.keySet()) {
			Glyph glyph = glyphBySigMap.get(sig);
			SVGPath path = (SVGPath)glyph.getPath();
			if (path != null) {
				Real2Range bbox = path.getBoundingBox();
				maxbox = maxbox.plus(bbox);
			}
		}
		double scale = Math.max(maxbox.getXRange().getRange(), maxbox.getYRange().getRange());
		sx = sx/scale;
		sy = sy/scale;
		
		for (String sig : glyphBySigMap.keySet()) {
			Glyph glyph = glyphBySigMap.get(sig);
			SVGPath path = (SVGPath)glyph.getPath();
			if (path != null) {
				SVGG g = new SVGG();
				g.applyTransform(new Transform2(new double[]{1, 0, x, 0, 1, y, 0, 0, 1}));
				svg.appendChild(g);
				SVGG gglyph = new SVGG();
				g.appendChild(gglyph);
				gglyph.applyTransform(new Transform2(new double[]{sx, 0, 0, 0, sy, 0, 0, 0, 1}));
				x += dx;
				if (x > xmax) {
					x = xmin;
					y += dy;
				}
				gglyph.appendChild(path.copy());
				SVGText text = new SVGText(new Real2(0, dy*0.5), glyph.getCharacter());
				text.addAttribute(new Attribute("font-size", ""+10));
				g.appendChild(text);
			}
		}
		try {
			CMLUtil.debug(svg, glyphStream, 2);
		} catch (IOException e) {
			throw new RuntimeException("Cannout output glyphs", e);
		}
	}
	
	public void debug(String msg) {
		CMLUtil.debug(fontRoot, msg);
	}

	Glyph addNewGlyph(String charname, SVGPath svgPath) {
		String sig = svgPath.getSignature();
		Glyph glyph = new Glyph(charname);
		svgPath.normalizeOrigin();
		glyph.addPath(svgPath);
		glyph.setSignature(sig);
		addGlyph(glyph);
		return glyph;
	}
	
	private void analyze(SVGElement pathElement,
			Boolean stats) {
		if (pathElement != null) {
			Nodes paths = pathElement.query("//*[local-name()='g']/*[local-name()='path']");
			List<SVGPath> svgPathList = new ArrayList<SVGPath>();
			for (int i = 0; i < paths.size(); i++) {
				svgPathList.add((SVGPath) paths.get(i));
			}
			this.analyzePaths(svgPathList);
		}
	}

	private static void runFont(String[] args) throws Exception {
		String genericFamily = null;
		String infontfile = null;
		String outglyphfile = null;
		String name = null;
		String pathfile = null;
		String outfontfile = null;
		OutlineFont font = null;
		
		SVGElement pathElement = null;
		Boolean stats = false;
		int i = 0;
		while (i < args.length) {
			if (false) {
			} else if ("-c".equals(args[i])) {
				pathfile = args[++i];i++;
			} else if ("-gf".equals(args[i])) {
				genericFamily = args[++i];i++;
			} else if ("-og".equals(args[i])) {
				outglyphfile = args[++i];i++;
			} else if ("-if".equals(args[i])) {
				infontfile = args[++i];i++;
			} else if ("-n".equals(args[i])) {
				name = args[++i];i++;
			} else if ("-of".equals(args[i])) {
				outfontfile = args[++i];i++;
			} else if ("-st".equals(args[i])) {
				stats = true;i++;
			} else {
				System.out.println("Unknown arg: "+args[i++]);
			}
		}
		if (infontfile == null) {
			if (name != null && genericFamily != null) {
				font = new OutlineFont(name, genericFamily);
			} else {
				System.err.println("Must give fontname and generic family to create new font");
			}
		} else {
			font = OutlineFont.readAndCreateStream(new FileInputStream(infontfile));
		}
		
		if (pathfile != null) {
			Element element = new Builder().build(pathfile).getRootElement();
			pathElement = SVGElement.createSVG(element);
		}
		font.analyze(pathElement, stats);
		if (outfontfile != null) {
			CMLUtil.debug(font.fontRoot, new FileOutputStream(outfontfile), 2);
		}
		if (outglyphfile != null) {
			font.plotGlyphs(new FileOutputStream(outglyphfile));
		}
	}

	private static void usage() {
		System.out.println(" -if <fontfile.xml> -c <corpus.xml> -of <outfont.xml> -og <glyphs.xml> -n <name> - gf <genfamily> -st");
		System.out.println("   corpus is SVG file for finding glyphs; og is glyph output; of is updated font;");
		System.out.println("   if no input font, use name anf generic family to create new one");
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			usage();
		} else {
			try {
				runFont(args);
			} catch (Exception e) {
				throw new RuntimeException("Cannot run fonts", e);
			}
		}
	}

}

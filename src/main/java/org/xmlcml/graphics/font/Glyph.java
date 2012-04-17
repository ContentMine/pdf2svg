package org.xmlcml.graphics.font;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGPath;

public class Glyph {

	private static final String CHARACTER = "character";
	private static final String GLYPH = "glyph";
	private static final String SIGNATURE = "signature";
	
	Element glyphElement;
	private SVGPath svgPath;
	private String sig;
	private String glyphChar;
	
	public Glyph(Element glyphElement) {
		this.glyphElement = (Element) glyphElement.copy();
//		CMLUtil.debug(glyphElement, "glyph");
		processChar();
		processSVGPath();
		processSig();
	}
	
	private Glyph() {
		this.glyphElement = new Element(GLYPH);
	}
		
	public Glyph(String character) {
		this();
		setCharacter(character);
	}
	
	public String getCharacter() {
		return glyphElement.getAttributeValue(Glyph.CHARACTER);
	}
	
	private void setCharacter(String character) {
		if (glyphElement.getAttribute(CHARACTER) != null) {
			throw new RuntimeException("Cannot reset glyph@character");
		}
		glyphElement.addAttribute(new Attribute(CHARACTER, character));
	}

	private void processChar() {
		glyphChar = glyphElement.getAttributeValue(CHARACTER);
		if (glyphChar == null) {
			throw new RuntimeException("No @char given for <glyph>");
		}
	}
	
	private void processSVGPath() {
		Nodes paths = glyphElement.query("./*[local-name()='"+SVGPath.TAG+"']");
		if (paths.size() == 0) {
			throw new RuntimeException("<glyph> Missing svg:path child");
		}
		if (paths.size() == 2) {
			throw new RuntimeException("<glyph> cannot process multiple svg:path children ("+paths.size()+")");
		}
		Element path = (Element) paths.get(0);
		svgPath = (SVGPath) SVGElement.createSVG(path);
		path.getParent().replaceChild(path,  svgPath);
		
	}
	
	private void processSig() {
		sig = svgPath.getSignature();
		if (glyphElement.getAttribute(SIGNATURE) == null) {
			glyphElement.addAttribute(new Attribute(SIGNATURE, sig));
		} else {
			String sigValue = glyphElement.getAttributeValue(SIGNATURE);
			if (!sigValue.equals(sig)) {
				throw new RuntimeException("mismatched signature ("+sigValue+") with glyph@signature: "+sig);
			}
		}
	}

	public SVGPath getPath() {
		return svgPath;
	}

	public void addPath(SVGPath svgPath) {
		Nodes paths = glyphElement.query("./*[local-name()='"+SVGPath.TAG+"']");
		if (paths.size() != 0) {
			throw new RuntimeException("<glyph> already has svg:path child");
		}
		this.svgPath = new SVGPath(svgPath);
		glyphElement.appendChild(this.svgPath);
	}

	public void setSignature(String sig) {
		if (glyphElement.getAttribute(SIGNATURE) != null) {
			throw new RuntimeException("glyph already has signature");
		} else {
			glyphElement.addAttribute(new Attribute(SIGNATURE, sig));
		}
	}

	public String getSignature() {
		return glyphElement.getAttributeValue(SIGNATURE);
	}

	public String validate() {
		String character = getCharacter();
		if (character == null) {
			throw new RuntimeException("missing char for glyph, element ");
		}
		SVGPath path = getPath();
		if (path == null) {
			throw new RuntimeException("glyph must have a path");
		}
		String sig = getSignature();
		if (sig == null) {
			sig = path.getSignature();
			setSignature(sig);
		}
		return character;
	}

}

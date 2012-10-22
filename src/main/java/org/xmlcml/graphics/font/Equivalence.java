package org.xmlcml.graphics.font;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGPath;

public class Equivalence {

	private Element equivalence;
	private Element textElement;
	private String textString;
	private List<SVGPath> svgPathList;
	private List<String> sigList;

	/**
		"<equivalence id='italic1'>" +
		"  <text fontName='font1-italic'>Ra</text>" +
		"  <pathList>" +
		"    <path d='M327.397 218.898 L328.024 215.899 L329.074 215.899 C329.433 ..."+
		"        212.888' style='clip-path:url(#clipPath1); stroke:none;'/>" +
		"    <path d='M336.571 218.898 L337.414 218.898 C337.347 218.609 337.313 ..."+ 
		"        217.535' style='clip-path:url(#clipPath1); stroke:none;'/> "+
		"  </pathList>" +
		"</equivalence>";
	 */
	public Equivalence() {
		
	}
	
	public static Equivalence createEquivalence(Element element) {
		if (element == null || !("equivalence".equals(element.getLocalName()))) {
			throw new RuntimeException("null or bad equivalence: "+element);
		}
		Equivalence equiv = new Equivalence();
		// delegate
		equiv.equivalence = (Element) element.copy();
		equiv.process();
		return equiv;
	}
	
		
	public void process() {
		Nodes textNodes = equivalence.query("./text");
		if (textNodes.size() != 1) {
			throw new RuntimeException("equivalence needs exactly one <text/> child");
		}
		textElement = (Element) textNodes.get(0);
		// remove all whitespace
		textString = textElement.getValue().replaceAll(CMLConstants.S_WHITEREGEX, "");
		Nodes paths = equivalence.query("./*[local-name()='pathList']/*[local-name()='path']");
		if (paths.size() != textString.length()) {
			throw new RuntimeException("text string length "+textString.length()+" != path count "+paths.size());
		}
		System.out.println("TEXT "+textString);
		svgPathList = new ArrayList<SVGPath>();
		sigList = new ArrayList<String>();
		for (int i = 0; i < paths.size(); i++) {
			Element path = (Element)paths.get(i);
			SVGPath svgPath = (SVGPath) SVGElement.readAndCreateSVG(path);
			svgPathList.add(svgPath);
			svgPath.normalizeOrigin();
			String sig = svgPath.getSignature();
			sigList.add(sig);
		}
	}
	
	public void checkAgainstFont(OutlineFont font) {
		int glyphNum = 0;
		for (int i = 0; i < sigList.size(); i++) {
			SVGPath svgPath = svgPathList.get(i);
			String sig = sigList.get(i);
			Glyph glyph = font.getGlyphBySig(sig);
			if (glyph == null) {
				glyph = font.addNewGlyph("?"+(++glyphNum), svgPath);
				System.out.println("NEW "+glyph.glyphElement.toXML());
			} else {
				System.out.println("FOUND: "+/*sig+*/" "+glyph.getCharacter());
			}
		}
	}
}

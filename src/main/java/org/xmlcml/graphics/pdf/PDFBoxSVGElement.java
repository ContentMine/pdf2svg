package org.xmlcml.graphics.pdf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.font.FontManager;
import org.xmlcml.graphics.font.Glyph;
import org.xmlcml.graphics.font.OutlineFont;
import org.xmlcml.graphics.graph.NodeHanger;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGText;

public class PDFBoxSVGElement extends SVGElement {

	private List<Glyph> glyphList;
	private List<SVGPath> nonTextPathList;
	private FontManager fontManager;

	public PDFBoxSVGElement(SVGElement elem) {
		super(elem);
		this.fontManager = new FontManager();
	}
	
	public static PDFBoxSVGElement createPDFBoxSVGElement(Element elem) {
		PDFBoxSVGElement pdfElem = new PDFBoxSVGElement(SVGElement.readAndCreateSVG(elem));
		return pdfElem;
	}
	
	public static void main(String[] args) {
		String filename = null;
		PDFBoxSVGElement element = null;
		if (args.length == 0) {
			usage();
			filename = "test6.svg";
		} else {
			filename = args[0];
		}
		try {
			Element elem = new Builder().build(new FileInputStream(filename)).getRootElement();
			element = PDFBoxSVGElement.createPDFBoxSVGElement(elem);
		} catch (Exception e) {
			throw new RuntimeException("cannot parse SVG", e);
		}
		element.analysePathNodes();
		try {
			CMLUtil.debug(element, new FileOutputStream(filename+".new"+".svg"), 0);
		} catch (Exception e) {
			throw new RuntimeException("Cannot write: ", e);
		}
	}

	private void analysePathNodes() {
//		System.out.println(this.query("/*/*[local-name()='g']").size());
		Nodes pathNodes = this.query("//*[local-name()='g']/*[local-name()='path']");
		ParentNode parent = pathNodes.get(0).getParent();
		System.out.println("path: "+pathNodes.size());
		List<SVGPath> pathList = new ArrayList<SVGPath>();
		List<SVGPath> allPathList = new ArrayList<SVGPath>();
		List<SVGPath> linePathList = new ArrayList<SVGPath>();
		List<SVGPolyline> polylineList = new ArrayList<SVGPolyline>();
		for (int i = 0; i < pathNodes.size(); i++) {
			SVGPath path = (SVGPath)pathNodes.get(i);
			SVGPolyline polyline = path.createPolyline();
			if (polyline != null) {
				linePathList.add(path);
				polylineList.add(polyline);
				parent.appendChild(polyline);
			} else {
				pathList.add(path);
			}
			allPathList.add(path);
		}
		analyzePolylines(polylineList);
		analyzePaths(allPathList);
		findTreeNodes(polylineList);
	}

	private void findTreeNodes(List<SVGPolyline> polylineList) {
		for (SVGPolyline polyline : polylineList) {
			List<SVGLine> lineList = polyline.createLineList();
			if (lineList.size() == 3) {
				Real2 end0 = lineList.get(0).getXY(0);
				getMatchedPoint(polyline, polylineList, end0);
				Real2 end1 = lineList.get(2).getXY(1);
				getMatchedPoint(polyline, polylineList, end1);
			}
		}
	}

	private void getMatchedPoint(SVGPolyline polyline, List<SVGPolyline> polylineList, Real2 end) {
		String col = "red";
		for (SVGPolyline polyline1 : polylineList) {
			List<SVGLine> lineList = polyline1.createLineList();
			if (lineList.size() != 3) {
				continue;
			}
			SVGLine line1 = polyline1.createLineList().get(1);
			Real2 midpoint = line1.getEuclidLine().getMidPoint();
			if (end.getDistance(midpoint) < 1.0) {
				col = "green";
//				System.out.println("GREEN"+end);
				break;
			}
		}
		SVGCircle circle = new SVGCircle(end, 2);
		circle.setFill(col);
		Element root = (Element) polyline.query("/*").get(0);
		ParentNode parent = polyline.getParent();
		root.appendChild(circle);
	}


	private void analyzePolylines(List<SVGPolyline> polylineList) {
		double epsilon = 0.0001;
		System.out.println("polyline "+polylineList.size());
		for (SVGPolyline polyline : polylineList) {
			if (polyline == null) {
				throw new RuntimeException("null polyline");
			}
			List<SVGLine> lineList = polyline.createLineList();
			int size = lineList.size();

			if (polyline.isBox(epsilon)) {
			} else {
				NodeHanger nodeHanger = NodeHanger.createNodeHanger(polyline, epsilon);
				if (nodeHanger != null) {
					polyline.appendChild(nodeHanger);
				} 
			}
		}
	}

	private void analyzePaths(List<SVGPath> pathList) {
		OutlineFont outlineFont = fontManager.getDefaultFont();
		System.out.println("paths "+pathList.size());
		double factor0 = 1;
		double factor = 10;
		String line = "";
		glyphList = new ArrayList<Glyph>();
		nonTextPathList = new ArrayList<SVGPath>();
		for (SVGPath path : pathList) {
			String sig = path.getSignature();
			Glyph glyph = outlineFont.getGlyphBySig(sig);
			if (glyph != null) {
				glyphList.add(glyph);
			} else {
				debugPath(path);
				nonTextPathList.add(path);
			}
		}
	}
	
	private void analyzeText(ParentNode parent) {
		int lasty = -1;
		int ix = 0;
		int iy = 0;
		String line = null;
		Real2 lastxy = null;
		double lastx = -9999;
		List<SVGText> textStringList = new ArrayList<SVGText>();
		for (Glyph glyph : glyphList) {
			double xmin = 0.0;
//			double xmin = glyph.getBoundingBox().getXRange().getMin();
			// new line?
			if (Math.abs(iy - lasty) > 3 && Math.abs(ix - lastx) > 3) {
				if (lastxy != null) {
					SVGText textString = new SVGText(lastxy, line);
					textStringList.add(textString);
					textString.addAttribute(new Attribute("style", "font-size:8pt; stroke-width:0.1; fill:red;"));
					parent.appendChild(textString);
					line = textString.getValue();
				}
				lasty = iy;
			} else {
				if (Math.abs(lastx - xmin) > 2) {
					System.out.print(" ");
					line = line+" ";
				}
				line = line+glyph;
			}
//			lastx = xmax;
		}
	}

	private static String debugPath(SVGPath path) {
//		String ss = "("+(((int)(factor*xmin))/factor)+"/"+(((int)(factor*ymin))/factor)+"/"+ratios+")";
		return null;
	}
	
//	
//	private String getInteger(Real2Range boundingBox) {
//		double rx = boundingBox.getXRange().getMin();
//		double ry = boundingBox.getYRange().getMin();
//		return ""+(int)rx+"/"+(int)ry+" ";
//	}

	private static void usage() {
		System.err.println("Usage: <svgfilein>");
	}
}

package org.xmlcml.graphics.pdf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.graphics.SVGCircle;
import org.xmlcml.cml.graphics.SVGElement;
import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPath;
import org.xmlcml.cml.graphics.SVGPolyline;
import org.xmlcml.cml.graphics.SVGText;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;

public class PDFBoxSVGElement extends SVGElement {

	static Map<String, String> glyphMap = new HashMap<String, String>();
	static {
		// italics
		glyphMap.put("1.1/6.3/19", "A");
		glyphMap.put("0", "B");
		glyphMap.put("1.1/6.4/51", "C");
		glyphMap.put("1.0/6.5/59", "D");
		glyphMap.put("0", "E");
		glyphMap.put("0", "F");
		glyphMap.put("0", "G");
		glyphMap.put("0", "H");
		glyphMap.put("0", "I");
		glyphMap.put("0", "J");
		glyphMap.put("0", "K");
		glyphMap.put("0", "L");
		glyphMap.put("0.8/7.2/32", "M");
		glyphMap.put("1.0/6.6/68", "O");
		glyphMap.put("1.0/6.5/64", "R");
		glyphMap.put("0", "S");
		glyphMap.put("1.2/6.0/9", "T");
		glyphMap.put("0", "U");
		glyphMap.put("0", "V");
		glyphMap.put("0", "W");
		glyphMap.put("0", "X");
		glyphMap.put("0", "Y");
		glyphMap.put("0", "Z");
		
		glyphMap.put("1.1/4.8/101", "a");
		glyphMap.put("1.4/5.6/71", "b");
		glyphMap.put("1.4/5.7/71", "b");
		glyphMap.put("1.1/4.6/57", "c");
		glyphMap.put("1.3/5.9/61", "d");
		glyphMap.put("1.1/4.8/68", "e");
		glyphMap.put("2.0/4.8/34", "f");
		glyphMap.put("1.3/5.9/100", "g");
		glyphMap.put("1.4/5.6/42", "h");
		glyphMap.put("3.0/3.8/10", "i");
		glyphMap.put("0", "j");
		glyphMap.put("0", "k");
		glyphMap.put("3.0/3.8/5", "l");
		glyphMap.put("0.6/6.0/75", "m");
		glyphMap.put("1.0/4.8/39", "n");
		glyphMap.put("1.1/4.8/71", "o");
		glyphMap.put("1.3/5.9/67", "p");
		glyphMap.put("0", "q");
		glyphMap.put("1.3/4.2/25", "r");
		glyphMap.put("1.1/4.7/84", "s");
		glyphMap.put("2.6/4.1/37", "t");
		glyphMap.put("2.7/4.1/37", "t");
		glyphMap.put("1.0/4.8/42", "u");
		glyphMap.put("1.0/4.7/14", "v");
		glyphMap.put("0", "w");
		glyphMap.put("0.9/4.9/19", "x");
		glyphMap.put("1.2/6.0/30", "y");
		glyphMap.put("0", "z");
		
		glyphMap.put("0.8/1.0/5", ".");
		
		// normal
		
		glyphMap.put("1.0/6.5/19", "A");
		glyphMap.put("1.1/6.4/63", "C");
		glyphMap.put("7.5/2.4/5", "I");
		glyphMap.put("1.0/6.5/20", "M");
		glyphMap.put("1.2/6.1/102", "S");
		
		glyphMap.put("1.1/4.7/105", "a");
		glyphMap.put("1.6/5.3/55", "b");
		glyphMap.put("1.2/4.6/54", "c");
		glyphMap.put("1.1/4.6/54", "c");
		glyphMap.put("1.6/5.3/58", "d");
		glyphMap.put("1.1/4.7/56", "e");
		glyphMap.put("1.1/4.8/56", "e");
		glyphMap.put("8.1/2.3/10", "i");
		glyphMap.put("8.1/2.3/5", "l");
		glyphMap.put("0.7/5.7/63", "m");
		glyphMap.put("1.2/4.4/39", "n");
		glyphMap.put("1.1/4.8/53", "o");
		glyphMap.put("1.8/3.6/28", "r");
		glyphMap.put("1.2/4.5/96", "s");
		glyphMap.put("1.0/4.7/20", "x");
		glyphMap.put("1.0/4.7/19", "x");
		
		glyphMap.put("3.9/4.4/27", "(");
		glyphMap.put("1.0/0.9/5", ".");
		// numbers
		glyphMap.put("1.5/3.0/62", "0");
		glyphMap.put("1.5/3.1/62", "0");
		glyphMap.put("2.7/2.3/18", "1");
		glyphMap.put("0", "2");
		glyphMap.put("1.5/3.1/83", "3");
		glyphMap.put("", "4");
		glyphMap.put("", "5");
		glyphMap.put("1.5/3.1/84", "6");
		glyphMap.put("1.5/3.0/24", "7");
		glyphMap.put("1.5/3.1/102", "8");
		glyphMap.put("1.5/3.1/88", "9");
		
	}
	public PDFBoxSVGElement(SVGElement elem) {
		super(elem);
	}
	
	public static PDFBoxSVGElement createPDFBoxSVGElement(Element elem) {
		PDFBoxSVGElement pdfElem = new PDFBoxSVGElement(SVGElement.createSVG(elem));
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
			CMLUtil.debug(element, new FileOutputStream("test6new.svg"), 0);
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
		printPolylines(polylineList);
		printPaths(allPathList);
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


	private void printPolylines(List<SVGPolyline> polylineList) {
		System.out.println("polyline "+polylineList.size());
		for (SVGPolyline polyline : polylineList) {
			List<org.xmlcml.cml.graphics.SVGLine> lineList = polyline.createLineList();
			int size = lineList.size();

			if (size == 1) {
				if (polyline.isAlignedWithAxes(0.01)) {
//					polyline.debug("aligned1");
				} else {
					polyline.debug("size1???");
				}
				// later?
			} else if (size == 3) {
					if (polyline.isAlignedWithAxes(0.01)) {
//						matchEnd(polyline, 0, 0, polylineList);
//						matchEnd(polyline, 2, 1, polylineList);
//						polyline.debug("aligned3");
					} else {
						// italic dash (closed)
//						polyline.debug("size3???");
					}
					// later?
			} else if (size == 4) {
				if (polyline.isBox(0.01)) {
//					polyline.debug("BOX");
				} else {
//					polyline.debug("size4??");
				}
			} else if (polyline.isAlignedWithAxes(0.01)) {
				if (size == 8) {
					// "T"
				} else if (size == 11) {
						// "+"
				} else {
					polyline.debug("aligned"+size);
				}
			} else {
				if (size != 5 && size != 8 && size != 10 && size != 11) {
					polyline.debug("size"+size);
				}
			}
		}
	}

	private void matchEnd(SVGPolyline polyline, int line, int end, List<SVGPolyline> polylineList) {
		List<SVGLine> lineList = polyline.createLineList();
		Real2 endpt = lineList.get(line).getXY(end);
		SVGCircle circle = new SVGCircle(endpt, 10.0);
		polyline.getParent().appendChild(circle);
	}

	private void printPaths(List<SVGPath> pathList) {
		System.out.println("paths "+pathList.size());
		int lasty = -1;
		double lastx = -9999;
		Real2 lastxy = null;
		double factor0 = 1;
		double factor = 10;
		String line = "";
		for (SVGPath path : pathList) {
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
			String glyph = glyphMap.get(ratios);
			String out = glyph; 
			if (glyph == null) {
				out = "("+(((int)(factor*xmin))/factor)+"/"+(((int)(factor*ymin))/factor)+"/"+ratios+")";
				glyph = "";
			}
			// new line?
			if (Math.abs(iy - lasty) > 3 && Math.abs(ix - lastx) > 3) {
				if (lastxy != null) {
					SVGText text = new SVGText(lastxy, line);
					text.addAttribute(new Attribute("style", "font-size:8pt; stroke-width:0.1; fill:red;"));
					path.getParent().appendChild(text);
					line = glyph;
				}
				lasty = iy;
				lastxy = path.getCoords().get(0);
				System.out.println(coords);
				System.out.print(glyph);
			} else {
				if (Math.abs(lastx - xmin) > 2) {
					System.out.print(" ");
					line = line+" ";
				}
				line = line+glyph;
				System.out.print(out);
			}
			lastx = xmax;
//			Real2Array r2a = path.getCoords();
//			Real2 orig = r2a.get(0);
//			Transform2 t2 = new Transform2(new Vector2(-orig.getX(), -orig.getY()));
//			r2a.transformBy(t2);
		}
	}
	
	
	private String getInteger(Real2Range boundingBox) {
		double rx = boundingBox.getXRange().getMin();
		double ry = boundingBox.getYRange().getMin();
		return ""+(int)rx+"/"+(int)ry+" ";
	}

	private static void usage() {
		System.err.println("Usage: <svgfilein>");
	}
}

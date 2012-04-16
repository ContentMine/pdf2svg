package org.xmlcml.graphics.graph;

import java.util.List;

import nu.xom.Element;

import org.xmlcml.cml.graphics.SVGLine;
import org.xmlcml.cml.graphics.SVGPolyline;


/** 
 * forms a yoke for two nodes 
 * move me to Tree hacking
 * @author pm286
 *
 */
public class NodeHanger extends Element {

	public static String TAG = "nodeHanger";
	private SVGLine orientationLine;
	
	public NodeHanger() {
		super(TAG);
	}
	
	public static NodeHanger createNodeHanger(SVGPolyline polyline, double epsilon) {
		NodeHanger nodeHanger = null;
		List<SVGLine> lineList = polyline.createLineList();
		if (lineList.size() == 1) {
			if (polyline.isAlignedWithAxes(epsilon)) {
				nodeHanger = new NodeHanger();
				nodeHanger.setOrientationLine(lineList.get(0));
			}
		} else if (lineList.size() == 3) {
			if (polyline.isAlignedWithAxes(epsilon) && 
				lineList.get(0).isPerpendicularTo(lineList.get(1), epsilon) && 
				lineList.get(0).isParallelTo(lineList.get(2), epsilon)) {
				nodeHanger = new NodeHanger();
				nodeHanger.setOrientationLine(lineList.get(0));
//				nodeHanger.setRootNode(new GraphNode(lineList.get(1).getEuclidLine().getMidPoint()));
			}
		}
		return nodeHanger;
	}

	private void setOrientationLine(SVGLine svgLine) {
		this.orientationLine = svgLine;
	}
	
	public SVGLine getOrientationLine() {
		return this.orientationLine;
	}
}

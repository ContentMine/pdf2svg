package org.xmlcml.graphics.pdf2svg.raw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDMatrix;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.graphics.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorState;
import org.apache.pdfbox.pdmodel.text.PDTextState;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.TextPosition;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.GraphicsElement.FontStyle;
import org.xmlcml.graphics.svg.GraphicsElement.FontWeight;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;

/** converts a PDPage to SVG
 * Originally used PageDrawer to capture the PDF operations.These have been
 * largely intercepted and maybe PageDrawer could be retired at some stage
 * @author pm286 and Murray Jensen
 *
 */
public class PDFPage2SVGConverter extends PageDrawer {

	private static final String BADCHAR_E = ">>";
	private static final String BADCHAR_S = "<<";
	private static final String FONT_NAME = "fontName";
	private static final String BOLD = "bold";
	private static final String ITALIC = "italic";
	private static final String OBLIQUE = "oblique";

	private final static Logger LOG = Logger.getLogger(PDF2SVGConverter.class);

	private static final Dimension DEFAULT_DIMENSION = new Dimension(800, 800);;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static double eps = 0.001;

	private BasicStroke basicStroke;
	private SVGSVG svg;
	private SVGG g;
	private Composite composite;
	private Paint paint;
	private PDGraphicsState graphicsState;
	private Matrix textPos;
	private PDFont font;

	private boolean createNewG;
	private String currentClipPath;
	private Composite currentComposite;
	private String currentFill;
	private String currentFontFamily;
	private String currentFontName;
	private Double currentFontSize;
	private String currentFontStyle;
	private String currentFontWeight;
	private String currentFormatFont;
	private Paint currentPaint;
	private String currentStroke;
	private SVGText currentSvgText;
	
	private int nPlaces = 3;

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

	}

	/** adds a default pagesize if not given
	 * 
	 */
	private void ensurePageSize() {
		if (pageSize == null) {
			pageSize = DEFAULT_DIMENSION;
		}
	}
	
	protected void processTextPosition(TextPosition text) {

		font = text.getFont();
		List<Float> widthList = font.getWidths();
		System.out.println("W "+widthList.size());
		for (int i = 32; i < 127; i++	)  {
			System.out.print("  "+(char)i+":"+(int) (double) widthList.get(i));
		}
		System.out.println();
			
		ensurePageSize();
		try {
			createNewG = false;
			currentSvgText = new SVGText();
			currentSvgText.setFontSize(null);
			currentSvgText.setStroke(null);
			currentSvgText.setFontWeight((FontWeight)null);
			currentSvgText.setFontStyle((FontStyle)null);
			normalizeFontFamilyNameStyleWeight();
			try {
				currentSvgText.setText(text.getCharacter());
			} catch (RuntimeException e) {
				
				tryToConvertStrangeCharactersOrFonts(text);
			}
			createGraphicsStateAndPaintAndComposite();
			createAndReOrientateTextPosition(text, currentSvgText);
			
			createNewGIfCurrentFontDescriptorChanged();
			createNewGIfFontSizeChanged(text, currentSvgText);
			createNewGIfClipPathChanged();
			checkStrokeUnchanged(currentSvgText);

			currentSvgText.format(nPlaces);
			if (createNewG) {
				createNewGAndFillWithCurrentValues();
			}
			g.appendChild(currentSvgText);
		} catch (Exception e) {
			throw new RuntimeException("drawPage", e);
		}
	}

	private void tryToConvertStrangeCharactersOrFonts(TextPosition text) {
//		System.out.println("L "+text.getCharacter().length());
		char cc = text.getCharacter().charAt(0);
//		String s = interpretCharacter(currentFontFamily, cc);
		String s = BADCHAR_S+(int)cc+BADCHAR_E;
		System.out.println(s+" "+currentFontName);
		currentSvgText.setText(s);
	}

	private String interpretCharacter(String fontFamily, char cc) {
		String s = null;
		if ("MathematicalPi-One".equals(fontFamily)) {
			if (cc == 1) {
				s = CMLConstants.S_PLUS;
			}
		} else {
			System.out.println("font "+fontFamily+" point "+(int)cc);
		}
		return s;
	}

	private void createNewGIfCurrentFontDescriptorChanged() {
		String formatFont = fmtFont(font);
		if (hasChanged(currentFormatFont, formatFont)) {
			createNewG = true;
			currentFormatFont = formatFont;
			LOG.trace("FontDescriptor changed from "+currentFormatFont+" to "+formatFont);
		}
		if (currentFontStyle != null || currentFontWeight != null) {
			LOG.trace("W "+currentFontWeight+" S "+currentFontStyle);
		}
	}

	private void normalizeFontFamilyNameStyleWeight() {
		PDFontDescriptor fd = font.getFontDescriptor();
		currentFontFamily = fd.getFontFamily();
		currentFontName = fd.getFontName();
		// currentFontFamily may be null?
		if (currentFontName == null) {
			throw new RuntimeException("No currentFontName");
		}
		if (currentFontFamily == null) {
			currentFontFamily = currentFontName;
		}
		// strip leading characters (e.g. KAIKCD+Helvetica-Oblique);
		stripFamilyPrefix();
		createFontStyle();
		createFontWeight();
	}

	private void stripFamilyPrefix() {
		int index = currentFontFamily.indexOf("+");
		if (index != -1) {
			currentFontFamily = currentFontFamily.substring(index+1);
		}
	}
	
	private void createFontStyle() {
		String ff = currentFontFamily.toLowerCase();
		int idx = currentFontFamily.indexOf("-");
		String suffix = ff.substring(idx+1).toLowerCase();
		if (suffix.equals(OBLIQUE) || suffix.equals(ITALIC)) {
			currentFontStyle = FontStyle.ITALIC.toString().toLowerCase();
			currentFontFamily = currentFontFamily.substring(0, idx);
		} else {
			currentFontStyle = null;
		}
	}

	/** looks for -bold in font name
	 * 
	 */
	private void createFontWeight() {
		String ff = currentFontFamily.toLowerCase();
		int idx = ff.indexOf("-");
		String suffix = ff.substring(idx+1);
		if (suffix.equals(BOLD)) {
			currentFontWeight = FontWeight.BOLD.toString().toLowerCase();
			currentFontFamily = currentFontFamily.substring(0, idx);
		} else {
			currentFontWeight = null;
		}
	}

	private void createNewGIfClipPathChanged() {
		String clipPath = graphicsState.getCurrentClippingPath().toString();
		if (hasChanged(clipPath, currentClipPath)) {
			createNewG = true;
			currentClipPath = clipPath;
		}
	}

	private void checkStrokeUnchanged(SVGText svgText) {
		String stroke = getCSSColor((Color) paint);
		if (hasChanged(currentStroke, stroke)) {
			createNewG = true;
			currentStroke = stroke;
		}
	}


	/**
	 * private boolean createNewG; private String currentClipPath; private
	 * Composite currentComposite; private String currentFill; private String
	 * currentFontFamily; private double currentFontSize; private String
	 * currentFontStyle; private String currentFontWeight; private String
	 * currentFormatFont; private Paint currentPaint; private String
	 * currentStroke; private SVGText currentSvgText;
	 */
	private void createNewGAndFillWithCurrentValues() {
		addNewSVGG();
		setNonNullClipPathInParentG();
		setNonNullFillInParentG();
		setNonNullFontSizeInParentG();
		setNonNullFontFamilyInParentG();
		setNonNullFontNameInParentG();
		setNonNullFontStyleInParentG();
		setNonNullFontWeightInParentG();
		setNonNullStrokeInParentG();
		g.format(nPlaces);
	}

	private void setNonNullClipPathInParentG() {
		if (currentClipPath != null) {
			g.setClipPath(currentClipPath);
		}
	}

	private void setNonNullFillInParentG() {
		if (currentFill != null) {
			g.setFill(currentFill);
		}
	}

	private void setNonNullFontSizeInParentG() {
		if (currentFontSize != null) {
			g.setFontSize(currentFontSize);
		}
	}

	private void setNonNullFontFamilyInParentG() {
		if (currentFontFamily != null) {
			g.setFontFamily(currentFontFamily);
			currentFontFamily = null;
		}
	}

	private void setNonNullFontNameInParentG() {
		if (currentFontName != null) {
			setFontName(g, currentFontName);
			currentFontName = null;
		}
	}

	private void setNonNullFontStyleInParentG() {
		if (currentFontStyle != null) {
			g.setFontStyle(currentFontStyle);
			currentFontStyle = null;
		}
	}

	private void setNonNullFontWeightInParentG() {
		if (currentFontWeight != null) {
			g.setFontWeight(currentFontWeight);
			currentFontWeight = null;
		}
	}

	private void setNonNullStrokeInParentG() {
		if (currentStroke != null) {
			g.setStroke(currentStroke);
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

	private void createNewGIfFontSizeChanged(TextPosition text, SVGText svgText) {
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
		if (hasChanged(scale, currentFontSize, eps)) {
			createNewG = true;
		}
		currentFontSize = scale;
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
	private void createAndReOrientateTextPosition(TextPosition text,
			SVGText svgText) {
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
		svgText.setXY(new Real2(x, y));
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
		if (hasChanged(currentPaint, paint)
				|| hasChanged(currentComposite, composite)) {
			createNewG = true;
			currentPaint = paint;
			currentComposite = composite;
		}
	}

	/** traps any remaining unimplemented PDDrawer calls
	 * 
	 */
	public Graphics2D getGraphics() {
		System.err.printf("getGraphics was called!!!!!!!%n");
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
		GeneralPath generalPath = getLinePath();
		if (windingRule != null) {
			generalPath.setWindingRule(windingRule);
		}
		SVGPath svgPath = new SVGPath(generalPath);
		if (windingRule != null) {
			svgPath.setFill(getCSSColor(currentPaint));
		} else {
			svgPath.setStroke(getCSSColor(currentPaint));
		}
		svgPath.format(nPlaces);
		svg.appendChild(svgPath);
		generalPath.reset();
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

	/** compares two objects, including null, to detect change
	 * if two objects are null returns false
	 * @param newObject
	 * @param oldObject
	 * @return
	 */
	private boolean hasChanged(Object newObject, Object oldObject) {
		boolean hasChanged = true;
		if (newObject == null && oldObject == null) {
			hasChanged = false;
		} else if (newObject != null && oldObject != null) {
			String newString = newObject.toString();
			String oldString = oldObject.toString();
			hasChanged = !(newString.equals(oldString));
		}
		return hasChanged;
	}

	/** compares two Doubles, including null, to detect change
	 * if two objects are null returns false
	 */
	private boolean hasChanged(Double newDouble, Double oldDouble, double eps) {
		boolean hasChanged = true;
		if (newDouble == null && oldDouble == null) {
			hasChanged = false;
		} else if (newDouble != null && oldDouble != null) {
			hasChanged = !Real.isEqual(oldDouble, newDouble, eps);
		}
		return hasChanged;
	}

	/** creates new <svg> and removes/sets some defaults
	 * this is partly beacuse SVGFoo may have defaults (bad idea?)
	 */
	public void resetSVG() {
		this.svg = new SVGSVG();
		svg.setStroke("none");
		svg.setStrokeWidth(0.0);
		addNewSVGG();
	}

	public SVGSVG getSVG() {
		return svg;
	}

	private void addNewSVGG() {
		this.g = new SVGG();
		svg.appendChild(g);
	}

	void convertPageToSVG(PDPage page) {
		resetSVG();
		drawPage(page);
	}
	
	private void setFontName(SVGElement svgElement, String currentFontName2) {
		svgElement.addAttribute(new Attribute(FONT_NAME, currentFontName));
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

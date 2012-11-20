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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGPath;

/**
 * a graphics2D for PDFBox applications to write to traps all Java2D graphics
 * calls mainly diagnostic - if psf2svg is comprehensive, these methods should
 * not be required
 */
public class PDFGraphics2D extends Graphics2D {

	private final static Logger LOG = Logger.getLogger(PDFGraphics2D.class);
	private AMIFont amiFont;
	private String currentPathString;

	public PDFGraphics2D(AMIFont amiFont) {
		this.amiFont = amiFont;
	}

	@Override
	public void draw(Shape s) {
		// TODO Auto-generated method stub
		System.out.printf("draw(shape=%s)%n", s.toString());
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		// TODO Auto-generated method stub
//		System.out.printf("drawImage(img=%s,xform=%s,obs=%s)%n",
//				img.toString(), xform.toString(), obs.toString());
		return false;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		// TODO Auto-generated method stub
		System.out.printf("drawImage(img=%s,op=%s,x=%d,y=%d)%n",
				img.toString(), op.toString(), x, y);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		System.out.printf("drawRenderedImage(img=%s,xform=%s)%n",
				img.toString(), xform.toString());
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		// TODO Auto-generated method stub
		System.out.printf("drawRenderableImage(img=%s,xform=%s)%n",
				img.toString(), xform.toString());
	}

	@Override
	public void drawString(String str, int x, int y) {
		// TODO Auto-generated method stub
//		System.out.printf("drawString(str=%s,x=%d,y=%d)%n", str.toString(), x,
//				y);
	}

	@Override
	public void drawString(String str, float x, float y) {
		// TODO Auto-generated method stub
//		System.out.printf("drawString(str=%s,x=%f,y=%f)%n", str.toString(), x,
//				y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		// TODO Auto-generated method stub
		System.out.printf("drawString(iterator=%s,x=%d,y=%d)%n",
				iterator.toString(), x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		// TODO Auto-generated method stub
		System.out.printf("drawString(iterator=%s,x=%f,y=%f)%n",
				iterator.toString(), x, y);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		// TODO Auto-generated method stub
//		System.out.printf("drawGlyphVector(g=%s,x=%f,y=%f)%n", g.toString(), x,
//				y);
		Shape shape = g.getOutline();
		AffineTransform at = new AffineTransform();
		this.currentPathString = SVGPath.getPathAsDString(shape.getPathIterator(at));
		LOG.trace("**D** "+this.currentPathString);
	}

	@Override
	public void fill(Shape s) {
		// TODO Auto-generated method stub
		System.out.printf("fill(shape=%s)%n", s.toString());
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		// TODO Auto-generated method stub
		System.out.printf("hit(rect=%s,shape=%s,onStroke=%s)%n",
				rect.toString(), s.toString(), Boolean.toString(onStroke));
		return false;
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		// TODO Auto-generated method stub
		System.out.printf("getDeviceConfiguration()%n");
		return null;
	}

	@Override
	public void setComposite(Composite comp) {
		// TODO Auto-generated method stub
		System.out.printf("setComposite(comp=%s)%n", comp.toString());
	}

	@Override
	public void setPaint(Paint paint) {
		// TODO Auto-generated method stub
		System.out.printf("setPaint(paint=%s)%n", paint.toString());
	}

	@Override
	public void setStroke(Stroke s) {
		// TODO Auto-generated method stub
		System.out.printf("setStroke(stroke=%s)%n", s.toString());
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		// TODO Auto-generated method stub
//		System.out.printf("setRenderingHint(hintKey=%s, hintValue=%s)%n",
//				hintKey.toString(), hintValue.toString());
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		// TODO Auto-generated method stub
		System.out.printf("getRenderingHint(hintKey=%s)%n", hintKey.toString());
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub
		System.out.printf("setRenderingHints(hints=%s)%n", hints.toString());
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub
		System.out.printf("addRenderingHints(hints=%s)%n", hints.toString());
	}

	@Override
	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		System.out.printf("getRenderingHints()%n");
		return null;
	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub
		System.out.printf("translate(x=%d, y=%d)%n", x, y);

	}

	@Override
	public void translate(double tx, double ty) {
		// TODO Auto-generated method stub
		System.out.printf("translate(tx=%lf, ty=%lf)%n", tx, ty);

	}

	@Override
	public void rotate(double theta) {
		// TODO Auto-generated method stub
		System.out.printf("rotate(theta=%lf)%n", theta);

	}

	@Override
	public void rotate(double theta, double x, double y) {
		// TODO Auto-generated method stub
		System.out.printf("rotate(theta=%lf, x=%lf, y=%lf)%n", theta, x, y);

	}

	@Override
	public void scale(double sx, double sy) {
		// TODO Auto-generated method stub
		System.out.printf("scale(sx=%lf, sy=%lf)%n", sx, sy);

	}

	@Override
	public void shear(double shx, double shy) {
		// TODO Auto-generated method stub
		System.out.printf("shear(shx=%lf, shy=%lf)%n", shx, shy);

	}

	@Override
	public void transform(AffineTransform Tx) {
//		// TODO Auto-generated method stub
//		System.out.printf("transform(Tx=%s)%n", Tx.toString());

	}

	@Override
	public void setTransform(AffineTransform Tx) {
		// TODO Auto-generated method stub
		System.out.printf("setTransform(Tx=%s)%n", Tx.toString());

	}

	@Override
	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		System.out.printf("getTransform()%n");
		return null;
	}

	@Override
	public Paint getPaint() {
		// TODO Auto-generated method stub
		System.out.printf("getPaint()%n");
		return null;
	}

	@Override
	public Composite getComposite() {
		// TODO Auto-generated method stub
		System.out.printf("getComposite()%n");
		return null;
	}

	@Override
	public void setBackground(Color color) {
		// TODO Auto-generated method stub
		System.out.printf("setBackground(color=%s)%n", color.toString());
	}

	@Override
	public Color getBackground() {
		// TODO Auto-generated method stub
		System.out.printf("getBackground()%n");
		return null;
	}

	@Override
	public Stroke getStroke() {
		// TODO Auto-generated method stub
		System.out.printf("getStroke()%n");
		return null;
	}

	@Override
	public void clip(Shape s) {
		// TODO Auto-generated method stub
		System.out.printf("clip(shape=%s)%n", s.toString());
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		// TODO Auto-generated method stub
		System.out.printf("getFontRenderContext()%n");
		return null;
	}

	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		System.out.printf("create()%n");
		return null;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		System.out.printf("getColor()%n");
		return null;
	}

	@Override
	public void setColor(Color c) {
		// TODO Auto-generated method stub
		System.out.printf("setColor(color=%s)%n", c.toString());

	}

	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub
		System.out.printf("setPaintMode()%n");

	}

	@Override
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub
		System.out.printf("setXORMode(color=%s)%n", c1.toString());

	}

	@Override
	public Font getFont() {
		// TODO Auto-generated method stub
		System.out.printf("getFont()%n");
		return null;
	}

	@Override
	public void setFont(Font font) {
		// TODO Auto-generated method stub
		System.out.printf("setFont(font=%s)%n", font.toString());

	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		// TODO Auto-generated method stub
		System.out.printf("getFontMetrics(font=%s)%n", f.toString());
		return null;
	}

	@Override
	public Rectangle getClipBounds() {
		// TODO Auto-generated method stub
		System.out.printf("getClipBounds()%n");
		return null;
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("clipRect(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
//		System.out.printf("setClip(x=%d, y=%d, width=%d, height=%d)%n", x, y,
//				width, height);
	}

	@Override
	public Shape getClip() {
		// TODO Auto-generated method stub
		System.out.printf("getClip()%n");
		return null;
	}

	@Override
	public void setClip(Shape clip) {
		// TODO Auto-generated method stub
//		System.out.printf("setClip(shape=%s)%n", clip.toString());
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
		System.out.printf(
				"copyArea(x=%d, y=%d, width=%d, height=%d, dx=%d, dy=%d)%n", x,
				y, width, height, dx, dy);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub
		System.out.printf("drawLine(x1=%d, y1=%d, x2=%d, y2=%d)%n", x1, y1, x2,
				y2);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("fillRect(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("clearRect(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawRoundRect(x=%d, y=%d, width=%d, height=%d, arcWidth=%d, arcHeight=%d)%n",
						x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		// TODO Auto-generated method stub
		System.out
				.printf("fillRoundRect(x=%d, y=%d, width=%d, height=%d, arcWidth=%d, arcHeight=%d)%n",
						x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("drawOval(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		System.out.printf("fillOval(x=%d, y=%d, width=%d, height=%d)%n", x, y,
				width, height);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawArc(x=%d, y=%d, width=%d, height=%d, startAngle=%d, arcAngle=%d)%n",
						x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		// TODO Auto-generated method stub
		System.out
				.printf("fillArc(x=%d, y=%d, width=%d, height=%d, startAngle=%d, arcAngle=%d)%n",
						x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub
		System.out.printf("drawPolyline(xPoints=%s, yPoints=%s, nPoints=%d)%n",
				Arrays.toString(xPoints), Arrays.toString(yPoints), nPoints);
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub
		System.out.printf("drawPolygon(xPoints=%s, yPoints=%s, nPoints=%d)%n",
				Arrays.toString(xPoints), Arrays.toString(yPoints), nPoints);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		// TODO Auto-generated method stub
		System.out.printf("fillPolygon(xPoints=%s, yPoints=%s, nPoints=%d)%n",
				Arrays.toString(xPoints), Arrays.toString(yPoints), nPoints);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out.printf("drawImage(img=%s, x=%d, y=%d, observer=%s)%n",
				img.toString(), x, y, observer.toString());
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, x=%d, y=%d, width=%d, height=%d, observer=%s)%n",
						img.toString(), x, y, width, height,
						observer.toString());
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out.printf(
				"drawImage(img=%s, x=%d, y=%d, bgcolor=%s, observer=%s)%n",
				img.toString(), x, y, bgcolor.toString(), observer.toString());
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, x=%d, y=%d, width=%d, height=%d, bgcolor=%s, observer=%s)%n",
						img.toString(), x, y, width, height,
						bgcolor.toString(), observer.toString());
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, dx1=%d, dy1=%d, dx2=%d, dy2=%d, sx1=%d, sy1=%d, sx2=%d, sy2=%d, observer=%s)%n",
						img.toString(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
						observer.toString());
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		// TODO Auto-generated method stub
		System.out
				.printf("drawImage(img=%s, dx1=%d, dy1=%d, dx2=%d, dy2=%d, sx1=%d, sy1=%d, sx2=%d, sy2=%d, bgcolor=%s, observer=%s)%n",
						img.toString(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
						bgcolor.toString(), observer.toString());
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
//		System.out.printf("dispose()%n");
	}

	public String getCurrentPathString() {
		return this.currentPathString;
	}

}

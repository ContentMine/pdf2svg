/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdfviewer;

import java.awt.Dimension;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JPanel;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * This is a simple JPanel that can be used to display a PDF page.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDFPagePanel extends JPanel
{

    private static final long serialVersionUID = -4629033339560890669L;
    
    private PDPage page;
    private org.apache.pdfbox.pdfviewer.PageDrawer drawer = null;
    private Dimension pageDimension = null;
    private Dimension drawDimension = null;

	private org.apache.batik.svggen.SVGGraphics2D  svg;

    /**
     * Constructor.
     *
     * @throws IOException If there is an error creating the Page drawing objects.
     */
    public PDFPagePanel() throws IOException
    {
        drawer = new PageDrawer();
    }

    /**
     * This will set the page that should be displayed in this panel.
     *
     * @param pdfPage The page to draw.
     */
    static int pageno = 0;

    public void setPage( PDPage pdfPage )
    {
        page = pdfPage;
        System.out.println(">>>>"+page);
        pageno++;
        PDRectangle cropBox = page.findCropBox();
        drawDimension = cropBox.createDimension();
        int rotation = page.findRotation();
        if (rotation == 90 || rotation == 270)
        {
            pageDimension = new Dimension(drawDimension.height, drawDimension.width);
        }
        else
        {
            pageDimension = drawDimension;
        }
        setSize( pageDimension );
        setBackground( java.awt.Color.white );
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics g )
    {
        try
        {
            g.setColor( getBackground() );
            g.fillRect( 0, 0, getWidth(), getHeight() );

            int rotation = page.findRotation();
            if (rotation == 90 || rotation == 270)
            {
                Graphics2D g2D = (Graphics2D)g;
                g2D.translate(pageDimension.getWidth(), 0.0f);
                g2D.rotate(Math.toRadians(rotation));
            }

            drawer.drawPage( g, page, drawDimension );
            svg = ensureSVG();
            drawer.drawPage( svg, page, drawDimension );
            writeSVG(svg);

        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

	private void writeSVG(SVGGraphics2D svg) {
		try {
		    boolean useCSS = true; // we want to use CSS style attributes
			Writer svgwriter = new OutputStreamWriter(new FileOutputStream("test"+pageno+".svg"), "UTF-8");
		    svg.stream(svgwriter, useCSS);
		    svgwriter.close();
		} catch (Exception e) {
			throw new RuntimeException("cannot write SVG", e);
		}
	}

	private SVGGraphics2D ensureSVG() {
		/**
  public static void main(String[] args) throws IOException {

        // Get a DOMImplementation.
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        TestSVGGen test = new TestSVGGen();
        test.paint(svgGenerator);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        Writer out = new OutputStreamWriter(System.out, "UTF-8");
        svgGenerator.stream(out, useCSS);
    }		 */
		org.w3c.dom.DOMImplementation domImpl =
				org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);
        SVGGraphics2D svg = new org.apache.batik.svggen.SVGGraphics2D(document);
        return svg;
	}
}

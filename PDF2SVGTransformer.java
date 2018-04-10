package org.contentmine.pdf2svg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * new version of PDF2SVGConverter for PDFBox 2.0
 * 
 * @author pm286
 *
 */
public class PDF2SVGTransformer {
	private static final Logger LOG = Logger.getLogger(PDF2SVGTransformer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private PDDocument document;
	private PDFRenderer renderer;
	private int pageCount;
	StringBuilder debugBuilder;
	private SVGSVG svgBuilder;
//	private Real2 currentPoint = new Real2(0.0, 0.0); // to avoid null pointer
	private Multiset<String> glyphSet;
	private Multiset<String> codePointSet;
	private double eps = 0.000001;
	private PDFPage2SVGConverter pdfPage2SVGConverter;

	public PDF2SVGTransformer() {
        this.debugBuilder = new StringBuilder();
        this.svgBuilder = new SVGSVG();
        this.glyphSet = HashMultiset.create();
        this.codePointSet = HashMultiset.create();
	}

	private void ensureCodePointSet() {
		if (codePointSet == null) {
	    	codePointSet = HashMultiset.create();
    	}
	}

	public void convert(File file) throws IOException {
		String fileRoot = FilenameUtils.getBaseName(file.toString());
        document = PDDocument.load(file);
        renderer = new PDFRenderer(document);
        pageCount = document.getNumberOfPages();
        LOG.info("Page count: "+pageCount);
        for (int ipage = 0; ipage < pageCount; ipage++) {
	        PDPage page = document.getPage(ipage);
	        LOG.info("page: "+ipage);
	        pdfPage2SVGConverter = new PDFPage2SVGConverter(this, renderer, page);
	        pdfPage2SVGConverter.processPage();
	        GraphicsElement svgElement = pdfPage2SVGConverter.getSVG();
	        this.writePage(new File("target/debug/"+fileRoot+"/page_"+ipage+".txt"));
	        XMLUtil.debug(svgElement, new FileOutputStream(new File("target/debug/"+fileRoot+"/page_"+ipage+".svg")), 1);
        }
        LOG.info("CodePoints "+codePointSet);
        document.close();
	}
	
	private void writeSVG(File file) throws IOException {
		LOG.info("wrote: "+file.getAbsolutePath());
		XMLUtil.debug(svgBuilder, file, 1);
		svgBuilder = new SVGSVG();
	}

	public void writePage(File file) throws IOException {
		LOG.info("wrote: "+file.getAbsolutePath());
		FileUtils.write(file, debugBuilder.toString());
		debugBuilder = new StringBuilder();
	}

	public void append(GraphicsElement element) {
		getPdfPage2SVGConverter().appendChild(element);
	}

	public void addCodePoint(String codePointS) {
		ensureCodePointSet();
		codePointSet.add(codePointS);
	}

	public double getEpsilon() {
		return eps;
	}

	public PDFPage2SVGConverter getPdfPage2SVGConverter() {
		if (pdfPage2SVGConverter == null) {
			throw new RuntimeException("Must create PDFPage2SVGConverter before now");
		}
		return pdfPage2SVGConverter;
	}
	
}

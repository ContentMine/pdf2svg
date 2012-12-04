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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.PageRanges;

import nu.xom.Document;

import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.util.MenuSystem;
import org.xmlcml.pdf2svg.util.PConstants;

/**
 * Simple app to read PDF documents ... based on ... * PDFReader.java
 * 
 */
public class PDF2SVGConverter extends PDFStreamEngine {

	private static final double _DEFAULT_PAGE_WIDTH = 600.0;
	private static final double _DEFAULT_PAGE_HEIGHT = 800.0;
	private static final String DEFAULT_PUBLISHER_SET_XML = PConstants.PDF2SVG_ROOT+"/"+"publisherSet.xml";
	private final static Logger LOG = Logger.getLogger(PDF2SVGConverter.class);
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	public static final String PASSWORD = "-password";
	public static final String NONSEQ = "-nonseq";
	public static final String PAGES = "-pages";
	public static final String PUB = "-pub";
	public static final String OUTDIR = "-outdir";
	public static final String NO_SVG = "-nosvg";
	public static final String INFO_FILES = "-infofiles";

	private String PDFpassword = "";
	private boolean useNonSeqParser = false;
	private String outputDirectory = ".";
	private PageRanges pageRanges = null;
	private Publisher publisher = null;

	private PDDocument document;
	private List<SVGSVG> svgPageList;
	private boolean fixFont = true;
	
	private AMIFontManager amiFontManager;
	private Map<String, AMIFont> amiFontMap;
	CodePointSet knownCodePointSet;
	CodePointSet newCodePointSet;
	private File outdir;
	private int iarg;
	private String publisherSetXmlResource = DEFAULT_PUBLISHER_SET_XML;
	private PublisherSet publisherSet;
	
	Double pageHeight = _DEFAULT_PAGE_HEIGHT;
	Double pageWidth = _DEFAULT_PAGE_WIDTH;
	private PDFPage2SVGConverter page2svgConverter;
	private SVGSVG currentSVGPage;
	private boolean writeFile = true;
	private boolean writeInfoFiles = false;
	
	public boolean drawBoxesForClipPaths = false;
	public boolean addTooltipDebugTitles = false;

	private static void usage() {
		System.err
				.printf("Usage: pdf2svg [%s <pw>] [%s] [%s <page-ranges>] [%s <pub>] [%s <dir>] [%s] [%s] <input-file> ...%n%n"
						+ "  %s <password>  Password to decrypt the document (default none)%n"
						+ "  %s               Enables the new non-sequential parser%n"
						+ "  %s <page-ranges>  Restrict pages to be output (default all)%n"
						+ "  %s <publisher>      Use publisher-specific info%n"
						+ "  %s <dirname>     Location to write output SVG pages (default '.')%n"
						+ "  %s                Don't write SVG files%n"
						+ "  %s            Write info files%n"
						+ "  <input-file>          The PDF document to be loaded%n",
						PASSWORD, NONSEQ, PAGES, PUB, OUTDIR, NO_SVG, INFO_FILES, PASSWORD, NONSEQ,
						PAGES, PUB, OUTDIR, NO_SVG, INFO_FILES);
	}

	private void openPDFFile(String filename) throws Exception {

		page2svgConverter = new PDFPage2SVGConverter();
		LOG.debug("Parsing PDF file "+ filename);
		readDocument(filename, useNonSeqParser, PDFpassword);

		@SuppressWarnings("unchecked")
		List<PDPage> pages = document.getDocumentCatalog().getAllPages();

		PageRanges pr = pageRanges;
		if (pr == null) {
			pr = new PageRanges(String.format("1-%d", pages.size()));
		}

		createOutputDirectory();

		LOG.debug("Processing pages "+pr.toString()+" (of "+pages.size()+")"); 

		File infile = new File(filename);
		String basename = infile.getName().toLowerCase();
		if (basename.endsWith(".pdf"))
			basename = basename.substring(0, basename.length() - 4);

		int pageNumber = pr.next(0);
		List<File> outfileList = new ArrayList<File>();
		while (pageNumber > 0) {
			PDPage page = pages.get(pageNumber - 1);

			LOG.debug("=== " + pageNumber + " ===");
			currentSVGPage = page2svgConverter.convertPageToSVG(page, this);

			addPageToPageList();
			if (writeFile) {
				File outfile = writeFile(basename, pageNumber);
				outfileList.add(outfile);
			}

			pageNumber = pr.next(pageNumber);
		}

		if (writeInfoFiles) {
			reportHighCodePoints();
			reportNewFontFamilyNames();
			writeHTMLSystem(outfileList);
			reportPublisher();
		}
	}

	private void addPageToPageList() {
		ensureSVGPageList();
		SVGSVG svgPage = page2svgConverter.getSVG();
		svgPageList.add(svgPage);
	}

	private void reportPublisher() {
		if (publisher != null) {
			LOG.debug("PUB "+publisher.createElement().toXML());
		}
	}

	private void createOutputDirectory() {
		outdir = new File(outputDirectory);
		if (!outdir.exists())
			outdir.mkdirs();
		if (!outdir.isDirectory())
			throw new RuntimeException(String.format(
					"'%s' is not a directory!", outputDirectory));
	}

	private File writeFile(String basename, int pageNumber) {

		File outfile = null;
		try {
			outfile = new File(outdir, basename + "-page" + pageNumber + ".svg");
			LOG.trace("Writing output to file '"+outfile.getCanonicalPath());
			SVGSerializer serializer = new SVGSerializer(new FileOutputStream(
					outfile), "UTF-8");
			Document document = currentSVGPage.getDocument();
			document = (document == null) ? new Document(currentSVGPage) : document;
			serializer.setIndent(1);
			serializer.write(document);
		} catch (Exception e) {
			throw new RuntimeException("Cannot convert PDF to SVG", e);
		}
		return outfile;
	}

	private void reportNewFontFamilyNames() {
		FontFamilySet newFontFamilySet = amiFontManager.getNewFontFamilySet();
		LOG.debug("new fontFamilyNames: "+newFontFamilySet.createElement().toXML());
	}

	private void writeHTMLSystem(List<File> outfileList) {
		MenuSystem menuSystem = new MenuSystem(outdir);
		menuSystem.writeDisplayFiles(outfileList, "");
	}

	private void ensureSVGPageList() {
		if (svgPageList == null) {
			svgPageList = new ArrayList<SVGSVG>();
		}
	}

	private void readDocument(String filename, boolean useNonSeqParser,
			String password) throws IOException {
		File file = new File(filename);
		if (useNonSeqParser) {
			document = PDDocument.loadNonSeq(file, null, password);
		} else {
			document = PDDocument.load(file);
			if (document.isEncrypted()) {
				try {
					document.decrypt(password);
				} catch (InvalidPasswordException e) {
					System.err
							.printf("Error: The document in file '%s' is encrypted (use '-password' option).%n",
									filename);
					return;
				} catch (CryptographyException e) {
					System.err
							.printf("Error: Failed to decrypt document in file '%s'.%n",
									filename);
					return;
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run(args);

		System.exit(0);
	}

	public void run(String argString) {
		run(argString.split("[\\s+]"));
	}

	public void run(String... args) {

		if (args.length == 0)
			usage();

		for (iarg = 0; iarg < args.length; iarg++) {

			if (args[iarg].equals(PASSWORD)) {
				incrementArg(args);
				PDFpassword = args[iarg];
				continue;
			}

			if (args[iarg].equals(NONSEQ)) {
				useNonSeqParser = true;
				continue;
			}

			if (args[iarg].equals(OUTDIR)) {
				incrementArg(args);
				outputDirectory = args[iarg];
				continue;
			}

			if (args[iarg].equals(NO_SVG)) {
				writeFile  = false;
				continue;
			}

			if (args[iarg].equals(INFO_FILES)) {
				writeInfoFiles  = true;
				continue;
			}

			if (args[iarg].equals(PAGES)) {
				incrementArg(args);
				pageRanges = new PageRanges(args[iarg]);
				continue;
			}
			if (args[iarg].equals(PUB)) {
				incrementArg(args);
				publisher = getPublisher(args[iarg]);
				continue;
			}

			try {
				this.openPDFFile(args[iarg]);
			} catch (Exception e) {
				throw new RuntimeException("Cannot parse PDF: " + args[iarg], e);
			}
		}
	}

	public Publisher getPublisher() {
		return publisher;
	}

	private Publisher getPublisher(String abbreviation) {
		ensurePublisherMaps();
		publisher = (publisherSet == null) ? null : publisherSet.getPublisherByAbbreviation(abbreviation);
		return publisher;
	}

	private void ensurePublisherMaps() {
		if (publisherSet == null && publisherSetXmlResource != null) {
			publisherSet = PublisherSet.readPublisherSet(publisherSetXmlResource );
		}
	}

	private void incrementArg(String... args) {
		iarg++;
		if (iarg >= args.length) {
			usage();
		}
	}

	public List<SVGSVG> getPageList() {
		ensureSVGPageList();
		return svgPageList;
	}

	private void reportHighCodePoints() {
		ensureCodePointSets();
		int newCodePointCount = newCodePointSet.size();
		if (newCodePointCount > 0) {
			LOG.debug("New High CodePoints: " + newCodePointSet.size());
			LOG.debug(newCodePointSet.createElementWithSortedIntegers().toXML());
		}
	}

	void ensureCodePointSets() {
		if (newCodePointSet == null) {
			newCodePointSet = new CodePointSet();
		}
		if (knownCodePointSet == null) {
			knownCodePointSet = CodePointSet.readCodePointSet(CodePointSet.KNOWN_HIGH_CODE_POINT_SET_XML); 
		}
	}

	public CodePointSet getKnownCodePointSet() {
		ensureCodePointSets();
		return knownCodePointSet;
	}

	public CodePointSet getNewCodePointSet() {
		ensureCodePointSets();
		return newCodePointSet;
	}

	public void setFixFont(boolean fixFont) {
		this.fixFont = fixFont;
	}

	public boolean isFixFont() {
		return fixFont ;
	}
	
	public AMIFontManager getAmiFontManager() {
		ensureAmiFontManager();
		return amiFontManager;
	}

	private void ensureAmiFontManager() {
		if (amiFontManager == null) {
			amiFontManager = new AMIFontManager();
			amiFontMap = AMIFontManager.readAmiFonts();
			for (String fontName : amiFontMap.keySet()) {
				AMIFont font = amiFontMap.get(fontName);
			}
		}
	}

	Map<String, AMIFont> getAMIFontMap() {
		ensureAmiFontManager();
		return amiFontMap;
	}
}

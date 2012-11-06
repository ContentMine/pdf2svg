package org.xmlcml.graphics.pdf2svg.raw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.standard.PageRanges;

import nu.xom.Builder;
import nu.xom.Document;

import org.apache.log4j.Logger;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.xmlcml.graphics.svg.SVGSVG;

/**
 * Simple app to read PDF documents ... based on ... * PDFReader.java
 * 
 */
public class PDF2SVGConverter extends PDFStreamEngine {

	private final static Logger LOG = Logger.getLogger(PDF2SVGConverter.class);
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private static final String PASSWORD = "-password";
	private static final String NONSEQ = "-nonseq";
	private static final String PAGES = "-pages";
	private static final String OUTDIR = "-outdir";

	private String PDFpassword = "";
	private boolean useNonSeqParser = false;
	private String outputDirectory = ".";
	private PageRanges pageRanges = null;

	private PDDocument document;
	private List<SVGSVG> svgPageList;
	private boolean fixFont = true;
	
	private AMIFontManager amiFontManager;
	private Map<String, AMIFont> amiFontMap;
	CodePointSet knownCodePointSet;
	CodePointSet newCodePointSet;

	private static void usage() {
		System.err
				.printf("Usage: pdf2svg [%s pw] [%s] [%s <page-ranges>] [%s dir] <input-file> ...%n%n"
						+ "  %s <password>  Password to decrypt the document (default none)%n"
						+ "  %s               Enables the new non-sequential parser%n"
						+ "  %s <page-ranges>  Restrict pages to be output (default all)%n"
						+ "  %s <dirname>     Location to write output pages (default '.')%n"
						+ "  <input-file>          The PDF document to be loaded%n",
						PASSWORD, NONSEQ, PAGES, OUTDIR, PASSWORD, NONSEQ,
						PAGES, OUTDIR);
		System.exit(1);
	}

	private void openPDFFile(String filename) throws Exception {

		PDFPage2SVGConverter drawer = new PDFPage2SVGConverter();
		System.out.printf("Parsing PDF file %s ...%n", filename);
		readDocument(filename, useNonSeqParser, PDFpassword);

		@SuppressWarnings("unchecked")
		List<PDPage> pages = document.getDocumentCatalog().getAllPages();

		PageRanges pr = pageRanges;
		if (pr == null)
			pr = new PageRanges(String.format("1-%d", pages.size()));

		File outdir = new File(outputDirectory);
		if (!outdir.exists())
			outdir.mkdirs();
		if (!outdir.isDirectory())
			throw new RuntimeException(String.format(
					"'%s' is not a directory!", outputDirectory));

		System.out.printf("Processing pages %s (of %d) ...%n", pr.toString(),
				pages.size());

		File infile = new File(filename);
		String basename = infile.getName().toLowerCase();
		if (basename.endsWith(".pdf"))
			basename = basename.substring(0, basename.length() - 4);

		int pageNumber = pr.next(0);
		while (pageNumber > 0) {
			PDPage page = pages.get(pageNumber - 1);

			System.out.println("=== " + pageNumber + " ===");
			drawer.convertPageToSVG(page, this);

			File outfile = new File(outdir, basename + "-page" + pageNumber
					+ ".svg");
			System.out.printf("Writing output to file '%s'%n", outfile.getCanonicalPath());

			SVGSVG svgPage = drawer.getSVG();
			SVGSerializer serializer = new SVGSerializer(new FileOutputStream(
					outfile), "UTF-8");
			Document document = svgPage.getDocument();
			document = (document == null) ? new Document(svgPage) : document;
			serializer.setIndent(1);
			serializer.write(document);
			ensureSVGPageList();
			svgPageList.add(svgPage);
			new Builder().build(outfile);

			pageNumber = pr.next(pageNumber);
		}

		reportHighCodePoints();
		reportNewFontFamilyNames();
	}

	private void reportNewFontFamilyNames() {
		FontFamilySet newFontFamilySet = amiFontManager.getNewFontFamilySet();
		LOG.debug("new fontFamilyNames: "+newFontFamilySet.createElement().toXML());
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

		for (int i = 0; i < args.length; i++) {

			if (args[i].equals(PASSWORD)) {
				i++;
				if (i >= args.length) {
					usage();
				}
				PDFpassword = args[i];
				continue;
			}

			if (args[i].equals(NONSEQ)) {
				useNonSeqParser = true;
				continue;
			}

			if (args[i].equals(OUTDIR)) {
				i++;
				if (i >= args.length) {
					usage();
				}
				outputDirectory = args[i];
				continue;
			}

			if (args[i].equals(PAGES)) {
				i++;
				if (i >= args.length) {
					usage();
				}
				pageRanges = new PageRanges(args[i]);
				continue;
			}

			try {
				this.openPDFFile(args[i]);
			} catch (Exception e) {
				throw new RuntimeException("Cannot parse PDF: " + args[i], e);
			}
		}
	}

	public List<SVGSVG> getPageList() {
		ensureSVGPageList();
		return svgPageList;
	}

	private void reportHighCodePoints() {
		ensureCodePointSets();
		LOG.debug("New High CodePoints: " + newCodePointSet.size());
		LOG.debug(newCodePointSet.createElementWithSortedIntegers().toXML());
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

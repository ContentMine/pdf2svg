package org.xmlcml.graphics.pdf2svg.raw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFStreamEngine;
import org.xmlcml.cml.base.CMLUtil;

/**
 * Simple app to read PDF documents ... based on ...
 * 
 * PDFReader.java
 * 
 * @author <a href="ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */
public class PDF2SVGConverter extends PDFStreamEngine {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	private static final String PASSWORD = "-password";
	private static final String NONSEQ = "-nonSeq";
	private static boolean useNonSeqParser = false;

	private PDDocument document;

	private static void usage() {
		System.err.println("usage: java -jar pdfbox-myexample-x.y.z.jar [OPTIONS] <input-file> ...\n"
						+ "  -password <password>      Password to decrypt the document\n"
						+ "  -nonSeq                   Enables the new non-sequential parser\n"
						+ "  <input-file>              The PDF document to be loaded\n");
		System.exit(1);
	}

	private void openPDFFile(String filename, String password) throws Exception {

		PDFPage2SVGConverter drawer = new PDFPage2SVGConverter();
		
		System.out.printf("Parsing PDF file %s ...%n", filename);
		
		readDocument(filename, useNonSeqParser, password);
		
		@SuppressWarnings("unchecked")
		List<PDPage> pages = document.getDocumentCatalog().getAllPages();
		int pageNumber = 1;

		System.out.printf("Processing %d pages ...%n", pages.size());

		for (PDPage page : pages) {
			drawer.convertPageToSVG(page);
			pageNumber++;
			System.out.println("=== "+pageNumber+" ===");
			CMLUtil.debug(drawer.getSVG(), new FileOutputStream("target/page" + pageNumber + ".svg"), 1);
		}
	}

	private void readDocument(String filename, boolean useNonSeqParser, String password) throws IOException {
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

		PDF2SVGConverter myPDFReader = new PDF2SVGConverter();
		String password = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(PASSWORD)) {
				i++;
				if (i >= args.length) {
					usage();
				}
				password = args[i];
			}
			if (args[i].equals(NONSEQ)) {
				useNonSeqParser = true;
			} else {
				myPDFReader.openPDFFile(args[i], password);
			}
		}

		System.exit(0);
	}

}

package org.xmlcml.pdf2svg.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.log4j.Logger;
import org.apache.pdfbox.encoding.Encoding;
import org.xmlcml.pdf2svg.AMIFont;

public class XMLLogger {

	private final static Logger LOG = Logger.getLogger(XMLLogger.class);

	private Element root;
	private Element fontlist;
	private Element file;
	private Element page;
	private Map<String, AMIFont> fontmap;

	public XMLLogger() {
		reset();
	}

	public void reset() {
		root = new Element("pdfLog");
		fontlist = new Element("fontList");
		root.appendChild(fontlist);
		file = null;
		page = null;
		fontmap = new HashMap<String, AMIFont>();
	}

	public void newPDFFile(String fileName, int pageCount) {
		file = new Element("pdf");
		file.addAttribute(new Attribute("filename", fileName));
		file.addAttribute(new Attribute("pageCount", Integer
				.toString(pageCount)));
		root.appendChild(file);
	}

	public void newPDFPage(int pageNumber) {
		if (file == null)
			throw new RuntimeException("no current PDF file!");
		page = new Element("page");
		page.addAttribute(new Attribute("num", Integer.toString(pageNumber)));
		file.appendChild(page);
	}

	public void newFont(AMIFont amiFont) {
		String fontName = amiFont.getFontName();
		if (fontName == null)
			return;

		if (fontmap.containsKey(fontName))
			return;
		fontmap.put(fontName, amiFont);

		Element font = new Element("font");
		font.addAttribute(new Attribute("isBold", Boolean.toString(amiFont
				.isBold())));
		font.addAttribute(new Attribute("isItalic", Boolean.toString(amiFont
				.isItalic())));
		font.addAttribute(new Attribute("isSymbol", Boolean.toString(amiFont
				.isSymbol())));

		String fontFamilyName = amiFont.getFontFamilyName();
		font.addAttribute(new Attribute("fontFamilyName",
				fontFamilyName == null ? "null" : fontFamilyName));

		font.addAttribute(new Attribute("fontName", fontName));

		/* pdFont="org.apache.pdfbox.pdmodel.font.PDType1Font@37567e6c" */
		/*
		 * fontDescriptor=
		 * "org.apache.pdfbox.pdmodel.font.PDFontDescriptorDictionary@f8600d6"
		 */

		String fontType = amiFont.getFontType();
		font.addAttribute(new Attribute("fontType", fontType == null ? "null"
				: fontType));

		Encoding encoding = amiFont.getEncoding();
		font.addAttribute(new Attribute("encoding", encoding == null ? "null"
				: encoding.getClass().getSimpleName()));

		String fontEncoding = amiFont.getFontEncoding();
		font.addAttribute(new Attribute("fontEncoding",
				fontEncoding == null ? "null" : fontEncoding));

		String baseFont = amiFont.getBaseFont();
		font.addAttribute(new Attribute("baseFont", baseFont == null ? "null"
				: baseFont));

		fontlist.appendChild(font);
	}

	public void newCharacter(String fontName, String charName, int charValue) {
		if (page == null)
			throw new RuntimeException("no current PDF page!");

		if (fontName == null) {
			return;
		}

		if (!fontmap.containsKey(fontName)) {
			LOG.error("new character ("+charName+","+charValue+") specifies font name '" + fontName
					+ "' - which doesn't exist!");
//			printFontMapKeys();
		}

		Element character = new Element("character");

		character.addAttribute(new Attribute("font", fontName));
		character.addAttribute(new Attribute("charname",
				charName == null ? "null" : charName));
		character.addAttribute(new Attribute("charval", Integer
				.toString(charValue)));

		page.appendChild(character);
	}

	private void printFontMapKeys() {
		int i = 0;
		List<String> keys = Arrays.asList(fontmap.keySet().toArray(new String[0]));
		Collections.sort(keys);
		for (String key : keys) {
			System.out.print(key+" ... ");
			if (++i %5 == 0) {
				System.out.println();
			}
		}
	}

	public void writeXMLFile(OutputStream outputStream) {
		Document doc = new Document(root);
		try {
			Serializer serializer = new Serializer(outputStream, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(64);
			serializer.write(doc);
			serializer.flush();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public void writeXMLFile(String outdir, String pdfname) {
		String logname = pdfname.replaceFirst("(?i)\\.pdf$", "")
				+ "-log.xml";

		File outputFile = new File(outdir, logname);
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					"caught File Not Found exception while creating logfile '"
							+ outputFile.getAbsolutePath() + "'.");
		}

		writeXMLFile(outputStream);
	}
}

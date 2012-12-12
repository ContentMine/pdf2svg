package org.xmlcml.pdf2svg.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.log4j.Logger;
import org.apache.pdfbox.encoding.Encoding;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.pdf2svg.AMIFont;

public class XMLLogger {

	private final static Logger LOG = Logger.getLogger(XMLLogger.class);

	private Element root;
	private Element fontlist;
	private Element file;
	private Element page;

	private List<String> fontnames; // names of all fonts in the fontlist
	private Map<String, AMIFont> fontmap; // only valid for the current PDF
	private final boolean logGlyphs;

	public XMLLogger() {
		logGlyphs = false;
		reset();
	}

	public XMLLogger(boolean logGlyphs) {
		this.logGlyphs = logGlyphs;
		reset();
	}

	public void reset() {
		root = new Element("pdfLog");
		root.addAttribute(new Attribute("glyphs", Boolean.toString(logGlyphs)));

		fontlist = new Element("fontList");
		root.appendChild(fontlist);

		file = null;
		page = null;

		fontnames = new ArrayList<String>();
		if (logGlyphs)
			fontmap = null;
	}

	public void newPDFFile(String fileName, int pageCount) {
		file = new Element("pdf");
		file.addAttribute(new Attribute("filename", fileName));
		file.addAttribute(new Attribute("pageCount", Integer
				.toString(pageCount)));
		root.appendChild(file);

		if (logGlyphs)
			fontmap = new HashMap<String, AMIFont>();
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
		if (logGlyphs)
			fontmap.put(fontName, amiFont);

		if (fontnames.contains(fontName))
			return;
		fontnames.add(fontName);

		Element font = new Element("font");

		font.addAttribute(new Attribute("name", fontName));
		String fontFamilyName = amiFont.getFontFamilyName();
		font.addAttribute(new Attribute("family",
				fontFamilyName == null ? "null" : fontFamilyName));

		String fontType = amiFont.getFontType();
		font.addAttribute(new Attribute("type", fontType == null ? "null"
				: fontType));
		Encoding encoding = amiFont.getEncoding();
		font.addAttribute(new Attribute("encoding", encoding == null ? "null"
				: encoding.getClass().getSimpleName()));
		String fontEncoding = amiFont.getFontEncoding();

		font.addAttribute(new Attribute("fontencoding",
				fontEncoding == null ? "null" : fontEncoding));
		String baseFont = amiFont.getBaseFont();
		font.addAttribute(new Attribute("basefont", baseFont == null ? "null"
				: baseFont));

		font.addAttribute(new Attribute("bold", Boolean.toString(amiFont
				.isBold())));
		font.addAttribute(new Attribute("italic", Boolean.toString(amiFont
				.isItalic())));
		font.addAttribute(new Attribute("symbol", Boolean.toString(amiFont
				.isSymbol())));

		fontlist.appendChild(font);
	}

	public void newCharacter(String fontName, String fontFamilyName,
			String charName, int charCode) {
		if (file == null || page == null)
			throw new RuntimeException("no current PDF file or page!");

		if (fontName == null) {
			LOG.error("fontName is null! (charName=" + charName + ",charValue="
					+ charCode + ")");
			return;
		}

		if (!fontnames.contains(fontName)) {
			LOG.error("new character (" + charName + "," + charCode
					+ ") specifies font name '" + fontName
					+ "' - which doesn't exist!");
		}

		Element character = new Element("character");

		character.addAttribute(new Attribute("font", fontName));
		character.addAttribute(new Attribute("family",
				fontFamilyName == null ? "null" : fontFamilyName));
		character.addAttribute(new Attribute("name", charName == null ? "null"
				: charName));
		character
				.addAttribute(new Attribute("code", Integer.toString(charCode)));

		if (logGlyphs) {
			AMIFont amiFont = fontmap.get(fontName);
			if (amiFont == null) {
				LOG.error(String.format("no AMIFont available for (%s,%s,%d)",
						fontName, charName, charCode));
			} else {
				String key = charName;
				if (key == null)
					key = "" + charCode;
				String d = amiFont.getPathStringByCharnameMap().get(key);
				if (d != null)
					character.appendChild(new SVGPath(d));
			}
		}

		page.appendChild(character);
	}

	public void writeXMLFile(OutputStream outputStream) {
		Document doc = new Document(root);
		try {
			Serializer serializer = new Serializer(outputStream, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(50);
			serializer.write(doc);
			serializer.flush();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public void writeXMLFile(String outdir, String pdfname) {
		String logname = pdfname.replaceFirst("(?i)\\.pdf$", "") + "-log.xml";

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

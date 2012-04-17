package org.xmlcml.graphics.font;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** manages the fonts available for parsing the PDF
 * 
 * @author pm286
 *
 */
public class FontManager {
	private static final String FONT_LIST_RESOURCES = "fontListResources.txt";

	public final static String packageName = FontManager.class.getPackage().getName();
	public final static String packageBase = packageName.replace(".", "/");
	
	List<OutlineFont> fontList = new ArrayList<OutlineFont>();

	private Map<String, OutlineFont> fontByNameMap = new HashMap<String, OutlineFont>();
	
	public FontManager() {
		// use default location
		this(packageBase+"/"+FONT_LIST_RESOURCES);
	}
	
	public FontManager(String resourceFile) {
		readFonts(resourceFile);
	}
	
	public void readFonts(String resourceFile) {
		fontList = new ArrayList<OutlineFont>();
		InputStream inputStream = FontManager.class.getClassLoader().getResourceAsStream(resourceFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		while (true) {
			String fontResource = null;
			try {
				fontResource = reader.readLine();
				if (fontResource == null) break;
				fontResource = fontResource.trim();
				if (fontResource.length() != 0) {
					OutlineFont outlineFont = OutlineFont.readAndCreateFont(packageBase+"/"+fontResource);
					if (outlineFont != null) {
						fontList.add(outlineFont);
						register(outlineFont);
					} else {
						throw new RuntimeException("Cannot create font: "+fontResource);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot read/parse "+fontResource, e);
			}
		}
	}
	public List<OutlineFont> getLocalFonts() {
		return fontList;
	}
	
	public OutlineFont getFontForName(String fontName) {
		return fontByNameMap.get(fontName);
	}
	
	public void register(OutlineFont font) {
		String fontName = font.getFontName();
		if (fontByNameMap.get(fontName) != null) {
			throw new RuntimeException("font already registered: "+fontName);
		}
		fontByNameMap.put(fontName, font);
	}
	
	public OutlineFont getDefaultFont() {
		List<OutlineFont> fontList = getLocalFonts();
		return fontList.size() == 0 ? null : fontList.get(0);
	}
}

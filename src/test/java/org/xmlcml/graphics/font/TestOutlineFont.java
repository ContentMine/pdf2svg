package org.xmlcml.graphics.font;

import java.io.InputStream;

import junit.framework.Assert;
import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Test;

public class TestOutlineFont {

	private static final String FONT1 = FontManager.packageBase+"/"+"outlineFonts/font1/equivalences.xml";
	private FontManager fontManager;

	@Before
	public void setUp() {
		fontManager = new FontManager();
	}
	
	@Test
	public void testProcessEquivalence0() throws Exception {
		InputStream inputStream = TestOutlineFont.class.getClassLoader().getResourceAsStream(FONT1);
		Element equivalence1 = new Builder().build(inputStream).getRootElement().getChildElements().get(0);
		String fontName = equivalence1.getChildElements("text").get(0).getAttributeValue(OutlineFont.FONT_NAME);
		OutlineFont font = fontManager.getFontForName(fontName);
		Assert.assertNotNull("font", font);
	}

}

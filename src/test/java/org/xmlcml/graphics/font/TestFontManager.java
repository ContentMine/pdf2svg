package org.xmlcml.graphics.font;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestFontManager {

	@Test
	public void dummy() {
		
	}
	@Test
	@Ignore
	public void testGetFonts() {
		FontManager fontManager = new FontManager();
		List<OutlineFont> fontList = fontManager.getLocalFonts();
		Assert.assertEquals("font list size", 3, fontList.size());
	}
	
	@Test
	@Ignore
	public void testGetFontByName() throws Exception {
		FontManager fontManager = new FontManager();
		OutlineFont font = fontManager.getFontForName("font1-italic");
		Assert.assertNotNull("font", font);
	}
}

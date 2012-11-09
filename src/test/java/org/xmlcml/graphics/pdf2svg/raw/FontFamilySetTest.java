package org.xmlcml.graphics.pdf2svg.raw;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;

public class FontFamilySetTest {

	@Test
	public void testCreateFromElement() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/nonStandardFontFamilySet.xml", this.getClass())).getRootElement();
		FontFamilySet nonStandardSet = FontFamilySet.createFromElement(fontFamilyElementSet); 
		Assert.assertNotNull(nonStandardSet);
	}
	
	@Test
	public void testChildren() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/nonStandardFontFamilySet.xml", this.getClass())).getRootElement();
		FontFamilySet nonStandardSet = FontFamilySet.createFromElement(fontFamilyElementSet); 
		FontFamily mathPiOne = nonStandardSet.getFontFamilyByName("MathematicalPi-One");
		Assert.assertNotNull(mathPiOne);
		FontFamily nonExist = nonStandardSet.getFontFamilyByName("nonExist");
		Assert.assertNull(nonExist);
	}
}

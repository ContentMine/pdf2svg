package org.xmlcml.graphics.pdf2svg.raw;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;

public class CodePointTest {

	@Test
	public void testGetCodePointAttributes() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByUnicode("U+039F");
		Assert.assertNotNull(codePoint);
		Assert.assertEquals((int)927, (int)codePoint.getDecimal());
		Assert.assertEquals("U+039F", codePoint.getUnicode());
		Assert.assertEquals("GREEK CAPITAL LETTER OMICRON", codePoint.getName());
	}
	
	@Test
	public void testDummy() {
		
	}
}

package org.xmlcml.graphics.pdf2svg.raw;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;

public class CodePointSetTest {

	private final static Logger LOG = Logger.getLogger(CodePointSet.class);
	
	@Test
	public void testCreateFromElementStandard() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/standardEncoding.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		Assert.assertNotNull(nonStandardSet);
	}
	
	@Test
	public void testCreateFromElementHighCodePoints() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		Assert.assertNotNull(nonStandardSet);
	}
	
	@Test
	public void testGetCodePointByUnicode() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByUnicode("U+039F");
		Assert.assertNotNull(codePoint);
		Assert.assertEquals("GREEK CAPITAL LETTER OMICRON", codePoint.getName());
	}
	
	@Test
	public void testGetCodePointByDecimal() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByDecimal((Integer)927);
		Assert.assertNotNull(codePoint);
		Assert.assertEquals("GREEK CAPITAL LETTER OMICRON", codePoint.getName());
	}
	
	@Test
	public void testConvertCharnameToUnicode() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		String unicode = nonStandardSet.convertCharnameToUnicode("GREEK CAPITAL LETTER OMICRON");
		Assert.assertEquals("unicode", "U+039F", unicode);
	}

	@Test
	public void testConvertIntegerToUnicode() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/highCodePoints.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		String unicode = nonStandardSet.convertCharCodeToUnicode((int)927);
		Assert.assertEquals("unicode", "U+039F", unicode);
	}

}

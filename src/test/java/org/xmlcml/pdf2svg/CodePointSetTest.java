/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.pdf2svg;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;
import org.xmlcml.pdf2svg.CodePoint;
import org.xmlcml.pdf2svg.CodePointSet;

public class CodePointSetTest {

	private final static Logger LOG = Logger.getLogger(CodePointSet.class);
	
	
	@Test
	public void testCreateFromElementHighCodePoints() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						CodePointSet.KNOWN_HIGH_CODE_POINT_SET_XML, this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		Assert.assertNotNull(nonStandardSet);
	}
	
	@Test
	public void testGetCodePointByUnicode() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						CodePointSet.KNOWN_HIGH_CODE_POINT_SET_XML, this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByUnicodeValue("U+039F");
		Assert.assertNotNull(codePoint);
		Assert.assertEquals("GREEK CAPITAL LETTER OMICRON", codePoint.getUnicodeName());
	}
	
	@Test
	public void testGetCodePointByDecimal() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						CodePointSet.KNOWN_HIGH_CODE_POINT_SET_XML, this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByDecimal((Integer)927);
		Assert.assertNotNull(codePoint);
		Assert.assertEquals("GREEK CAPITAL LETTER OMICRON", codePoint.getUnicodeName());
	}
	
	@Test
	public void testConvertCharnameToUnicode() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						CodePointSet.KNOWN_HIGH_CODE_POINT_SET_XML, this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByUnicodeName("GREEK CAPITAL LETTER OMICRON");
		Assert.assertEquals("unicode", "U+039F", codePoint.getUnicodeValue());
	}

	@Test
	public void testConvertIntegerToUnicode() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						CodePointSet.KNOWN_HIGH_CODE_POINT_SET_XML, this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		CodePoint codePoint = nonStandardSet.getByDecimal((int)927);
		Assert.assertEquals("unicode", "U+039F", codePoint.getUnicodeValue());
	}

}

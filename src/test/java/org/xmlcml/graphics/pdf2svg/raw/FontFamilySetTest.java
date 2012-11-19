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

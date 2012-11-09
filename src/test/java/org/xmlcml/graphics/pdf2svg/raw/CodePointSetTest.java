package org.xmlcml.graphics.pdf2svg.raw;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;

public class CodePointSetTest {

	@Test
	public void testCreateFromElement() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						"org/xmlcml/graphics/pdf2svg/raw/standardEncoding.xml", this.getClass())).getRootElement();
		CodePointSet nonStandardSet = CodePointSet.createFromElement(fontFamilyElementSet); 
		Assert.assertNotNull(nonStandardSet);
	}
	
}

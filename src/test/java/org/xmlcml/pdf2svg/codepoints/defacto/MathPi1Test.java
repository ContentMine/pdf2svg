package org.xmlcml.pdf2svg.codepoints.defacto;

import junit.framework.Assert;

import org.junit.Test;
import org.xmlcml.pdf2svg.CodePointSet;
import org.xmlcml.pdf2svg.codepoints.Fixtures;

/** tests contents of codePointSet
 * 
 * @author pm286
 *
 */
public class MathPi1Test {

	private static final String DEFACTO_DIR = Fixtures.CODEPOINTS_DIR+"defacto/";
	private static final String MATHPI1_XML = "mathpi1.xml";

	@Test
	public void testReadCodePointSet() {
		CodePointSet codePointSet = CodePointSet.readCodePointSet(DEFACTO_DIR+MATHPI1_XML);
		Assert.assertNotNull(MATHPI1_XML, codePointSet);
		Assert.assertTrue(MATHPI1_XML+" "+codePointSet.size(), codePointSet.size() > 10 && codePointSet.size() < 300);
	}
	
}

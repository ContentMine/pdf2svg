package org.xmlcml.pdf2svg.codepoints.defacto;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.font.CodePointSet;

/** tests contents of codePointSet
 * 
 * @author pm286
 *
 */
public class MathPi1Test {

//	private static final File CODEPOINTS_DIR = new File("src/main/resources/org/xmlcml/pdf2svg/codepoints/");
//	private static final File DEFACTO_DIR = new File(CODEPOINTS_DIR, "defacto/");
	private static final String MATHPI1_XML = "mathpi1.xml";

	@Test
	public void testReadCodePointSet() {
//		Assert.assertTrue("exists", CODEPOINTS_DIR.exists());
//		Assert.assertTrue("exists", DEFACTO_DIR.exists());
//		File mathpi = new File(DEFACTO_DIR, MATHPI1_XML);
//		Assert.assertTrue("mathpi", mathpi.exists());
		CodePointSet codePointSet = CodePointSet.readCodePointSet("org/xmlcml/pdf2svg/codepoints/defacto/mathpi1.xml");
		Assert.assertNotNull(MATHPI1_XML, codePointSet);
		Assert.assertTrue(MATHPI1_XML+" "+codePointSet.size(), codePointSet.size() > 10 && codePointSet.size() < 300);
	}
	
}

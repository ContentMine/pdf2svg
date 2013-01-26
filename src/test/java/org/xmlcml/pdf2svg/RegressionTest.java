package org.xmlcml.pdf2svg;

import java.io.File;

import junit.framework.Assert;
import nu.xom.Element;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.testutil.JumboTestUtils;

/** test complete output of Open conversions
 *  
 * @author pm286
 *
 */
public class RegressionTest {

	@Test
	@Ignore // FIXME ASAP symbol fails on p12
	public void testBMCRegression() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/regression", "src/test/resources/regression/BMCBioinfLin2009.pdf");
		for (int i = 1; i <= 15; i++) {
			System.out.print("t"+i+"=");
			File outfile = new File("target/regression/bmcbioinflin2009-page"+i+".svg");
			Assert.assertTrue("page"+i, outfile.exists());
			Element test = CMLUtil.parseQuietlyToDocument(outfile).getRootElement();
			File reffile = new File("src/test/resources/regression/bmcbioinflin2009-page"+i+".svg");
			Assert.assertTrue("page"+i, reffile.exists());
			Element ref = CMLUtil.parseQuietlyToDocument(reffile).getRootElement();
			JumboTestUtils.assertEqualsIncludingFloat("page"+i, ref, test, true, 0.00001);
		}
	}
	
	@Test
	public void testBMCRegression313() {
		testMultipage("src/test/resources/regression/313.pdf", "target/regression/", "src/test/resources/regression/", "313", 8);
	}

	private void testMultipage(String pdffile, String outdir, String refroot, String paperroot, int npage) {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", outdir, pdffile);
		for (int i = 1; i <= npage; i++) {
			System.out.print("t"+i+"=");
			File outfile = new File(outdir+paperroot+"-page"+i+".svg");
			Assert.assertTrue(outfile.toString(), outfile.exists());
			Element test = CMLUtil.parseQuietlyToDocument(outfile).getRootElement();
			File reffile = new File(refroot+paperroot+"-page"+i+".svg");
			Assert.assertTrue(reffile.toString(), reffile.exists());
			Element ref = CMLUtil.parseQuietlyToDocument(reffile).getRootElement();
			JumboTestUtils.assertEqualsIncludingFloat("page"+i, ref, test, true, 0.00001);
		}
	}
}

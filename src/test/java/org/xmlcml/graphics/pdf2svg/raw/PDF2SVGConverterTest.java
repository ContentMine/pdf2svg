package org.xmlcml.graphics.pdf2svg.raw;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;

public class PDF2SVGConverterTest {

	@Test
	public void testPage6() {
		File page6File = new File("target/ajc/page6-page1.svg"); // yes, this serial number is what is is output as
		if (page6File.exists()) {
			page6File.delete();
		}
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/ajc", "-pages", "1",
				"src/test/resources/page6.pdf");
		// results have been written to target
//		Assert.assertTrue(page6File.exists());
		Assert.assertEquals("Page count", 1, converter.getPageList().size());
		SVGSVG svgPage = converter.getPageList().get(0);
		List<SVGText> texts = SVGText.extractTexts(SVGUtil.getQuerySVGElements(
				svgPage, "//svg:text"));
		int nTexts = texts.size();
		Assert.assertTrue("count: (" + nTexts + ")", nTexts > 4090
				&& nTexts < 4100);
		List<SVGPath> paths = SVGPath.extractPaths(SVGUtil.getQuerySVGElements(
				svgPage, "//svg:path"));
		int nPaths = paths.size();
		Assert.assertTrue("count: (" + nPaths + ")", nPaths > 195
				&& nPaths < 200);
	}

	@Test
	@Ignore
	// do not normally run this
	public void testAJC() {
		for (int pageNum = 1; pageNum <= 131; pageNum++) {
			File pageNFile = new File("target/ajc/xx-page" + pageNum + ".svg");
			if (pageNFile.exists()) {
				pageNFile.delete();
			}
		}

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/ajc", "-pages", "1-131",
				"../pdfs/ajctest/xx.pdf");

		for (int pageNum = 1; pageNum <= 131; pageNum++) {
			File pageNFile = new File("target/ajc/xx-page" + pageNum + ".svg");
			Assert.assertTrue(pageNFile.exists());
		}
	}

	@Test
	@Ignore // do not normally run this
	public void testBMC() {
		for (int pageNum = 1; pageNum <= 14; pageNum++) {
			File pageNFile = new File("../pdfs/312-page" + pageNum + ".svg");
			if (pageNFile.exists()) {
				pageNFile.delete();
			}
		}

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/bmc", "-pages", "1-14",
				"src/test/resources/org/xmlcml/graphics/pdf/312.pdf");

		for (int pageNum = 1; pageNum <= 14; pageNum++) {
			File pageNFile = new File("../pdfs/bmc/312-page" + pageNum + ".svg");
			Assert.assertTrue(pageNFile.exists());
		}
	}

	@Test
	@Ignore
	// do not normally run this
	public void testE() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/ajc", 
				"../pdfs/e/6048.pdf");
	}

	@Test
	@Ignore
	// do not normally run this
	public void testRSC() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/rsc", "../pdfs/rsc/b306241d.pdf");
	}

	@Test
	@Ignore
	// do not normally run this
	public void testRSC1() {
		// this has very bad performance // no idea yet why
		PDF2SVGConverter converter = new PDF2SVGConverter();
//		converter.run("-outdir", "../pdfs/rsc", "-pages", "3", "../pdfs/rsc/problemChars.pdf");
		converter.run("-outdir", "../pdfs/rsc", "../pdfs/rsc/problemChars.pdf");
	}

	@Test
	@Ignore
	// do not normally run this
	public void testPsyc() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/psyc", "../pdfs/psyc/Holcombe2012.pdf");
	}

	@Test
	@Ignore
	// do not normally run this
	public void testPsyc1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/psyc1", "../pdfs/psyc/Liu2005.pdf");
	}

	@Test
	@Ignore
	// do not normally run this
	public void testSocDir() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/socdir", "../pdfs/socdir/1-PB.pdf");
	}

	@Test
	@Ignore
	// do not normally run this
	public void testACS() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/acs", "../pdfs/acs/nl072516n.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testNPG() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/npg", "../pdfs/npg/srep00778.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testWiley() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/wiley", "../pdfs/wiley/1032.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testBMJ() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/bmj", "../pdfs/bmj/e001553.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testElife() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/elife", "src/test/resources/elife/00013.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testJB() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/jb", "../pdfs/jb/100-14.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testPlosOne() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/plosone", "src/test/resources/plosone/0049149.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testPlosOne1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/plosone", "-pages", "2", "src/test/resources/plosone/2009_rip_loop_conformations.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testElsevier2() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/els", "../pdfs/e2/1-s2.0-S2212877812000129-main.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testWord() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/word", "../pdfs/word/test.pdf");
	}

	@Test
	@Ignore // do not normally run this
	public void testWordMath() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/word", "../pdfs/word/testmath.pdf");
	}

	@Test
	@Ignore
	public void testHelp() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run();
	}

}

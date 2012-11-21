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

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.pdf2svg.PDF2SVGConverter;

/** Not really tests.
 * Run over a large number of different PDFs to gather information and
 * a vague hope of cataching regressions
 * Run manually
 * 
 * @author pm286
 *
 */
public class SemiTest {

	public final static Logger LOG = Logger.getLogger(SemiTest.class);

	public static void main(String[] args) {
		new SemiTest().testPaperCollection();
	}
	/* not a JUNIT test
	 * 
	 */
	public void testPaperCollection() {
		testAJC();
		testAJCMany();
		testBMC();
		testE();
		testRSC();
		testRSC1();
		testRSCMany();
		testPsyc();
		testAPA();
		testSocDir();
		testACS();
		testNPG();
		testWiley();
		testBMJ();
		testElife();
		testJB();
		testPlosOne();
		testPlosOne1();
		testElsevier2();
		testWord();
		testWordMath();
		testThesis();
		testThesis1();
		testThesis2();
		testThesis5();
		testThesisMany();
		testArxivMany();
		testECU();
	}

	public void testAJC() {
		for (int pageNum = 1; pageNum <= 131; pageNum++) {
			File pageNFile = new File("target/ajc/xx-page" + pageNum + ".svg");
			if (pageNFile.exists()) {
				pageNFile.delete();
			}
		}

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "target/ajc", "-pages", "1-131",
				"-pub", "ajc", "../pdfs/ajctest/xx.pdf");

		for (int pageNum = 1; pageNum <= 131; pageNum++) {
			File pageNFile = new File("target/ajc/xx-page" + pageNum + ".svg");
			Assert.assertTrue(pageNFile.exists());
		}
	}

	
	
	public void testAJCMany() {
		convertPDFsToSVG("../pdfs/ajc/many", "target/ajc/many");
	}

	static void convertPDFsToSVG(String pdfDirName, String outdir) {
		File pdfDir = new File(pdfDirName);
		File[] files = pdfDir.listFiles();
		if (files != null) {
			for (File file : files){
				if (file.toString().endsWith(".pdf")) {
					PDF2SVGConverter converter = new PDF2SVGConverter();
					converter.run("-outdir", outdir, file.toString());
				}
			}
		}
	}
	
	
	 // do not normally run this
	public void testBMC() {
		for (int pageNum = 1; pageNum <= 14; pageNum++) {
			File pageNFile = new File("../pdfs/312-page" + pageNum + ".svg");
			if (pageNFile.exists()) {
				pageNFile.delete();
			}
		}

		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/bmc", "-pages", "1-14",
				"-pub", "bmc", "src/test/resources/org/xmlcml/graphics/pdf/312.pdf");

		for (int pageNum = 1; pageNum <= 14; pageNum++) {
			File pageNFile = new File("../pdfs/bmc/312-page" + pageNum + ".svg");
			Assert.assertTrue(pageNFile.exists());
		}
	}

	
	
	// do not normally run this
	public void testE() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/e", "-pub", "els", "../pdfs/e/6048.pdf");
	}

	
	
	// do not normally run this
	public void testRSC() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/rsc", "-pub", "rsc", "../pdfs/rsc/b306241d.pdf");
	}

	
	
	// do not normally run this
	public void testRSC1() {
		// this has very bad performance because of colour conversion in bitmap fonts
		PDF2SVGConverter converter = new PDF2SVGConverter();
//		converter.run("-outdir", "../pdfs/rsc", "-pages", "3", "../pdfs/rsc/problemChars.pdf");
		converter.run("-outdir", "../pdfs/rsc", "-pub", "pccp", "../pdfs/rsc/problemChars.pdf");
	}
	
	
	
	public void testRSCMany() {
		convertPDFsToSVG("../pdfs/rsc/many", "target/rsc/many");
	}

	
	
	// do not normally run this
	public void testPsyc() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/psyc", "-pub", "frpsyc","../pdfs/psyc/Holcombe2012.pdf");
	}

	
	
	// do not normally run this
	public void testAPA() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/apa", "-pub", "apa", "../pdfs/apa/Liu2005.pdf");
	}

	
	
	// do not normally run this
	public void testSocDir() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/socdir", "-pub", "socdir", "../pdfs/socdir/1-PB.pdf");
	}

	
	 // not behaving right
	// do not normally run this
	public void testACS() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/acs", "-pub", "acs", "../pdfs/acs/nl072516n.pdf");
	}

	
	 // do not normally run this
	public void testNPG() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/npg", "-pub", "npg", "../pdfs/npg/srep00778.pdf");
	}

	
	 // do not normally run this
	public void testWiley() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/wiley", "-pub", "wiley", "../pdfs/wiley/1032.pdf");
	}

	
	 // do not normally run this
	public void testBMJ() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/bmj", "-pub", "bmj", "../pdfs/bmj/e001553.pdf");
	}

	
	 // do not normally run this
	public void testElife() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/elife", "-pub", "elife", "src/test/resources/elife/00013.pdf");
	}

	
	 // do not normally run this
	public void testJB() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/jb", "-pub", "jb", "../pdfs/jb/100-14.pdf");
	}

	
	 // do not normally run this
	public void testPlosOne() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/plosone", "-pub", "plosone", "src/test/resources/plosone/0049149.pdf");
	}

	
	 // do not normally run this
	public void testPlosOne1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/plosone", "-pages", "2", "src/test/resources/plosone/2009_rip_loop_conformations.pdf");
	}

	
	 // do not normally run this
	public void testElsevier2() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/els", "../pdfs/e2/1-s2.0-S2212877812000129-main.pdf");
	}

	
	 // do not normally run this
	public void testWord() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/word", "-pub", "word", "../pdfs/word/test.pdf");
	}

	
	 // do not normally run this
	public void testWordMath() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/word", "-pub", "word", "../pdfs/word/testmath.pdf");
	}

	
	 // do not normally run this
	public void testThesis() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/darmstadt.pdf");
	}
	
	 // do not normally run this
	public void testThesis1() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/keruzore.pdf");
	}
	
	 // do not normally run this
	public void testThesis2() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/Mawer.pdf");
	}
	
	 // do not normally run this
	public void testThesis5() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("-outdir", "../pdfs/thesis", "../pdfs/thesis/zakrysphd.pdf");
	}
	
	
	public void testThesisMany() {
		convertPDFsToSVG("../pdfs/thesis", "target/thesis");
	}

	
	
	public void testArxivMany() {
		convertPDFsToSVG("../pdfs/arxiv", "target/arxiv");
	}
	
	
	
	public void testECU() {
		convertPDFsToSVG("../../documents/standalone/ecu2012", "target/ecu");
	}

	
	
	public void testPPT() {
		convertPDFsToSVG("../pdfs/ppt", "target/ppt");
	}

	
	
	public void testHelp() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run();
	}

}

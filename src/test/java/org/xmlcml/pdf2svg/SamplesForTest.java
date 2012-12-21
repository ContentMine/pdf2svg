package org.xmlcml.pdf2svg;

import org.junit.Ignore;
import org.junit.Test;

/** typical files that may or may give errors
 * will not be run in normal testing
 * 
 * @author pm286
 *
 */
public class SamplesForTest {
	
	public static void main(String[] args) {
//		// Comment in/out what you want
//		// Word thesis 1 document
//		new PDF2SVGConverter().run("-outdir", "target/word", "src/test/resources//word/harterchap7small.pdf");
//		// encryption 1 article
//		new PDF2SVGConverter().run("-outdir", "target/ajc", "../pdfs/ajc/CH01182.pdf");
//		// AJC corpus 52 sec
//		new PDF2SVGConverter().run( "-logger","-outdir", "target/ajc/sample", "../pdfs/ajc/sample");
//		// ?? 42 secs
//		new PDF2SVGConverter().run("-logger", "-outdir", "target/0all/0", "../pdfs/0all/0");
//		// 100 CSIRO all work
//		new PDF2SVGConverter().run("-logger", "-outdir", "target/csiro/pick100", "../pdfs/csiro/pick100");
//		// IUCR has symbol fonts MT_MI/MT_SY
//		new PDF2SVGConverter().run("-logger", "-outdir", "target/iucr", "../pdfs/iucr");
//		// CSIRO 
//		new PDF2SVGConverter().run("-logger", "-outdir", "target/csiro/test", "../pdfs/csiro/test");
//		// contains brace
//`		// calls draw routine
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/csiro/test0", "../pdfs/csiro/test0");
//	    // BMC evol biol
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc/evolbio/", "../ami2/pdfs/bmcevolbiol/2/");
//		// other BMC
//		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/bmc/", "../pdfs/bmc");
	}

}

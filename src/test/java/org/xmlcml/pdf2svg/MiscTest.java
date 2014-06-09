package org.xmlcml.pdf2svg;

import org.junit.Ignore;
import org.junit.Test;

public class MiscTest {

	@Test
	public void dummy() {
		
	}
	
	@Test
	@Ignore
	public void testHelp() {
		new PDF2SVGConverter().run();
	}
	
// comment out @Ignore to test these
	@Test
	@Ignore
	public void testDingbatsFont() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "../pdfs/peerj/36.pdf"
//				,"-debugFontName", "RNMPIC+Dingbats"
				);
	}
	
	@Test
	@Ignore
	public void testCambriaMathFont() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mdpi", "src/test/resources/mdpi"
//			,"-debugFontName" , "KBEJAP+CambriaMath"
			);
	}
	
	@Test
	@Ignore
	public void testBold() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/mdpi", "src/test/resources/mdpi/materials-05-00027.pdf"
//			,"-debugFontName" , "KBDOLG+TimesNewRoman"
			);
	}
	
}

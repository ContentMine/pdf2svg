package org.xmlcml.pdf2svg;

import org.junit.Test;

public class CharactersInPDFTest {

	@Test
	public void testChars() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "../pdfs/test");
	}

}

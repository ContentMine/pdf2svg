package org.xmlcml.pdf2svg;

import org.junit.Test;

public class CharactersInPDFIT {

	@Test
	public void testChars() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "src/test/resources/org/xmlcml/pdf2svg/misc/");
	}

}

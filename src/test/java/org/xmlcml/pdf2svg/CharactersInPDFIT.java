package org.xmlcml.pdf2svg;

import org.junit.Ignore;
import org.junit.Test;

public class CharactersInPDFIT {

	@Test
	@Ignore
	/** this has some unusual fonts and probable fails.
	 * 
	 * e.g. 
12856 [main] DEBUG org.apache.fontbox.util.FontManager  - Unsupported font format for external font: /Library/Fonts/STIXSizTwoSymBol.otf
12856 [main] DEBUG org.apache.fontbox.util.FontManager  - Unsupported font format for external font: /Library/Fonts/STIXSizTwoSymReg.otf
	 * will 
	 */
	public void testChars() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/test", "src/test/resources/org/xmlcml/pdf2svg/misc/");
	}

}

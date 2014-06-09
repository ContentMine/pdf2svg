package org.xmlcml.pdf2svg;

import org.junit.Ignore;
import org.junit.Test;

public class LogfileTest {

	@Test
	@Ignore
	public void testAJC() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
//		converter.run();
		converter.run("" +
				"-outdir target/multiple " +
				"-logger " +
				"src/test/resources/multiple " +
				""
				);
	}
	
	@Test
	@Ignore
	public void testAJCSample() {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.run("" +
				"-outdir target/multiple/ajc/sample " +
				"-logger " +
				"../pdfs/ajc/sample " +
				""
				);
	}
}

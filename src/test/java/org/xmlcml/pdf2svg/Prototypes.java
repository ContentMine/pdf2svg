package org.xmlcml.pdf2svg;

import org.junit.Test;


public class Prototypes {

	public static void main(String[] args) {
//		phytochem1();
//		carnosic();
		funnel();
	}

	private static void phytochem1() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/phytochem", "src/test/resources/elsevier/S1874390014000469.pdf"
		);
	
	}
	private static void carnosic() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/phytochem", "src/test/resources/elsevier/carnosic.pdf"
		);
	
	}
	private static void funnel() {
		new PDF2SVGConverter().run("-logger", "-infofiles", "-logglyphs", "-outdir", "target/funnel", "demos/sage/Sbarra-454-74.pdf"
		);
	
	}
}

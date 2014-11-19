package org.xmlcml.pdf2svg.demos;

import org.junit.Test;
import org.xmlcml.pdf2svg.PDF2SVGConverter;

public class Demos {

	public static void main(String[] args) {
			ebola1();

	}

	private static void ebola1() {
		new PDF2SVGConverter().run(
				"-logger", 
				"-infofiles", 
				"-logglyphs", 
				"-outdir", "target/ebola", 
				"demos/ebola/roadmapsitrep_14Nov2014_eng.pdf"
		);
	}
}

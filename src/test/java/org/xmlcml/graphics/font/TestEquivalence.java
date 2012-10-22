package org.xmlcml.graphics.font;

import java.io.InputStream;
import java.io.StringReader;

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestEquivalence {

	private static final String EQUIVALENCE1 = FontManager.packageBase+"/"+"outlineFonts/font1/equivalences1.xml";
	private static final String FONT1ITALIC = FontManager.packageBase+"/"+"outlineFonts/font1/italic.xml";
	private FontManager fontManager;
	private String equivalenceS;
	private Equivalence equiv;

//	@Before
//	@Ignore
//	public void setUp() {
//		fontManager = new FontManager();
//		equivalenceS = "" +
//				"<equivalence id='italic1'>" +
//				"  <text fontName='font1-italic'>Ra</text>" +
//				"  <pathList>" +
//				"    <path d='M327.397 218.898 L328.024 215.899 L329.074 215.899 C329.433 215.899 329.693 215.936 329.854 216.008 C330.012 216.08 330.168 216.232 330.322 216.461 C330.546 216.793 330.745 217.186 330.92 217.64 L331.404 218.898 L332.413 218.898 L331.898 217.626 C331.725 217.202 331.497 216.793 331.215 216.397 C331.089 216.222 330.903 216.044 330.657 215.863 C331.46 215.755 332.039 215.52 332.4 215.158 C332.759 214.796 332.938 214.341 332.938 213.791 C332.938 213.397 332.856 213.072 332.692 212.814 C332.527 212.557 332.301 212.381 332.013 212.287 C331.724 212.192 331.3 212.146 330.741 212.146 L327.908 212.146 L326.494 218.898 L327.397 218.898 ZM328.654 212.888 L330.857 212.888 C331.203 212.888 331.448 212.914 331.593 212.967 C331.737 213.018 331.855 213.117 331.942 213.264 C332.032 213.41 332.075 213.58 332.075 213.776 C332.075 214.011 332.017 214.228 331.898 214.431 C331.779 214.634 331.609 214.794 331.39 214.914 C331.171 215.034 330.893 215.111 330.552 215.145 C330.376 215.16 330 215.168 329.424 215.168 L328.176 215.168 L328.654 212.888' style='clip-path:url(#clipPath1); stroke:none;'/>" +
//				"    <path d='M336.571 218.898 L337.414 218.898 C337.347 218.609 337.313 218.389 337.313 218.235 C337.313 217.993 337.356 217.678 337.443 217.291 L337.714 216.075 C337.79 215.731 337.827 215.429 337.827 215.168 C337.827 214.824 337.694 214.544 337.423 214.329 C337.07 214.04 336.591 213.896 335.987 213.896 C335.419 213.896 334.948 214.025 334.573 214.283 C334.202 214.541 333.93 214.913 333.758 215.398 L334.601 215.471 C334.702 215.186 334.863 214.965 335.086 214.811 C335.308 214.656 335.595 214.578 335.945 214.578 C336.317 214.578 336.599 214.654 336.791 214.808 C336.933 214.919 337.003 215.07 337.003 215.264 C337.003 215.415 336.972 215.603 336.907 215.83 C336.682 215.92 336.305 215.975 335.781 215.996 C335.259 216.019 334.904 216.048 334.715 216.084 C334.417 216.14 334.165 216.231 333.959 216.358 C333.754 216.485 333.589 216.654 333.465 216.865 C333.341 217.075 333.279 217.317 333.279 217.59 C333.279 218.005 333.417 218.345 333.693 218.61 C333.969 218.875 334.332 219.008 334.783 219.008 C335.089 219.008 335.373 218.951 335.643 218.835 C335.908 218.721 336.185 218.538 336.475 218.29 C336.493 218.536 336.526 218.738 336.571 218.898 M336.439 217.535 C336.29 217.795 336.09 217.999 335.836 218.145 C335.579 218.291 335.314 218.364 335.038 218.364 C334.74 218.364 334.511 218.289 334.35 218.138 C334.189 217.988 334.109 217.795 334.109 217.562 C334.109 217.387 334.157 217.227 334.259 217.083 C334.36 216.939 334.512 216.835 334.71 216.768 C334.91 216.702 335.254 216.647 335.737 216.604 C336.051 216.577 336.275 216.551 336.407 216.526 C336.541 216.501 336.667 216.467 336.784 216.42 C336.701 216.902 336.585 217.274 336.439 217.535' style='clip-path:url(#clipPath1); stroke:none;'/> "+
//				"  </pathList>" +
//				"</equivalence>";
//		try {
//			Element elem = new Builder().build(new StringReader(equivalenceS)).getRootElement();
//			equiv = Equivalence.createEquivalence(elem);
//		} catch (Exception e) {
//			throw new RuntimeException("bad test equivalence", e);
//		}
//
//	}
	
	@Test
	public void dummy() {
		
	}
	
	@Test
	@Ignore
	public void testProcessEquivalence1() throws Exception {
		InputStream inputStream = TestOutlineFont.class.getClassLoader().getResourceAsStream(EQUIVALENCE1);
		Element equivalence1 = new Builder().build(inputStream).getRootElement().getChildElements().get(0);
		Equivalence equiv = Equivalence.createEquivalence(equivalence1);
		OutlineFont font1Italic = OutlineFont.readAndCreateFont(FONT1ITALIC);
		font1Italic.processEquivalence(equiv);
//		font1Italic.debug("altered font");
	}
}

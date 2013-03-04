package org.xmlcml.pdf2svg.util;

import org.junit.Assert;
import org.junit.Test;

public class PDF2SVGUtilTest {

	@Test
	public void testNormal() {
		String s = "a/b/c";
		String s1 = PDF2SVGUtil.normalizeResource(s);
		Assert.assertEquals(s, s, s1);
	}
	
	@Test
	public void testSlash() {
		String s = "a/b/c/";
		String s1 = PDF2SVGUtil.normalizeResource(s);
		Assert.assertEquals(s, s, s1);
	}
	
	@Test
	public void testDot() {
		String s = "a/./c";
		String s1 = PDF2SVGUtil.normalizeResource(s);
		Assert.assertEquals(s, "a/c", s1);
	}
	
	
	@Test
	public void testDotDot() {
		String s = "a/../c";
		String s1 = PDF2SVGUtil.normalizeResource(s);
		Assert.assertEquals(s, "c", s1);
	}
	
	@Test
	public void testDotDotDotDot() {
		String s = "a/b/../../c/d";
		String s1 = PDF2SVGUtil.normalizeResource(s);
		Assert.assertEquals(s, "c/d", s1);
	}
	
	@Test
	public void testDotDotDotDotSlash() {
		String s = "a/b/../../c/d/";
		String s1 = PDF2SVGUtil.normalizeResource(s);
		Assert.assertEquals(s, "c/d/", s1);
	}
	
	@Test
	public void testDotDotAtStart() {
		String s = "../c/d";
		try {
			String s1 = PDF2SVGUtil.normalizeResource(s);
			Assert.fail("should throw RuntimeException");
		} catch (RuntimeException e) {
			
		}
	}
}

package org.xmlcml.pdf2svg;

import org.xmlcml.cml.base.CMLConstants;

public class UnicodePoint {

	/** this displays a filled glyph so that it stands out as an error */
	private static final String POINT_REPRESENTING_UNKNOWN = "U+274E"; // negative square cross 
	
	public static final String UNICODE_PREFIX = "U+";
	public static final String HEX_PREFIX = "0X";
	public static final UnicodePoint UNKNOWN = UnicodePoint.createUnicodeValue(POINT_REPRESENTING_UNKNOWN);

	private Integer decimalValue;
	private String unicodeValue;  // e.g. U+1234
	private String unicodeName; // e.g. "LEFT PAREN TOP", normalized to Uppercase 
	private UnicodePoint[] replacementPoints; // a sequence of one of more concatenated code points that 
	                                      //could be used for replacement

	private UnicodePoint() {
	}
	
	/** two UnicodePoints are equal if the have the same unicodeValue
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof UnicodePoint)) {
			return false;
		}
		return ((UnicodePoint)obj).unicodeValue.equals(this.unicodeValue);
	}

	@Override
	public int hashCode() {
		return unicodeValue.hashCode();
	}
	
	/** create from "U+1234"
	 * normalizes to uppercase.
	 * @param uString
	 * @return null if arg is null
	 */
	public static UnicodePoint createUnicodeValue(String uString) {
		UnicodePoint unicodePoint = null;
		if (uString != null) {
			uString = uString.toUpperCase();
			Integer decimalValue = translateToDecimal(uString);
			if (decimalValue == null) {
				throw new RuntimeException("Bad Unicode value: "+uString);
			}
			unicodePoint = new UnicodePoint();
			unicodePoint.decimalValue = decimalValue;
			unicodePoint.unicodeValue = uString;
			
		}
		return unicodePoint;
	}
	
	public static Integer translateToDecimal(String unicodeValue) {
		Integer codepoint = null;
		if (unicodeValue != null && unicodeValue.startsWith(UNICODE_PREFIX)) {
			String hex = HEX_PREFIX+unicodeValue.substring(UNICODE_PREFIX.length());
			try {
				codepoint = Integer.decode(hex);
			} catch (Exception e) {
				throw new RuntimeException("Bad hex: "+hex);
			}
		}
		return codepoint;
	}

	/** split a concatenated list of unicode points
	 * normalizes whitespace and case
	 * @param replace
	 * @return
	 */
	public static UnicodePoint[] getUnicodeValues(String stringToParse) {
		String replace = stringToParse;
		replace = replace.replaceAll("U", " U");
		replace = replace.replaceAll(CMLConstants.S_WHITEREGEX, " ");
		replace = replace.trim();
		String[] replaceStrings = replace.split(" ");
		int nStrings = replaceStrings.length;
		UnicodePoint[] points = new UnicodePoint[nStrings];
		for (int i = 0; i < nStrings; i++) {
			points[i] = UnicodePoint.createUnicodeValue(replaceStrings[i]);
			if (points[i] == null) {
				throw new RuntimeException("Cannot create Unicode point: "+points[i]+" in: "+ stringToParse);
			}
		}
		return points;
	}

	public void addReplacmentPoints(String replace) {
		if (replace != null) {
			replacementPoints = UnicodePoint.getUnicodeValues(replace);
		}
	}
	
	public Integer getDecimalValue() {
		return decimalValue;
	}

	public String getUnicodeValue() {
		return unicodeValue;
	}

	public String getUnicodeName() {
		return unicodeName;
	}

	public UnicodePoint[] getReplacmentPoints() {
		return replacementPoints;
	}

	public void setUnicodeName(String name) {
		unicodeName = name;
	}

	/** string of form "U+1234" or "U+1234 U+2345... "
	 * @return
	 */
	public String getReplacementPointString() {
		String replacementString = null;
		if (replacementPoints != null) {
			for (int i = 0; i < replacementPoints.length; i++) {
				if (i > 0) {
					replacementString += " ";
				}
				replacementString += replacementPoints[i];
			}
		}
		return replacementString;
	}
	
	public String toString(){
		String s ="";
		s += " unicode: "+unicodeValue+";";
		s += " decimal: "+decimalValue+";";
		s += " unicodeName: "+unicodeName+";";
		String r = getReplacementPointString();
		if (r != null) {
			s += "replacement: "+r+";";
		}
		return s;
	}

}

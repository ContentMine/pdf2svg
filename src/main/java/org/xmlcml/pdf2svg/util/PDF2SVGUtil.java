/**
 * Copyright (C) 2012 pm286 <peter.murray.rust@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.pdf2svg.util;

import nu.xom.Attribute;

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.graphics.svg.SVGElement;

public class PDF2SVGUtil {

	public final static String SVGX_NS = "http://www.xml-cml.org/schema/svgx";
	public final static String SVGX_PREFIX = "svgx";
	public static final String CHARACTER_WIDTH = "width";
	public static final String CHARACTER_CODE = "charCode";
	public static final String CHARACTER_NAME = "charName";
	public static final String CHARACTER_NEW_CODE = "newCode";
	public static final String FONT_ENCODING = "fontEnc";
	public static final String LIGATURE = "ligature";
	
	public static void setSVGXAttribute(SVGElement svgElement, String attName, String value) {
		Attribute attribute = new Attribute(SVGX_PREFIX+CMLConstants.S_COLON+attName, SVGX_NS, value);
		svgElement.addAttribute(attribute);
	}

	public static String getSVGXAttribute(SVGElement svgElement, String attName) {
		Attribute attribute = svgElement.getAttribute(attName, SVGX_NS);
		return (attribute == null) ? null : attribute.getValue();
	}

}

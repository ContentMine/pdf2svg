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
package org.xmlcml.graphics.pdf2svg.raw;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

public class Publisher {




	private final static Logger LOG = Logger.getLogger(Publisher.class);
	
	// XML
	public static final String FONT_FAMILY_NAME = "fontFamilyName";
	public static final String PUBLISHER = "publisher";
	public static final String ABBREVIATION = "abbreviation";
	public static final String NAME = "name";
	public static final String NOTE = "note";
	
	
	private String abbreviation;
	private String name;
	private String note;

	private Set<String> fontFamilyNameSet;
	
	public Publisher() {
		
	}

	public static Publisher createFromElement(Element publisherElement) {
		Publisher publisher = null;
		try {
			publisher = new Publisher();
			if (!(PUBLISHER.equals(publisherElement.getLocalName()))) {
				throw new RuntimeException(PublisherSet.PUBLISHER_SET+" children must be "+PUBLISHER);
			}
			publisher.abbreviation = publisherElement.getAttributeValue(ABBREVIATION);
			if (publisher.abbreviation == null) {
				throw new RuntimeException("<"+PUBLISHER+"> must have "+ABBREVIATION+" attribute");
			}
			publisher.name = publisherElement.getAttributeValue(NAME);
			publisher.note = publisherElement.getAttributeValue(NOTE);
			Elements fontFamilyNameElements = publisherElement.getChildElements(FONT_FAMILY_NAME);
			for (int i = 0; i < fontFamilyNameElements.size(); i++) {
				publisher.addFontFamilyName(fontFamilyNameElements.get(i).getValue());
			}
		} catch (Exception e) {
			throw new RuntimeException("invalid publisherElement: "+((publisherElement == null) ? null : publisherElement.toXML()), e);
		}
		return publisher;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Element createElement() {
		Element publisherElement = new Element(PUBLISHER);
		if (abbreviation == null) {
			throw new RuntimeException("Abbreviation must not be null");
		} else {
			publisherElement.addAttribute(new Attribute(ABBREVIATION, abbreviation));
		}
		if (name != null) {
			publisherElement.addAttribute(new Attribute(NAME, name));
		}
		if (note != null) {
			publisherElement.addAttribute(new Attribute(NOTE, note));
		}
		ensureFontFamilyNameSet();
		String[] names = fontFamilyNameSet.toArray(new String[0]);
		if (names != null) {
			Arrays.sort(names);
			for (String fontFamilyName : names) {
				Element fontFamilyNameElement = new Element(FONT_FAMILY_NAME);
				fontFamilyNameElement.appendChild(fontFamilyName);
				publisherElement.appendChild(fontFamilyNameElement);
			}
		}
		return publisherElement;
	}

	public boolean containsFontFamilyName(String fontFamilyName) {
		ensureFontFamilyNameSet();
		return fontFamilyNameSet.contains(fontFamilyName);
	}

	private void ensureFontFamilyNameSet() {
		if (fontFamilyNameSet == null) {
			this.fontFamilyNameSet = new HashSet<String>();
		}
	}

	public void addFontFamilyName(String fontFamilyName) {
		ensureFontFamilyNameSet();
		if (fontFamilyName != null) {
			this.fontFamilyNameSet.add(fontFamilyName);
		}
	}

}

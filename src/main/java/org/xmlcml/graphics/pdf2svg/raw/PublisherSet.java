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
import java.util.HashMap;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.pdfbox.util.TextPosition;
import org.xmlcml.euclid.Util;

/** local implementation of publisherSet
 * 
    <publisher decimal="9702" unicode="U+25E6" charName="WHITE BULLET" note="probably for lists and graph symbols" 
        confusions="ring operator U+2218 inverse bullet U+25D8"/>
    <publisher decimal="12296" unicode="U+3008" charName="LEFT ANGLE BRACKET" 
        note="quasi-synonym" replaceByUnicode="U+003C" replaceName="LESS-THAN SIGN"/>
        
 * @author pm286
 *
 */
public class PublisherSet {

	// XML
	public static final String PUBLISHER_SET = "publisherSet";
	
	// resources
	public static final String PUBLISHER_SET_XML = "org/xmlcml/graphics/pdf2svg/raw/publisherSet.xml";
	
	// publishers
	public  static final String BMC_ABB = "bmc";
	public static final String BMC_NAME = "BioMedCentral";
	
	private Map<String, Publisher> publisherByNameMap;
	private Map<String, Publisher> publisherByAbbreviationMap;

	public PublisherSet() {
		ensureMaps();
	}

	private void ensureMaps() {
		if (publisherByNameMap == null) {
			publisherByNameMap = new HashMap<String, Publisher>();
			publisherByAbbreviationMap = new HashMap<String, Publisher>();
		}
	}

	public static PublisherSet readPublisherSet(String publisherSetXmlResource) {
		PublisherSet publisherSet = new PublisherSet();
		try {
			Element publisherSetElement = new Builder().build(
					Util.getResourceUsingContextClassLoader(publisherSetXmlResource, PublisherSet.class)).getRootElement();
			publisherSet = createSetAndMapsFromElement(publisherSetElement);

		} catch (Exception e) {
			throw new RuntimeException("Cannot read publisherSet: "+publisherSetXmlResource, e);
		}
		return publisherSet;
	}

	public static PublisherSet createSetAndMapsFromElement(Element publisherSetElement) {
		PublisherSet publisherSet = new PublisherSet();
		if (!(PUBLISHER_SET.equals(publisherSetElement.getLocalName()))) {
			throw new RuntimeException("publisherSet must have rootElement: "+PUBLISHER_SET);
		}
		Elements childElements = publisherSetElement.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			Element publisherElement = childElements.get(i);
			Publisher publisher = Publisher.createFromElement(publisherElement);
			String abbreviation = publisher.getAbbreviation();
			if (publisherSet.containsAbbreviationKey(abbreviation)) {
				throw new RuntimeException("Duplicate abbreviation: "+abbreviation);
			}
			publisherSet.addIfNovel(publisher);
		}
		return publisherSet;
	}

	/** adds if no duplicates
	 * 
	 * @param publisher
	 */
	private void addIfNovel(Publisher publisher) {
		String abbreviation = publisher.getAbbreviation();
		String name = publisher.getName();
		if (abbreviation == null) {
			throw new RuntimeException("publisher must have "+Publisher.ABBREVIATION);
		}
		if (!containsAbbreviationKey(abbreviation)) {
			if (name == null || !publisherByNameMap.containsKey(name)) {
				publisherByAbbreviationMap.put(abbreviation, publisher);
				if (name != null) {
					publisherByNameMap.put(name, publisher);
				}
			}
		}
	}

	private boolean containsAbbreviationKey(String abbrev) {
		return publisherByAbbreviationMap.containsKey(abbrev);
	}
	
	public int size() {
		return publisherByAbbreviationMap.size();
	}

	public Publisher getPublisherByAbbreviation(String abbreviation) {
		return (abbreviation == null) ? null : publisherByAbbreviationMap.get(abbreviation);
	}

	public Publisher getPublisherByName(String name) {
		return (name == null) ? null : publisherByNameMap.get(name);
	}
}

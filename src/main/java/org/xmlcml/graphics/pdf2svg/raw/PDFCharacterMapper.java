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

import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;

import org.javatuples.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class PDFCharacterMapper {

	private static final String RESOURCE_SUFFIX = "-charmap.xml";
	private static final String XPATH_ABSPATH_FONTNAME = "/fontmapping/fontname";
	private static final String XPATH_ABSPATH_CHARMAP = "/fontmapping/charmap";
	private static final String XPATH_RELPATH_CHARNAME = "charname";
	private static final String XPATH_RELPATH_UNICODE_NAME = "unicode/name";
	private static final String XPATH_RELPATH_UNICODE_CODE = "unicode/code";

	private Table<String, String, Pair<String, String>> charmapTable = HashBasedTable
			.create();

	public PDFCharacterMapper(String fontname) throws Exception {

		String resourceName = fontname + RESOURCE_SUFFIX;

		InputStream inputStream = getClass().getResourceAsStream(resourceName);
		if (inputStream == null)
			throw new Exception(String.format("Resource not found: %s",
					resourceName));

		Builder parser = new Builder();
		Document doc;
		try {
			doc = parser.build(inputStream);
		} catch (ParsingException e) {
			throw new Exception(String.format(
					"ParsingException while parsing resource '%s': %s",
					resourceName, e.getLocalizedMessage()));
		}

		Nodes fontnameNodes = doc.query(XPATH_ABSPATH_FONTNAME);
		if (fontnameNodes.size() != 1)
			throw new Exception(String.format(
					"Xpath '%s' matched incorrectly (%s)",
					XPATH_ABSPATH_FONTNAME, fontnameNodes.toString()));

		String mapFontname = fontnameNodes.get(0).getValue();
		if (!fontname.equalsIgnoreCase(mapFontname))
			throw new Exception(String.format(
					"Fontname in map file did not match (%s!=%s)",
					fontnameNodes.toString()));

		Nodes charmapNodes = doc.query(XPATH_ABSPATH_CHARMAP);
		int nCharmapNodes = charmapNodes.size();
		for (int i = 0; i < nCharmapNodes; i++) {
			Node charmapNode = charmapNodes.get(i);

			Nodes charnameNodes = charmapNode.query(XPATH_RELPATH_CHARNAME);
			if (charnameNodes.size() != 1)
				throw new Exception(String.format(
						"Xpath 'charname' matched incorrectly (%s)",
						fontnameNodes.toString()));
			String charname = charnameNodes.get(0).getValue();

			Nodes unicodeNameNodes = charmapNode
					.query(XPATH_RELPATH_UNICODE_NAME);
			if (unicodeNameNodes.size() != 1)
				throw new Exception(String.format(
						"Xpath 'unicode/name' matched incorrectly (%s)",
						unicodeNameNodes.toString()));
			String unicodeName = unicodeNameNodes.get(0).getValue();

			Nodes unicodeCodeNodes = charmapNode
					.query(XPATH_RELPATH_UNICODE_CODE);
			if (unicodeCodeNodes.size() != 1)
				throw new Exception(String.format(
						"Xpath 'unicode/code' matched incorrectly (%s)",
						unicodeCodeNodes.toString()));
			String unicodeCode = unicodeCodeNodes.get(0).getValue();

			Pair<String, String> unicodePair = new Pair<String, String>(
					unicodeName, unicodeCode);

			charmapTable.put(fontname, charname, unicodePair);
		}
	}
}

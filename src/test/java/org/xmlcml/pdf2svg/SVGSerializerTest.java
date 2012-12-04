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
package org.xmlcml.pdf2svg;

import java.io.FileOutputStream;

import nu.xom.Document;
import nu.xom.Element;

import org.junit.Test;
import org.xmlcml.pdf2svg.SVGSerializer;

public class SVGSerializerTest {

	@Test
	public void testSerializer() {
		Element element = new Element("myString");
		Document doc = new Document(element);
		String content = "char 945 is "+(char)945+" i.e. alpha ";
		System.out.println(content);
		element.appendChild(content);
		
		try {
			FileOutputStream os = new FileOutputStream("target/test.svg");
			SVGSerializer serializer = new SVGSerializer(os);
			serializer.write(doc);
		} catch (Exception e) {
			throw new RuntimeException("cannot serialize ", e);
		}
	}
}

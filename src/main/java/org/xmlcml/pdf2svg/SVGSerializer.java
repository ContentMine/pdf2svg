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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import nu.xom.Serializer;
import nu.xom.Text;

import org.apache.log4j.Logger;

public class SVGSerializer extends Serializer {
	private final static Logger LOG = Logger.getLogger(SVGSerializer.class);
	public SVGSerializer(OutputStream os) {
		super(os);
	}

	public SVGSerializer(OutputStream os, String encoding) throws UnsupportedEncodingException {
		super(os, encoding);
	}


	@Override
	/**
	 * replaces occurrences of (char)12345 by &#12345; in outputStream
	 */
	public void write(Text text) throws IOException {
		String s = text.getValue();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			int ch = s.charAt(i);
			if (ch > 255) {
				sb.append("&#");
				sb.append(ch);
				sb.append(";");
// escape the main XML characters				
			} else if (ch =='&') {
				sb.append("&amp;");
			} else if (ch =='<') {
				sb.append("&lt;");
			} else if (ch =='>') {
				sb.append("&gt;");
			} else if (ch =='\'') {
				sb.append("&apos;");
			} else if (ch =='"') {
				sb.append("&quot;");
			} else {
				sb.append((char)ch);
			}
		}
		writeRaw(sb.toString());
	}

}

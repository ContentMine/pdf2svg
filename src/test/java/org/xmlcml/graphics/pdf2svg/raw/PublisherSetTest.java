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

import nu.xom.Builder;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.euclid.Util;

public class PublisherSetTest {

	@Test
	public void testCreateFromElement() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						PublisherSet.PUBLISHER_SET_XML, this.getClass())).getRootElement();
		PublisherSet publisherSet = PublisherSet.createSetAndMapsFromElement(fontFamilyElementSet); 
		Assert.assertNotNull(publisherSet);
	}
	
	@Test
	public void testChildren() throws Exception {
		Element fontFamilyElementSet = new Builder().build(
				Util.getResourceUsingContextClassLoader(
						PublisherSet.PUBLISHER_SET_XML, this.getClass())).getRootElement();
		PublisherSet publisherSet = PublisherSet.createSetAndMapsFromElement(fontFamilyElementSet); 
		Publisher bmc = publisherSet.getPublisherByAbbreviation(PublisherSet.BMC_ABB);
		Assert.assertNotNull(bmc);
		Publisher bmc1 = publisherSet.getPublisherByName(PublisherSet.BMC_NAME);
		Assert.assertNotNull(bmc1);
	}
}

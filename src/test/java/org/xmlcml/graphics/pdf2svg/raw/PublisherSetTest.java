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

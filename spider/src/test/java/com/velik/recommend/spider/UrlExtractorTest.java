package com.velik.recommend.spider;

import java.net.URL;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class UrlExtractorTest {

	@Test
	public void testOne() {
		Set<HashableUrl> result = new UrlExtractor().extract("<a href=\"http://www.hej.com\">");

		Assert.assertEquals(1, result.size());
		Assert.assertEquals("http://www.hej.com", result.iterator().next().toString());
	}

	@Test
	public void testTwo() throws Exception {
		Set<HashableUrl> result = new UrlExtractor()
				.extract("<a href=\"http://www.hej.com\">duh</a> <img src=\"http://www.a.com/hej\">");

		Assert.assertTrue(result.contains(new HashableUrl(new URL("http://www.hej.com"))));
		Assert.assertTrue(result.contains(new HashableUrl(new URL("http://www.a.com/hej"))));
	}
}

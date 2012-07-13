package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class UrlsToVisitTest {

	public class HashShortener implements UrlShortener {

		@Override
		public URL shorten(URL url) {
			try {
				return new URL(url.getProtocol(), url.getHost(), Integer.toString(url.getPath().hashCode()));
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		}

	}

	@Test
	public void test() throws Exception {
		UrlsToVisit toVisit = new UrlsToVisit();

		Assert.assertTrue(toVisit.add(new URL("http://www.a.com/hello")));
		Assert.assertTrue(toVisit.contains(new URL("http://www.a.com/hello")));
		Assert.assertFalse(toVisit.contains(new URL("http://www.a.com/holle")));

		Assert.assertEquals(new URL("http://www.a.com/hello"), toVisit.next("www.a.com"));
	}
}

package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class VisitedUrlsTest {

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
		VisitedUrls visited = new VisitedUrls(new HashShortener());

		Assert.assertTrue(visited.add(new URL("http://www.a.com/hello")));
		Assert.assertTrue(visited.contains(new URL("http://www.a.com/hello")));
		Assert.assertFalse(visited.contains(new URL("http://www.a.com/holle")));

	}
}

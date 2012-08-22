package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class N24UrlShortenerTest {

	@Test
	public void test() throws Exception {
		assertShortensTo("/a/auto_november_2011_3.html",
				"/archiv/archiv_auto/auto_2011/auto_november_2011/auto_november_2011_3.html");
		assertShortensTo("/6567855", "/news/newsitem_6567855.html");
	}

	private void assertShortensTo(String to, String from) throws MalformedURLException {
		String host = "http://www.spiegel.de";
		Assert.assertEquals(host + to, new N24UrlShortener().shorten(new URL(host + from)).toString());
	}
}

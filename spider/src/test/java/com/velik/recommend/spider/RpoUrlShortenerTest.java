package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class RpoUrlShortenerTest {

	@Test
	public void test() throws Exception {
		assertShortensTo("/1.2938183",
				"/sport/olympia-sommer/deutschland/silber-happy-end-nach-schwarzkopf-drama-1.2938183");
	}

	private void assertShortensTo(String to, String from) throws MalformedURLException {
		String host = "http://www.rp-online.de";
		Assert.assertEquals(host + to, new RpoUrlShortener().shorten(new URL(host + from)).toString());
	}
}

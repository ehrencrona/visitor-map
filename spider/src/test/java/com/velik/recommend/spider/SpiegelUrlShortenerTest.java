package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class SpiegelUrlShortenerTest {

	@Test
	public void test() throws Exception {
		assertShortensTo("/thema/a", "/thema/a");
		assertShortensTo("/659899",
				"/wirtschaft/unternehmen/drittes-quartal-freddie-mac-schreibt-erneut-milliardenverlust-a-659899.html");
	}

	private void assertShortensTo(String to, String from) throws MalformedURLException {
		String host = "http://www.spiegel.de";
		Assert.assertEquals(host + to, new SpiegelUrlShortener().shorten(new URL(host + from)).toString());
	}
}

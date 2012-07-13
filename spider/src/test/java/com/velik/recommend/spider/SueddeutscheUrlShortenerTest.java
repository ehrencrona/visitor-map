package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class SueddeutscheUrlShortenerTest {

	@Test
	public void test() throws Exception {
		assertShortensTo("/thema/a", "/thema/a");
		assertShortensTo("/thema/a?b=c", "/thema/a?b=c");
		assertShortensTo("/1.1348831",
				"/politik/oberbuergermeisterwahl-stuttgart-gegner-tritt-als-kandidat-an-1.1348831");
		assertShortensTo("/1.1348831?a=b",
				"/politik/oberbuergermeisterwahl-stuttgart-gegner-tritt-als-kandidat-an-1.1348831?a=b");
		assertShortensTo("/1.1348831-2",
				"/politik/oberbuergermeisterwahl-stuttgart-gegner-tritt-als-kandidat-an-1.1348831-2");
	}

	private void assertShortensTo(String to, String from) throws MalformedURLException {
		String host = "http://www.sueddeutsche.de";
		Assert.assertEquals(host + to, new SueddeutscheUrlShortener().shorten(new URL(host + from)).toString());
	}
}

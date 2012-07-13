package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class FazUrlShortenerTest {

	@Test
	public void test() throws Exception {
		assertShortensTo("/thema/a", "/thema/a");
		assertShortensTo("/thema/a?b=c", "/thema/a?b=c");
		assertShortensTo("/13488311", "/politik/oberbuergermeisterwahl-13488311.html");
		assertShortensTo("/844163",
				"/rating-agentur-moody-s-stuft-kreditwuerdigkeit-von-italien-herab-a-844163.html");
	}

	private void assertShortensTo(String to, String from) throws MalformedURLException {
		String host = "http://www.faz.net";
		Assert.assertEquals(host + to, new FazUrlShortener().shorten(new URL(host + from)).toString());
	}
}

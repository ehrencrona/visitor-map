package com.velik.recommend.corpus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.velik.recommend.parser.ZeitArticleParser;

public class ZeitArticleParserTest {

	private ZeitArticleParser parser;

	@Before
	public void setUp() throws Exception {
		InputStream resource = getClass().getResourceAsStream("/" + getClass().getSimpleName() + ".html");

		BufferedReader reader = new BufferedReader(new InputStreamReader(resource, "UTF-8"));

		parser = new ZeitArticleParser(reader.readLine(), new URL("http://www.a.com"));
	}

	@Test
	public void testBody() {
		String body = parser.getArticle().getBody();
		Assert.assertTrue(body.startsWith("Gewollt sind sie nicht"));
		Assert.assertTrue(body.trim().endsWith(" eingefordert werden."));
	}

	@Test
	public void testTitle() {
		Assert.assertEquals("Auch Flüchtlinge haben Anspruch auf Menschenwürde", parser.getArticle().getTitle());
	}
}

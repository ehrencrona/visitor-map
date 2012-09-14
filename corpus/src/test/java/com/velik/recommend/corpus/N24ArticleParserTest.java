package com.velik.recommend.corpus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.velik.recommend.parser.N24ArticleParser;

public class N24ArticleParserTest {
	private N24ArticleParser parser;

	@Before
	public void setUp() throws Exception {
		URL url = new URL("http://www.google.com");
		InputStream resource = getClass().getResourceAsStream("/" + getClass().getSimpleName() + ".html");

		BufferedReader reader = new BufferedReader(new InputStreamReader(resource, "UTF-8"));

		parser = new N24ArticleParser(reader.readLine(), url);
	}

	@Test
	public void testBody() {
		String body = parser.getArticle().getBody();

		Assert.assertTrue(body.startsWith("Rio de Janeiro (dpa)"));
		Assert.assertTrue(body.endsWith("(DPA)\n"));
	}

	@Test
	public void testTitle() {
		Assert.assertEquals("Rio als bestes Reiseziel für Homosexuelle gekürt", parser.getArticle().getTitle());
	}

}

package com.velik.recommend.corpus;

import static com.velik.recommend.corpus.ArticleType.GALLERY;
import static com.velik.recommend.corpus.ArticleType.STANDARD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.velik.recommend.parser.RpArticleParser;

public class RpArticleParserTest {

	private static RpArticleParser articleParser;
	private static RpArticleParser galleryParser;

	@BeforeClass
	public static void setUp() throws Exception {
		articleParser = new RpArticleParser(readHtml("article"), new URL("http://www.a.com"));
		galleryParser = new RpArticleParser(readHtml("gallery"), new URL("http://www.a.com"));
	}

	private static String readHtml(String suffix) throws UnsupportedEncodingException, IOException {
		Class<RpArticleParserTest> klass = RpArticleParserTest.class;
		InputStream resource = klass.getResourceAsStream("/" + klass.getSimpleName() + "." + suffix + ".html");

		BufferedReader reader = new BufferedReader(new InputStreamReader(resource, "UTF-8"));

		String html = reader.readLine();
		return html;
	}

	@Test
	public void testArticleBody() {
		String body = articleParser.getArticle().getBody();

		Assert.assertTrue(body.startsWith(" Krefeld (RP)."));
		Assert.assertTrue(body.trim().endsWith("\"Was uns freut ist die große Anteilnahme, die uns erreicht.\""));
	}

	@Test
	public void testGalleryBody() {
		String body = galleryParser.getArticle().getBody();

		System.out.println(body);
		Assert.assertTrue(body.startsWith("Nach sechsjähriger Bauzeit ist das BoA-Kraftwerk"));

		String last = "3,5 Millliarden Kilowatt  Strom.";
		body = body.trim();
		Assert.assertEquals(last, body.substring(body.length() - last.length()));
	}

	@Test
	public void testType() {
		Assert.assertEquals(GALLERY, galleryParser.getArticle().getType());
		Assert.assertEquals(STANDARD, articleParser.getArticle().getType());
	}

	@Test
	public void testArticleDepartment() {
		Assert.assertEquals("niederrhein-sued/krefeld/nachrichten", articleParser.getArticle().getDepartment());
	}

	@Test
	public void testGalleryDepartment() {
		Assert.assertEquals("grevenbroich/nachrichten", galleryParser.getArticle().getDepartment());
	}

	@Test
	public void testArticleTitle() {
		Assert.assertEquals("Tier reißt acht Schafe in Krefeld: Lebendfalle soll Husky überlisten", articleParser
				.getArticle().getTitle());
	}

	@Test
	public void testGalleryTitle() {
		Assert.assertEquals("Das ist das neue Braunkohle-Kraftwerk", galleryParser.getArticle().getTitle());
	}
}

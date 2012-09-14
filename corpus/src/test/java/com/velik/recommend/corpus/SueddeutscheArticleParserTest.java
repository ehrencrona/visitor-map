package com.velik.recommend.corpus;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.velik.recommend.parser.SueddeutscheArticleParser;

public class SueddeutscheArticleParserTest {
	private static URL url;

	@Before
	public void setUp() throws Exception {
		url = new URL("http://www.google.com");
	}

	@Test
	public void testTitle() {
		Assert.assertEquals("Atomausstieg - Altmaier", new SueddeutscheArticleParser(
				"<li class='first last' itemscope itemtype='http://data-vocabulary.org/Breadcrumb'"
						+ " itemprop='breadcrumb'><h1>Atomausstieg - Altmaier</h1>" + "</li>".replace('\'', '"'), url)
				.getArticle().getTitle());
	}

	@Test
	public void testSubline() {
		Assert.assertEquals("\"Da sind Fehler gemacht worden\".",
				new SueddeutscheArticleParser(("<p></p>\n<p class='entry-summary'>"
						+ "&quot;Da sind Fehler gemacht worden&quot;." + "</p>").replace('\'', '"'), url).getArticle()
						.getSubline());
	}

	@Test
	public void testBody() {
		Assert.assertEquals("Es stelle\n" + "Altmaier, der \"Prognosen\" Êgewesen.\n" + "paragraph2\n",
				new SueddeutscheArticleParser(
						("</noscript></div><!-- IQ ADTAG END --> <!-- end ad tag --></div></div>  <p>Es stelle</p>\n"
								+ " \t<p>Altmaier, der 'Prognosen' &nbsp;gewesen.</p>" + "  <p>paragraph2</p>"
								+ " <!-- [START: Sociallinks] -->"
								+ "<div class='articlefooter sociallinks full-column footer '> "
								+ "<ul class='actions'>" + "<p></p>\n<p class='entry-summary'>"
								+ "&quot;Da sind Fehler gemacht worden&quot;.</p>").replace('\'', '"'), url)
						.getArticle().getBody());
	}
}

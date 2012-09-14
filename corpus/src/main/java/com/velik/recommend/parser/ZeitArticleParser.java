package com.velik.recommend.parser;

import java.net.URL;

import com.velik.recommend.corpus.AbstractParser;
import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.EmptyArticle;

public class ZeitArticleParser extends AbstractParser {
	private static final String START_SUPER_TITLE = "<!--AB HIER IHR CONTENT-->";
	private static final String START_TITLE = "<span class=\"title\">";
	private static final String START_SUBLINE = "<p class=\"excerpt\">";
	private static final String START_BODY = "<!--[ byline ]-->";
	private static final String END_BODY = "<div class=\"comment_tools\">";

	private int i;
	private int j;
	private int k;
	private int l;

	public ZeitArticleParser(String html, URL url) {
		super(html, url);
		i = html.indexOf(START_TITLE);
		j = html.indexOf(START_SUBLINE, i + 1);
		k = html.indexOf(START_BODY, i + 1);
		l = html.indexOf(END_BODY, k + 1);
	}

	@Override
	public Article getArticle() {
		return new EmptyArticle() {

			public String getBody() {
				if (k < 0 || l < 0) {
					return "";
				}

				String rawBody = html.substring(k, l);

				return getBodyFromRawHtml(rawBody);
			}

			public String getTitle() {
				if (i < 0 || j < 0) {
					return "";
				}

				String rawTitle = html.substring(i, j);

				return stripHtml(rawTitle).trim();
			}

			@Override
			public URL getUrl() {
				return url;
			}

		};
	}
}

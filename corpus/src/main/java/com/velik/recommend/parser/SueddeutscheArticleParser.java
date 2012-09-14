package com.velik.recommend.parser;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.velik.recommend.corpus.AbstractParser;
import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.EmptyArticle;

public class SueddeutscheArticleParser extends AbstractParser {
	private static final Pattern WHOLE_BODY_PATTERN = Pattern.compile("<p>(.*?)</p>(\\s*<p>.*?</p>)+");
	private static final Pattern TITLE_PATTERN = Pattern.compile("<h1>([^<]*)</h1>");
	private static final Pattern SUBLINE_PATTERN = Pattern.compile("<p class=\"entry-summary\">([^<]*)</p>");

	public SueddeutscheArticleParser(String html, URL url) {
		super(html, url);
	}

	@Override
	public Article getArticle() {
		return new EmptyArticle() {
			public String getTitle() {
				return firstMatch(TITLE_PATTERN);
			}

			public String getSubline() {
				return firstMatch(SUBLINE_PATTERN);
			}

			public String getBody() {
				Matcher m = WHOLE_BODY_PATTERN.matcher(html);

				if (m.find()) {
					String body = m.group(0);

					return getBodyFromRawHtml(body);
				} else {
					return "";
				}
			}

			@Override
			public URL getUrl() {
				return url;
			}
		};
	}
}

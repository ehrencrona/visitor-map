package com.velik.recommend.parser;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.velik.recommend.corpus.AbstractParser;
import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.EmptyArticle;

public class N24ArticleParser extends AbstractParser {
	private static final Pattern TITLE_PATTERN = Pattern.compile("<h1>([^<]*)</h1>");

	public N24ArticleParser(String html, URL url) {
		super(html, url);
	}

	@Override
	public Article getArticle() {
		return new EmptyArticle() {

			@Override
			public String getTitle() {
				Matcher matcher = TITLE_PATTERN.matcher(html);

				if (matcher.find()) {
					return StringEscapeUtils.unescapeHtml(matcher.group(1)).trim();
				} else {
					return "";
				}
			}

			@Override
			public String getBody() {
				int i = html.indexOf("<p class=\"subtext\">");

				int j = html.indexOf("p class=\"subtext date\">");

				if (i < 0 || j < 0) {
					return "";
				} else {
					return getBodyFromRawHtml(html.substring(i, j));
				}
			}

			@Override
			public URL getUrl() {
				return url;
			}
		};
	}
}

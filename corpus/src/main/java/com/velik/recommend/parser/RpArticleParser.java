package com.velik.recommend.parser;

import static com.velik.recommend.corpus.ArticleType.GALLERY;
import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.velik.recommend.corpus.AbstractParser;
import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleType;
import com.velik.recommend.corpus.EmptyArticle;

public class RpArticleParser extends AbstractParser {
	private static final Pattern TITLE_PATTERN = Pattern.compile("<title>([^<]*)</title>");
	private static final Pattern ARTICLE_BODY_PARAGRAPH_PATTERN = Pattern.compile("<p class=\"absatz\">(.*?)</p>");
	private static final Pattern CANONICAL_URL_PATTERN = Pattern
			.compile("<link rel=\"canonical\" href=\"([^\"]*)\" />");
	protected static final Pattern GALLERY_BODY_PARAGRAPH_PATTERN = Pattern.compile("<div class=\"text\">(.*?)</div>");

	public RpArticleParser(String html, URL url) {
		super(html, url);
	}

	@Override
	public Article getArticle() {

		return new EmptyArticle() {
			public ArticleType getType() {
				if (html.contains("http://www.movingimage24.com/videojs")) {
					return ArticleType.VIDEO;
				} else if (html.contains("teaser fotostrecke slidecontainer")) {
					return ArticleType.GALLERY;
				} else {
					return ArticleType.STANDARD;
				}
			}

			@Override
			public String getTitle() {
				return firstMatch(TITLE_PATTERN);
			}

			@Override
			public String getBody() {
				Pattern pattern;

				if (getType() == GALLERY) {
					pattern = GALLERY_BODY_PARAGRAPH_PATTERN;
				} else {
					pattern = ARTICLE_BODY_PARAGRAPH_PATTERN;
				}

				Matcher m = pattern.matcher(html);

				String body = "";

				while (m.find()) {
					body += m.group(0);
				}

				if (getType() == GALLERY) {
					return unescapeHtml(stripHtml(body)).trim();
				} else {
					return getBodyFromRawHtml(body);
				}
			}

			@Override
			public String getDepartment() {
				String canonicalUrl = firstMatch(CANONICAL_URL_PATTERN);

				try {
					String path = new URL(canonicalUrl).getPath();

					if (path.startsWith("/")) {
						path = path.substring(1);
					}

					int i = path.lastIndexOf('/');

					if (i > 0) {
						return path.substring(0, i);
					}
				} catch (MalformedURLException e) {
					// ignore
				}

				return "";
			}

			@Override
			public URL getUrl() {
				return url;
			}
		};

	}
}

package com.velik.recommend.corpus;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.velik.recommend.parser.Parser;

public abstract class AbstractParser implements Parser {
	private static final Pattern BODY_PARAGRAPH_PATTERN = Pattern.compile("<p[^>]*>(.*?)</p>");
	private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]*>");
	private static final Pattern HTML_COMMENT_PATTERN = Pattern.compile("<!--(.*?)-->");

	protected String html;
	protected URL url;

	public AbstractParser(String html, URL url) {
		this.html = html;
		this.url = url;
	}

	protected String firstMatch(Pattern pattern) {
		Matcher matcher = pattern.matcher(html);

		if (matcher.find()) {
			return StringEscapeUtils.unescapeHtml(matcher.group(1));
		} else {
			return "";
		}
	}

	protected String getBodyFromRawHtml(String body) {
		StringBuffer result = new StringBuffer(10000);

		Matcher m = BODY_PARAGRAPH_PATTERN.matcher(body);

		while (m.find()) {
			result.append(unescapeHtml(m.group(1)).trim());
			result.append('\n');
		}

		return stripHtml(result.toString());
	}

	protected String stripHtml(String string) {
		string = HTML_COMMENT_PATTERN.matcher(string).replaceAll(" ");
		return HTML_PATTERN.matcher(string).replaceAll(" ");
	}
}

package com.velik.recommend.spider.names;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaNameSpider {
	private static String START_URL = "http://en.wikipedia.org/wiki/Lists_of_people_by_nationality";

	public static void main(String[] args) throws Exception {

		StringBuffer start = new UrlDownloader().load(new URL(START_URL), "UTF-8");

		Matcher matcher = Pattern.compile("a href=\"(/wiki/List_of[^\"]*)").matcher(start);

		List<URL> pages = new ArrayList<URL>();

		while (matcher.find()) {
			String link = matcher.group(1);

			pages.add(new URL("http", "en.wikipedia.org", link));

			if (link.contains("Quebecers")) {
				break;
			}
		}

		Pattern nameLinkPattern = Pattern.compile("<li><a href=\"[^\"]*\" title=\"([^\"]*)\"");

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(
				"names-wikipedia.txt")), "UTF-8"));

		for (URL page : pages) {
			Thread.sleep(1000);
			System.out.println(page);

			writer.write("--" + page + "\n");

			StringBuffer content = new UrlDownloader().load(page, "UTF-8");

			matcher = nameLinkPattern.matcher(content);

			int i = content.indexOf("<div id=\"toctitle\">");

			if (i < 0) {
				continue;
			}

			int j = content.indexOf("id=\"See_also\"");

			matcher.find(i);

			while (matcher.find()) {
				if (matcher.start() > j) {
					break;
				}

				if (isName(matcher.group(1))) {
					writer.write(matcher.group(1) + "\n");
				}
			}
		}

		writer.close();
	}

	private static boolean isName(String group) {
		if (Character.isLowerCase(group.charAt(0))) {
			return false;
		}

		int i = group.indexOf(' ');

		if (i < 0) {
			return false;
		}

		if (group.contains("ikimedia") || group.contains("ikipedia") || group.contains("Category")
				|| group.contains("Edit section") || group.contains("List of")) {
			return false;
		}

		return true;
	}
}

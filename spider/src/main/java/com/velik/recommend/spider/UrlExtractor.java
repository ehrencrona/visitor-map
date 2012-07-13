package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class UrlExtractor {
	public Set<HashableUrl> extract(String html) {
		Set<HashableUrl> result = new HashSet<HashableUrl>();

		int i = 0;

		do {
			i = html.indexOf("\"http://", i + 1);

			if (i > 0) {
				int j = html.indexOf('"', i + 10);

				if (j > 0) {
					String urlString = html.substring(i + 1, j);

					try {
						URL url = new URL(urlString);

						result.add(new HashableUrl(url));
					} catch (MalformedURLException e) {
						// ignore.
					}
				}
			}

		} while (i > 0);

		return result;
	}
}

package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class UrlExtractor {

	public Set<HashableUrl> extract(String html, URL relativeTo) {
		Set<HashableUrl> result = new HashSet<HashableUrl>();

		int i = 0;

		do {
			i = html.indexOf(" href=\"", i + 1);

			if (i > 0) {
				i += 6;
				int j = html.indexOf('"', i + 1);

				if (j > 0) {
					String urlString = html.substring(i + 1, j);

					if (urlString.indexOf(' ') < 0) {
						try {
							URL url;

							if (urlString.startsWith("http://") || urlString.startsWith("www.")) {
								url = new URL(urlString);
							} else {
								url = new URL(relativeTo, urlString);
							}

							result.add(new HashableUrl(url));
						} catch (MalformedURLException e) {
							// ignore.
						}
					}
				}
			}

		} while (i > 0);

		return result;
	}
}

package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

public class N24UrlShortener implements UrlShortener {

	@Override
	public URL shorten(URL url) {
		String path = url.getPath();

		if (path.startsWith("/archiv/archiv_")) {
			int i = path.lastIndexOf("/");

			if (i > 0) {
				try {
					return new URL(url.getProtocol(), url.getHost(), "/a" + path.substring(i));
				} catch (MalformedURLException e) {
				}
			}
		}

		if (path.startsWith("/news/newsitem_") && path.endsWith(".html")) {
			String idString = path.substring(15, path.length() - 5);

			try {
				Integer.parseInt(idString);

				return new URL(url.getProtocol(), url.getHost(), "/" + idString);
			} catch (NumberFormatException e) {
				// not a known URL format.
			} catch (MalformedURLException e) {
			}
		}

		return url;
	}

}

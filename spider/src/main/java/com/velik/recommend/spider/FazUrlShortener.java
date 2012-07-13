package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

public class FazUrlShortener implements UrlShortener {

	@Override
	public URL shorten(URL url) {
		String path = url.getPath();

		if (path.endsWith(".html")) {
			int i = path.lastIndexOf('-');

			if (i > 0) {
				String idString = path.substring(i + 1, path.length() - 5);

				try {
					Integer.parseInt(idString);

					return new URL(url.getProtocol(), url.getHost(), "/" + idString);
				} catch (NumberFormatException e) {
					// not a known URL format.
				} catch (MalformedURLException e) {
				}
			}
		}

		return url;
	}

}

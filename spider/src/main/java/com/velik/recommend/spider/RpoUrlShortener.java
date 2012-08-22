package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;

public class RpoUrlShortener implements UrlShortener {

	@Override
	public URL shorten(URL url) {
		String path = url.getPath();

		int i = path.lastIndexOf('-');

		if (i > 0 && path.length() > i + 2 && path.charAt(i + 2) == '.') {
			try {
				return new URL(url.getProtocol(), url.getHost(), "/" + path.substring(i + 1));
			} catch (NumberFormatException e) {
				// not a known URL format.
			} catch (MalformedURLException e) {
			}
		}

		return url;
	}

}

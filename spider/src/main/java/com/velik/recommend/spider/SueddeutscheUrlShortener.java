package com.velik.recommend.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.util.ContentId;
import com.velik.util.InvalidContentIdException;

public class SueddeutscheUrlShortener implements UrlShortener {
	private static final Logger LOGGER = Logger.getLogger(SueddeutscheUrlShortener.class.getName());

	@Override
	public URL shorten(URL url) {
		String path = url.getPath();

		if (path.length() < 5) {
			return url;
		}

		// -4 to avoid page numbers.
		int i = path.lastIndexOf('-', path.length() - 4);

		if (i < 0) {
			return url;
		}

		int j = path.lastIndexOf('-');

		String contentIdString;

		boolean hasPageNumber = j > i;

		String page;

		if (hasPageNumber) {
			contentIdString = path.substring(i + 1, j);
			page = path.substring(j);
		} else {
			contentIdString = path.substring(i + 1);
			page = "";
		}

		if (isContentId(contentIdString)) {
			path = "/" + contentIdString + (url.getQuery() != null ? "?" + url.getQuery() : "") + page;

			try {
				url = new URL(url.getProtocol(), url.getHost(), path);
			} catch (MalformedURLException e) {
				LOGGER.log(Level.WARNING, "For " + url + ": " + e.toString());
			}
		}

		return url;
	}

	private boolean isContentId(String contentIdString) {
		try {
			new ContentId(contentIdString);

			return true;
		} catch (InvalidContentIdException e) {
			return false;
		}
	}

}

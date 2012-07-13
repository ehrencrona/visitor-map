package com.velik.recommend.spider;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HostSpecificUrlShortener implements UrlShortener {

	private Map<String, UrlShortener> shortenerByHost = new HashMap<String, UrlShortener>();

	public void put(String host, UrlShortener urlShortener) {
		shortenerByHost.put(host, urlShortener);
	}

	@Override
	public URL shorten(URL url) {
		UrlShortener shortener = shortenerByHost.get(url.getHost());

		if (shortener == null) {
			shortener = IdentityUrlShortener.INSTANCE;
		}

		return shortener.shorten(url);
	}

}

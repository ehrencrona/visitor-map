package com.velik.recommend.spider;

import java.net.URL;

public class IdentityUrlShortener implements UrlShortener {
	public static final UrlShortener INSTANCE = new IdentityUrlShortener();

	@Override
	public URL shorten(URL url) {
		return url;
	}

}

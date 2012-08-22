package com.velik.recommend.spider;

import java.net.URL;

public class SameHostUrlFilter implements UrlFilter {

	@Override
	public boolean isFollow(URL link, URL from) {
		return link.getHost().equalsIgnoreCase(from.getHost());
	}

}

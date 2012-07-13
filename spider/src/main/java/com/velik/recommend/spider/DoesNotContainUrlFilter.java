package com.velik.recommend.spider;

import java.net.URL;

public class DoesNotContainUrlFilter implements UrlFilter {

	private String string;

	public DoesNotContainUrlFilter(String string) {
		this.string = string;
	}

	@Override
	public boolean isFollow(URL link, URL from) {
		return link.toString().indexOf(string) < 0;
	}

}

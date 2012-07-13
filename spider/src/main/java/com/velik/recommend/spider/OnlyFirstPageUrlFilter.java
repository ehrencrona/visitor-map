package com.velik.recommend.spider;

import java.net.URL;

public class OnlyFirstPageUrlFilter implements UrlFilter {

	@Override
	public boolean isFollow(URL link, URL from) {
		String file = link.getFile();

		int i = file.lastIndexOf('-');

		if (i > 0 && i >= file.length() - 3) {
			String page = file.substring(i + 1);

			try {
				return Integer.parseInt(page) <= 1;
			} catch (NumberFormatException e) {
				return true;
			}
		}

		return true;
	}

}

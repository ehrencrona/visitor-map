package com.velik.recommend.spider;

import java.net.URL;

public class TopLevelUrlFilter implements UrlFilter {
	private String[] allowedTopLevels;
	private String host;

	public TopLevelUrlFilter(String host, String... allowedTopLevels) {
		this.allowedTopLevels = allowedTopLevels;
		this.host = host;
	}

	@Override
	public boolean isFollow(URL link, URL from) {
		if (!link.getHost().equals(host)) {
			return true;
		}

		String path = link.getPath();

		int i = path.indexOf('/', 1);

		String topLevel;

		if (i != -1) {
			topLevel = path.substring(0, i);
		} else {
			topLevel = path;
		}

		if (topLevel.startsWith("/")) {
			topLevel = topLevel.substring(1);
		}

		for (String allowedTopLevel : allowedTopLevels) {
			if (topLevel.equals(allowedTopLevel)) {
				return true;
			}
		}

		return false;
	}

}

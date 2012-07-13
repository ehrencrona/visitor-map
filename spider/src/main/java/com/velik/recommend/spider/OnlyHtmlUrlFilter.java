package com.velik.recommend.spider;

import java.net.URL;

public class OnlyHtmlUrlFilter implements UrlFilter {

	@Override
	public boolean isFollow(URL link, URL from) {
		String file = link.getFile();

		int i = file.lastIndexOf('.');

		if (i > 0 && i >= file.length() - 5) {
			String extension = file.substring(i + 1);

			if (isLettersOnly(extension)) {
				return extension.equals("html");
			}
		}

		return true;
	}

	private boolean isLettersOnly(String extension) {
		for (int i = 0; i < extension.length(); i++) {
			if (!Character.isLetter(extension.charAt(i))) {
				return false;
			}
		}

		return true;
	}
}

package com.velik.recommend.spider;

import java.net.URL;

public class HashableUrl {
	private URL url;

	HashableUrl(URL url) {
		this.url = url;
	}

	public String toString() {
		return url.toString();
	}

	public URL toUrl() {
		return url;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof HashableUrl && obj.toString().equals(toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}

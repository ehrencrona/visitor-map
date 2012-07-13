package com.velik.recommend.spider;

import java.net.URL;

public interface UrlFilter {

	boolean isFollow(URL link, URL from);

}

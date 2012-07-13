package com.velik.recommend.spider;

import java.net.URL;

public interface UrlCollection extends Iterable<URL> {

	boolean add(URL url);

	boolean contains(URL url);

}
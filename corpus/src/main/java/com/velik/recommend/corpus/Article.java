package com.velik.recommend.corpus;

import java.net.URL;

public interface Article {
	String getTitle();

	String getOverline();

	String getSubline();

	String getAuthor();

	String[] getEditorialKeywords();

	String[] getAutomaticKeywords();

	String getBody();

	URL getUrl();

	String getDepartment();

	ArticleType getType();
}

package com.velik.recommend.corpus;

public interface Article {
	String getTitle();

	String getOverline();

	String getSubline();

	String getAuthor();

	String[] getEditorialKeywords();

	String[] getAutomaticKeywords();

	String getBody();
}

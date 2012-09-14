package com.velik.recommend.corpus;

import java.net.URL;

public class ArticleWithOnlyBody implements Article {

	private String body;

	public ArticleWithOnlyBody(String body) {
		this.body = body;
	}

	@Override
	public String getTitle() {
		return "";
	}

	@Override
	public String getOverline() {
		return "";
	}

	@Override
	public String getSubline() {
		return "";
	}

	@Override
	public String getAuthor() {
		return "";
	}

	@Override
	public String[] getEditorialKeywords() {
		return new String[0];
	}

	@Override
	public String[] getAutomaticKeywords() {
		return new String[0];
	}

	@Override
	public String getBody() {
		return body;
	}

	@Override
	public URL getUrl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDepartment() {
		return "";
	}

	@Override
	public ArticleType getType() {
		return ArticleType.STANDARD;
	}
}

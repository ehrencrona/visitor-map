package com.velik.recommend.corpus;

public abstract class EmptyArticle implements Article {

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
		return "";
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

package com.velik.recommend.factory;

import java.net.URL;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleType;

public class PojoArticle implements Article {
	private String title = "";
	private String overline = "";
	private String subline = "";
	private String author = "";
	private String body = "";
	private String department = "";

	private ArticleType type = ArticleType.STANDARD;
	private URL url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOverline() {
		return overline;
	}

	public void setOverline(String overline) {
		this.overline = overline;
	}

	public String getSubline() {
		return subline;
	}

	public void setSubline(String subline) {
		this.subline = subline;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public String[] getEditorialKeywords() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getAutomaticKeywords() {
		throw new UnsupportedOperationException();
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public ArticleType getType() {
		return type;
	}

	public void setType(ArticleType type) {
		this.type = type;
	}
}

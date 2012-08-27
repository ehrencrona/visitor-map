package com.velik.recommend.map;

import java.io.Serializable;

import com.velik.recommend.corpus.ArticleType;

public class ArticleInfo implements Serializable {
	private static final long serialVersionUID = 0L;

	public String title = "";
	public String department = "";
	public ArticleType type;
}

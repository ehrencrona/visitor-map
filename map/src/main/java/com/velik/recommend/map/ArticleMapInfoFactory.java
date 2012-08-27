package com.velik.recommend.map;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.velik.recommend.corpus.Article;
import com.velik.recommend.corpus.ArticleVisitor;
import com.velik.recommend.factory.Visitee;
import com.velik.util.Factory;

public class ArticleMapInfoFactory implements Factory<Map<Integer, ArticleInfo>> {
	private static Pattern ID_PATTERN = Pattern.compile("[-/]1\\.([0-9]*)$");

	private Map<Integer, ArticleInfo> result = new HashMap<Integer, ArticleInfo>();
	private Visitee visitee;

	public ArticleMapInfoFactory(Visitee visitee) {
		this.visitee = visitee;
	}

	@Override
	public Map<Integer, ArticleInfo> create() {
		visitee.visit(new ArticleVisitor() {
			@Override
			public void processArticle(Article article) {
				Matcher matcher = ID_PATTERN.matcher(article.getUrl().toString());

				if (matcher.find()) {
					int minor = Integer.parseInt(matcher.group(1));

					ArticleInfo articleInfo = new ArticleInfo();
					articleInfo.title = article.getTitle();
					articleInfo.department = article.getDepartment();
					articleInfo.type = article.getType();

					result.put(minor, articleInfo);
				}
			}
		});

		return result;
	}

}

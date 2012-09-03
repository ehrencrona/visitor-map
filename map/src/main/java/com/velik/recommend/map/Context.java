package com.velik.recommend.map;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.velik.recommend.factory.UnparsedSpideredArticlesVisitee;
import com.velik.recommend.factory.Visitee;
import com.velik.recommend.log.AccessLogDirectoryReader;
import com.velik.recommend.stats.AccessByArticleFactory;
import com.velik.recommend.stats.ArticleCounter;
import com.velik.recommend.util.AbstractContext;

public class Context extends AbstractContext {
	private static Context context;
	private File dataDirectory;

	public Context(File dataDirectory, boolean persist) {
		super(persist);

		this.dataDirectory = dataDirectory;
	}

	public Map<Integer, Integer> getFaithfulnessByArticle() {
		return readOrCreate("faithfulness-by-article.ser", new FaithfulnessByArticleFactory(getAccessIterator(),
				getArticleMinors()));
	}

	public StressMatrix getStressMatrix() {
		return readOrCreate("stress-matrix.ser", new CommonReadersStressMatrixFactory(getArticleMinors(),
				getAccessIterator()));
	}

	public Map<Integer, ArticleCounter> getAccessesByArticle() {
		return readOrCreate("count-by-minor.ser", new AccessByArticleFactory(getAccessIterator()));
	}

	AccessLogDirectoryReader getAccessIterator() {
		return new AccessLogDirectoryReader(dataDirectory);
	}

	public Set<Integer> getArticleMinors() {
		return readOrCreate("articles.ser", new ArticleMinorsForMapFactory(this));
	}

	public StressMap getMap() {
		return readOrCreate("stress-map.ser", new StressMapFactory(getStressMatrix()));
	}

	public Map<Integer, ArticleInfo> getArticleInfo() {
		return readOrCreate("article-info.ser", new ArticleMapInfoFactory(getArticleVisitee()));
	}

	private Visitee getArticleVisitee() {
		return new UnparsedSpideredArticlesVisitee(new File(dataDirectory, "spidered-html-for-som.zip"), 1);
	}

	public synchronized static Context getContext() {
		if (context == null) {
			context = new Context(new File("/projects/recommend/accesslog/data"), true);
		}

		return context;
	}
}

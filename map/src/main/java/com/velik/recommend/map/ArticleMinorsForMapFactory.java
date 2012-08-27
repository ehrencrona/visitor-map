package com.velik.recommend.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.velik.recommend.stats.ArticleCounter;
import com.velik.recommend.stats.Distribution;
import com.velik.util.Factory;

public class ArticleMinorsForMapFactory implements Factory<Set<Integer>> {
	private Context context;

	public ArticleMinorsForMapFactory(Context context) {
		this.context = context;
	}

	public Set<Integer> create() {
		Collection<ArticleCounter> articleCounters = context.getAccessesByArticle().values();

		Distribution<ArticleCounter> distribution = new Distribution<ArticleCounter>();

		for (ArticleCounter counter : articleCounters) {
			distribution.add(counter);
		}

		distribution.print(99, 100, 50);

		HashSet<Integer> articles = new HashSet<Integer>(2048);

		for (int i = distribution.getValues().size() - 2048; i < distribution.getValues().size(); i++) {
			articles.add(distribution.getValues().get(i).minor);
		}

		return articles;
	}

}

package com.velik.recommend.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.velik.recommend.stats.ArticleCounter;
import com.velik.recommend.stats.Distribution;
import com.velik.util.Factory;

public class ArticleMinorsForMapFactory implements Factory<Set<Integer>> {
	private static final int TARGET_SIZE = 4096;
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

		HashSet<Integer> articles = new HashSet<Integer>(TARGET_SIZE);

		List<ArticleCounter> values = distribution.getValues();

		int i = values.size() - 1;

		while (articles.size() < TARGET_SIZE) {
			ArticleCounter articleCounter = values.get(i--);

			articles.add(articleCounter.minor);
		}

		for (Integer minor : articles) {
			System.out.println("http://www.rp-online.de/1." + minor);
		}

		return articles;
	}
}

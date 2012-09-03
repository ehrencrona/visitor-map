package com.velik.recommend.stats;

import java.util.List;
import java.util.Map.Entry;

import com.velik.recommend.log.Access;

public class DistributionFactory {
	private Distribution<UserCounter> pageLoadDistribution = new Distribution<UserCounter>();
	private Distribution<ArticleCounter> readerDistribution = new Distribution<ArticleCounter>();

	private UsersByMinors readersByMinors = new UsersByMinors();
	private PageLoadsByUser pageLoadsByUser = new PageLoadsByUser();

	private Iterable<Access> accessIterable;

	public DistributionFactory(Iterable<Access> accessIterator) {
		this.accessIterable = accessIterator;
	}

	public void print() {
		for (Access access : accessIterable) {
			pageLoadsByUser.visit(access);
			readersByMinors.visit(access);
		}
		/*
		 * x * for (Entry<Integer, List<Long>> entry :
		 * readersByMinors.usersByMinor.entrySet()) { readerDistribution.add(new
		 * ArticleCounter(entry.getKey(), entry.getValue().size())); }
		 * 
		 * System.out.println("Readers by page");
		 * readerDistribution.printCumulative(100);
		 */
		for (Entry<Long, List<Integer>> entry : pageLoadsByUser.minorsByUser.entrySet()) {
			pageLoadDistribution.add(new UserCounter(entry.getKey(), entry.getValue().size()));
		}

		System.out.println();
		System.out.println("Pages loads by user");
		pageLoadDistribution.printCumulative(100);
	}
}

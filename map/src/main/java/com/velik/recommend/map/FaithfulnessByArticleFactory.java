package com.velik.recommend.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.velik.recommend.log.Access;
import com.velik.recommend.stats.UserCounter;
import com.velik.recommend.stats.UsersByMinors;
import com.velik.util.Factory;

public class FaithfulnessByArticleFactory implements Factory<Map<Integer, Integer>> {
	private UsersByMinors usersByMinors;
	private Iterable<Access> accessIterator;

	public FaithfulnessByArticleFactory(Iterable<Access> accessIterator, Set<Integer> articles) {
		this.usersByMinors = new UsersByMinors(articles);
		this.accessIterator = accessIterator;
	}

	@Override
	public Map<Integer, Integer> create() {
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		Map<Long, UserCounter> pageLoadsByUser = new HashMap<Long, UserCounter>();

		for (Access access : accessIterator) {
			usersByMinors.visit(access);

			UserCounter counter = pageLoadsByUser.get(access.getUserId());

			if (counter == null) {
				counter = new UserCounter(access.getUserId());

				pageLoadsByUser.put(access.getUserId(), counter);
			}

			counter.count++;
		}

		for (Entry<Integer, List<Long>> entry : usersByMinors.getUsersByMinors().entrySet()) {
			Integer minor = entry.getKey();
			List<Long> readersOfMinor = entry.getValue();

			int totalPageLoads = 0;

			for (Long user : readersOfMinor) {
				UserCounter pageLoads = pageLoadsByUser.get(user);

				if (pageLoads != null) {
					totalPageLoads += pageLoads.count;
				}
			}

			result.put(minor, totalPageLoads / readersOfMinor.size());
		}

		return result;
	}

}

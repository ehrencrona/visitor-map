package com.velik.recommend.map;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.velik.recommend.log.Access;
import com.velik.recommend.stats.UsersByMinors;
import com.velik.util.Factory;

public class CommonReadersStressMatrixFactory implements Factory<StressMatrix> {

	private Iterable<Access> accesses;
	private Set<Integer> articles;

	CommonReadersStressMatrixFactory(Set<Integer> articles, Iterable<Access> accesses) {
		this.articles = articles;
		this.accesses = accesses;
	}

	public Map<Integer, List<Long>> createUsersByMinors() {
		UsersByMinors usersByMinors = new UsersByMinors(articles);

		for (Access access : accesses) {
			usersByMinors.visit(access);
		}

		return usersByMinors.usersByMinor;
	}

	public StressMatrix create() {
		return StressMatrix.fromUsersByMinor(createUsersByMinors());
	}

}

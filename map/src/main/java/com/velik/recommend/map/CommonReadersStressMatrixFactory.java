package com.velik.recommend.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.velik.recommend.log.Access;
import com.velik.util.Factory;

public class CommonReadersStressMatrixFactory implements Factory<StressMatrix> {

	private Set<Integer> articles;
	private Iterable<Access> accesses;

	CommonReadersStressMatrixFactory(Set<Integer> articles, Iterable<Access> accesses) {
		this.articles = articles;
		this.accesses = accesses;
	}

	public Map<Integer, List<Long>> createUsersByMinors() {
		Map<Integer, List<Long>> usersByMinor = new HashMap<Integer, List<Long>>();

		for (Access access : accesses) {
			if (access.getMajorId() != 1 || !articles.contains(access.getMinorId())) {
				continue;
			}

			List<Long> users = usersByMinor.get(access.getMinorId());

			if (users == null) {
				users = new ArrayList<Long>();

				usersByMinor.put(access.getMinorId(), users);
			}

			int i = Collections.binarySearch(users, access.getUserId());

			if (i < 0) {
				users.add(-1 - i, access.getUserId());
			}
		}

		return usersByMinor;
	}

	public StressMatrix create() {
		return StressMatrix.fromUsersByMinor(createUsersByMinors());
	}

}

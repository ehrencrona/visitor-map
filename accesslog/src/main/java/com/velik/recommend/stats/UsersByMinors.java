package com.velik.recommend.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.velik.recommend.log.Access;

public class UsersByMinors {
	private Set<Integer> articles;
	public Map<Integer, List<Long>> usersByMinor = new HashMap<Integer, List<Long>>();

	public UsersByMinors() {
	}

	public UsersByMinors(Set<Integer> articles) {
		this.articles = articles;
	}

	public void visit(Access access) {
		if (access.getMajorId() != 1 || (articles != null && !articles.contains(access.getMinorId()))) {
			return;
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

	public Map<Integer, List<Long>> getUsersByMinors() {
		return usersByMinor;
	}
}
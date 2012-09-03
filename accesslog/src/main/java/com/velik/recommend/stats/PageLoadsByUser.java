package com.velik.recommend.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.velik.recommend.log.Access;

public class PageLoadsByUser {
	Map<Long, List<Integer>> minorsByUser = new HashMap<Long, List<Integer>>();

	public PageLoadsByUser() {
	}

	void visit(Access access) {
		if (access.getMajorId() != 1) {
			return;
		}

		List<Integer> minors = minorsByUser.get(access.getUserId());

		if (minors == null) {
			minors = new ArrayList<Integer>();

			minorsByUser.put(access.getUserId(), minors);
		}

		int i = Collections.binarySearch(minors, access.getMinorId());

		if (i < 0) {
			minors.add(-1 - i, access.getMinorId());
		}
	}

	public Map<Long, List<Integer>> getUsersByMinors() {
		return minorsByUser;
	}
}
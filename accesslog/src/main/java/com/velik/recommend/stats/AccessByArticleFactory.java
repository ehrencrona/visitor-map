package com.velik.recommend.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.velik.recommend.log.Access;
import com.velik.util.Factory;

public class AccessByArticleFactory implements Factory<Map<Integer, ArticleCounter>> {
	private Iterable<Access> accesses;

	public AccessByArticleFactory(Iterable<Access> accesses) {
		this.accesses = accesses;
	}

	public Map<Integer, ArticleCounter> create() {
		Map<Integer, ArticleCounter> accessByMinor = new HashMap<Integer, ArticleCounter>(10000);

		Set<Long> users = new HashSet<Long>(10000);
		long total = 0;

		for (Access access : accesses) {
			if (access.getMajorId() != 1) {
				continue;
			}

			total++;
			users.add(access.getUserId());

			{
				ArticleCounter counter = accessByMinor.get(access.getMinorId());

				if (counter == null) {
					counter = new ArticleCounter(access.getMinorId());

					accessByMinor.put(access.getMinorId(), counter);
				}

				counter.count++;
			}
			/*
			 * UserCounter counter = accessByUser.get(access.getUserId());
			 * 
			 * if (counter == null) { counter = new
			 * UserCounter(access.getUserId());
			 * 
			 * accessByUser.put(access.getUserId(), counter); }
			 * 
			 * counter.count++;
			 */
		}

		System.out.println("Unique users:" + users.size());
		System.out.println("total counts: " + total);

		return accessByMinor;
		/*
		 * { Distribution<UserCounter> distribution = new
		 * Distribution<UserCounter>();
		 * 
		 * for (UserCounter counter : accessByUser.values()) {
		 * distribution.add(counter); }
		 * 
		 * distribution.print(95, 100, 300); }
		 */
	}
}

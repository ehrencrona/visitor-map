package com.velik.recommend.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EnergyMatrix implements Stresses {
	int[][] stress;
	int[] minor;

	EnergyMatrix() {
	}

	Stresses fromUsersByMinor(Map<Integer, List<Long>> usersByMinor) {
		int articleCount = usersByMinor.size();
		stress = new int[articleCount][];

		for (int i = 0; i < articleCount; i++) {
			stress[i] = new int[i];
		}

		minor = new int[articleCount];
		List<List<Long>> usersByIndex = new ArrayList<List<Long>>();

		{
			int i = 0;

			for (Entry<Integer, List<Long>> entry : usersByMinor.entrySet()) {
				minor[i] = entry.getKey();

				usersByIndex.add(entry.getValue());

				assertSorted(entry.getValue());
			}
		}

		for (int i = 0; i < articleCount; i++) {
			for (int j = 0; j < i; j++) {
				stress[i][j] = -overlap(usersByIndex.get(i), usersByIndex.get(j));
			}
		}

		return this;
	}

	private void assertSorted(List<Long> list) {
		if (list.isEmpty()) {
			return;
		}

		long lastValue = list.get(0);

		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) <= lastValue) {
				throw new RuntimeException(list + " was not sorted.");
			}

			lastValue = list.get(i);
		}
	}

	private int overlap(List<Long> list1, List<Long> list2) {
		int result = 0;
		int a = 0;
		int b = 0;

		while (a < list1.size() && b < list2.size()) {
			Long value1 = list1.get(a);
			Long value2 = list2.get(b);

			if (value1 < value2) {
				a++;
			} else if (value1 == value2) {
				result++;
				a++;
				b++;
			} else {
				b++;
			}
		}

		return result;
	}

	@Override
	public int get(int i, int j) {
		if (i == j) {
			return 0;
		}

		if (j > i) {
			int t = i;
			i = j;
			j = t;
		}

		return stress[i][j];
	}

	public int size() {
		return stress.length;
	}
}

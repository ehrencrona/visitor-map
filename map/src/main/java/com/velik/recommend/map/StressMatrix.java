package com.velik.recommend.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StressMatrix implements Stresses, Serializable {
	private static final Logger LOGGER = Logger.getLogger(StressMatrix.class.getName());

	private static final long serialVersionUID = 0L;

	int[][] stress;
	int[] minors;

	transient int[] minorByIndex;

	StressMatrix(int side) {
		stress = new int[side][];

		for (int i = 0; i < side; i++) {
			stress[i] = new int[i];
		}

		minors = new int[side];
	}

	static StressMatrix fromUsersByMinor(Map<Integer, List<Long>> usersByMinor) {
		LOGGER.log(Level.INFO, "Calculating stress matrix for " + usersByMinor.size() + " articles...");

		int articleCount = usersByMinor.size();

		StressMatrix result = new StressMatrix(articleCount);

		List<List<Long>> usersByIndex = new ArrayList<List<Long>>();

		{
			int i = 0;

			for (Entry<Integer, List<Long>> entry : usersByMinor.entrySet()) {
				result.minors[i++] = entry.getKey();

				usersByIndex.add(entry.getValue());

				assertSorted(entry.getValue());
			}
		}

		for (int i = 0; i < articleCount; i++) {
			for (int j = 0; j < i; j++) {
				result.stress[i][j] = -overlap(usersByIndex.get(i), usersByIndex.get(j));
			}
		}

		LOGGER.log(Level.INFO, "Done.");

		return result;
	}

	private static void assertSorted(List<Long> list) {
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

	static int overlap(List<Long> list1, List<Long> list2) {
		int result = 0;
		int a = 0;
		int b = 0;

		while (a < list1.size() && b < list2.size()) {
			Long value1 = list1.get(a);
			Long value2 = list2.get(b);

			if (value1 < value2) {
				a++;
			} else if (value1.equals(value2)) {
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

	public int getMinorByIndex(int index) {
		if (minorByIndex == null) {
			minorByIndex = new int[minors.length];

			for (int atIndex = 0; atIndex < minors.length; atIndex++) {
				int minor = minors[atIndex];

				minorByIndex[atIndex] = minor;
			}
		}

		return minorByIndex[index];
	}
}

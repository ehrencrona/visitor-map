package com.velik.recommend.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StressMatrix implements Stresses, Serializable {
	private static final Logger LOGGER = Logger.getLogger(StressMatrix.class.getName());

	private static final long serialVersionUID = 0L;

	private transient Map<Integer, Integer> indexByMinor;

	int[][] stress;
	int[] minors;

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

		int maxUsers = 0;

		{
			int i = 0;

			for (Entry<Integer, List<Long>> entry : usersByMinor.entrySet()) {
				result.minors[i++] = entry.getKey();

				usersByIndex.add(entry.getValue());

				if (entry.getValue().size() > maxUsers) {
					maxUsers = entry.getValue().size();
				}

				assertSorted(entry.getValue());
			}
		}

		int scale = maxUsers * maxUsers / 100;

		for (int i = 0; i < articleCount; i++) {
			long countI = usersByMinor.get(result.minors[i]).size();

			for (int j = 0; j < i; j++) {
				long countJ = usersByMinor.get(result.minors[j]).size();

				long overlap = overlap(usersByIndex.get(i), usersByIndex.get(j));
				int thisStress = (int) (-scale * overlap / countI / countJ);

				result.stress[i][j] = thisStress;
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
		return minors[index];
	}

	public int getIndexByMinor(int articleMinor) throws NoSuchMinorException {
		if (indexByMinor == null) {
			indexByMinor = new HashMap<Integer, Integer>();

			for (int i = 0; i < minors.length; i++) {
				indexByMinor.put(minors[i], i);
			}
		}

		Integer result = indexByMinor.get(articleMinor);

		if (result == null) {
			throw new NoSuchMinorException();
		}

		return result;
	}
}

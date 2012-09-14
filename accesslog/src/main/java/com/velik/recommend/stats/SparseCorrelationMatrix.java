package com.velik.recommend.stats;

import java.util.HashMap;
import java.util.Map;

public class SparseCorrelationMatrix {
	private static final int GROWTH = 5;

	public class ArticleNeighborhood {
		private int size;
		private int[] neighbors = new int[GROWTH];
		private int[] commonality = new int[GROWTH];

		public void add(int minor) {
			int index = indexOf(minor, neighbors, size);

			if (index < 0) {
				index = -index - 1;

				if (neighbors.length - size == 0) {
					int[] newNeighbors = new int[neighbors.length + GROWTH];
					int[] newCommonality = new int[commonality.length + GROWTH];

					System.arraycopy(neighbors, 0, newNeighbors, 0, index);
					System.arraycopy(commonality, 0, newCommonality, 0, index);
					System.arraycopy(neighbors, index, newNeighbors, index + 1, size - index);
					System.arraycopy(commonality, index, newCommonality, index + 1, size - index);

					neighbors = newNeighbors;
					commonality = newCommonality;
				} else if (size - index > 0) {
					System.arraycopy(neighbors, index, neighbors, index + 1, size - index);
					System.arraycopy(commonality, index, commonality, index + 1, size - index);
					commonality[index] = 0;
				}

				size++;
				neighbors[index] = minor;
			}

			commonality[index]++;
		}

		public int get(int minor) {
			int at = indexOf(minor, neighbors, size);

			if (at >= 0) {
				return commonality[at];
			} else {
				return 0;
			}
		}
	}

	private Map<Integer, ArticleNeighborhood> correlations = new HashMap<Integer, ArticleNeighborhood>();

	public void add(int minor1, int minor2) {
		assert minor1 != minor2;

		if (minor1 > minor2) {
			int tmp = minor1;
			minor1 = minor2;
			minor2 = tmp;
		}

		ArticleNeighborhood neighborhood = correlations.get(minor2);

		if (neighborhood == null) {
			neighborhood = new ArticleNeighborhood();
			correlations.put(minor2, neighborhood);
		}

		neighborhood.add(minor1);
	}

	public int get(int minor1, int minor2) {
		if (minor1 > minor2) {
			int tmp = minor1;
			minor1 = minor2;
			minor2 = tmp;
		}

		ArticleNeighborhood neighborhood = correlations.get(minor2);

		if (neighborhood != null) {
			return neighborhood.get(minor1);
		}

		return 0;
	}

	private static int indexOf(int x, int[] a, int length) {
		int low = 0;
		int high = length - 1;
		int mid;

		while (low <= high) {
			mid = (low + high) / 2;

			if (a[mid] < x) {
				low = mid + 1;
			} else if (a[mid] > x) {
				high = mid - 1;
			} else {
				return mid;
			}
		}

		int[] shortArr = new int[length];

		System.arraycopy(a, 0, shortArr, 0, length);

		return -low - 1;
	}

}

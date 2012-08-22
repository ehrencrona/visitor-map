package com.velik.recommend.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Distribution<T extends Comparable<T>> {
	private List<T> values = new ArrayList<T>();

	public void add(T value) {
		int i = Collections.binarySearch(values, value);

		if (i < 0) {
			i = -1 - i;
		}

		values.add(i, value);
	}

	public void print(int startP, int endP, int steps) {
		double p = (double) values.size() / 100;

		for (int i = 0; i <= steps; i++) {
			int index = (int) (p * startP + p * i * (endP - startP) / (steps - 1));

			printPercentile(Math.min(index, values.size() - 1));
		}
	}

	public List<T> getValues() {
		return values;
	}

	private void printPercentile(int i) {
		System.out.println((100 * i / (values.size() - 1)) + "% (index " + i + "), " + values.get(i));
	}

}

package com.velik.recommend.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Distribution<T extends Counter<T>> {
	private List<T> values = new ArrayList<T>();

	public void add(T value) {
		int i = Collections.binarySearch(values, value);

		if (i < 0) {
			i = -1 - i;
		}

		values.add(i, value);
	}

	public void printTabSeparated(int steps) {
		for (int i = 0; i < steps; i++) {
			int index = i * (values.size() - 1) / (steps - 1);

			System.out.println(index + "\t" + values.get(index).getCount());
		}
	}

	/**
	 * @param resolution
	 *            e.g. "100" to print changes larger than 1%.
	 */
	public void printCumulative(int resolution) {
		long sum = 0;

		long total = 0;

		for (int i = values.size() - 1; i >= 0; i--) {
			total += values.get(i).getCount();
		}

		long lastPrintedSum = -total;

		for (int i = values.size() - 1; i > 0; i--) {
			int value = values.get(i).getCount();

			sum += value;

			if (sum - lastPrintedSum > total / resolution || i > values.size() - 100) {
				System.out.println((values.size() - i) + "\t" + sum);

				lastPrintedSum = sum;
			}
		}

		System.out.println(values.size() + "\t" + sum);
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

	public int size() {
		return values.size();
	}

	public T getValue(int i) {
		return values.get(i);
	}
}

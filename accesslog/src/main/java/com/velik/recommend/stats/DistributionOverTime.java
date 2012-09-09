package com.velik.recommend.stats;

public class DistributionOverTime {
	long[] timesToPassCounterUnit;
	int size;
	int counter;
	int counterUnit = 1;

	public DistributionOverTime(int percentiles) {
		percentiles = (percentiles / 2) * 2;
		timesToPassCounterUnit = new long[percentiles];
	}

	public int getCounter() {
		return counter;
	}

	public void access(long time) {
		counter++;

		if (counter % counterUnit == 0) {
			if (size == timesToPassCounterUnit.length) {

				for (int i = 0; i < size; i++) {
					System.out.println(i + ": " + timesToPassCounterUnit[i]);
				}

				for (int i = 0; i * 2 < timesToPassCounterUnit.length; i++) {
					timesToPassCounterUnit[i] = timesToPassCounterUnit[i * 2 + 1];
				}

				size = size / 2;
				counterUnit *= 2;
			}

			timesToPassCounterUnit[size++] = time;
		}
	}
}

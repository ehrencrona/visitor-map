package com.velik.recommend.stats;

/**
 * Counts the rise and fall of article visits to an article by storing the time
 * at which a certain number of visits was reached. At most a certain upper
 * level of times is stored; to avoid overflowing it, it is compacted regularly
 * and every second milestone is removed.
 */
public class VisitDistributionOverTime {
	/**
	 * The access IDs at which counterUnit more visits had occurred since last
	 * milestone.
	 */
	long[] milestones;
	int size;
	int counter;
	int counterUnit = 1;
	long firstAppearance = -1;

	public VisitDistributionOverTime(int numberOfMilestones) {
		numberOfMilestones = (numberOfMilestones / 2) * 2;
		milestones = new long[numberOfMilestones];
	}

	public int getCounter() {
		return counter;
	}

	public void access(long accessId) {
		counter++;

		if (firstAppearance == -1) {
			firstAppearance = accessId;
		}

		if (counter % counterUnit == 0) {
			if (size == milestones.length) {
				for (int i = 0; i * 2 < milestones.length; i++) {
					milestones[i] = milestones[i * 2 + 1];
				}

				size = size / 2;
				counterUnit *= 2;
			}

			if (counter % counterUnit == 0) {
				milestones[size++] = accessId;
			}
		}
	}

	public long getFirstAppearance() {
		return firstAppearance;
	}

	/**
	 * Returns the percentage of the total time since the article appeared and
	 * now ("currentAccessId") that it took to reach visitPercentage percent of
	 * the current total of visits.
	 */
	public int getPercentageOfTimeBeforePercentile(int visitPercentage, long currentAccessId) {
		int targetCounter = counter * visitPercentage / 100;
		int i = targetCounter / counterUnit;

		int counterBefore = i * counterUnit;
		int counterAfter = (i + 1) * counterUnit;

		assert targetCounter >= counterBefore;
		assert targetCounter <= counterAfter;

		long timeBefore = (i > 0 ? milestones[i - 1] : firstAppearance);
		long timeAfter = (i < size ? milestones[i] : currentAccessId);

		long time = (timeBefore * (counterAfter - targetCounter) + timeAfter * (targetCounter - counterBefore))
				/ (counterAfter - counterBefore);

		return (int) (100 * (time - firstAppearance) / (currentAccessId - firstAppearance));
	}
}

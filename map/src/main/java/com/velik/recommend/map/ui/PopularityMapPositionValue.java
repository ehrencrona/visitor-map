package com.velik.recommend.map.ui;

import java.util.HashMap;
import java.util.Map;

import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;
import com.velik.recommend.stats.ArticleCounter;
import com.velik.recommend.stats.Distribution;

public class PopularityMapPositionValue implements MapPositionValue {

	private Map<Integer, ArticleCounter> counterByMinor;
	private int max;
	private int min;
	private StressMap map;
	private StressMatrix matrix;
	private double span;

	public PopularityMapPositionValue(Map<Integer, ArticleCounter> counterByMinor, StressMap map, StressMatrix matrix) {
		this.counterByMinor = counterByMinor;
		this.map = map;
		this.matrix = matrix;

		Distribution<ArticleCounter> dist = new Distribution<ArticleCounter>();

		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;

		for (ArticleCounter counter : counterByMinor.values()) {

			dist.add(counter);

			int count = counter.getCount();

			if (count > max) {
				max = count;
			}

			if (count < min) {
				min = count;
			}
		}

		max = dist.getValue(dist.size() - 2).getCount();

		span = Math.log(max - min + 1);
	}

	@Override
	public long getValue(MapPosition position) {
		int index = map.getIndex(position);

		int minor = matrix.getMinorByIndex(index);

		ArticleCounter counter = counterByMinor.get(minor);

		if (counter != null) {
			return getValue(counter.getCount());
		} else {
			return 0;
		}
	}

	private long getValue(int count) {
		return (long) (255 * Math.log(count + 1 - min) / span);
	}

	@Override
	public Scale getScale() {
		return Scale.CONTINUOUS;
	}

	@Override
	public Map<Integer, String> getLegend() {
		Map<Integer, String> result = new HashMap<Integer, String>();

		for (int i = min; i <= max;) {
			result.put((int) getValue(i), Integer.toString(i));

			int newI = i * 2;

			if (newI - i > 10) {
				newI = (newI / 10) * 10;
			}

			i = newI;
		}

		return result;
	}

}

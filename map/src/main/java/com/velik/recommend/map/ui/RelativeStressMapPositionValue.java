package com.velik.recommend.map.ui;

import java.util.Collections;
import java.util.Map;

import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

/**
 * The stress relative to a specified point.
 */
public class RelativeStressMapPositionValue implements MapPositionValue {

	private StressMatrix matrix;
	private int relativeToIndex;
	private StressMap map;
	private int min;
	private int max;

	RelativeStressMapPositionValue(StressMap map, StressMatrix matrix, MapPosition relativeToPosition) {
		this.matrix = matrix;
		this.map = map;
		relativeToIndex = map.getIndex(relativeToPosition);

		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;

		for (int i = 0; i < matrix.size(); i++) {
			if (i != relativeToIndex) {
				int value = -matrix.get(relativeToIndex, i);

				if (value > max) {
					max = value;
				}

				if (value < min) {
					min = value;
				}
			}
		}
	}

	@Override
	public long getValue(MapPosition position) {
		if (max == min) {
			return 0;
		}

		int index = map.getIndex(position);

		if (index == relativeToIndex) {
			return 255;
		}

		int unnormalizedValue = -matrix.get(relativeToIndex, index);

		return 255 * (unnormalizedValue - min) / (max - min);
	}

	@Override
	public Scale getScale() {
		return Scale.CONTINUOUS;
	}

	@Override
	public Map<Integer, String> getLegend() {
		return Collections.emptyMap();
	}

}

package com.velik.recommend.map.ui;

import java.util.HashMap;
import java.util.Map;

import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

public class FaithfulnessMapPositionValue implements MapPositionValue {

	private Map<Integer, Integer> faithfulnessByMinor;
	private StressMap map;
	private StressMatrix matrix;

	public FaithfulnessMapPositionValue(Map<Integer, Integer> faithfulnessByMinor, StressMap map, StressMatrix matrix) {
		this.faithfulnessByMinor = faithfulnessByMinor;
		this.map = map;
		this.matrix = matrix;
	}

	@Override
	public long getValue(MapPosition position) {
		int index = map.getIndex(position);

		int minor = matrix.getMinorByIndex(index);

		Integer counter = faithfulnessByMinor.get(minor);

		if (counter != null) {
			return counter;
		} else {
			return 0;
		}
	}

	@Override
	public Scale getScale() {
		return Scale.CONTINUOUS;
	}

	@Override
	public Map<Integer, String> getLegend() {
		return new HashMap<Integer, String>();
	}
}

package com.velik.recommend.map.ui;

import java.util.Collections;
import java.util.Map;

import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;

public class StressMapPositionValue implements MapPositionValue {
	private StressMap map;

	StressMapPositionValue(StressMap map) {
		this.map = map;
	}

	@Override
	public long getValue(MapPosition position) {
		return map.calculateStress(position);
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

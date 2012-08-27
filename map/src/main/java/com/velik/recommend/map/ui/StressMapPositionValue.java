package com.velik.recommend.map.ui;

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
		return Scale.LINEAR;
	}

}

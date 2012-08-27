package com.velik.recommend.map.ui;

import com.velik.recommend.map.StressMap.MapPosition;

public interface MapPositionValue {

	long getValue(MapPosition position);

	Scale getScale();

}

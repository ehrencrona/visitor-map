package com.velik.recommend.map;

import com.velik.util.Factory;

public class StressMapFactory implements Factory<StressMap> {
	private Stresses stresses;

	public StressMapFactory(Stresses stresses) {
		this.stresses = stresses;
	}

	public StressMap create() {
		StressMap map = new StressMap(stresses, 64, 64);

		map.setForceReach(3);
		map.anneal(40000000);

		return map;
	}
}

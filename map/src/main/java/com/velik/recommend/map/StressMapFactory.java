package com.velik.recommend.map;

import com.velik.util.Factory;

public class StressMapFactory implements Factory<StressMap> {
	private Stresses stresses;

	public StressMapFactory(Stresses stresses) {
		this.stresses = stresses;
	}

	public StressMap create() {
		StressMap map = new StressMap(stresses, 64, 32);

		map.setForceReach(4);
		map.anneal(3000000);

		return map;
	}
}

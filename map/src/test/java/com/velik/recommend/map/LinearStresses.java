package com.velik.recommend.map;

import com.velik.recommend.map.Stresses;

public class LinearStresses implements Stresses {

	private int size;

	public LinearStresses(int size) {
		this.size = size;
	}

	@Override
	public int get(int i, int j) {
		assert i != j;

		return Math.abs(i - j);
	}

	@Override
	public int size() {
		return size;
	}

}

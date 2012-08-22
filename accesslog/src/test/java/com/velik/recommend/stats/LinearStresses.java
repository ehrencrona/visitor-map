package com.velik.recommend.stats;

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

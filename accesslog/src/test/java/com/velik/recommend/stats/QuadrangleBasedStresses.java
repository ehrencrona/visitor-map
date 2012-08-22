package com.velik.recommend.stats;

public class QuadrangleBasedStresses implements Stresses {

	private int size;
	private int side;

	public QuadrangleBasedStresses(int size) {
		this.size = size;
		this.side = (int) Math.sqrt(size);
	}

	@Override
	public int get(int i, int j) {
		assert i != j;

		return Math.abs(quadrangle(i) - quadrangle(j));
	}

	private int quadrangle(int i) {
		int verticalIndex = (i * 2) / size;
		int horizontalIndex = (2 * i / side) % 2;

		return 2 * verticalIndex + horizontalIndex;
	}

	@Override
	public int size() {
		return size;
	}
}

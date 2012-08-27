package com.velik.recommend.map;

import com.velik.recommend.map.Stresses;

public class CheckerBoardStresses implements Stresses {
	private int size;
	private int cols;

	CheckerBoardStresses(int size, int cols) {
		this.size = size;
		this.cols = cols;
	}

	@Override
	public int get(int i, int j) {
		assert i != j;

		return (Math.abs((i % cols) - (j % cols)) + Math.abs((i / cols) - (j / cols))) % 2;
	}

	@Override
	public int size() {
		return size;
	}

}

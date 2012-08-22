package com.velik.recommend.stats;

import static java.lang.Math.abs;

import java.util.Random;

public class DefaultRandomNumberGenerator implements RandomNumberGenerator {
	private Random random = new Random();

	@Override
	public int nextInt(int max) {
		return abs(random.nextInt(max));
	}

}

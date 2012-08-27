package com.velik.recommend.map;

import static java.lang.Math.abs;

import java.io.Serializable;
import java.util.Random;

public class DefaultRandomNumberGenerator implements RandomNumberGenerator, Serializable {
	private static final long serialVersionUID = 0L;
	private Random random = new Random();

	@Override
	public int nextInt(int max) {
		return abs(random.nextInt(max));
	}

}

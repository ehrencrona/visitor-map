package com.velik.recommend.map;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

public class StressMatrixTest {

	@Test
	public void test() {

		Map<Integer, List<Long>> usersByMinor = new TreeMap<Integer, List<Long>>();

		usersByMinor.put(10, Arrays.asList(1L, 2L));
		usersByMinor.put(20, Arrays.asList(2L, 3L));
		usersByMinor.put(30, Arrays.asList(3L, 4L));
		usersByMinor.put(40, Arrays.asList(1L, 4L));

		Stresses matrix = StressMatrix.fromUsersByMinor(usersByMinor);

		Assert.assertEquals(-1, matrix.get(0, 1));
		Assert.assertEquals(-1, matrix.get(1, 2));
		Assert.assertEquals(-1, matrix.get(0, 3));
		Assert.assertEquals(-1, matrix.get(3, 0));
		Assert.assertEquals(0, matrix.get(0, 2));
		Assert.assertEquals(0, matrix.get(1, 3));
		Assert.assertEquals(0, matrix.get(3, 1));

	}

	@Test
	public void test2() {

		Map<Integer, List<Long>> usersByMinor = new TreeMap<Integer, List<Long>>();

		usersByMinor.put(30, Arrays.asList(1L, 2L));
		usersByMinor.put(20, Arrays.<Long> asList());
		usersByMinor.put(10, Arrays.asList(1L, 2L));

		Stresses matrix = StressMatrix.fromUsersByMinor(usersByMinor);

		Assert.assertEquals(0, matrix.get(0, 1));
		Assert.assertEquals(-2, matrix.get(0, 2));
		Assert.assertEquals(0, matrix.get(1, 2));
	}

	@Test
	public void testOverlap() {
		Assert.assertEquals(3, StressMatrix.overlap(Arrays.asList(1L, 2L, 4L, 8L), Arrays.asList(2L, 4L, 5L, 8L, 9L)));
	}
}

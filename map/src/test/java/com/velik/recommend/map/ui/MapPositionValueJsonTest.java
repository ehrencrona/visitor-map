package com.velik.recommend.map.ui;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.Stresses;

public class MapPositionValueJsonTest {

	@Test
	public void test() throws Exception {
		Stresses stresses = new Stresses() {

			@Override
			public int get(int i, int j) {
				return i;
			}

			@Override
			public int size() {
				return 16;
			}
		};

		StringWriter writer = new StringWriter();

		new MapPositionValueJson(new MapPositionValue() {

			@Override
			public long getValue(MapPosition position) {
				return position.getX();
			}

			@Override
			public Scale getScale() {
				return Scale.DISCRETE;
			}

		}, new StressMap(stresses, 4, 4)).print(writer);

		Assert.assertEquals(
				"[[0],[85],[170],[255]],[[0],[85],[170],[255]],[[0],[85],[170],[255]],[[0],[85],[170],[255]]", writer
						.getBuffer().toString());
	}
}

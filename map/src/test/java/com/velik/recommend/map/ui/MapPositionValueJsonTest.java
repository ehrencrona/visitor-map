package com.velik.recommend.map.ui;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

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

			@Override
			public Map<Integer, String> getLegend() {
				Map<Integer, String> result = new HashMap<Integer, String>();

				result.put(1, "foo");
				result.put(2, "bar");

				return result;
			}

		}, new StressMap(stresses, 4, 4)).print(writer);

		Assert.assertEquals(
				"{'map':[[0,85,170,255],[0,85,170,255],[0,85,170,255],[0,85,170,255]],'legend':{1:'foo',2:'bar'},'scale':'discrete'}"
						.replace('\'', '"'), writer.getBuffer().toString());
	}
}

package com.velik.recommend.map.ui;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.velik.json.JsonArray;
import com.velik.json.JsonMap;
import com.velik.json.JsonObject;
import com.velik.recommend.map.StressMap;

public class MapPositionValueJson extends JsonObject {
	private static final Logger LOGGER = Logger.getLogger(MapPositionValueJson.class.getName());
	private MapPositionValue positionValue;
	private StressMap map;

	MapPositionValueJson(MapPositionValue positionValue, StressMap map) {
		this.positionValue = positionValue;
		this.map = map;
	}

	@Override
	public void print(Writer writer) throws IOException {
		JsonArray array = new JsonArray();

		if (positionValue.getScale() == Scale.CONTINUOUS) {
			long[][] values = new long[map.getHeight()][map.getWidth()];

			long max = Long.MIN_VALUE;
			long min = Long.MAX_VALUE;

			for (int y = 0; y < map.getHeight(); y++) {
				for (int x = 0; x < map.getWidth(); x++) {
					long value = positionValue.getValue(map.pos(x, y));

					if (value > max) {
						max = value;
					}

					if (value < min) {
						min = value;
					}

					values[y][x] = value;
				}
			}

			long valueInterval = max - min;

			if (valueInterval == 0) {
				LOGGER.log(Level.WARNING, "All cells have the same value.");

				valueInterval = 1;
			}

			for (int y = 0; y < values.length; y++) {
				JsonArray row = new JsonArray();
				for (int x = 0; x < values[y].length; x++) {
					row.add(255 * (values[y][x] - min) / valueInterval);
				}
				array.add(row);
			}
		} else {
			for (int y = 0; y < map.getHeight(); y++) {
				JsonArray row = new JsonArray();
				for (int x = 0; x < map.getWidth(); x++) {
					row.add(positionValue.getValue(map.pos(x, y)));
				}
				array.add(row);
			}
		}

		new JsonMap().put("map", array).put("scale", positionValue.getScale().toString().toLowerCase())
				.put("legend", new JsonMap().putAll(positionValue.getLegend())).print(writer);
	}
}

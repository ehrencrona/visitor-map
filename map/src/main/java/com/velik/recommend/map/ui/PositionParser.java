package com.velik.recommend.map.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;

public class PositionParser {
	private static final Pattern POSITION_PATTERN = Pattern.compile("([0-9]*)x([0-9]*)");

	private StressMap map;

	public PositionParser(StressMap map) {
		this.map = map;
	}

	public MapPosition parse(String string) throws ServletException {
		Matcher matcher = POSITION_PATTERN.matcher(string);

		if (!matcher.find()) {
			throw new ServletException("Position \"" + string + "\" did not match <x>x<y> pattern.");
		}

		int col = Integer.parseInt(matcher.group(1));
		int row = Integer.parseInt(matcher.group(2));

		return map.pos(col, row);
	}

}

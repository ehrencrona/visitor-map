package com.velik.recommend.map.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

public class DepartmentMapPositionValue implements MapPositionValue {
	private StressMap map;
	private Map<Integer, ArticleInfo> articleInfos;
	protected Map<String, Integer> indexByName = new HashMap<String, Integer>();
	private Map<Integer, String> nameByIndex = new HashMap<Integer, String>();
	private StressMatrix matrix;
	private boolean onlyTopLevel;
	private double scale;
	private boolean initialized;

	public DepartmentMapPositionValue(StressMap map, Map<Integer, ArticleInfo> articleInfos, StressMatrix matrix,
			boolean onlyTopLevel) {
		this.map = map;
		this.articleInfos = articleInfos;
		this.matrix = matrix;
		this.onlyTopLevel = onlyTopLevel;
	}

	protected synchronized void initialize() {
		initialized = true;

		for (ArticleInfo info : articleInfos.values()) {
			String department = getDepartment(info);

			if (!indexByName.containsKey(department)) {
				int index = indexByName.size() + 1;
				indexByName.put(department, index);
			}
		}

		scale = 255.0 / indexByName.size();

		for (Entry<String, Integer> entry : indexByName.entrySet()) {
			nameByIndex.put((int) (scale * entry.getValue()), Obfuscator.obfuscate(entry.getKey()));
		}
	}

	protected String getDepartment(ArticleInfo info) {
		if (!initialized) {
			initialize();
		}

		String current = info.department;

		if (onlyTopLevel) {
			int i = current.indexOf('/', 1);

			if (i > 0) {
				current = current.substring(0, i);
			}
		}

		if ("".equals(current)) {
			return "Unbekannt";
		}

		return current;
	}

	@Override
	public long getValue(MapPosition position) {
		if (!initialized) {
			initialize();
		}

		int index = map.getIndex(position);

		int minor = matrix.getMinorByIndex(index);

		ArticleInfo articleInfo = articleInfos.get(minor);

		if (articleInfo != null) {
			Integer departmentIndex = indexByName.get(getDepartment(articleInfo));

			if (departmentIndex != null) {
				return (int) (scale * departmentIndex);
			}
		}

		return 0;
	}

	@Override
	public Scale getScale() {
		return Scale.DISCRETE;
	}

	@Override
	public Map<Integer, String> getLegend() {
		if (!initialized) {
			initialize();
		}

		return nameByIndex;
	}

}

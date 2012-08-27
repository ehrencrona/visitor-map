package com.velik.recommend.map.ui;

import java.util.HashMap;
import java.util.Map;

import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

public class DepartmentMapPositionValue implements MapPositionValue {
	private StressMap map;
	private Map<Integer, ArticleInfo> articleInfos;
	private Map<String, Integer> departments = new HashMap<String, Integer>();
	private StressMatrix matrix;

	public DepartmentMapPositionValue(StressMap map, Map<Integer, ArticleInfo> articleInfos, StressMatrix matrix,
			boolean onlyTopLevel) {
		this.map = map;
		this.articleInfos = articleInfos;
		this.matrix = matrix;

		for (ArticleInfo info : articleInfos.values()) {
			String current = info.department;

			if (onlyTopLevel) {
				int i = current.indexOf('/');

				if (i > 0) {
					current = current.substring(0, i);
				}
			}

			if (!departments.containsKey(current)) {
				departments.put(current, departments.size() + 1);
			}
		}
	}

	@Override
	public long getValue(MapPosition position) {
		int index = map.getIndex(position);

		int minor = matrix.getMinorByIndex(index);

		ArticleInfo articleInfo = articleInfos.get(minor);

		if (articleInfo != null) {
			Integer departmentIndex = departments.get(articleInfo.department);

			if (departmentIndex != null) {
				return departmentIndex;
			}
		}

		return 0;
	}

	@Override
	public Scale getScale() {
		return Scale.DISCRETE;
	}

}

package com.velik.recommend.map.ui;

import java.util.Map;

import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

public class ArticleTypeMapPositionValue implements MapPositionValue {
	private StressMap map;
	private Map<Integer, ArticleInfo> articleInfos;
	private StressMatrix matrix;

	public ArticleTypeMapPositionValue(StressMap map, Map<Integer, ArticleInfo> articleInfos, StressMatrix matrix) {
		this.map = map;
		this.articleInfos = articleInfos;
		this.matrix = matrix;
	}

	@Override
	public long getValue(MapPosition position) {
		int index = map.getIndex(position);

		int minor = matrix.getMinorByIndex(index);

		ArticleInfo articleInfo = articleInfos.get(minor);

		if (articleInfo != null) {
			return articleInfo.type.ordinal() + 1;
		}

		return 0;
	}

	@Override
	public Scale getScale() {
		return Scale.DISCRETE;
	}

}

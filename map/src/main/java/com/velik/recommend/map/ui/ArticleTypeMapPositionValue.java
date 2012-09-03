package com.velik.recommend.map.ui;

import java.util.HashMap;
import java.util.Map;

import com.velik.recommend.corpus.ArticleType;
import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

public class ArticleTypeMapPositionValue implements MapPositionValue {
	private StressMap map;
	private Map<Integer, ArticleInfo> articleInfos;
	private StressMatrix matrix;
	private int scale;

	public ArticleTypeMapPositionValue(StressMap map, Map<Integer, ArticleInfo> articleInfos, StressMatrix matrix) {
		this.map = map;
		this.articleInfos = articleInfos;
		this.matrix = matrix;

		scale = 255 / (ArticleType.values().length + 1);
	}

	@Override
	public long getValue(MapPosition position) {
		int index = map.getIndex(position);

		int minor = matrix.getMinorByIndex(index);

		ArticleInfo articleInfo = articleInfos.get(minor);

		if (articleInfo != null) {
			return scale * (articleInfo.type.ordinal() + 1);
		}

		return 0;
	}

	@Override
	public Scale getScale() {
		return Scale.DISCRETE;
	}

	@Override
	public Map<Integer, String> getLegend() {
		Map<Integer, String> result = new HashMap<Integer, String>();

		for (ArticleType type : ArticleType.values()) {
			result.put(scale * (type.ordinal() + 1), type.name().toLowerCase());
		}

		return result;
	}

}

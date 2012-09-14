package com.velik.recommend.map.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.velik.recommend.map.MapContext;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.util.Factory;
import com.velik.util.MemoizingFactory;

public class MapPositionValueServlet extends AbstractHttpServlet {
	private static final String DEFAULT_DIMENSION = "department";
	private static Map<String, Factory<MapPositionValue>> valueByName = new HashMap<String, Factory<MapPositionValue>>();

	static {
		valueByName.put("stress", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				return new StressMapPositionValue(MapContext.getContext().getMap());
			}
		}));
		valueByName.put("type", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new ArticleTypeMapPositionValue(context.getMap(), context.getArticleInfo(), context
						.getStressMatrix());
			}
		}));
		valueByName.put("department", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new DepartmentMapPositionValue(context.getMap(), context.getArticleInfo(), context
						.getStressMatrix(), true);
			}
		}));
		valueByName.put("groups", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new MappedDepartmentMapPositionValue(context.getMap(), context.getArticleInfo(), context
						.getStressMatrix());
			}
		}));
		valueByName.put("regio", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new MappedDepartmentMapPositionValue(context.getMap(), context.getArticleInfo(), context
						.getStressMatrix(), "regio");
			}
		}));
		valueByName.put("sport", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new MappedDepartmentMapPositionValue(context.getMap(), context.getArticleInfo(), context
						.getStressMatrix(), "sport");
			}
		}));
		valueByName.put("allgemein", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new MappedDepartmentMapPositionValue(context.getMap(), context.getArticleInfo(), context
						.getStressMatrix(), "allgemein");
			}
		}));
		valueByName.put("popularity", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new PopularityMapPositionValue(context.getAccessesByArticle(), context.getMap(), context
						.getStressMatrix());
			}
		}));
		valueByName.put("faithfulness", memoize(new Factory<MapPositionValue>() {
			@Override
			public MapPositionValue create() {
				MapContext context = MapContext.getContext();
				return new FaithfulnessMapPositionValue(context.getFaithfulnessByArticle(), context.getMap(), context
						.getStressMatrix());
			}
		}));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MapContext context = MapContext.getContext();

		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			pathInfo = "/" + DEFAULT_DIMENSION;
		}

		String valueName = pathInfo.substring(1);

		MapPositionValue positionValue;

		if (valueName.equals("relativestress")) {
			String positionString = request.getParameter("pos");

			if (positionString == null) {
				throw new ServletException("Expected \"pos\" parameter for relative stress.");
			}

			MapPosition position = new PositionParser(context.getMap()).parse(positionString);

			positionValue = new RelativeStressMapPositionValue(context.getMap(), context.getStressMatrix(), position);
		} else {
			positionValue = getMapPositionValue(valueName);
		}

		respondWithJson(new MapPositionValueJson(positionValue, context.getMap()), response);
	}

	private static Factory<MapPositionValue> memoize(Factory<MapPositionValue> factory) {
		return new MemoizingFactory<MapPositionValue>(factory);
	}

	private synchronized MapPositionValue getMapPositionValue(String name) throws ServletException {
		Factory<MapPositionValue> result = valueByName.get(name);

		if (result == null) {
			throw new ServletException("Unknown value " + name + ".");
		}

		return result.create();
	}
}

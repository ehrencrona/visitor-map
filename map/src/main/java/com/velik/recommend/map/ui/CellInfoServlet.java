package com.velik.recommend.map.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.velik.json.JsonMap;
import com.velik.recommend.map.ArticleInfo;
import com.velik.recommend.map.Context;
import com.velik.recommend.map.StressMap;
import com.velik.recommend.map.StressMap.MapPosition;
import com.velik.recommend.map.StressMatrix;

public class CellInfoServlet extends AbstractHttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();

		if (pathInfo == null) {
			throw new ServletException("No position provided.");
		}

		if (!pathInfo.startsWith("/")) {
			throw new ServletException("Path info should start with slash.");
		}

		Context context = Context.getContext();
		StressMap map = context.getMap();

		MapPosition position = new PositionParser(map).parse(pathInfo.substring(1));

		int index = map.getIndex(position);

		StressMatrix matrix = context.getStressMatrix();

		int minor = matrix.getMinorByIndex(index);

		ArticleInfo articleInfo = null;

		int count = 0;

		if (minor > 0) {
			articleInfo = context.getArticleInfo().get(minor);

			count = context.getAccessesByArticle().get(minor).getCount();
		}

		if (articleInfo != null) {
			String subline = "";
			String title = articleInfo.title;

			int i = title.indexOf(':');

			if (i != -1) {
				subline = title.substring(0, i).trim();
				title = title.substring(i + 1).trim();
			}

			respondWithJson(new JsonMap().put("col", position.getX()).put("minor", minor).put("row", position.getY())
					.put("title", title).put("subline", subline).put("type", articleInfo.type.toString().toLowerCase())
					.put("count", count).put("department", articleInfo.department), resp);
		} else {
			respondWithJson(new JsonMap().put("title", "Unknown article 1." + minor), resp);
		}
	}
}
